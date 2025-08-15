variable "resource_group_name" {
  description = "Name of the Azure Resource Group"
  type        = string
  default     = "amoshlbRG"
}

variable "location" {
  description = "Azure region where resources will be created"
  type        = string
  default     = "East US"
}

variable "vnet_name" {
  description = "Name of the Virtual Network"
  type        = string
  default     = "vnet-lb"
}

variable "ssh_public_key" {
  description = "SSH public key for VM access - set via TF_VAR_ssh_public_key environment variable"
  type        = string
  sensitive   = true
}

variable "app_install_script" {
  description = "Custom script to install and run the Spring Boot application"
  type        = string
  default     = <<-EOF
#!/bin/bash
apt update
apt install -y openjdk-21-jre-headless
mkdir -p /home/azureuser/app
cd /home/azureuser/app
wget https://amoshstorageapp.blob.core.windows.net/app/azure-loadbalancer-0.0.1-SNAPSHOT.jar
cat > startup.sh << 'INNER_EOF'
#!/bin/bash
cd /home/azureuser/app
nohup java -jar azure-loadbalancer-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
INNER_EOF
chmod +x startup.sh
echo "@reboot /home/azureuser/app/startup.sh" | crontab -u azureuser -
su azureuser -c "/home/azureuser/app/startup.sh"
EOF
}
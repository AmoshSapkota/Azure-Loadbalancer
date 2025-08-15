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
  description = "SSH public key for VM access"
  type        = string
  default     = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQDgxYJDzW8FWjuRhYnEXWmYTooGQi6fc7qJiMyaioSmeVmT8qkOrTe7jALFvTg9PZyZqxYbH0leH+JBIzkZRhW9NOXyOzrphEmVmM1xuvphifDnPfU0vmQpqDpFn8oD/MmkzOOPQjCjwLxttYb4adYDPx06ayrJNupilnHhiW+X/oyYr54TvIFz0v3HpdyCpG74CWT+Y8cpRVXzfaavLft+lSJ3R3ZMexFfe72FbbDD1AVMZp5jvTFK60pgtZjHCEw4nSGbs4rs8UopD7wBDMG396MFxLBR5KxVqa0DFDDx2e4gNWWGRal03SQiipyxsGA2KSmK+/awiXOq64hz+U9TRMfPo2w5P/6Wl6MZmwXDGPJpmhYAmBI+cfw8hWuIrguSrpyl8OrDK3I7KLt8u0t4zH8bN/Yy1QLtobJaqm3TdpPiMUKd3VpA4Hh8YJofcaaUPsqcjZMVyzT3+XN7PsLU3YDCrWkSBv7s05xXRPAfIoKjRxpEHQHmQtdiWplEvbs= amoshsapkota@Amoshs-MBP"
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
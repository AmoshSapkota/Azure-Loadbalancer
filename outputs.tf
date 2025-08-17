output "resource_group_name" {
  description = "Name of the resource group"
  value       = azurerm_resource_group.main.name
}

output "load_balancer_public_ip" {
  description = "Public IP address of the load balancer"
  value       = azurerm_public_ip.lb.ip_address
}

output "load_balancer_fqdn" {
  description = "FQDN of the load balancer"
  value       = azurerm_public_ip.lb.fqdn
}

output "vnet_name" {
  description = "Name of the virtual network"
  value       = azurerm_virtual_network.main.name
}

output "subnet_id" {
  description = "ID of the backend subnet"
  value       = azurerm_subnet.backend.id
}

output "load_balancer_id" {
  description = "ID of the load balancer"
  value       = azurerm_lb.main.id
}

output "vmss_id" {
  description = "ID of the VM Scale Set"
  value       = azurerm_linux_virtual_machine_scale_set.main.id
}

output "single_vm_id" {
  description = "ID of the single VM"
  value       = azurerm_linux_virtual_machine.vm_single.id
}

output "application_url" {
  description = "URL to access the application through the load balancer"
  value       = "http://${azurerm_public_ip.lb.ip_address}"
}

output "storage_account_name" {
  description = "Name of the storage account for app hosting"
  value       = azurerm_storage_account.app.name
}

output "app_blob_url" {
  description = "URL to upload the application JAR file"
  value       = "https://${azurerm_storage_account.app.name}.blob.core.windows.net/app/webapp-0.0.1-SNAPSHOT.jar"
}

output "ssh_instructions" {
  description = "SSH access instructions"
  value = <<-EOT
    SSH Access:
    - Single VM: ssh azureuser@${azurerm_public_ip.lb.ip_address} -p 2201
    - VMSS Instance 1: ssh azureuser@${azurerm_public_ip.lb.ip_address} -p 50000
    - VMSS Instance 2: ssh azureuser@${azurerm_public_ip.lb.ip_address} -p 50001
    - VMSS Instance 3: ssh azureuser@${azurerm_public_ip.lb.ip_address} -p 50002
    
    Test outbound connectivity:
    curl google.com
  EOT
}

output "vm_builder_ip" {
  description = "Public IP of the builder VM (temporary)"
  value       = azurerm_public_ip.vm_builder.ip_address
}

output "deployment_steps" {
  description = "Steps to complete the deployment"
  value = <<-EOT
    1. Upload your JAR file to: ${azurerm_storage_account.app.name}.blob.core.windows.net/app/webapp-0.0.1-SNAPSHOT.jar
    2. Wait for the image creation process to complete
    3. Test the load balancer at: http://${azurerm_public_ip.lb.ip_address}
    4. Use the load-test.sh script to trigger autoscaling
  EOT
}
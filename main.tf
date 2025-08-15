Here are the contents for the file /terraform-loadbalancer-static-website/terraform-loadbalancer-static-website/main.tf:

resource "azurerm_resource_group" "main" {
  name     = var.resource_group_name
  location = var.location
}

module "loadbalancer" {
  source                = "./modules/loadbalancer"
  load_balancer_name   = var.load_balancer_name
  frontend_ip_config    = var.frontend_ip_configuration
  backend_pool_settings = var.backend_pool_settings
  resource_group_name   = azurerm_resource_group.main.name
}

output "load_balancer_ip" {
  value = module.loadbalancer.public_ip_address
}
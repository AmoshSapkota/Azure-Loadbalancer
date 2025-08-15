resource "azurerm_public_ip" "example" {
  name                = "example-public-ip"
  location            = var.location
  resource_group_name = var.resource_group_name
  allocation_method   = "Static"
}

output "public_ip_address" {
  value = azurerm_public_ip.example.ip_address
}
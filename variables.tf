variable "load_balancer_name" {
  description = "The name of the load balancer."
  type        = string
}

variable "frontend_ip_configuration" {
  description = "Frontend IP configuration for the load balancer."
  type        = object({
    name                 = string
    public_ip_address_id = string
  })
}

variable "backend_pool_settings" {
  description = "Settings for the backend pool."
  type        = object({
    name    = string
    backends = list(object({
      ip_address = string
      port       = number
    }))
  })
}

variable "sku" {
  description = "The SKU of the load balancer."
  type        = string
  default     = "Standard"
}
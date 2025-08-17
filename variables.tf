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
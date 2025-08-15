# Terraform Load Balancer for Static Website

This project sets up a load balancer to serve a static website using Terraform. It is structured into modules for better organization and reusability.

## Project Structure

```
terraform-loadbalancer-static-website
├── modules
│   └── loadbalancer
│       ├── main.tf          # Main configuration for the load balancer module
│       ├── variables.tf     # Input variables for the load balancer module
│       ├── outputs.tf       # Output values for the load balancer module
│       └── README.md        # Documentation for the load balancer module
├── main.tf                  # Entry point for the Terraform configuration
├── variables.tf             # Input variables for the root module
├── outputs.tf               # Output values for the root module
└── README.md                # Documentation for the overall project
```

## Getting Started

To set up the load balancer for your static website, follow these steps:

1. **Clone the Repository**: Clone this repository to your local machine.
2. **Configure Variables**: Update the `variables.tf` files in both the root and the load balancer module with your desired configuration.
3. **Initialize Terraform**: Run `terraform init` in the root directory to initialize the Terraform configuration.
4. **Plan the Deployment**: Execute `terraform plan` to see the resources that will be created.
5. **Apply the Configuration**: Run `terraform apply` to create the load balancer and associated resources.

## Module Usage

The load balancer module can be used to create a load balancer with customizable settings. Refer to the `modules/loadbalancer/README.md` for detailed information on the available variables and outputs.

## Outputs

After applying the Terraform configuration, you will receive outputs such as the public IP address of the load balancer, which can be used to access your static website.

## License

This project is licensed under the MIT License. See the LICENSE file for more information.
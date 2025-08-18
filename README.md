# Azure Load Balancer with Auto-Scaling

Terraform configuration for Azure Load Balancer with VM Scale Set and auto-scaling capabilities.

## Architecture

- Load Balancer (port 80 → 8080)
- VM Scale Set with auto-scaling (1-3 instances, CPU-based)
- Spring Boot application deployment
- Network security group (HTTP/SSH access)

## Quick Start

1. **Configure SSH key**:
   ```bash
   cp Terraform/terraform.tfvars.example Terraform/terraform.tfvars
   # Edit terraform.tfvars with your SSH public key
   ```

2. **Deploy**:
   ```bash
   cd Terraform
   terraform init
   terraform apply
   ```

3. **Test**:
   ```bash
   # Get load balancer IP
   terraform output load_balancer_public_ip
   
   # Test application
   curl http://<LOAD_BALANCER_IP>
   ```

## Load Testing

Run the included load test to trigger auto-scaling:
```bash
./load-test.sh
```

Monitor scaling:
```bash
watch "az vmss list-instances --resource-group amoshlbRG --name vmss-backend --output table"
```

## Cleanup

```bash
terraform destroy
```
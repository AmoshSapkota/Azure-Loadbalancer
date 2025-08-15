# Azure Load Balancer with Terraform

This Terraform configuration creates a complete Azure Load Balancer setup with auto-scaling capabilities.

## Architecture

- **Resource Group**: Container for all resources
- **Virtual Network**: 10.0.0.0/16 with backend subnet 10.0.1.0/24
- **Network Security Group**: Allows HTTP (8080) and SSH (22)
- **Standard Load Balancer**: Routes traffic from port 80 to 8080
- **Single VM**: Individual virtual machine running Spring Boot app
- **VM Scale Set**: Auto-scaling group (1-3 instances based on CPU)
- **Auto-scaling**: Scale out at >70% CPU, scale in at <30% CPU

## Prerequisites

1. **Azure CLI**: `az login` to authenticate
2. **Terraform**: Install Terraform CLI
3. **SSH Key**: Generate SSH key pair for VM access

## Quick Start

### 1. Prerequisites
```bash
# Install Azure CLI and login
az login

# Install Terraform
# Download from: https://terraform.io/downloads

# Verify installations
az account show
terraform version
```

### 2. Clone and Configure
```bash
# Clone the repository
git clone <your-repo-url>
cd Azure-Loadbalancer

# Copy example variables and configure with your SSH key
cp terraform.tfvars.example terraform.tfvars
nano terraform.tfvars  # Edit ssh_public_key with your actual key
```

### 3. Helper Scripts (Optional)
Create these helper scripts for easier management:

**cleanup.sh:**
```bash
#!/bin/bash
echo "🗑️ Cleaning up Azure resources..."
az group delete --name amoshlbRG --yes --no-wait
echo "✅ Cleanup initiated"
```

**deploy.sh:**
```bash
#!/bin/bash
echo "🚀 Deploying with Terraform..."
terraform init
terraform plan -out=tfplan
terraform apply tfplan
echo "✅ Deployment complete!"
terraform output
```

**load-test.sh:**
```bash
#!/bin/bash
LOAD_BALANCER_URL="http://$(terraform output -raw load_balancer_public_ip)"
echo "🧪 Load testing $LOAD_BALANCER_URL"
for i in {1..100}; do
  curl -s $LOAD_BALANCER_URL > /dev/null &
  sleep 0.1
done
wait
echo "✅ Load test complete"
```

Make scripts executable:
```bash
chmod +x cleanup.sh deploy.sh load-test.sh
```

### 4. Deploy Infrastructure
```bash
# Option A: Using helper script
./deploy.sh

# Option B: Manual steps
terraform init
terraform plan
terraform apply
```

### 5. Access Application
```bash
# Get load balancer IP
terraform output load_balancer_public_ip

# Test the application
curl http://<LOAD_BALANCER_IP>

# Or visit in browser
open http://<LOAD_BALANCER_IP>
```

## Configuration Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `resource_group_name` | Resource group name | `amoshlbRG` |
| `location` | Azure region | `East US` |
| `vnet_name` | Virtual network name | `vnet-lb` |
| `ssh_public_key` | SSH public key for VMs | Set in `terraform.tfvars` (git-ignored) |

## Application

The Spring Boot application:
- Runs on port 8080
- Serves static HTML page
- Shows which VM processed the request
- Automatically installs via cloud-init script

## Load Testing

Run load testing to trigger auto-scaling:

```bash
# Start load test (using script from step 3)
./load-test.sh

# Monitor scaling in real-time
watch "az vmss list-instances --resource-group amoshlbRG --name vmss-backend --output table"

# Check CPU metrics
az monitor metrics list \
  --resource /subscriptions/$(az account show --query id -o tsv)/resourceGroups/amoshlbRG/providers/Microsoft.Compute/virtualMachineScaleSets/vmss-backend \
  --metric "Percentage CPU" \
  --interval 1m
```

## Outputs

After deployment, Terraform provides:
- Load balancer public IP
- Application URL
- Resource IDs
- VMSS and VM information

## Cleanup

```bash
# Destroy all resources
terraform destroy
```

## Features Implemented

✅ Standard Load Balancer with health probes  
✅ VM Scale Set with auto-scaling rules  
✅ Network security groups  
✅ Custom application deployment  
✅ Load balancing between VMs  
✅ Automatic VM provisioning  

## Testing

1. **Basic Connectivity**: `curl http://<LB_IP>`
2. **Load Balancing**: Multiple requests to see distribution
3. **Auto-scaling**: Run load test to trigger scaling
4. **Health Checks**: Stop app on VM to test failover

## Troubleshooting

- **SSH Access**: Use VM public IPs or NAT rules
- **App Not Starting**: Check cloud-init logs in `/var/log/cloud-init-output.log`
- **Load Balancer Issues**: Verify health probe and backend pool configuration
- **Scaling Issues**: Check autoscale rules and CPU metrics

## Architecture Diagram

```
Internet
    ↓
Load Balancer (Port 80)
    ↓
Backend Pool
    ├── Single VM (Port 8080)
    └── VMSS Instances (Port 8080)
        ├── Instance 1
        ├── Instance 2 (auto-scaled)
        └── Instance 3 (auto-scaled)
```
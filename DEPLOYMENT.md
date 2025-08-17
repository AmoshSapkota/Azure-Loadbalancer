# Azure Load Balancer Deployment Guide

This Terraform configuration implements a complete Azure load balancer solution with VM Scale Set autoscaling as specified in the requirements.

## Architecture Overview

The solution creates:

1. **VM Image Gallery**: Custom image with pre-installed Spring Boot application
2. **Single VM**: Standard_B1s instance (cheapest) without public IP
3. **VM Scale Set**: Standard_B1s instances with CPU autoscaling (1-3 instances)
4. **Standard Load Balancer**: With public IP, health probes, and load balancing rules
5. **NAT Rules**: For SSH access to all VMs
6. **Outbound Rules**: For internet connectivity from backend VMs
7. **Network Security Group**: With required port access
8. **Autoscaling**: CPU-based scaling rules

## Prerequisites

1. Azure CLI installed and logged in
2. Terraform installed
3. SSH key pair generated
4. Spring Boot application JAR file ready

## Deployment Steps

### 1. Set Environment Variables

```bash
export TF_VAR_ssh_public_key="$(cat ~/.ssh/id_rsa.pub)"
```

### 2. Initialize and Plan

```bash
terraform init
terraform plan
```

### 3. Apply Infrastructure

```bash
terraform apply
```

This will create:

- Resource group and networking
- Storage account for application hosting
- Image gallery and custom image
- Load balancer with all configurations
- Single VM and VMSS (both without public IPs)
- Autoscaling rules

### 4. Upload Application JAR

After terraform apply completes, upload your JAR file:

```bash
# Get the blob URL from terraform output
BLOB_URL=$(terraform output -raw app_blob_url)

# Upload your JAR file using Azure CLI
az storage blob upload \
  --account-name $(terraform output -raw storage_account_name) \
  --container-name app \
  --name webapp-0.0.1-SNAPSHOT.jar \
  --file target/webapp-0.0.1-SNAPSHOT.jar \
  --auth-mode login
```

### 5. Wait for Image Creation

The image creation process takes time. Monitor the builder VM:

```bash
# Check builder VM status
az vm show --resource-group $(terraform output -raw resource_group_name) --name vm-builder --query "provisioningState"
```

### 6. Test the Load Balancer

Once deployment is complete:

```bash
# Get load balancer IP
LB_IP=$(terraform output -raw load_balancer_public_ip)

# Test the application
curl http://$LB_IP
```

### 7. SSH Access

Access VMs through NAT rules:

```bash
LB_IP=$(terraform output -raw load_balancer_public_ip)

# Single VM
ssh azureuser@$LB_IP -p 2201

# VMSS instances
ssh azureuser@$LB_IP -p 50000  # Instance 0
ssh azureuser@$LB_IP -p 50001  # Instance 1
ssh azureuser@$LB_IP -p 50002  # Instance 2
```

### 8. Test Outbound Connectivity

```bash
ssh azureuser@$LB_IP -p 2201
curl google.com
```

### 9. Load Testing and Autoscaling

Run the load test to trigger autoscaling:

```bash
chmod +x load-test.sh
./load-test.sh
```

This will:

- Generate sustained load for 5 minutes
- Trigger CPU-based autoscaling
- Scale up to 3 instances when CPU > 70%
- Scale down when CPU < 30%

### 10. Monitor Autoscaling

```bash
# Check VMSS instances
az vmss list-instances --resource-group $(terraform output -raw resource_group_name) --name vmss-main --output table

# Monitor CPU metrics
az monitor metrics list \
  --resource $(terraform output -raw vmss_id) \
  --metric "Percentage CPU" \
  --interval PT1M
```

### 11. Upgrade VMSS Instances

After infrastructure changes:

```bash
az vmss update-instances --resource-group $(terraform output -raw resource_group_name) --name vmss-main --instance-ids "*"
```

## Key Features Implemented

✅ VM Image Gallery with custom application image  
✅ Cheapest VM SKU (Standard_B1s)  
✅ No public IPs on backend VMs  
✅ Standard Load Balancer with public IP  
✅ Health probe on application endpoint (/health)  
✅ Load balancing rule (port 80 → 8080)  
✅ NAT rules for SSH access  
✅ Outbound rules for internet access  
✅ CPU-based autoscaling (1-3 instances)  
✅ Network Security Group with required ports

## Troubleshooting

### Application Not Responding

1. Check if JAR file was uploaded correctly
2. SSH into VMs and check application logs: `tail -f /home/azureuser/app/app.log`
3. Verify Java process is running: `ps aux | grep java`

### Autoscaling Not Working

1. Ensure sufficient load is generated
2. Check autoscale settings: `az monitor autoscale show`
3. Monitor CPU metrics in Azure portal

### SSH Access Issues

1. Verify NSG rules allow SSH ports
2. Check NAT rule configuration
3. Ensure SSH key is correct

### Health Probe Failures

1. Verify application is running on port 8080
2. Check /health endpoint is accessible
3. Review NSG rules for port 8080

## Clean Up

```bash
terraform destroy
```

This will remove all created resources.

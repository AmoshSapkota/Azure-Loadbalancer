# Project Structure

```
Azure-Loadbalancer/
├── src/                           # Spring Boot Application
│   ├── main/
│   │   ├── java/
│   │   │   └── com/azure/loadbalancer/
│   │   │       └── AzureLbApplication.java
│   │   └── resources/
│   │       ├── static/
│   │       │   └── index.html     # Frontend HTML page
│   │       └── application.properties
│   └── target/                    # Build artifacts (gitignored)
├── main.tf                        # Main Terraform configuration
├── variables.tf                   # Terraform variables
├── outputs.tf                     # Terraform outputs
├── terraform.tfvars.example       # Example configuration
├── terraform.tfvars              # Your configuration (gitignored)
├── pom.xml                        # Maven build file
├── load-test.sh                   # Load testing script
├── README.md                      # Documentation
├── project-structure.md           # This file
├── .gitignore                     # Git ignore rules
└── helper-scripts/               # Generated from README
    ├── cleanup.sh                # Clean up Azure resources
    ├── deploy.sh                 # Deploy with Terraform
    └── load-test.sh              # Advanced load testing
```

## Key Files

### Infrastructure (Terraform)
- **`main.tf`**: Complete Azure infrastructure definition
- **`variables.tf`**: Configurable parameters  
- **`outputs.tf`**: Important values after deployment
- **`terraform.tfvars`**: Your specific configuration

### Application (Spring Boot)
- **`src/main/java/`**: Java application code
- **`src/main/resources/static/`**: Static web files
- **`pom.xml`**: Maven dependencies and build

### Scripts & Documentation
- **`README.md`**: Complete setup and usage guide
- **`load-test.sh`**: Load testing for autoscaling
- Helper scripts are documented in README (not committed)

## Gitignored Files

These files are excluded from git for security/cleanliness:
- `terraform.tfvars` (contains your SSH key)
- `.terraform/` directory and state files
- `target/` Java build artifacts
- Helper scripts (recreate from README)
- Azure CLI specific files
#!/bin/bash

# Update system packages
apt update

# Install Java 21 (required for Spring Boot)
apt install -y openjdk-21-jre-headless

# Create application directory
mkdir -p /home/azureuser/app
cd /home/azureuser/app

# Download all required files from Azure Storage
wget -O webapp-0.0.1-SNAPSHOT.jar https://${storage_account_name}.blob.core.windows.net/app/webapp-0.0.1-SNAPSHOT.jar
wget -O applicationinsights-agent.jar https://${storage_account_name}.blob.core.windows.net/app/applicationinsights-agent.jar
wget -O applicationinsights.json https://${storage_account_name}.blob.core.windows.net/app/applicationinsights.json
wget -O .env https://${storage_account_name}.blob.core.windows.net/app/.env

# Create startup script with Application Insights and environment variables
cat > startup.sh << 'INNER_EOF'
#!/bin/bash
cd /home/azureuser/app

# Source environment variables
export $(cat .env | grep -v '^#' | xargs)

# Start the application with Application Insights agent
nohup java -javaagent:applicationinsights-agent.jar \
  -jar webapp-0.0.1-SNAPSHOT.jar > app.log 2>&1 &

echo "Spring Boot application started with Application Insights"
INNER_EOF

# Make startup script executable
chmod +x startup.sh

# Set up cron job to start app on reboot
echo "@reboot /home/azureuser/app/startup.sh" | crontab -u azureuser -

# Change ownership to azureuser
chown -R azureuser:azureuser /home/azureuser/app

# Start the application immediately
su azureuser -c "/home/azureuser/app/startup.sh"

echo "Application installation completed with Application Insights!"
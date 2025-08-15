apt update
apt install -y openjdk-21-jre-headless
mkdir -p /home/azureuser/app
cd /home/azureuser/app
  wget https://amoshstorageapp.blob.core.windows.net/app
/azure-loadbalancer-0.0.1-SNAPSHOT.jar
cat > startup.sh << 'INNER_EOF'
cd /home/azureuser/app
nohup java -jar azure-loadbalancer-0.0.1-SNAPSHOT.jar .app.log 2>&1 &
INNER_EOF
chmod +x startup.sh
echo "@reboot /home/azureuser/app/startup.sh" |crontab -u azureuser - su azureuser -c "/home/azureuser/app/startup.sh"

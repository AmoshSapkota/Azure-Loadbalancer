#!/bin/bash

# Get load balancer IP from Terraform output
LOAD_BALANCER_IP=$(terraform output -raw load_balancer_public_ip 2>/dev/null)
if [ -z "$LOAD_BALANCER_IP" ]; then
    echo "Error: Could not get load balancer IP from terraform output"
    exit 1
fi

LOAD_BALANCER_URL="http://$LOAD_BALANCER_IP"
DURATION=180  # 3 minutes
CONCURRENT_REQUESTS=20

echo "Starting stress test against $LOAD_BALANCER_URL"
echo "Duration: $DURATION seconds with $CONCURRENT_REQUESTS concurrent requests"
echo "This will generate high load to trigger autoscaling..."

# Function to send intensive requests
send_stress_requests() {
    local thread_id=$1
    local end_time=$(($(date +%s) + DURATION))
    local count=0
    
    while [ $(date +%s) -lt $end_time ]; do
        # Make multiple rapid requests to increase CPU load
        for i in {1..5}; do
            curl -s --max-time 5 $LOAD_BALANCER_URL/api/products >/dev/null 2>&1 &
            curl -s --max-time 5 $LOAD_BALANCER_URL >/dev/null 2>&1 &
        done
        count=$((count + 5))
        echo "Thread $thread_id: Sent $count requests"
        sleep 0.05  # Very short sleep for high load
    done
}

# Start concurrent stress requests
for i in $(seq 1 $CONCURRENT_REQUESTS); do
    send_stress_requests $i &
done

echo "Started $CONCURRENT_REQUESTS background stress processes"
echo "Stress test will run for $DURATION seconds..."

# Wait for all background processes
wait

echo "Stress test completed!"
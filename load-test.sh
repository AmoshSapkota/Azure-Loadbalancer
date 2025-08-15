#!/bin/bash

LOAD_BALANCER_URL="http://172.190.161.191"
DURATION=300  # 5 minutes
CONCURRENT_REQUESTS=10

echo "Starting load test against $LOAD_BALANCER_URL"
echo "Duration: $DURATION seconds with $CONCURRENT_REQUESTS concurrent requests"
echo "This will generate load to trigger autoscaling..."

# Function to send requests
send_requests() {
    local thread_id=$1
    local end_time=$(($(date +%s) + DURATION))
    local count=0
    
    while [ $(date +%s) -lt $end_time ]; do
        response=$(curl -s -w "%{http_code}" -o /dev/null --max-time 10 $LOAD_BALANCER_URL)
        count=$((count + 1))
        echo "Thread $thread_id: Request $count - Status: $response"
        sleep 0.1
    done
}

# Start concurrent requests
for i in $(seq 1 $CONCURRENT_REQUESTS); do
    send_requests $i &
done

echo "Started $CONCURRENT_REQUESTS background processes"
echo "Load test will run for $DURATION seconds..."
echo "Press Ctrl+C to stop early"

# Wait for all background processes
wait

echo "Load test completed!"
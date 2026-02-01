#!/bin/bash

# Load environment variables from .env if it exists
if [ -f .env ]; then
  echo "Loading configuration from .env file..."
  export $(grep -v '^#' .env | xargs)
  
  # Load environment variables
  export $(grep -v '^#' .env | xargs)
  
  # ... (debug print) ...

  # Pass keys as System Properties (-D) to ensure they reach the forked process
  mvn spring-boot:run \
    -Dspring-boot.run.jvmArguments="-DZHIPU_API_KEY=${ZHIPU_API_KEY} -DDEEPSEEK_API_KEY=${DEEPSEEK_API_KEY}"
else
  echo "Warning: .env file not found..."
  mvn spring-boot:run \
    -Dspring-boot.run.jvmArguments="-DZHIPU_API_KEY=dummy-key -DDEEPSEEK_API_KEY=dummy-key"
fi

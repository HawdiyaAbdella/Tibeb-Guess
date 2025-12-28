#!/bin/bash

echo "Building Tibeb Guess..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo ""
echo "Running Tibeb Guess..."
mvn javafx:run



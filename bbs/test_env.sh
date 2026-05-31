#!/bin/bash

if command -v "kotlinc" > /dev/null 2>&1; then
    version=$(kotlinc -version 2>&1 | rg info)
    echo -e "kotlin compiler version:\n $version\n"
else
    echo -e "kotlin compiler not found.\n"
fi

echo -e "===================================\n"

if command -v "javac" > /dev/null 2>&1; then
    version=$(javac -version)
    echo -e "java compiler version:\n $version\n"
else
    echo "java compiler not found."
fi

echo -e "=================================\n"

if command -v "gradle" > /dev/null 2>&1; then
    info=$(gradle --version)
    echo -e "gradle info:\n $info\n"
else
    echo "kotlin compiler not found\n"
fi

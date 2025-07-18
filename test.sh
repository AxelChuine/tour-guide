#!/bin/zsh

if [ $# -eq 0 ]; then
	echo "Mauvais argument"
	exit 1
fi

for ((i = 1; i < $1; i++)); do
	mvn clean test >> result.txt
done

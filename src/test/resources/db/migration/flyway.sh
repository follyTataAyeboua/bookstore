#!/bin/sh

clear

echo "Please describe your change in freeform text (Spaces will be replaced automatically):"

read description

touch "V$(date +"%Y%m%d%H%M%S")__${description// /_}.sql"

echo Generated File!

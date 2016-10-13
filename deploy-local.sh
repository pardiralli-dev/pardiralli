#!/bin/bash


# creds for dummy db on heroku
export PARDIRALLI_DB_URL="jdbc:postgresql://ec2-54-75-230-132.eu-west-1.compute.amazonaws.com:5432/dcincbfrrg4p4u?user=hmogzaxukzxolw&password=tKe2Ugf8n8fr3uAXyHMC4GQqId&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory"
export PARDIRALLI_DB_USER="hmogzaxukzxolw"
export PARDIRALLI_DB_PASS="tKe2Ugf8n8fr3uAXyHMC4GQqId"

./gradlew bootRun


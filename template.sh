#!/bin/bash

SPARK_APPLICATION_JAR_LOCATION=`find /app/target/scala-2.11 -iname 'spark-sql-01-22.jar' | head -n1`
export SPARK_APPLICATION_JAR_LOCATION

if [ -z "$SPARK_APPLICATION_JAR_LOCATION" ]; then
	echo "Can't find a file spark-sql-01-22.jar in /app/target/scala-2.11"
	exit 1
fi

/submit.sh
#!/usr/bin/env bash

# working dir
WORKSPACE=$(cd `dirname $0`; pwd)

docker pull wnameless/oracle-xe-11g

# startup oracle
docker run -p 1521:1521 \
	-v ${WORKSPACE}/oracle.sh:/home/auto_service_bash.sh \
	-t -i wnameless/oracle-xe-11g /home/auto_service_bash.sh


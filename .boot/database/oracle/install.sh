#!/bin/bash

docker build -t oracle/test .

# startup oracle
docker run -p 1521:1521 \
	-d -i oracle/test /usr/local/work/oracle.sh


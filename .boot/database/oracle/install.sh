#!/bin/bash

WORKSPACE=$(cd `dirname $0`; pwd)

cd ./.boot/database/oracle

docker build -t oracle/test .

# startup oracle
docker run -p 11521:1521 -d -i oracle/test



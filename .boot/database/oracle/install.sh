#!/bin/bash

WORKSPACE=$(cd `dirname $0`; pwd)

cd ./.boot/database/oracle

docker build -t oracle/test .

# startup oracle
docker run -p 11521:1521 -d -i oracle/test


var=$(docker ps | grep oracle/test)
id=${var:0:12}



docker exec -i $id bash << EOF
exit;
EOF

docker exec -i $id bash << EOF
exit;
EOF


sleep 10


docker exec -i $id bash << EOF
echo '======正在调用启动'
sh /usr/local/work/enter.sh;
exit;
EOF

sleep 10


docker exec -i $id bash << EOF
sh /usr/local/work/enter.sh;
exit;
EOF
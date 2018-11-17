#!/usr/bin/env bash

var=$(docker ps | grep oracle/test)
id=${var:0:12}



docker exec -i $id bash << EOF
exit;
EOF

sleep 10


docker exec -i $id bash << EOF
echo '======正在调用启动'
sh /usr/local/work/oracle.sh;
exit;
EOF

sleep 10


docker exec -i $id bash << EOF
sh /usr/local/work/oracle.sh;
exit;
EOF
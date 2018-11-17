#!/bin/bash

export ORACLE_HOME=/u01/app/oracle/product/11.2.0/xe
export PATH=$ORACLE_HOME:$PATH
export LD_LIBRARY_PATH=$ORACLE_HOME/lib:$LD_LIBRARY_PATH
export ORACLE_SID=XE

su oracle <<EOF
sh /usr/local/work/oracle.sh
exit;
EOF


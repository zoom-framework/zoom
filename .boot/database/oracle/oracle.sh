#!/bin/bash

export ORACLE_HOME=/u01/app/oracle/product/11.2.0/xe
export PATH=$ORACLE_HOME:$PATH
export LD_LIBRARY_PATH=$ORACLE_HOME/lib:$LD_LIBRARY_PATH

/u01/app/oracle/product/11.2.0/xe/bin/sqlplus /nolog <<EOF
conn sys/oracle@xe as sysdba
create tablespace zoom datafile '/u01/app/oracle/zoom.ora' size 50m autoextend on next 50M;
create user root identified by root default tablespace zoom ;
grant all privileges to root;
exit
EOF



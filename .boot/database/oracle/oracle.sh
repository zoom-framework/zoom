#!/bin/bash

export ORACLE_HOME=/u01/app/oracle/product/11.2.0/xe
export PATH=$ORACLE_HOME:$PATH
export LD_LIBRARY_PATH=$ORACLE_HOME/lib:$LD_LIBRARY_PATH
export ORACLE_SID=XE

"$ORACLE_HOME/bin/sqlplus" -L -S / AS SYSDBA <<SQL
create user root identified by root;
grant all privileges to root;
GRANT EXECUTE ON SYS.DBMS_LOCK TO root;
exit;
SQL



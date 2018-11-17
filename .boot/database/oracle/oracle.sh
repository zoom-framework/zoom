#!/bin/bash

export ORACLE_HOME=/u01/app/oracle/product/11.2.0/xe
export PATH=$ORACLE_HOME:$PATH
export LD_LIBRARY_PATH=$ORACLE_HOME/lib:$LD_LIBRARY_PATH
export ORACLE_SID=XE

"$ORACLE_HOME/bin/sqlplus" -L -S / AS SYSDBA <<SQL
create tablespace zoom datafile '/u01/app/oracle/zoom.ora' size 50m autoextend on next 50M;
create user root identified by root default tablespace zoom ;
grant connect,resource,sysdba to root;
grant all privileges to root;
exit;
SQL


sqlplus "/as sysdba"
create tablespace zoom datafile '/u01/app/oracle/zoom.ora' size 50m autoextend on next 50M;
create user root identified by root default tablespace zoom ;
grant connect,resource,sysdba to root;
grant all privileges to root;


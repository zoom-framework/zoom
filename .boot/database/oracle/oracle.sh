#!/bin/bash

su oracle
sqlplus
create tablespace zoom datafile '/u01/app/oracle/zoom.ora' size 50m autoextend on next 50M;
create user root identified by root default tablespace zoom ;
grant all privileges to root;

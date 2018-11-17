
FROM wnameless/oracle-xe-11g

#author
MAINTAINER JZoom <jzoom8112@gmail.com>

#Working dir
ENV WORK_PATH /usr/local/work

RUN mkdir -p $WORK_PATH

COPY ./oracle.sh $WORK_PATH/oracle.sh
COPY ./enter.sh $WORK_PATH/enter.sh

RUN chmod a+x $WORK_PATH/oracle.sh
RUN chmod a+x $WORK_PATH/enter.sh

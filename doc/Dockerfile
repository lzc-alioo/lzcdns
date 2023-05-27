
# base image
FROM ibex/debian-mysql-server-5.7:latest

# MAINTAINER
MAINTAINER lzc.java@icloud.com

RUN mkdir /lzcinit
WORKDIR  /lzcinit
ADD 0-init-start.sh .

RUN chmod -R 777 /lzcinit/*
RUN /lzcinit/0-init-start.sh


#WORKDIR /docker-entrypoint-initdb.d
#ADD 1.1-init-db.sql .
#ADD 1.2-init-table.sql .
COPY 1.1-init-db.sql    /docker-entrypoint-initdb.d/1.1-init-db.sql
COPY 1.2-init-table.sql /docker-entrypoint-initdb.d/1.2-init-table.sql



#RUN apt-get update
#RUN  apt-get install vim -y && apt-get install g++ -y
#RUN echo 'alias psl="ps -eo user,pid,%cpu,%mem,vsz,rss,tty,stat,lstart,etime,command --sort=%cpu |grep java"' >> ~/.bashrc
#RUN echo 'cd ~' >> ~/.bashrc
#RUN ["/bin/bash”, "-c", "echo hello”]
#ENTRYPOINT [ "sh", "/lzcinit/0-init-start.sh" ]

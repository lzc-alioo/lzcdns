#sudo docker build -t lzcmysql57:0.2 /home/pi/work/dns/docker

#sudo docker container stop mysql57
#sudo docker container rm  mysql57
#sudo docker run --name mysql57 -p 3317:3306  -v /home/pi/work:/root/work -e MYSQL_ROOT_PASSWORD=123456 -d lzcmysql57:0.2
#
#sudo docker ps -a
#sudo docker logs mysql57

#sudo docker exec -it mysql57 bash -c 'mysql -h127.0.0.1  -P 3306 -utest -p123456  -Ddnscheater  < /root/work/dns/dbbak/20220912.sql'

# base image
FROM ibex/debian-mysql-server-5.7:latest

# MAINTAINER
MAINTAINER lzc.java@icloud.com

RUN mkdir /lzcinit
WORKDIR  /lzcinit
ADD 0-init-start.sh .

RUN chmod -R 777 /lzcinit/*
RUN /lzcinit/0-init-start.sh


WORKDIR /docker-entrypoint-initdb.d
ADD 1.1-init-db.sql .
ADD 1.2-init-table.sql .

#RUN apt-get update
#RUN  apt-get install vim -y && apt-get install g++ -y
#RUN echo 'alias psl="ps -eo user,pid,%cpu,%mem,vsz,rss,tty,stat,lstart,etime,command --sort=%cpu |grep java"' >> ~/.bashrc
#RUN echo 'cd ~' >> ~/.bashrc
#RUN ["/bin/bash”, "-c", "echo hello”]
#ENTRYPOINT [ "sh", "/lzcinit/0-init-start.sh" ]


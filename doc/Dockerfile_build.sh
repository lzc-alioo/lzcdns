
##################################
sudo docker container stop mysql57
sudo docker container rm  mysql57
sudo docker image rm lzcmysql57:0.5

sudo docker build -t lzcmysql57:0.5 /home/pi/work/dns/docker

sudo docker run --name mysql57 -p 3317:3306  -v /home/pi/work:/root/work -e MYSQL_ROOT_PASSWORD=123456 -d lzcmysql57:0.5

sudo docker ps -a
sudo docker logs mysql57

#方法1:
sudo docker exec -it mysql57 bash
#进入容器后执行
mysql -u root -p123456
mysql -u root -p123456

#方法2:
sudo docker exec -it mysql57 bash -c 'mysql -h127.0.0.1  -P 3306 -utest -p123456  -Dlzcdns'

#方法3:
sudo docker exec -it mysql57 bash -c 'mysql -h127.0.0.1  -P 3306 -utest -p123456  -Dlzcdns  < /docker-entrypoint-initdb.d/1.2-init-table.sql'

#方法4:
mysql -h192.168.16.126  -P 3317 -utest -p123456  -Dlzcdns

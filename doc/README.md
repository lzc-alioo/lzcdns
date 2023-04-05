参考这个而来 https://gitee.com/matrixy/dns-ali.git


计划支持的功能
支持批量自定义dns解析结果（比如将广告域名拦截到0.0.0.0）
springboot1升级到springboot2（放弃了，本来已经成功了，后来发现升级后后管理后台http://192.168.16.126:8053/无法访问了,涉及需要的配置太多所以放弃了）


DSN报文协议说明
http://c.biancheng.net/view/6457.html


请求报文
```
Domain Name System (query)
    Transaction ID: 0x9ad0                              #事务ID
    Flags: 0x0000 Standard query                        #报文中的标志字段
        0... .... .... .... = Response: Message is a query
                                                        #QR字段, 值为0, 因为是一个请求包
        .000 0... .... .... = Opcode: Standard query (0)
                                                        #Opcode字段, 值为0, 因为是标准查询
        .... ..0. .... .... = Truncated: Message is not truncated
                                                        #TC字段
        .... ...0 .... .... = Recursion desired: Don't do query recursively 
                                                        #RD字段
        .... .... .0.. .... = Z: reserved (0)           #保留字段, 值为0
        .... .... ...0 .... = Non-authenticated data: Unacceptable   
                                                        #保留字段, 值为0
    Questions: 1                                        #问题计数, 这里有1个问题
    Answer RRs: 0                                       #回答资源记录数
    Authority RRs: 0                                    #权威名称服务器计数
    Additional RRs: 0                                   #附加资源记录数
```
在请求中 Questions 的值不可能为 0；Answer RRs，Authority RRs，Additional RRs 的值都为 0，因为在请求中还没有响应的查询结果信息。这些信息在响应包中会有相应的值。

响应报文
```
Domain Name System (response)
    Transaction ID: 0x9ad0                                    #事务ID
    Flags: 0x8180 Standard query response, No error           #报文中的标志字段
        1... .... .... .... = Response: Message is a response
                                                              #QR字段, 值为1, 因为是一个响应包
        .000 0... .... .... = Opcode: Standard query (0)      # Opcode字段
        .... .0.. .... .... = Authoritative: Server is not an authority for
        domain                                                #AA字段
        .... ..0. .... .... = Truncated: Message is not truncated
                                                              #TC字段
        .... ...1 .... .... = Recursion desired: Do query recursively 
                                                              #RD字段
        .... .... 1... .... = Recursion available: Server can do recursive
        queries                                               #RA字段
        .... .... .0.. .... = Z: reserved (0)
        .... .... ..0. .... = Answer authenticated: Answer/authority portion
        was not authenticated by the server
        .... .... ...0 .... = Non-authenticated data: Unacceptable
        .... .... .... 0000 = Reply code: No error (0)        #返回码字段
    Questions: 1
    Answer RRs: 2
    Authority RRs: 5
    Additional RRs: 5
```




相关docker脚本
sudo docker run --name mysql57 -p 3317:3306  -v /home/pi/work:/root/work  -e MYSQL_ROOT_PASSWORD=123456 -d ibex/debian-mysql-server-5.7:latest

# (设置 Docker 开机启动)
sudo systemctl enable docker
# 设置 Docker下指定容器开机启动
sudo docker update --restart=always  mysql57

sudo docker exec -it mysql57 bash
mysql -uroot  -p123456 
CREATE DATABASE dnscheater ;
CREATE USER 'test'@'%' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON dnscheater.* to 'test'@'%';

#远程登录容器mysql
mysql -h192.168.16.126  -P 3317 -utest -p123456 -Ddnscheater
mysqldump -h192.168.16.126  -P 3317 -u test -p123456  dnscheater > `date "+%Y%m%d"`.sql 
(注：mysqldump这个命令在mac上执行有问题，默认没有安装mysqldump命令，https://blog.csdn.net/weixin_42405819/article/details/118652365)

#登录当前容器mysql
mysql -h127.0.0.1  -P 3317 -utest -p123456  -Ddnscheater
mysqldump -h127.0.0.1 -P 3306 -u test -p123456  dnscheater > /root/work/dns/dbbak/`date "+%Y%m%d"`.sql

#导入之前的备份数据
mysql -h127.0.0.1  -P 3306 -utest -p123456  -Ddnscheater  < /root/work/dns/dbbak/`date "+%Y%m%d"`.sql

#在容器外导出导入sql数据
sudo docker exec -it mysql57 bash -c "mysqldump -h127.0.0.1 -P 3306 -u test -p123456  dnscheater > /root/work/dns/dbbak/`date "+%Y%m%d"`.sql"
sudo docker exec -it mysql57 bash -c 'mysql -h127.0.0.1  -P 3306 -utest -p123456  -Ddnscheater  < /root/work/dns/dbbak/20220912.sql'




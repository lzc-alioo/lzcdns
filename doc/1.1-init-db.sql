CREATE USER 'test'@'%' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON lzcdns.* to 'test'@'%';

CREATE DATABASE lzcdns ;
USE lzcdns;

CREATE USER 'test'@'%' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON dnscheater.* to 'test'@'%';

CREATE DATABASE dnscheater ;
USE dnscheater;

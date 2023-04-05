mvn clean package -DskipTests=true

rm -rf build
mkdir build

rm -rf logs/*

path_target=/Users/alioo/work/gitstudy/dns-ali/target/
path_output=/Users/alioo/work/gitstudy/dns-ali/build/


cp $path_target/dns-ali-1.0-SNAPSHOT.jar $path_output
cp shell/* $path_output

chmod 777 $path_output/*.sh

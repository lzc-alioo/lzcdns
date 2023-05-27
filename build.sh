mvn clean package -DskipTests=true

rm -rf build/*
rm -rf logs/*

mkdir build


PATH_BASE=$(cd $(dirname $0); pwd)

path_target=${PATH_BASE}/target/
path_output=${PATH_BASE}/build/


cp $path_target/lzc-dns-1.0.0-SNAPSHOT.jar $path_output
cp shell/* $path_output

chmod 777 $path_output/*.sh

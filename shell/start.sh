#!/usr/bin/env bash
# /home/pi/work/dns/start.sh add to /etc/rc.local

PATH_BASE=$(cd $(dirname $0); pwd)
RUN_APP=lzc-dns-1.0.0-SNAPSHOT.jar
LOG_PATH=$PATH_BASE/logs
LOG_FILE=$LOG_PATH/$(date +%Y%m%d%H%M%S).log

mkdir -p $LOG_PATH
echo "$(date "+%Y-%m-%d %H:%M:%S") $RUN_APP start... user_dir: $PATH_BASE, and you can see log: $LOG_FILE"

cd "$PATH_BASE"
sudo chown -R pi:pi "$PATH_BASE"/*

#java -jar $PATH_BASE/$RUN_APP &
cmd="java -jar $PATH_BASE/$RUN_APP --spring.profiles.active=prod"
#nohup $cmd >> $LOG_FILE 2>&1  &
#sudo su - pi  -s /bin/bash -c "nohup $cmd >> $LOG_FILE 2>&1   &"
sudo -s /bin/bash -c "nohup $cmd >> $LOG_FILE 2>&1   &"


# 查询日志检测java程序是否启动成功
echo "$(date "+%Y-%m-%d %H:%M:%S") checking if started ..."

rm "${LOG_PATH}/console.log"
ln -s "${LOG_FILE}" "${LOG_PATH}/console.log"

check_times=0
#while [ -f "$LOG_FILE" ]
while [ $check_times -lt 30 ]
do
    current=$(date +%Y-%m-%d\ %H:%M)
    echo "cmd:grep \"$current\" $LOG_FILE | grep \"Started Application\""
    result=$(grep "$current" "$LOG_FILE" | grep "Started Application")

    if [ "x$result" != "x" ]
    then
        echo "$(date "+%Y-%m-%d %H:%M:%S") springboot started successful."
        break
    else
        echo "$(date "+%Y-%m-%d %H:%M:%S") waiting for start,already check_times:${check_times} ..."
        sleep 2s
    fi
    check_times=$((check_times+1))
done

if [ $check_times -eq 30 ]; then
    echo "$(date "+%Y-%m-%d %H:%M:%S") $RUN_APP started failed."
fi




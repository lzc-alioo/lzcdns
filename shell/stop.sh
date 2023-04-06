
echo "开始执行stop.sh..."

pid=$(ps aux|grep lzc-dns|grep -v grep |awk -F ' ' '{print $2}')

if [ "" != "$pid" ]; then
    sudo kill -9 $pid
    echo "执行stop.sh时计划kill掉进程pid:$pid,执行结果:$?"
else
    echo "执行stop.sh时发现进程不存在，直接跳过"
fi


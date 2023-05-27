echo 'lzc init start'

echo 'alias psl="ps -eo user,pid,%cpu,%mem,vsz,rss,tty,stat,lstart,etime,command --sort=%cpu |grep java"' >> ~/.bashrc
echo 'echo "this container maked by lzc"' >> ~/.bashrc
echo 'echo "home path : ~"' >> ~/.bashrc
echo 'cd ~' >> ~/.bashrc

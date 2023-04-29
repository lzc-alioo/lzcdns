

echo 'restart dnsserver start,check dnsserver process...';
ps aux|grep lzc-dns;

/home/pi/work/dns/stop.sh;
sleep 10
/home/pi/work/dns/start.sh;

echo 'restart dnsserver end,check dnsserver process...';
ps aux|grep lzc-dns;




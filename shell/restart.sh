

echo 'restart dnsserver start,check dnsserver process...';
jps -ml|grep dnsserver;

/home/pi/work/dns/stop.sh;
sleep 10
/home/pi/work/dns/start.sh;

echo 'restart dnsserver end,check dnsserver process...';
sudo jps -ml|grep dnsserver;




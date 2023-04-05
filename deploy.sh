

ssh pi@raspberrypi "mkdir -p /home/pi/work/dns/"

#提前配置ssh-key
scp -c chacha20-poly1305@openssh.com  -o "Compression yes" build/* pi@raspberrypi:/home/pi/work/dns/

ssh pi@raspberrypi "chown pi:pi /home/pi/work/dns/"
ssh pi@raspberrypi "chmod +x /home/pi/work/dns/*.sh"

ssh pi@raspberrypi "/home/pi/work/dns/restart.sh"
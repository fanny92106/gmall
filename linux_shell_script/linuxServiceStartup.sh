# startup zookeeper
cd zookeeper
bin/zkServer.sh start
cd ..

# startup tomcat
cd tomcat
bin/startup.sh sh
cd ..

#disable centos 7 firewall
systemctl stop firewalld
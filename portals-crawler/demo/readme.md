## User interface to submit urls and traces to crawler.
## Installation steps
# git clone trace-crawler repo
```
cd </home/ludab>
git clone https://github.com/lanl/trace-crawler/
```
# Compile the trace-crawler

 you must first generate an uberjar:

``` sh
cd ./portals-crawler
$ mvn clean package
```  
# Run trace-crawler  on Docker
A configuration to run crawler via docker-compose is provided. 
The file ./docker-compose.yaml puts every component (Mysql, Apache storm, Warcproxy, Chrome Selenium hub) into  containers.
First we launch all components:

``` sh
cd ./trace-crawler
docker-compose -f docker-compose.yaml up --build  --remove-orphans
```
# Install nginx and copy ../portals-crawler/demo/config/tracershow.conf to /etc/nginx/conf.d
``` sh
Adjust the config to your own hostname
change root directive with your project directory
change access_log to your location (create new directory to keep your nginx logs) 
change directories with your project directory: location /trace 
                                                location /capture/warc 
                           </home/ludab>/trace-crawler/traces
```
#start nginx
``` sh
 sudo service nginx start
```
# change hostname in my.properties at ../portals-crawler/demo/
#start demo server



 

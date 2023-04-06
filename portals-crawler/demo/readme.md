# User interface to submit urls and traces to crawler.
## Installation of crawler and configuration steps for interface
## git clone trace-crawler repo
```
cd </dirofproject/>
git clone https://github.com/lanl/trace-crawler/
```
## Compile the trace-crawler

 you must first generate an uberjar:

``` sh
cd ./portals-crawler
$ mvn clean package
```  
## Run trace-crawler  on Docker
A configuration to run crawler via docker-compose is provided (see readme at trace-crawler). 
The file ./docker-compose.yaml puts every component (Mysql, Apache storm, Warcproxy, Chrome Selenium hub) into  containers.
First we  can lauch all components :

``` sh
cd ./trace-crawler
docker-compose  up --build  --remove-orphans
```
and in tracer-archiver container
``` sh
docker-compose run --rm tracer-archiver
```
and in the running container run topology to run crawler:
``` sh
tracer-crawler> storm jar tracer-crawler.jar  gov.lanl.crawler.CrawlTopology -conf crawler-conf-docker.yaml
```
see also how to look up logs. 

## Configure
``` sh
change hostname, project dir in my.properties at ../portals-crawler/demo/
change project dir in start.sh at ../portals-crawler/demo/
```
## start demo server
``` sh
nohup ./start.sh > out.txt & 
```
## stop demo server
find java process and kill it
ps aux|grep templates
kill -9 <pid>

## Install nginx and copy ../portals-crawler/demo/config/tracershow.conf to /etc/nginx/conf.d
``` sh
Adjust the config to your own hostname
change root directive with your project directory
change access_log to your location (create new directory to keep your nginx logs) 
change directories with your project directory: location /trace 
                                                location /capture/warc 
                           </dirofproject>/trace-crawler/traces
```
#start nginx
``` sh
 sudo service nginx start
```  
## Nginx: changing permissions of directories where static files will reside
** Nginx needs to have read permission the files that should be served AND have execute permission in each of the parent directories along the path from the root to the served files.**
``` sh
sudo chmod 751 -R  <dirofproject>
sudo chmod 777 -R  <dirofproject>/warcs

sudo systemctl restart nginx

```
## wabac https://github.com/webrecorder/wabac.js-1.0
at </dirofproject>/trace-crawler/ 

``` sh
git  clone https://github.com/webrecorder/wabac.js  wabac
cd wabac
ln -s  </dirofproject>/trace-crawler/warcs/warcstore warcs
the wabac app  needs https://, so configure ssl for wabac url .
```
## check demo service at
``` sh
http://<hostname>/results

```

 

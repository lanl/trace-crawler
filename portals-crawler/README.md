# Crawler for scholarly portals  based on StormCrawler. 
Produces WARC files to be stored in ./warcs/warcstore directory.
The crawling of each url is guided by human recorded trace. 
The urls for crawling added to urls table from external source.
Each portal url produces separate warc file. Portals not emmit urls for further crawling. 

## Prerequisites:
* Install Mysql ~ 5.7.23
* Install Apache Storm >= 1.2.2
    * https://storm.apache.org/downloads.html
    * https://vincenzogulisano.com/2015/07/30/5-minutes-storm-installation-guide-single-node-setup/
* Install Docker    
* Install Warcprox
    * https://github.com/internetarchive/warcprox
  

To setup the crawler, please follow the steps below in order:

## Create user/ table  in mysql using 

```sh
./mysql/tablesetup.sql
```
## Selenium-Docker Browsers

The Dockerfile to create the browser containers is in the `docker-selenium` directory of the source. To build and start the browser container,

```sh
$ cd ./docker-selenium

# building the container
$ sudo docker build -t lanlproto/selenium-chrome .

# starting the browser
$ sudo docker run -d -p 4444:4444  --shm-size 8G lanlproto/selenium-chrome
```
## Create directory to store warc files and check that  warcprox can be started
For docker instances, the hostname will have to be the IP address of the host as seen by the container.
```sh
$ mkdir /warc/storage/location
$ 
$ cd path/to/warc/storage/location
$ warcprox -b $(sudo docker inspect --format "{{ .NetworkSettings.Gateway }}" $(sudo docker ps -ql)) -p 8080 --certs-dir certs -d warcs -g md5 -v --trace  -s 2000000000 
$ ps aux |grep warcprox
$ kill -9  <pid>
```

## Configure portals-crawler
Open the file ./crawler-conf.yaml in an editor and fill in the values:

* Change directory where to store warc files:
``` 
http.proxy.dir:./warcs/warcstore .
```
* Add the warcprox port and domain name in storm-crawler. The host name of the proxy should be the same as the host name provided to `warcprox` above. i.e, the output of the command:
```sh
$ sudo docker inspect --format "{{ .NetworkSettings.Gateway }}" $(sudo docker ps -ql)
```
    Eg:
      - `http.proxy.host: 172.17.0.1`
      - `http.proxy.port: 8080`
```
The program will use 5 ports from http.proxy.port to http.proxy.port +5, make sure they are free.
* The browser running as a docker container will be listening in an IP address and port for requests from selenium. So, this information will have to be entered in the property `selenium.addresses`. The URL will be of the form `http://<container-ip>:<container-port>/wd/hub`.
The container IP can be obtained by executing the command:
```sh
$ sudo docker inspect --format "{{ .NetworkSettings.IPAddress }}" $(sudo docker ps -ql)
```
The container port is the port number that was used to start the container in the command above (4444).
Eg: `selenium.addresses: "http://172.17.0.2:4444/wd/hub"`

* Change mysql parameters
``` 
 mysql.url: "jdbc:mysql://localhost:3306/portals?autoReconnect=true"
 mysql.table: "urls"
 mysql.user: "curator"
 mysql.password: "me_plenty"
```
 
* This metadata.persist parameters will be stored in mysqldb in metadata column
```
  metadata.persist:
   - warcs
   - event
   - trace
   - discoveryDate
   - filter
  metadata.transfer:
   - event
   - trace
   
```
* point out to traces config file which is at ./src/main/resources
```
//dynamic behavior  works with seedswithtraces.txt
navigationfilters.config.file: "boundary-filters3.json"
//default behavior, when filters applyed via regular expressions.
navigationfilters.config.file: "boundary-filters.json"
```
## Inject Seeds for crawl

With Storm installed, you must first generate an uberjar or use provided:

``` sh
$ mvn clean package
```

* Inject seeds with corresponding traces using seedswithtraces.txt (no recompiling requered). 
* It is tab separated in a format url metadatakey=metadatavalue metadatakey=metadatavalue.
* Populate with your own urls and traces. Trace file is referenced as url (HTTP/HTTPS of File:// protocol).


```
$ apache-storm-1.2.3/bin/storm jar target/stormcapture-0.2.jar  gov.lanl.crawler.SeedInjector ./seeds seedswithtraces.txt   -local -conf crawler-conf.yaml
```
or 
Inject seeds to the mysql table to use with default traces, which are currently part of ./src/main/resources .
  urls for crawl can be added to ./seeds/seeds.txt file

```
$ apache-storm-1.2.3/bin/storm jar target/stormcapture-0.2.jar  gov.lanl.crawler.SeedInjector ./seeds seeds.txt   -local -conf crawler-conf.yaml
```


## Run the crawler

With Storm installed, you must first generate an uberjar or use provided binary in ./target directory:

``` sh
$ mvn clean package
```

submit default topology using the storm command:

``` sh
storm jar target/stormcapture-0.2.jar gov.lanl.crawler.CrawlTopology -conf crawler-conf.yaml -local 
```

or

run crawler with flux to use flexible topology (described in flux.conf) : 
```sh
 nohup storm jar target/stormcapture-0.2.jar  -Djava.io.tmpdir=/data/tmp  org.apache.storm.flux.Flux    crawler.flux -s 10000000000 > storm.txt &
```
## To stop  crawler 
Once started,  crawler or seedinjector will runs continuously. To stop follow below instructions.
```sh
$ ps aux|grep flux
$ kill -9  <pid>
```
check also that no hanging sessions of warcprox, if crawler killed 
```sh
$ ps aux |grep warcprox
ludab     65245 38.0  0.0 5506560 40728 pts/5   Sl   19:53   0:00 /usr/bin/python2.7 /usr/local/bin/warcprox -b 172.17.0.1 -p 8072 --certs-dir certs -d /data/web/warcs/warcstore8072 -g md5 -v --trace -s 3000000000 --dedup-db-file=/dev/null --stats-db-file=/dev/null
ludab     65322  0.0  0.0 110512  2264 pts/5    S+   19:53   0:00 grep --color=auto warcprox
```

to be on the save side also clean tmp directories - 
to ensure no half cooked files left (sometimes permissions are broke if you delete directories and crawler restarted by different user).
 
```sh 
  check that no files in  ./warcs/warcstore80*
  
```
## Adding new trace for default behavoir.
To apply navigation filter(trace) with automatic url matching by  regular expression in the trace.

* copy you new file {trace}.json to ./src/main/resources;

* registester it at the  boundary-filters.json ;

* update stormcapture-0.2.jar  jar 


``` sh
$ mvn clean package
```

### Test  trace using  desktop chrome 

* install chrome driver, see https://github.com/SeleniumHQ/selenium/wiki/ChromeDriver  or http://chromedriver.chromium.org/downloads
* select chrome driver version matching your desktop chrome browser.
* clone stormarchiver repository   git clone https://github.com/lanl/trace-crawler/
* go to the portals-crawler directory
```sh
cd portals-crawler
```
* java 1.8 required ; adjust JAVA_HOME in tracetest.sh if it is  not working for you
* change directory at tracetest.sh  to directory where is your driver installed.
* compile using maven to produce stormcapture-0.2.jar   in ./target directory 
```sh
mvn clean install -f pomdemo.xml
    
```
start the trace replay
```sh
  ./tracetest.sh https://www.heise.de/  file:///Users/Lyudmila/stormarchiver/bihiyolgbh.json
  
```
 



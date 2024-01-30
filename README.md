
## The Memento Tracer toolset is a set of four components that can be used to capture the essence of web publications. The components are:

### Browser extension:
This extension records a user's interactions with a web page, such as clicks, scrolls, and form submissions. The recorded interactions are stored in a JSON file called a "trace."
The chrome browser extension to create the trace  can be found at  https://github.com/lanl/Memento-Tracer-Extention 
### Trace-Crawler: 
This component uses traces to navigate web pages and capture their content. The crawler can be configured to crawl specific URLs, or it can crawl entire websites.
### Web application:
This application allows users to submit URLs and traces to the crawler. The application also provides a dashboard that displays the status of crawling jobs.
To install demo web interface to submit the crawling jobs see :
https://github.com/lanl/trace-crawler/tree/master/portals-crawler/demo
Example of tracerdemo installation: http://tracerdemo.mementoweb.org/
### Desktop Trace test tool:
This tool allows users to visualize the steps taken by the crawler to follow a trace.
see https://github.com/lanl/trace-crawler/tree/master/portals-crawler/  section "desktop test"
this short video recorded work of desktop test. You can see the elements  about to be activated turn yellow. 
https://github.com/lanl/Memento-Tracer-Extention/blob/master/examples/git_issues_next/githubnext-2022-06-08.avi

## Trace-Crawler  based on StormCrawler library, see https://github.com/DigitalPebble/storm-crawler/wiki. 
To craft compact archives of web resources based on user-specified criteria, we've implemented a focused crawling approach. This method employs a StormCrawler plugin to execute traces. The plugin interprets JSON-formatted trace data, utilizing the Selenium API to issue commands via the Selenium hub, which in turn forwards these commands to individual browser nodes. Configured with a proxy service, warcprox, the Remote WebDriver captures all traffic and records it into WARC files. For every URL seed, the crawler initializes and halts a dedicated proxy upon completion, ensuring that each step of a particular trace, along with its associated resources, is isolated within a single WARC file.

Here are some of the benefits of this targeted crawling process:
* Empowers users to curate personalized archives of web resources according to their defined parameters or boundaries.
* Ensures that all navigational steps within a specific trace, along with its corresponding resources, are encapsulated within a unified WARC file, maintaining temporal integrity.
* Provides a versatile and robust method for creating individualized mini-archives of web resources.

We provided two options for loading URLs and traces to the crawler to support batch operations and individual requests through our web interface. The crawler can use a list of tuples as input, where each tuple contains the URL of the web resource and the URL of the corresponding JSON trace file. The second option involves using a combination of a seed list of URLs and a registry of predefined trace files with corresponding URLs, as well as regular expressions that indicate which trace to apply to each URL.

## Build the Project:
* if you do not want to use docker go to the portals-crawler directory and follow instructions 
* or see docker instructions below
 
## Compile the trace-crawler

 you must first generate an uberjar:

``` sh
cd ./portals-crawler
$ mvn clean package
```   



## Run trace-crawler Topology on Docker
A configuration to run the topologies via docker-compose is provided. 
The file ./docker-compose.yml puts every component (Mysql, Apache storm, Warcproxy, Chrome Selenium hub) into  containers.
First we launch all components:



``` sh
docker-compose -f docker-compose.yml up --build  --remove-orphans
docker-compose run

```
Now we can launch the container tracer-archiver
``` sh
docker-compose run --rm tracer-archiver
```
and in the running container first  topology to load seeds to mysql:
``` sh
tracer-crawler> storm jar tracer-crawler.jar   gov.lanl.crawler.SeedInjector /seeds seedswithtraces.txt   -conf crawler-conf-docker.yaml
```
and in the running container second  topology to run crawler:
``` sh
tracer-crawler> storm jar tracer-crawler.jar  gov.lanl.crawler.CrawlTopology -conf crawler-conf-docker.yaml
```

Let's check whether topology is running:
``` sh
tracer-crawler> storm list
```
To kill the topology
``` sh
tracer-crawler> storm kill SeedInjector -w 10
```
Let's check logs - logs are mapped to host machine at /data
``` sh
ls -la data/supervisor/logs/workers-artifacts/*/*/worker.log
more data/supervisor/logs/workers-artifacts/CrawlTopology-1-1644877391/6700/worker.log
```
to leave the container (exit) and shut down all running containers:
``` sh
docker-compose down
```

to look at the mysql db
``` sh
docker exec -it tracer-db /bin/bash
```
in the contaner
``` sh
mysql -u cache -p 
```
at  the mysql prompt:
``` sh
use portals;
show tables;
select * from urls;
if you want to clean the db:  delete from urls;
```
if you want to bring back url to crawl again  for testing 
``` sh
update urls set status='DISCOVERED',nextfetchdate='2018-01-21 15:41:22' where url='https://wormbase.org/species/c_elegans/gene/WBGene00006604#0-9g-3';
```
Also the Storm UI on localhost is available and will provide metrics about the running topology.
## Configuration trace-crawler
the  configuration file at  the file ./storm-docker-conf/crawler-conf-docker.yaml 
* default points out to traces config file
``` 
navigationfilters.config.file: "boundary-filters3.json"
  
```
The ./seeds , ./traces, ./warcs, ./data, ./certs directories are shared between docker container and host machine. 
Make permissions for directories readable writable. 
## LANL C number C22054
© 2022. Triad National Security, LLC. All rights reserved.
This program was produced under U.S. Government contract 89233218CNA000001 for Los Alamos
National Laboratory (LANL), which is operated by Triad National Security, LLC for the U.S.
Department of Energy/National Nuclear Security Administration. All rights in the program are
reserved by Triad National Security, LLC, and the U.S. Department of Energy/National Nuclear
Security Administration. The Government is granted for itself and others acting on its behalf a
nonexclusive, paid-up, irrevocable worldwide license in this material to reproduce, prepare
derivative works, distribute copies to the public, perform publicly and display publicly, and to permit
others to do so.

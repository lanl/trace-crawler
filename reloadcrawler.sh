#!/bin/bash
# this script reboots the crawler: kills the crawler and start it again.
#add projectdir
cd /{projectdir}/trace-crawler
docker-compose down 
#docker-compose run --rm tracer-archiver storm kill CrawlTopology  -w 10 
#docker-compose run --rm tracer-archiver    storm list
#docker-compose run --rm tracer-archiver chmod 777 -R /warcs  
docker-compose up -d
## optionally you can load seeds from file 
## to crawl just landing pages you can use robot.json (it does not perform any actions on page) put your seeds file to ./seeds directory
## your_seeds.csv  is tab separated csv file with fields: url,trace url, filter
## example
##http://openaccess.mef.edu.tr/	trace=http://tracerdemo.mementoweb.org/trace/robot.json	filter=dynamic.json
## the urls should be valid urls.
#docker-compose run --rm tracer-archiver storm jar tracer-crawler.jar  gov.lanl.crawler.SeedInjector /seeds  your_seeds.csv    -conf crawler-conf-docker.yaml
docker-compose run --rm tracer-archiver storm jar tracer-crawler.jar  gov.lanl.crawler.CrawlTopology -conf crawler-conf-docker.yaml
docker-compose run --rm tracer-archiver chmod 777 -R /warcs 

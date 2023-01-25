package gov.lanl.crawler.boundary;

import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import org.openqa.selenium.remote.RemoteWebDriver;

import com.fasterxml.jackson.databind.JsonNode;

import gov.lanl.crawler.core.StatusUpdaterBolt;


public class InteraptedBlock implements Runnable {
	String urlValue;
	JsonNode root;
	RemoteWebDriver driver;
	volatile boolean shutdown=false;
	//Callback c; 
	//EventFiringWebDriver driver;
	List<SimpleEntry> urls;
	String event;
	//int ccount;
	TracePlayer trplay;
	StatusUpdaterBolt su;
	public Runnable init(JsonNode root, String urlValue, RemoteWebDriver driver, List<SimpleEntry> urls,TracePlayer trplay,String  event) {
		this.root = root;
		this.urlValue = urlValue;
		this.driver =  driver;
		this.urls = urls;
		this.trplay=trplay;
		this.event=event;
		//this.su=su;
		//this.ccount = ccount;
		return (this);
	}
	
    public void run() {
        while ((!Thread.currentThread().isInterrupted())){
        	try {
            System.out.println("Do your thing here");            
            trplay.traverseTrace(root, driver, urlValue, urls);
            //urls.forEach(link -> processLinks((SimpleEntry) link, driver, event, su));
        	}
        	catch (Exception e) {
                // good practice
                Thread.currentThread().interrupt();
                return;
            }
        }
            
    }	                
            
    }

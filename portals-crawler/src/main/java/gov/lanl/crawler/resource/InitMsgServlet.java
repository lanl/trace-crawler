package gov.lanl.crawler.resource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;



public class InitMsgServlet implements ServletContextListener {
	ExecutorService bexec ;
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		bexec.shutdown();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("context initialised");
	    bexec = Executors.newFixedThreadPool( 1);		
		bexec.execute(new QUpdater());
	}

}

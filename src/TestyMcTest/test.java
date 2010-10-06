package TestyMcTest;

import org.apache.xmlrpc.webserver.ServletWebServer;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

public class test{

	private final int port = 8080;
	
	public test()  throws Exception{

	          XmlRpcServlet servlet = new XmlRpcServlet();
	          ServletWebServer webServer = new ServletWebServer(servlet, port);
	          webServer.start();
	      }

	
	
	public static void main(String[] args) throws Exception {

		new test();
		
		
	}

}

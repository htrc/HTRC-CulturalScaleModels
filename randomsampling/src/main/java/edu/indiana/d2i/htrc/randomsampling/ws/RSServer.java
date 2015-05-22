package edu.indiana.d2i.htrc.randomsampling.ws;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.sun.jersey.spi.container.servlet.ServletContainer;

import edu.indiana.d2i.htrc.randomsampling.Configuration;

public class RSServer {
	private static Server startJetty(int port, String packageName)
		throws Exception {
		ServletHolder sh = new ServletHolder(ServletContainer.class);
		sh.setInitParameter(
			"com.sun.jersey.config.property.resourceConfigClass",
			"com.sun.jersey.api.core.PackagesResourceConfig");
		sh.setInitParameter("com.sun.jersey.config.property.packages",
			packageName);// Set the package where the services reside
		sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature",
			"true");

		Server server = new Server(port);
		ServletContextHandler context = new ServletContextHandler(server, "/",
			ServletContextHandler.SESSIONS);
		context.addServlet(sh, "/*");
		server.start();
		return server;
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = Configuration.getSingleton();
		startJetty(conf.getInt("htrc.random.port", 2000), "edu.indiana.d2i.htrc.randomsampling.ws");
	}
}

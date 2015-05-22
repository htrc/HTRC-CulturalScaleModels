package edu.indiana.d2i.htrc.randomsampling;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.PropertyConfigurator;

public class Configuration {
	private Map<String, String> properties = new HashMap<String, String>();
	private static Configuration instance = null;
	
	public static class PropertyNames {
		public static final String LOCC_RDF = "htrc.random.locc";
		public static final String VOLUME_CALLNO = "htrc.random.volume.callno";
	}
	
	private void loadConfigurations(String xmlPath) {
		try {
		    XMLConfiguration config = new XMLConfiguration(xmlPath);
		    int size = config.getList("property.name").size();
		    for (int i = 0; i < size; i++) {
		    	HierarchicalConfiguration sub = config.configurationAt(
		    		String.format("property(%d)", i));
		    	String name = sub.getString("name");
		    	String val = sub.getString("value");
		    	properties.put(name, val);
		    }
		} catch(ConfigurationException cex) {
		    throw new RuntimeException(cex);
		}
	}
	
	private Configuration() {		
		loadConfigurations("default.xml");
		
		String log4jPropertiesPath = getString("log4j.properties.path"); 
		PropertyConfigurator.configure(log4jPropertiesPath);
	}
	
	public synchronized static Configuration getSingleton() {
		if (instance == null) {
			instance = new Configuration();
		}		
		return instance;
	}
	
	public String getString(String name) {
		return properties.get(name);
	}

	public String getString(String name, String defaultVal) {
		String res = properties.get(name);
		return (res == null) ? defaultVal : res;
	}
	
	public long getLong(String name) {
		return Long.valueOf(properties.get(name));
	}
	
	public long getLong(String name, long defaultValue) {
		return (properties.get(name) == null) ? defaultValue: Long.valueOf(properties.get(name));
	}
	
	public int getInt(String name) {
		return Integer.valueOf(properties.get(name));
	}
	
	public int getInt(String name, int defaultValue) {
		return (properties.get(name) == null) ? defaultValue: Integer.valueOf(properties.get(name));
	}
	
	public void setString(String name, String value) {
		properties.put(name, value);
	}
}

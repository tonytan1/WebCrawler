package netd.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import netd.webcrawler.utils.MapFormatUtils;


/**
 * @author lester.li
 * This is config. class which is used to load all config. item with key value pair 
 * This is also a singleton class which has one instance only in whole system
 */
public class Config {

	public static boolean DEBUG_MODE= true;
	
	public static boolean enableHighlighter = false;
	
	public static boolean PRINTELEMENTNOTFOUNDMSG = false;
	
	public static String DELIMIT = "|";

	private static Properties testingProperties;
	
	private  Properties ekpProperties;
	
	private  Properties standard_en_properties;
	
	private  Properties allProperties;
	
	private static final Config instance = new Config();
	
	public static Config getInstance() {
	        return instance;
    }

	public  String getProperty(String key){
		return allProperties.getProperty(key);
	}
	
	public  void setProperty(String key, String value){
		allProperties.setProperty(key, value);
	}
	
	private static void loadProperties(Properties prop, String sProperties)
	{
		
		InputStream input = null;
		try {
			input = new FileInputStream(sProperties);
			// load a properties file
			prop.load(input);

			Map<String, String> map = new HashMap<String, String>();
			map.put("IP", prop.getProperty("IP"));
			map.put("port",  prop.getProperty("port"));
			map.put("domain",  prop.getProperty("domain"));
			map.put("baseURL", prop.getProperty("baseURL"));
			map.put("configDir",  prop.getProperty("configDir"));
			map.put("resourceDir",  prop.getProperty("resourceDir"));
			map.put("test.report.dir",  prop.getProperty("test.report.dir"));
			map.put("screenShotDir", prop.getProperty("screenShotDir"));
			map.put("skikuliDir", prop.getProperty("skikuliDir"));
			map.put("tomcatDir", System.getenv("CATALINA_HOME") +"/webapps");
			map.put("ts.instance", prop.getProperty("ts.instance"));
			map.put("ImplicitWait_millis", prop.getProperty("ImplicitWait_millis"));
			map.put("ExplicitWait_millis", prop.getProperty("ExplicitWait_millis"));
			map.put("HighlightElement_millis", prop.getProperty("HighlightElement_millis"));
			map.put("dateFormat", prop.getProperty("dateFormat"));
			
			//replace the {baseURL} with = prop.getProperty("baseURL") in loginURL property
			 for (Iterator iter = prop.keySet().iterator(); iter.hasNext();) {
				 String key = (String) iter.next();
				 prop.setProperty(key, MapFormatUtils.format(prop.getProperty(key), map)); 
			 }
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private Config(){
		ekpProperties = new Properties();
		testingProperties =new Properties();
		standard_en_properties = new Properties();
		allProperties = new Properties();
		loadProperties(testingProperties, "./conf/config.properties");
		allProperties.putAll(testingProperties);
		
		
		loadProperties(ekpProperties, getProperty("ekp.properties"));
		// sys label key and message.
		// when lable changed by dev, pls extract standard_en.properties from ekp-non-obfuscated.jar/com/netdimen/locale to 
		// project conf dirtory
		loadProperties(standard_en_properties, getProperty("standard_en.properties"));
		allProperties.putAll(ekpProperties);
		allProperties.putAll(standard_en_properties);
		
	/*	if(Config.DEBUG_MODE){
			System.out.println("baseURL:" + getProperty("baseURL"));
			System.out.println("loginURL:" + getProperty("loginURL"));
			System.out.println("UID:" + getProperty("sys.ndadmin"));
			System.out.println("PWD:" + getProperty("sys.ndadmin.pass"));	
			System.out.println("HomePage:" + getProperty("HomePage"));
			System.out.println("ManageCenter:" + getProperty("ManageCenter"));	
			System.out.println("ManageCenter:" + getProperty("ManageCenter"));	
			System.out.println("ekp.properties:" + getProperty("ekp.properties"));
			System.out.println("default.user" + getProperty("default.user"));
		}*/

	}
		
}

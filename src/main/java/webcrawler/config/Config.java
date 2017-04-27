package webcrawler.config;

/**
 *
 * Created by tonytan on 27/4/2017.
 */
public class Config {

    public static Config getInstance(){
        return new Config();
    }

    public static String getProperty(String key) {
        return "";
    }

}

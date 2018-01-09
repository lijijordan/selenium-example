import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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

    private static final String filePath = "/root/selenium-example/users/dd.txt_1402.txt";
    private List<String> accounts;
    private LinkedList<Account> accountList = new LinkedList<>();

    private WebDriver driver;

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
        System.setProperty(
                "webdriver.chrome.driver",
                "/usr/local/bin/chromedriver");
        accounts = readTxtFileIntoStringArrList(filePath);
        for (String s :
                accounts) {
            String[] s1 = s.split("\\s+");
            if (s1 != null) {
                String name = s1[0];
                String password = s1[1];
                Account accountObj = new Account();
                accountObj.setName(name);
                accountObj.setPassword(password);
                accountList.add(accountObj);
            }
        }
        System.out.println("Account size : " + accountList.size());

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(CapabilityType.ForSeleniumServer.AVOIDING_PROXY, true);
        capabilities.setCapability(CapabilityType.ForSeleniumServer.ONLY_PROXYING_SELENIUM_TRAFFIC, true);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
//        chromeOptions.addArguments("--window-size=400,800");
//        chromeOptions.addArguments("--proxy-server=socks5://127.0.0.1:2080");
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        driver = new ChromeDriver(capabilities);
//        this.driver.get("http://ip.cn");
        this.reload();
    }

    private void reload() {
        driver.manage().deleteAllCookies();
        driver.get("https://m.facebook.com");
    }


    private void loginFacebook(Account account) {
        long start = System.currentTimeMillis();
        try {
            this.reload();
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
                            WebElement msg = driver.findElement(By.xpath("//*[@id=\"root\"]/div[1]/div/div[1]/div/span"));
                            System.out.println(String.format("Message:%s", msg.getText()));
                            return true;
                        }
                    }
            );
        } catch (RuntimeException e) {
        } finally {
            System.out.println("Close driver!");
//            driver.close();
//            driver.quit();
        }
        System.out.println(String.format("Current url : %s", driver.getCurrentUrl()));
        System.out.println("Cost time:" + (System.currentTimeMillis() - start));
    }

    @Test
    public void loginFacebook() {
        Account account = this.popAccount();
        while (account != null) {
            this.loginFacebook(account);
            account = this.popAccount();
        }
    }

    private Account popAccount() {
        return this.accountList.pop();
    }

    static class Account {
        String name;
        String password;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

}

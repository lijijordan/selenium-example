import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by haozuo on 3/22/16.
 */
public class ChromeDriverTest {

    private static final int SCRAPE_FRIENDS_SIZE = 10;
    private String testUrl;
    private WebDriver driver;

    @Before
    public void prepare() {
        //setup chromedriver
        System.setProperty(
                "webdriver.chrome.driver",
                "webdriver/chromedriver");
        testUrl = "https://m.facebook.com";
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        driver = new ChromeDriver();
        //maximize window
        driver.manage().window().maximize();
    }

    @Test
    public void testMultipleTab() {
        String baseUrl = "http://www.google.com";
        driver.get(baseUrl);
//        driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"t");

        for (int i = 1; i <= SCRAPE_FRIENDS_SIZE; i++) {
            ((JavascriptExecutor) driver).executeScript("window.open()");
        }
        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());

        for (int i = 0; i < tabs.size(); i++) {
            driver.switchTo().window(tabs.get(i)); // switch back to main screen
            driver.get("https://www.facebook.com");
        }
    }

    @Test
    public void testGoogle() {

        // And now use this to visit Google
        driver.get("http://www.google.com");
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.google.com");

        // Find the text input element by its name
        WebElement element = driver.findElement(By.name("q"));

        // Enter something to search for
        element.sendKeys("Cheese!");

        // Now submit the form. WebDriver will find the form for us from the element
        element.submit();

        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());

        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().toLowerCase().startsWith("cheese!");
            }
        });

        // Should see: "cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());

        //Close the browser
        driver.quit();
    }

    @Test
    public void testTitle() throws IOException {

        // Find elements by attribute lang="READ_MORE_BTN"
        List<WebElement> elements = driver
                .findElements(By.cssSelector("[lang=\"READ_MORE_BTN\"]"));

        //Click the selected button
        elements.get(0).click();


        assertTrue("The page title should be chagned as expected",
                (new WebDriverWait(driver, 5))
                        .until(new ExpectedCondition<Boolean>() {
                            public Boolean apply(WebDriver d) {
                                System.out.println(d.getTitle());
                                return d.getTitle().equals("我眼中软件工程人员该有的常识");
                            }
                        })
        );

    }


    @Test
    public void loginFacebook(){
        driver.get("https://m.facebook.com");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Login
        System.out.println("Login");
        WebElement formEmail = driver.findElement(By.name("email"));
        formEmail.sendKeys("+8613438874692");
        WebElement formPassword = driver.findElement(By.name("pass"));
        formPassword.sendKeys("nameaini00");
        driver.findElement(By.name("login")).click();
        // Click OK
        System.out.println("Click OK");
        WebElement myDynamicElement = new WebDriverWait(driver, 180).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"root\"]/div[1]/div/div/div[3]/div[2]/form/div/button")));
    }

    @Test
    public void facebook() throws Exception {
        driver.get("https://m.facebook.com");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Login
        System.out.println("Login");
        WebElement formEmail = driver.findElement(By.name("email"));
        formEmail.sendKeys("+8613438874692");
        WebElement formPassword = driver.findElement(By.name("pass"));
        formPassword.sendKeys("nameaini00");
        driver.findElement(By.name("login")).click();
        // Click OK
        System.out.println("Click OK");
        WebElement myDynamicElement = new WebDriverWait(driver, 180).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"root\"]/div[1]/div/div/div[3]/div[2]/form/div/button")));
        myDynamicElement.click();
        // Click More
        Thread.sleep(2000);
        System.out.println("Click More");
        driver.findElement(By.xpath("//*[@id=\"mJewelNav\"]/div[6]")).click();

        // Click Friends
        System.out.println("Click Friends");
        Thread.sleep(2000);
        js.executeScript("scrollTo(0,10000)");
        new WebDriverWait(driver, 180).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"bookmarks_jewel\"]/div/div[1]/div/div/div/div/div/div[1]/ul/li[13]/div/a/div[2]"))).click();
        new WebDriverWait(driver, 180).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"root\"]/div[1]/div/div/a[5]"))).click();

        // scroll down
        System.out.println("Scroll down");
        js.executeScript("scrollTo(0,10000)");
        Thread.sleep(2000);
        js.executeScript("scrollTo(0,10000)");
        Thread.sleep(2000);
        js.executeScript("scrollTo(0,10000)");
        // Get friends list
        System.out.println("Get friends list");
        List<String> hrefList = new ArrayList<String>();
        for (int i = 1; i <= SCRAPE_FRIENDS_SIZE; i++) {
            WebElement f1 = driver.findElement(By.xpath("//*[@id=\"friends_center_main\"]/div[1]/div[" + i + "]/div[1]/a"));
            hrefList.add(f1.getAttribute("href"));
            ((JavascriptExecutor) driver).executeScript("window.open()");
        }
        // Create tabs
        System.out.println("Create tabs");
        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        for (int i = 0; i < hrefList.size(); i++) {
            driver.switchTo().window(tabs.get(i + 1)).get(hrefList.get(i)); // switch back to main screen
        }
        // Iterator photo buttons
        System.out.println("Iterator photo buttons");
        for (int i = 0; i < hrefList.size(); i++) {
            driver.switchTo().window(tabs.get(i + 1)); // switch back to main screen
            try {
                WebElement photo = new WebDriverWait(driver, 180).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"timelineBody\"]/form/div/div[1]/div/span/button[1]")));
                photo.click();
            } catch (RuntimeException exception) {
                System.out.println("no photo button!");
            }
        }
        // Upload Images
        System.out.println("Upload Images");
        for (int i = 0; i < hrefList.size(); i++) {
            driver.switchTo().window(tabs.get(i + 1)); // switch back to main screen
            driver.findElement(By.xpath("//*[@id=\"file1\"]")).sendKeys("/Users/liji/Downloads/11.png");
        }

        // Click Preview
        System.out.println("Click Preview");
        for (int i = 0; i < hrefList.size(); i++) {
            driver.switchTo().window(tabs.get(i + 1)); // switch back to main screen
            driver.findElement(By.xpath("//*[@id=\"root\"]/div[1]/div/form/div[4]/button")).click();
        }
        // Click Post
        System.out.println("Click Post");
        for (int i = 0; i < hrefList.size(); i++) {
            driver.switchTo().window(tabs.get(i + 1)); // switch back to main screen
            driver.findElement(By.xpath("//*[@id=\"composer_form\"]/div[1]/div[1]/div[3]/div/button")).click();
            WebElement webElement = new WebDriverWait(driver, 180).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), 'a new photo')]")));
            System.out.println("Post finished!!!");
        }
        driver.close();
//        new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(By.xpath("xxx")));
    }

    @After
    public void teardown() throws IOException {
        driver.quit();
    }

}

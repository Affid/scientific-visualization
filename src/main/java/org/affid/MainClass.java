package org.affid;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MainClass {

    public static void main(String[] args) {
        String name = "aafyodorov@edu.hse.ru", password = "ugybBeQ2Q";

        System.setProperty("webdriver.chrome.driver", "D:\\Programming\\Java\\testselenium\\drivers\\chromedriver.exe");

        WebDriver driver = getChromeDriver();

        WebDriverWait waiter = new WebDriverWait(driver, 10);

        scopusAuth(driver, waiter, name, password);

        click(driver, "//*[@id=\"advSearchLink\"]/span");

        wait(2,waiter);

        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"contentEditLabel\"]")));

        click(driver, "//*[@id=\"contentEditLabel\"]");
        driver.findElement(By.xpath("//*[@id=\"searchfield\"]")).sendKeys("KEY(Physics)");
        click(driver, "//*[@id=\"advSearch\"]/span[1]");

        click(driver, "//*[@id=\"resultsPerPage-button\"]/span[2]");
        click(driver, "//*[@id=\"ui-id-4\"]");

        wait(10,waiter);

        String mainPage = driver.getCurrentUrl();

        openAuthors(driver);

        List<WebElement> authors = driver.findElements(By.xpath("//div[@id = 'overlayBody_AUTHOR_NAME_AND_ID']/ul/li/label/span"));

        System.out.println(authors.size());

        if(authors.size()>0) {
            waiter.until(ExpectedConditions.elementToBeClickable(authors.get(0)));
            authors.get(0).click();
            click(driver, "//*[@id=\"overlayFooter_AUTHOR_NAME_AND_ID\"]/div/input[1]");
            click(driver, "//*[@id=\"mainResults-allPageSelectedValue\"]");
            click(driver, "//*[@id=\"selectAllMenuItem\"]/span[2]/span/ul/li[1]/label");
            wait(10,waiter);

            click(driver, "//*[@id=\"export_results\"]");
            setExportOptions(driver, waiter);
            waiter.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"exportTrigger\"]")));
            click(driver, "//*[@id=\"exportTrigger\"]");

            driver.get(mainPage);
            click(driver, "//*[@id=\"viewAllLink_AUTHOR_NAME_AND_ID\"]");
            authors = driver.findElements(By.xpath("//div[@id = 'overlayBody_AUTHOR_NAME_AND_ID']/ul/li/label/span"));

            for (int i = 1; i < authors.size(); i++) {
                waiter.until(ExpectedConditions.elementToBeClickable(authors.get(i)));
                authors.get(i).click();
                click(driver, "//*[@id=\"overlayFooter_AUTHOR_NAME_AND_ID\"]/div/input[1]");
                downloadAllPages(driver, waiter);
                driver.get(mainPage);
                click(driver, "//*[@id=\"viewAllLink_AUTHOR_NAME_AND_ID\"]");
                authors = driver.findElements(By.xpath("//div[@id = 'overlayBody_AUTHOR_NAME_AND_ID']/ul/li/label/span"));
            }
        }
        else
            driver.quit();

//        while (driver.findElement(By.xpath("//*[@id=\"resultsFooter\"]/div[2]/ul/li[@title = \"Следующая страница\"]/a")).isEnabled()) {
//            click(driver, "//*[@id=\"resultsFooter\"]/div[2]/ul/li[8]/a");
//            downloadPage(driver, waiter);
//            wait(10,waiter);
//        }
    }

    private static void setExportOptions(WebDriver driver, WebDriverWait waiter){
        waiter.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"citationGroupCheckboxes\"]/span/label")));
        click(driver, "//*[@id=\"citationGroupCheckboxes\"]/span/label");
        wait(5,waiter);

        click(driver, "//*[@id=\"citationCheckBoxes\"]/ul/li[1]/label");
        waiter.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"citationCheckBoxes\"]/ul/li[3]/label")));
        click(driver, "//*[@id=\"citationCheckBoxes\"]/ul/li[3]/label");
        waiter.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"citationCheckBoxes\"]/ul/li[8]/label")));
        click(driver, "//*[@id=\"citationCheckBoxes\"]/ul/li[8]/label");
        waiter.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"citationCheckBoxes\"]/ul/li[11]/label")));
        click(driver, "//*[@id=\"citationCheckBoxes\"]/ul/li[11]/label");
        waiter.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"otherInfoCheckboxes\"]/ul/li[4]/label")));
        click(driver, "//*[@id=\"otherInfoCheckboxes\"]/ul/li[4]/label");

        click(driver, "//*[@id=\"exportList\"]/li[5]/label");

    }

    private static void downloadAllPages(WebDriver driver, WebDriverWait waiter){
        click(driver, "//*[@id=\"mainResults-allPageSelectedValue\"]");
        click(driver, "//*[@id=\"selectAllMenuItem\"]/span[2]/span/ul/li[1]/label");
        wait(10,waiter);
        download(driver, waiter);
    }

    private static void openAuthors(WebDriver driver){
        click(driver, "//*[@id=\"viewMoreLink_AUTHOR_NAME_AND_ID\"]");
        click(driver, "//*[@id=\"viewAllLink_AUTHOR_NAME_AND_ID\"]");
    }

    private static void wait(int timeout, WebDriverWait waiter){
        waiter.withTimeout(Duration.ofSeconds(timeout));
    }

    private static WebDriver getChromeDriver() {
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("download.default_directory", "D:\\Programming\\Java\\testselenium\\export");

        DesiredCapabilities caps = DesiredCapabilities.chrome();

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--disable-extensions");

        caps.setCapability(ChromeOptions.CAPABILITY, options);

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);

        return driver;

    }

    private static void scopusAuth(WebDriver driver, WebDriverWait waiter, String name, String password) {
        driver.get("http://proxylibrary.hse.ru:2048/login?url=http://www.scopus.com");

        WebElement nameField = driver.findElement(By.xpath("//tr[1]/td[2]/input"));
        WebElement passwordField = driver.findElement(By.xpath("//tr[2]/td[2]/input"));
        nameField.sendKeys(name);
        passwordField.sendKeys(password);
        driver.findElement(By.xpath("//input[3]")).click();

        wait(20,waiter);

        try {
            click(driver, "//*[contains(@id,'pendo-close-guide')]");
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println("В этот раз окна не было");
        }

    }

    private static void downloadPage(WebDriver driver, WebDriverWait waiter) {
        wait(5,waiter);

        click(driver, "//*[@id=\"mainResults-allPageSelectedValue\"]");

        click(driver, "//*[@id=\"selectAllMenuItem\"]/span[2]/span/ul/li[2]/label");
        wait(10,waiter);

        download(driver, waiter);
        wait(10,waiter);
    }

    private static void download(WebDriver driver, WebDriverWait waiter){
        click(driver, "//*[@id=\"export_results\"]");

        waiter.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"exportTrigger\"]")));
        click(driver, "//*[@id=\"exportTrigger\"]");
    }

    private static void click(WebDriver driver, String xpath) throws NoSuchElementException{
        driver.findElement(By.xpath(xpath)).click();
        new WebDriverWait(driver, 10).withTimeout(Duration.ofSeconds(2));
    }

    private static void vkAuth(String name, String password) {
        WebDriver driver = new ChromeDriver();

        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

        driver.get("http://vk.com/");

        driver.findElement(By.xpath("//*[@id=\"index_email\"]")).sendKeys(name);
        driver.findElement(By.xpath("//*[@id=\"index_pass\"]")).sendKeys(password);
        driver.findElement(By.xpath("//*[@id=\"index_login_button\"]")).click();

        Scanner scanner = new Scanner(System.in);

        String code = scanner.nextLine();

        driver.findElement(By.xpath("//*[@id=\"authcheck_code\"]")).sendKeys(code);
        driver.findElement(By.xpath("//*[@id=\"login_authcheck_submit_btn\"]")).click();

        boolean $continue;

        do {
            sendMessage(driver, scanner.nextLine(), scanner.nextLine());
            System.out.println("Написать кому-нибудь еще?");
            $continue = scanner.nextLine().equals("Да");
        } while ($continue);
    }

    private static void sendMessage(WebDriver driver, String getter, String message) {
        driver.findElement(By.xpath("//*[@id=\"l_msg\"]/a/span/span[3]")).click();

        driver.findElement(By.xpath("//*[@id=\"im_dialogs_search\"]")).sendKeys(getter);

        (new WebDriverWait(driver, 10)).until(ExpectedConditions.
                textToBePresentInElement(driver.findElement
                                (By.xpath("//*[@id=\"im_dialogs\"]/li[3]/div[2]/div/div[1]/span[1]/span")),
                        getter));

        driver.findElement(By.xpath("//*[@id=\"im_dialogs\"]/li[3]")).click();

        driver.findElement(By.xpath("//*[@id=\"im_editable0\"]")).sendKeys(message);

        new WebDriverWait(driver, 10);

        driver.findElement(By.xpath("//*[@id=\"content\"]/div/div[1]/div[3]/div[2]/div[4]/div[2]/div[4]/div[1]/button")).click();
    }
}

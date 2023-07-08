package org.biouno.unochoice;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.LocalData;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UiAcceptanceTest {

    private static final Duration MAX_WAIT = Duration.parse(System.getProperty("ui.loading.timeout", "PT60S"));

    @Rule
    public JenkinsRule j = new JenkinsRule();

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public static void setUpClass() {
        if (isCi()) {
            // The browserVersion needs to match what is provided by the Jenkins Infrastructure
            // If you see an exception like this:
            //
            // org.openqa.selenium.SessionNotCreatedException: Could not start a new session. Response code 500. Message: session not created: This version of ChromeDriver only supports Chrome version 114
            // Current browser version is 112.0.5615.49 with binary path /usr/bin/chromium-browser
            //
            // Then that means you need to update the version here to match the current browser version.
            WebDriverManager.chromedriver().browserVersion("112").setup();
        } else {
            WebDriverManager.chromedriver().setup();
        }
    }

    private static boolean isCi() {
        return StringUtils.isNotBlank(System.getenv("CI"));
    }

    @Before
    public void setUp() throws Exception {
        if (isCi()) {
            driver = new ChromeDriver(new ChromeOptions().addArguments("--headless", "--disable-dev-shm-usage", "--no-sandbox"));
        } else {
            driver = new ChromeDriver(new ChromeOptions());
        }
        wait = new WebDriverWait(driver, MAX_WAIT);
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @LocalData
    @Test
    public void test() throws Exception {
        // Load the page
        driver.get(j.getURL().toString() + "job/test/build");

        /*
         PARAM1 and PARAM1A are SINGLE_SELECT based
         */
        WebElement param1 = findSelect("PARAM1");

        assertTrue(param1.isDisplayed());
        assertTrue(param1.isEnabled());

        checkOptions(() -> findSelect("PARAM1"), "A", "B", "C");

        assertEquals("A", new Select(param1).getFirstSelectedOption().getText());
        checkOptions(() -> findSelect("PARAM1A"), "A1", "A2", "A3");

        new Select(param1).selectByValue("B");
        checkOptions(() -> findSelect("PARAM1A"), "B1", "B2", "B3");

        new Select(param1).selectByValue("C");
        checkOptions(() -> findSelect("PARAM1A"), "C1", "C2", "C3");

        new Select(param1).selectByValue("A");
        checkOptions(() -> findSelect("PARAM1A"), "A1", "A2", "A3");

        /*
         PARAM2 and PARAM2A are RADIO based
         */
        List<WebElement> param2Choices = findRadios("PARAM2");

        checkRadios(() -> param2Choices, "A", "B", "C");
        // this is until something is selected
        checkRadios(() -> findRadios("PARAM2A"), "1", "2", "3");

        param2Choices.get(0).click();
        checkRadios(() -> findRadios("PARAM2A"), "A1", "A2", "A3");

        param2Choices.get(1).click();
        checkRadios(() -> findRadios("PARAM2A"), "B1", "B2", "B3");

        param2Choices.get(2).click();
        checkRadios(() -> findRadios("PARAM2A"), "C1", "C2", "C3");

        param2Choices.get(0).click();
        checkRadios(() -> findRadios("PARAM2A"), "A1", "A2", "A3");

        /*
         PARAM3 and PARAM3A are CHECKBOX based
         */
        List<WebElement> param3Choices = findCheckboxes("PARAM3");

        checkRadios(() -> param3Choices, "A", "B", "C");
        // this is until something is selected
        checkRadios(() -> findCheckboxes("PARAM3A"));

        param3Choices.get(0).click();
        checkRadios(() -> findCheckboxes("PARAM3A"), "A1", "A2", "A3");

        param3Choices.get(1).click();
        checkRadios(() -> findCheckboxes("PARAM3A"), "A1", "B1", "A2", "B2", "A3", "B3");

        param3Choices.get(2).click();
        checkRadios(() -> findCheckboxes("PARAM3A"), "A1", "B1", "C1", "A2", "B2", "C2", "A3", "B3", "C3");

        param3Choices.get(0).click();
        checkRadios(() -> findCheckboxes("PARAM3A"), "B1", "C1", "B2", "C2", "B3", "C3");

        param3Choices.get(1).click();
        checkRadios(() -> findCheckboxes("PARAM3A"), "C1", "C2", "C3");

        param3Choices.get(0).click();
        checkRadios(() -> findCheckboxes("PARAM3A"), "A1", "C1", "A2", "C2", "A3", "C3");

        param3Choices.get(2).click();
        checkRadios(() -> findCheckboxes("PARAM3A"), "A1", "A2", "A3");

        param3Choices.get(0).click();
        checkRadios(() -> findCheckboxes("PARAM3A"));

        /*
         PARAM4 and PARAM4A are MULTI_SELECT based
         */
        WebElement param4Input = findSelect("PARAM4");

        checkOptions(() -> param4Input, "A", "B", "C");
        // this is until something is selected
        checkOptions(() -> findSelect("PARAM4A"));

        new Select(param4Input).selectByVisibleText("A");
        checkOptions(() -> findSelect("PARAM4A"), "A1", "A2", "A3");

        new Select(param4Input).selectByVisibleText("B");
        checkOptions(() -> findSelect("PARAM4A"), "A1", "B1", "A2", "B2", "A3", "B3");

        new Select(param4Input).selectByVisibleText("C");
        checkOptions(() -> findSelect("PARAM4A"), "A1", "B1", "C1", "A2", "B2", "C2", "A3", "B3", "C3");

        new Select(param4Input).deselectByVisibleText("A");
        checkOptions(() -> findSelect("PARAM4A"), "B1", "C1", "B2", "C2", "B3", "C3");

        new Select(param4Input).deselectByVisibleText("B");
        checkOptions(() -> findSelect("PARAM4A"), "C1", "C2", "C3");

        new Select(param4Input).selectByValue("A");
        checkOptions(() -> findSelect("PARAM4A"), "A1", "C1", "A2", "C2", "A3", "C3");

        new Select(param4Input).deselectByValue("C");
        checkOptions(() -> findSelect("PARAM4A"), "A1", "A2", "A3");

        new Select(param4Input).deselectByValue("A");
        checkOptions(() -> findSelect("PARAM4A"));

    }

    private List<WebElement> findRadios(String paramName) {
        return driver.findElements(By.cssSelector("div.active-choice:has([name='name'][value='" + paramName + "']) input[type='radio']"));
    }

    private List<WebElement> findCheckboxes(String paramName) {
        return driver.findElements(By.cssSelector("div.active-choice:has([name='name'][value='" + paramName + "']) input[type='checkbox']"));
    }

    private WebElement findSelect(String paramName) {
        return driver.findElement(By.cssSelector("div.active-choice:has([name='name'][value='" + paramName + "']) > select"));
    }

    private void checkOptions(Supplier<WebElement> param1Input, String... options) {
        wait.withMessage(() -> {
                    List<WebElement> optionElements = param1Input.get().findElements(By.cssSelector("option"));
                    List<String> optionValues = optionElements.stream().map(WebElement::getText).collect(Collectors.toList());
                    return MessageFormat.format("{0} should have had {1}. Had {2}", param1Input, Arrays.asList(options), optionValues);
                })
                .until(d -> {
                    try {
                        List<WebElement> optionElements = param1Input.get().findElements(By.cssSelector("option"));
                        List<String> optionValues = optionElements.stream().map(WebElement::getText).collect(Collectors.toList());
                        return optionValues.equals(Arrays.asList(options));
                    } catch (StaleElementReferenceException e) {
                        return false;
                    }
                });
    }

    private void checkRadios(Supplier<List<WebElement>> radios, String... options) {
        wait.withMessage(() -> {
                    List<String> optionValues = radios.get().stream().map(it -> it.getAttribute("value")).collect(Collectors.toList());
                    return MessageFormat.format("{0} should have had {1}. Had {2}", radios, Arrays.asList(options), optionValues);
                })
                .until(d -> {
                    try {
                        List<String> optionValues = radios.get().stream().map(it -> it.getAttribute("value")).collect(Collectors.toList());
                        return optionValues.equals(Arrays.asList(options));
                    } catch (StaleElementReferenceException e) {
                        return false;
                    }
                });
    }
}

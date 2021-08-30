package appium;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.mitmproxy.InterceptedMessage;
import io.appium.mitmproxy.MitmproxyJava;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IosMitmProxyTest
{
    private AppiumDriver driver;
    private MitmproxyJava proxy;

    List<InterceptedMessage> messages = new ArrayList<>();

    @BeforeEach
    public void before() throws IOException, TimeoutException
    {
        proxy = new MitmproxyJava("/usr/local/bin/mitmdump", (InterceptedMessage m) -> {
            System.out.println("intercepted request for " + m.getRequest().toString());
            messages.add(m);
            return m;
        });

        proxy.start();

        File appDir = new File("/opt/sahibinden");
        File app = new File(appDir, "sahibinden.app");

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUITest");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "IOS");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "14.4");
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Iphone 7");
        capabilities.setCapability("language", "tr");
        capabilities.setCapability("newCommandTimeout", "15000");
        capabilities.setCapability("waitForQuiescence", "false");
        capabilities.setCapability("usePrebuiltWDA", "true");
        capabilities.setCapability("udid", "06a121961f9cd9818857b1f5fa93c8393cd20274");
        capabilities.setCapability(MobileCapabilityType.APP, app.getAbsolutePath());
        capabilities.setCapability("bundleid", "com.sahibinden.sahibinden.beta");

        driver = new IOSDriver(new URL("http://0.0.0.0:4723/wd/hub"), capabilities);
    }

    @Test
    public void sampleMitmproxyTest() throws InterruptedException
    {
        Thread.sleep(5000);
        MobileElement locationPopup = (MobileElement) driver.findElement(By.id("Uygulamayı Kullanırken İzin Ver"));
        locationPopup.click();
        Thread.sleep(5000);
        MobileElement showcase = (MobileElement) driver.findElement(By.id("Vitrin"));
        showcase.click();

        assertTrue(messages.size() > 0);
    }

    @AfterEach
    public void after() throws InterruptedException
    {
        proxy.stop();
    }
}

package de.test.antennapod.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.test.antennapod.ui.UITestUtils;

public class ExplicitNavigationTest {
    private static final String PACKAGE_NAME = "de.danoeh.antennapod.debug";
    private static final int LAUNCH_TIMEOUT = 5000;
    private UiDevice device;
    private UITestUtils uiTestUtils;

    @Before
    public void setUp() throws Exception {
        uiTestUtils = new UITestUtils(ApplicationProvider.getApplicationContext());
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
        device.pressHome();

        // Wait for launcher
        final String launcherPackage = getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // Launch the app
        Context context = ApplicationProvider.getApplicationContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(PACKAGE_NAME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // Wait for the app to appear
        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public void testStartActivityExplicitly() throws UiObjectNotFoundException {
        // Click on hamburger menu to open navigation drawer
        UiObject menuButton = device.findObject(new UiSelector()
                .description("Navigate up")
                .className("android.widget.ImageButton"));
        assertTrue("Menu button should be visible", menuButton.waitForExists(LAUNCH_TIMEOUT));
        menuButton.click();

        // Click on Settings in navigation drawer
        UiObject2 settingsItem = device.wait(Until.findObject(
                By.text("Settings")), LAUNCH_TIMEOUT);
        assertTrue("Settings menu item should be visible", settingsItem != null);
        settingsItem.click();

        // Verify we're in Settings and see a challenge
        UiObject2 settingsHeader = device.wait(Until.findObject(
                By.text("Settings")), LAUNCH_TIMEOUT);
        assertTrue("Should be on Settings screen", settingsHeader != null);

        // Look for any engineering challenge text (could be about synchronization, performance, etc.)
        UiObject2 challengeText = device.wait(Until.findObject(
                By.textContains("Auto")), LAUNCH_TIMEOUT);  // Looking for text containing "Auto" as an example
        assertTrue("Should see engineering challenge text", challengeText != null);
    }

    private String getLauncherPackageName() {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        PackageManager pm = ApplicationProvider.getApplicationContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }
}
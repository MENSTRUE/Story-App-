package com.dicoding.storyapp

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.dicoding.storyapp.utils.EspressoIdlingResource
import com.dicoding.storyapp.view.welcome.WelcomeActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddStoryTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(WelcomeActivity::class.java)

    // Perlu izin untuk memastikan akses ke penyimpanan jika diperlukan oleh FileProvider
    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        Intents.init()
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        Intents.release()
    }

    @Test
    fun testAddStoryScenario() {
        onView(withId(R.id.loginButton)).perform(click())
        onView(withId(R.id.emailEditText)).perform(typeText("saipul@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText("saipul123"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        onView(withId(R.id.rv_stories)).check(matches(isDisplayed()))

        onView(withId(R.id.fab_add_story)).perform(click())

        onView(withId(R.id.tv_new_story_title)).check(matches(isDisplayed()))

        val dummyImageFile = createDummyImageFile(InstrumentationRegistry.getInstrumentation().targetContext)
        val dummyUri = Uri.fromFile(dummyImageFile)

        val resultData = Intent().apply {
            data = dummyUri
        }
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)

        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(result)

        onView(withId(R.id.btn_gallery)).perform(click())

        onView(withId(R.id.ed_add_description)).perform(typeText("Cerita test dari UI Test."), closeSoftKeyboard())

        onView(withId(R.id.btn_upload)).perform(click())

        onView(withId(R.id.rv_stories)).check(matches(isDisplayed()))

    }

    @Throws(IOException::class)
    private fun createDummyImageFile(context: Context): File {
        val filename = "test_image.jpg"
        val file = File(context.cacheDir, filename)

        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.image_dicoding)
        val outputStream = FileOutputStream(file)

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        return file
    }
}
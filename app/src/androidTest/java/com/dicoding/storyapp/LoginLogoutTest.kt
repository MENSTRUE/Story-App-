package com.dicoding.storyapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dicoding.storyapp.utils.EspressoIdlingResource
import com.dicoding.storyapp.view.welcome.WelcomeActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginLogoutTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(WelcomeActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testLoginAndLogoutScenario() {
        onView(withId(R.id.loginButton)).perform(click())

        onView(withId(R.id.emailEditText)).perform(typeText("saipul@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText("saipul123"), closeSoftKeyboard())

        onView(withId(R.id.loginButton)).perform(click())

        onView(withId(R.id.rv_stories)).check(matches(isDisplayed()))

        onView(withContentDescription(R.string.app_name)).perform(click())

        onView(withId(R.id.nav_logout)).perform(click())

        onView(withId(R.id.titleTextView)).check(matches(isDisplayed()))
    }
}
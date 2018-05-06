package com.michaeljordanr.baking;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.michaeljordanr.baking.activity.RecipeDetailActivity;
import com.michaeljordanr.baking.model.Recipe;
import com.michaeljordanr.baking.util.Constants;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RecipeDetailTest {

    private Recipe mRecipe = Recipe.mockObject();
    @Rule
    public ActivityTestRule<RecipeDetailActivity> mActivityRule =
            new ActivityTestRule<RecipeDetailActivity>(RecipeDetailActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, RecipeDetailActivity.class);
                    result.putExtra(Constants.RECIPE_ARG, mRecipe);
                    return result;
                }
            };
    private int mSelectPosition = 0;

    @Test
    public void recipeDetailActivityTest() {
        // Check if ingredient and step list are displayed
        onView(withId(R.id.rv_ingredients)).check(matches(isDisplayed()));
        onView(withId(R.id.rv_steps)).check(matches(isDisplayed()));
        // Select the first step
        onView(withId(R.id.rv_steps)).perform(RecyclerViewActions.actionOnItemAtPosition(mSelectPosition, click()));
        // Check if detail view is displayed
        onView(withId(R.id.cv_step_detail)).check(matches(isDisplayed()));
        // Check if description is displayed
        onView(withId(R.id.tv_description)).check(matches(isDisplayed()));
        // Check if description is correct
        onView(withId(R.id.tv_description)).check(matches(withText(mRecipe.getSteps().get(mSelectPosition).getDescription())));
    }
}

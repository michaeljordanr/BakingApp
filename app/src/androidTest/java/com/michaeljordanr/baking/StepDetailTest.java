package com.michaeljordanr.baking;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.michaeljordanr.baking.activity.StepDetailActivity;
import com.michaeljordanr.baking.model.Recipe;
import com.michaeljordanr.baking.model.Step;
import com.michaeljordanr.baking.util.Constants;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class StepDetailTest {

    private ArrayList<Step> mStepList = new ArrayList<>(Recipe.mockObject().getSteps());
    private int mSelectedPosition = 0;

    @Rule
    public ActivityTestRule<StepDetailActivity> mActivityRule =
            new ActivityTestRule<StepDetailActivity>(StepDetailActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, StepDetailActivity.class);
                    result.putParcelableArrayListExtra(Constants.STEP_LIST_ARG, mStepList);
                    result.putExtra(Constants.STEP_LIST_POSITION_ARG, mSelectedPosition);
                    return result;
                }
            };

    @Test
    public void stepDetailActivityTest() {
        // Check if description is displayed
        onView(withId(R.id.tv_description)).check(matches(isDisplayed()));

        // Check if description is correct
        onView(withId(R.id.tv_description)).check(matches(withText(mStepList.get(mSelectedPosition).getDescription())));

        // Check if btn next step is displayed
        ViewInteraction tvNextStep = onView(allOf(withId(R.id.tv_next_step), isDisplayed()));
        if (tvNextStep != null) {
            tvNextStep.perform(click());

            // Check if next description is correct
            onView(withId(R.id.tv_description)).check(matches(withText(mStepList.get(mSelectedPosition + 1).getDescription())));

            // Check if btn previously step is displayed
            ViewInteraction tvPreviouslyStep = onView(allOf(withId(R.id.tv_previous_step), isDisplayed()));
            if (tvPreviouslyStep != null) {
                tvPreviouslyStep.perform(click());

                // Check if previously description is correct
                onView(withId(R.id.tv_description)).check(matches(withText(mStepList.get(mSelectedPosition).getDescription())));
            }
        }
    }
}
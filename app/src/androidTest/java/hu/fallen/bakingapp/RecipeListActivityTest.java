package hu.fallen.bakingapp;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static org.junit.Assert.*;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class RecipeListActivityTest {

    @Rule
    public final IntentsTestRule<RecipeListActivity> mIntentsRule =
            new IntentsTestRule(RecipeListActivity.class);

    @Before
    public void setUp() {
        IdlingRegistry.getInstance().register(mIntentsRule.getActivity().countingIdlingResource);
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(mIntentsRule.getActivity().countingIdlingResource);
    }

    @Test
    public void listHas4Elements() {
        onView(withId(R.id.recipe_list)).check(new ViewAssertion() {
            private final int expected = 4;

            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (noViewFoundException != null) throw noViewFoundException;
                RecyclerView recyclerView = (RecyclerView) view;
                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                assertEquals(expected, adapter.getItemCount());
            }
        });
    }

    @Test
    public void testCakeNames() {
        onView(withId(R.id.recipe_list)).check(new AssertCakeName(0, "Nutella Pie"));
        onView(withId(R.id.recipe_list)).check(new AssertCakeName(1, "Brownies"));
        onView(withId(R.id.recipe_list)).check(new AssertCakeName(2, "Yellow Cake"));
        onView(withId(R.id.recipe_list)).check(new AssertCakeName(3, "Cheesecake"));
    }

    @Test
    public void testNavigation() {
        onView(withId(R.id.recipe_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        intended(hasComponent(RecipeDetailsActivity.class.getName()));
        intended(hasExtraWithKey(RecipeDetailsActivity.ARG_ITEM));
    }

    private class AssertCakeName implements ViewAssertion {
        private final int index;
        private final String cakeName;
        AssertCakeName(int index, String cakeName) {
            this.index = index;
            this.cakeName = cakeName;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) throw noViewFoundException;
            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assertTrue(String.format("Assertion failed: %d > %d", adapter.getItemCount(), index), adapter.getItemCount() > index);
            ViewGroup card = (ViewGroup) recyclerView.findViewHolderForAdapterPosition(index).itemView;
            TextView nameView = card.findViewById(R.id.content);
            assertEquals(cakeName, nameView.getText());
        }
    }
}

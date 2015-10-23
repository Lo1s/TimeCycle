package com.hydra.android.timecycle;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class TimerEditActivity extends AppCompatActivity implements
        TimerSummaryFragment.OnSummaryFragmentInteractionListener,
        TimerExerciseFragment.OnExerciseFragmentInteractionListener,
        TimerRestFragment.OnRestFragmentInteractionListener,
        TimerRepetitionsFragment.OnRepetitionsFragmentInteractionListener,
        TimerCountDownFragment.OnCountDownFragmentInteractionListener {

    private long exerciseTime = 0;
    private long restTime = 0;
    private int repetitions = 0;
    private long countDownTime = 0;
    private float intensity = 0;

    private Bundle summaryBundle;

    private TimerSummaryFragment summaryFragment;
    private TimerExerciseFragment exerciseFragment;
    private TimerRestFragment restFragment;
    private TimerRepetitionsFragment repetitionsFragment;
    private TimerCountDownFragment countDownFragment;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_edit);

        summaryBundle = new Bundle();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewPager_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    summaryFragment = TimerSummaryFragment.newInstance(exerciseTime, restTime,
                            repetitions, countDownTime, intensity);
                    return summaryFragment;
                case 1:
                    exerciseFragment = TimerExerciseFragment.newInstance(exerciseTime);
                    return exerciseFragment;
                case 2:
                    restFragment = TimerRestFragment.newInstance(restTime);
                    return restFragment;
                case 3:
                    repetitionsFragment = TimerRepetitionsFragment.newInstance(repetitions);
                    return repetitionsFragment;
                case 4:
                    countDownFragment = TimerCountDownFragment.newInstance(countDownTime);
                    return countDownFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SUMMARY";
                case 1:
                    return "EXERCISE";
                case 2:
                    return "REST";
                case 3:
                    return "REPETITIONS";
                case 4:
                    return "COUNTDOWN";
            }
            return null;
        }
    }

    @Override
    public void onSummaryFragmentInteraction(float intensity) {
        this.intensity = intensity;
        summaryBundle.putFloat(MyConstants.ARG_INTENSITY, intensity);
        summaryFragment.setSummary(summaryBundle);
    }

    @Override
    public void onExerciseFragmentInteraction(long exerciseTime) {
        this.exerciseTime = exerciseTime;
        summaryBundle.putLong(MyConstants.ARG_EXERCISE_TIME, exerciseTime);
        summaryFragment.setSummary(summaryBundle);
    }

    @Override
    public void onRestFragmentInteraction(long restTime) {
        this.restTime = restTime;
        summaryBundle.putLong(MyConstants.ARG_REST_TIME, restTime);
        summaryFragment.setSummary(summaryBundle);
    }

    @Override
    public void onRepetitionsFragmentInteraction(int repetitions) {
        this.repetitions = repetitions;
        summaryBundle.putInt(MyConstants.ARG_REPETITIONS, repetitions);
        summaryFragment.setSummary(summaryBundle);
    }

    @Override
    public void onCountDownFragmentInteraction(long countDownTime) {
        this.countDownTime = countDownTime;
        summaryBundle.putLong(MyConstants.ARG_COUNTDOWN, countDownTime);
        summaryFragment.setSummary(summaryBundle);
    }
}

package proyectohabitos.example.neita.habitos;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Statistics extends ActionBarActivity {

    DemoCollectionPagerAdapterSta mDemoCollectionPagerAdapter;
    ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final ActionBar actionBar = getSupportActionBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);


        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapterSta(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.frmTasksPagerStatistics);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    // getFragmentManager().findFragmentByTag()
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {


            @Override
            public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

            }
        };

        // Add 3 tabs, specifying the tab's text and TabListener

        actionBar.addTab(actionBar.newTab().setText("Sem").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Mes").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Global").setTabListener(tabListener));
    }

    public class DemoCollectionPagerAdapterSta extends FragmentStatePagerAdapter {
        public DemoCollectionPagerAdapterSta(FragmentManager fm) {
            super(fm);
        }



        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null; //do i need another method? yes, calling DemoObjectFragment twicehe same fragment twice, and we want different gragments ... whaaaaaaaat
            if (i == 0) {
                fragment = new WeeklyStatistics();

            } else if (i == 1) {
                fragment = new MonthlyStatistics();
            }

         else if (i == 2) {
            fragment = new GlobalStatistics();
        }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        } //whats this OBJECT? i wanna change the tittles
        //change it if you want to, right now the titles are not visible, we'l need to add tabs to see them 6hhhhhh
    }
}

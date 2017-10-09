package proyectohabitos.example.neita.habitos.Task;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import proyectohabitos.example.neita.habitos.R;
import proyectohabitos.example.neita.habitos.Task.FragmentsTasks.FrgAllTasks;
import proyectohabitos.example.neita.habitos.Task.FragmentsTasks.FrgTodayTasks;

public class FrmTasks extends AppCompatActivity {
//esta actividad contiene dos fragmentos: FrgAllTasks,FrgTodayTasks que corresponden a dos pestañas


/* Instances of this class are fragments representing a single
 object in our collection.*/

    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final ActionBar actionBar = getSupportActionBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_tasks);

        // ViewPager and its adapters use support library fragments, so use getSupportFragmentManager.
        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.frmTasksPager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            //avisa cuando el usuario accede a una pestaña dandote la posición de ésta
            public void onPageSelected(int position) {
                Fragment frag = mDemoCollectionPagerAdapter.getFragment(position);
                if(frag!=null) {
                    if (frag instanceof FrgTodayTasks) {
                        ((FrgTodayTasks) frag).update();
                    } else if (frag instanceof FrgAllTasks) {
                        ((FrgAllTasks) frag).update();
                    } else {
                    }
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

        // Add 2 tabs, specifying the tab's text and TabListener
        actionBar.addTab(actionBar.newTab().setText("Hoy").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Todas").setTabListener(tabListener));

    }

    //Maneja operaciones con fragments
    public class DemoCollectionPagerAdapter extends FragmentPagerAdapter {
        private FragmentManager fragmentM;
        private Map<Integer, String> fragTags = new HashMap<>();

        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
            fragmentM = fm;
        }
        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            if (i == 0) {
                fragment = new FrgTodayTasks();
            } else if (i == 1) {
                fragment = new FrgAllTasks();
            }
            return fragment;
        }

        @Override
        //method is called by android everytime a page is added
        public Object instantiateItem(ViewGroup container, int position) {
            Object instantiateItem = super.instantiateItem(container, position);
            if (instantiateItem instanceof Fragment) {//si el objeto es un fragmento
                Fragment fragment = (Fragment) instantiateItem;
                fragTags.put(position, fragment.getTag());
            }
            return instantiateItem;
        }

        public Fragment getFragment(int posit) {
            if (fragTags != null && fragTags.size() > 0 && fragTags.get(posit) != null) {
                return fragmentM.findFragmentByTag(fragTags.get(posit));
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }
}

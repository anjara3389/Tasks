package proyectohabitos.example.neita.habitos.Task;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.google.android.gms.gcm.GcmNetworkManager;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.Chronometer.Services.ChronometerNotification.ServiceChrNotification;
import proyectohabitos.example.neita.habitos.DateUtils;
import proyectohabitos.example.neita.habitos.R;
import proyectohabitos.example.neita.habitos.Reminder.AlarmNotification.Services.ServiceAlarmNotification;
import proyectohabitos.example.neita.habitos.Result.Result;
import proyectohabitos.example.neita.habitos.Result.Services.ServiceCreateDailyResult;
import proyectohabitos.example.neita.habitos.Task.fragments.FrgAllTasks;
import proyectohabitos.example.neita.habitos.Task.fragments.FrgTodayTasks;

public class FrmTasks extends AppCompatActivity {
//Ésta actividad contiene dos fragmentos: FrgAllTasks,FrgTodayTasks que corresponden a dos pestañas(Hoy,Todas)

    TasksPagerAdapter mTaskPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final ActionBar actionBar = getSupportActionBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_tasks);

        GregorianCalendar gc = DateUtils.getGregCalendar(new Date());
        gc.set(Calendar.HOUR_OF_DAY, 23);
        gc.set(Calendar.MINUTE, 58);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);
        ServiceCreateDailyResult.scheduleInsertResultToday(FrmTasks.this, (gc.getTimeInMillis() - System.currentTimeMillis()) / 1000);
        // ViewPager and its adapters use support library fragments, so use getSupportFragmentManager.
        mTaskPagerAdapter = new TasksPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.frmTasksPager);
        mViewPager.setAdapter(mTaskPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            //avisa cuando el usuario accede a una pestaña dandote la posición de ésta
            public void onPageSelected(int position) {
                Fragment frag = mTaskPagerAdapter.getFragment(position);
                if (frag != null) {
                    if (frag instanceof FrgTodayTasks) {
                        ((FrgTodayTasks) frag).update();
                    } else if (frag instanceof FrgAllTasks) {
                        ((FrgAllTasks) frag).update();
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

    //Menu de la action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar, menu);
        menu.findItem(R.id.okTask).setVisible(false);
        setTitle("Abby");
        return true;
    }

    //Menu de la action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_delete_spans: //BORRAR DESPUÉS *************** vacia los spams***+*SOLO PRUEBA PATITO

                /*String sql = "UPDATE activity SET since_date=since_date-2629800000";
                db.execSQL(sql);
                Toast.makeText(this, "1 MES MENOS CUAK!", Toast.LENGTH_SHORT).show();*/
                SQLiteDatabase db = BaseHelper.getReadable(this);
                // String sql = "DELETE FROM activity";
                // String sql2 = "DELETE FROM span";
                try {
                    Result.selectResults(db);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //  String sql3 = "DELETE FROM result";

                GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(this);
                mGcmNetworkManager.cancelAllTasks(ServiceAlarmNotification.class);
                mGcmNetworkManager.cancelAllTasks(ServiceChrNotification.class);
                /* try {
                      Result.insertResultToday(db);
                    Toast.makeText(this, "RESULTADOS INSERTADOS CUAKKK!!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "ERROR CUAK" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                */

                //db.execSQL(sql);
                //db.execSQL(sql2);
                // db.execSQL(sql3);
                BaseHelper.tryClose(db);

                /*Toast.makeText(this, "SE BORRÓ TODITO! SPANS!CUAK!", Toast.LENGTH_SHORT).show();


                MediaPlayer player = MediaPlayer.create(this, R.raw.quack);
                player.setLooping(false); // Set looping
                player.setVolume(100, 100);
                player.start();*/
                Fragment frag = mTaskPagerAdapter.getFragment(mViewPager.getCurrentItem());

                if (frag instanceof FrgTodayTasks) {
                    ((FrgTodayTasks) frag).update();
                } else if (frag instanceof FrgAllTasks) {
                    ((FrgAllTasks) frag).update();
                }
                break;
            default:
                break;
        }
        return true;
    }

    //Maneja operaciones con fragments
    public class TasksPagerAdapter extends FragmentPagerAdapter {
        private FragmentManager fragmentM;
        private Map<Integer, String> fragTags = new HashMap<>();

        public TasksPagerAdapter(FragmentManager fm) {
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

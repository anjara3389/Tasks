package proyectohabitos.example.neita.habitos.Statistics;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.DateUtils;
import proyectohabitos.example.neita.habitos.R;
import proyectohabitos.example.neita.habitos.Task.Task;

public class FrmStatistics extends AppCompatActivity {

    private int taskId;
    private CircularProgressBar weekBar, wholeWeekBar;
    private TextView txtPorWeek, txtWholeWeek;

    ViewPager viewPager;
    MonthsPagerAdapter pagerAdapter;
    private ArrayList<Long> months;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_statistics);

        Bundle bundle = getIntent().getExtras();
        taskId = bundle.getInt("id");

        weekBar = (CircularProgressBar) findViewById(R.id.week_pbar);
        //monthBar = (CircularProgressBar) findViewById(R.id.month_pbar);
        wholeWeekBar = (CircularProgressBar) findViewById(R.id.whole_week_pbar);
        //wholeMonthBar = (CircularProgressBar) findViewById(R.id.whole_month_pbar);

        txtPorWeek = (TextView) findViewById(R.id.frm_sta_txt_per_sem);
        //txtPorMonth = (TextView) findViewById(R.id.frm_sta_txt_per_mont);
        txtWholeWeek = (TextView) findViewById(R.id.frm_sta_txt_whole_sem);
        // txtWholeMonth = (TextView) findViewById(R.id.frm_sta_txt_whole_month);

        SQLiteDatabase db = BaseHelper.getReadable(FrmStatistics.this);

        weekBar.setProgress((int) Statistics.getWeeklyStatistics(taskId, false, db));
        txtPorWeek.setText((int) Statistics.getWeeklyStatistics(taskId, false, db) + "%");

        wholeWeekBar.setProgress((int) Statistics.getWeeklyStatistics(taskId, true, db));
        txtWholeWeek.setText((int) Statistics.getWeeklyStatistics(taskId, true, db) + "%");

      /*  monthBar.setProgress((int) Statistics.getStatistics(taskId, 1, false, db));
        txtPorMonth.setText((int) Statistics.getStatistics(taskId, 1, false, db) + "%");

        wholeMonthBar.setProgress((int) Statistics.getStatistics(taskId, 1, true, db));
        txtWholeMonth.setText((int) Statistics.getStatistics(taskId, 1, true, db) + "%");
*/

        // ViewPager and its adapters use support library fragments, so use getSupportFragmentManager.
        Task task = new Task().select(db, taskId);
        BaseHelper.tryClose(db);
        Date d = new Date();
        d.setTime(task.sinceDate);
        months = DateUtils.getMonthsIntoDates(d, new Date());
        pagerAdapter = new MonthsPagerAdapter(getSupportFragmentManager(), months);
        viewPager = (ViewPager) findViewById(R.id.frm_stat_pager);
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override

            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    //Maneja operaciones con fragments
    public class MonthsPagerAdapter extends FragmentPagerAdapter {
        private FragmentManager fragmentM;
        private Map<Integer, String> fragTags = new HashMap<>();
        private ArrayList<Long> months;

        public MonthsPagerAdapter(FragmentManager fm, ArrayList<Long> months) {
            super(fm);
            fragmentM = fm;
            this.months = months;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new FrgMonthStatistics();
            System.out.println("i" + i);
            System.out.println("size" + months.size());
            Bundle b = new Bundle();
            b.putLong("month", months.get(i));
            b.putInt("id", taskId);
            fragment.setArguments(b);//cambio los parámetros del fragment
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

        @Override
        public int getCount() {
            return months.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }
}

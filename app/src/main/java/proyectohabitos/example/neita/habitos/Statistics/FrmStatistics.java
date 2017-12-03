package proyectohabitos.example.neita.habitos.Statistics;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.R;
import proyectohabitos.example.neita.habitos.Task.Task;

public class FrmStatistics extends AppCompatActivity {

    private int taskId;
    private CircularProgressBar weekBar, monthBar, wholeWeekBar, wholeMonthBar;
    private TextView txtPorWeek, txtPorMonth, txtWholeWeek, txtWholeMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_statistics);

        Bundle bundle = getIntent().getExtras();
        taskId = bundle.getInt("id");

        weekBar = (CircularProgressBar) findViewById(R.id.week_pbar);
        monthBar = (CircularProgressBar) findViewById(R.id.month_pbar);
        wholeWeekBar = (CircularProgressBar) findViewById(R.id.whole_week_pbar);
        wholeMonthBar = (CircularProgressBar) findViewById(R.id.whole_month_pbar);

        txtPorWeek = (TextView) findViewById(R.id.frm_sta_txt_per_sem);
        txtPorMonth = (TextView) findViewById(R.id.frm_sta_txt_per_mont);
        txtWholeWeek = (TextView) findViewById(R.id.frm_sta_txt_whole_sem);
        txtWholeMonth = (TextView) findViewById(R.id.frm_sta_txt_whole_month);


        SQLiteDatabase db = BaseHelper.getReadable(FrmStatistics.this);

        weekBar.setProgress((int) Task.getStatistics(taskId, 0, false, db));
        txtPorWeek.setText((int) Task.getStatistics(taskId, 0, false, db) + "%");

        wholeWeekBar.setProgress((int) Task.getStatistics(taskId, 0, true, db));
        txtWholeWeek.setText((int) Task.getStatistics(taskId, 0, true, db) + "%");

        monthBar.setProgress((int) Task.getStatistics(taskId, 1, false, db));
        txtPorMonth.setText((int) Task.getStatistics(taskId, 1, false, db) + "%");

        wholeMonthBar.setProgress((int) Task.getStatistics(taskId, 1, true, db));
        txtWholeMonth.setText((int) Task.getStatistics(taskId, 1, true, db) + "%");

        BaseHelper.tryClose(db);
    }

    //Menu de la action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar, menu);
        menu.findItem(R.id.action_delete_spans).setVisible(false);
        menu.findItem(R.id.okTask).setVisible(false);
        setTitle("   Estadísticas");
        return true;
    }
}

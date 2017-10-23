package proyectohabitos.example.neita.habitos.Statistics;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.R;
import proyectohabitos.example.neita.habitos.Task.Task;

public class FrmSta extends AppCompatActivity {

    private int taskId;
    private CircularProgressBar weekBar, monthBar, GlobBar;
    private TextView txtPorWeek, txtPorMonth, txtPorGlob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_sta);

        Bundle bundle = getIntent().getExtras();
        taskId = bundle.getInt("id");

        weekBar = (CircularProgressBar) findViewById(R.id.week_pbar);
        monthBar = (CircularProgressBar) findViewById(R.id.month_pbar);
        GlobBar = (CircularProgressBar) findViewById(R.id.global_pbar);
        txtPorWeek = (TextView) findViewById(R.id.frm_sta_txt_per_sem);
        txtPorMonth = (TextView) findViewById(R.id.frm_sta_txt_per_mont);
        txtPorGlob = (TextView) findViewById(R.id.frm_sta_txt_per_glob);


        SQLiteDatabase db = BaseHelper.getReadable(FrmSta.this);

        weekBar.setProgress((int) Task.getStatistics(taskId, 0, db));
        txtPorWeek.setText((int) Task.getStatistics(taskId, 0, db) + "%");

        monthBar.setProgress((int) Task.getStatistics(taskId, 1, db));
        txtPorMonth.setText((int) Task.getStatistics(taskId, 1, db) + "%");

        GlobBar.setProgress((int) Task.getStatistics(taskId, 2, db));
        txtPorGlob.setText((int) Task.getStatistics(taskId, 2, db) + "%");

        BaseHelper.tryClose(db);
    }
}

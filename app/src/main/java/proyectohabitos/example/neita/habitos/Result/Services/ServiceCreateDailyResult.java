package proyectohabitos.example.neita.habitos.Result.Services;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.TaskParams;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import proyectohabitos.example.neita.habitos.BaseHelper;
import proyectohabitos.example.neita.habitos.DateUtils;
import proyectohabitos.example.neita.habitos.Result.Result;

import static com.google.android.gms.gcm.Task.NETWORK_STATE_ANY;

public class ServiceCreateDailyResult extends GcmTaskService {

    public ServiceCreateDailyResult() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        try {
            System.out.println("GUARDAAANDO YEY///");
            SQLiteDatabase db = BaseHelper.getReadable(this.getBaseContext());
            Result.insertResultToday(db);
            BaseHelper.tryClose(db);
            GregorianCalendar gc = DateUtils.getGregCalendar(new Date());
            gc.add(Calendar.DAY_OF_YEAR, 1);
            gc.set(Calendar.HOUR_OF_DAY, 23);
            gc.set(Calendar.MINUTE, 58);
            gc.set(Calendar.SECOND, 0);
            gc.set(Calendar.MILLISECOND, 0);
            scheduleInsertResultToday(this.getBaseContext(), (gc.getTimeInMillis() - System.currentTimeMillis()) / 1000);
            System.out.println(((gc.getTimeInMillis() - System.currentTimeMillis()) / 1000) + " SEGUNDOS");
            return GcmNetworkManager.RESULT_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this.getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT);
        }
        return GcmNetworkManager.RESULT_FAILURE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public static void scheduleInsertResultToday(Context cnxt, Long seconds) {
        GcmNetworkManager mGcmNetworkManager = GcmNetworkManager.getInstance(cnxt);
        OneoffTask task = new OneoffTask.Builder()
                .setService(ServiceCreateDailyResult.class)
                .setExecutionWindow(seconds - 10, seconds)
                .setRequiredNetwork(NETWORK_STATE_ANY)
                .setPersisted(true)
                .setRequiresCharging(false)
                .setUpdateCurrent(true)
                .setTag("result")
                .build();
        mGcmNetworkManager.schedule(task);


    }
}

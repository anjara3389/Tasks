package proyectohabitos.example.neita.habitos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {

    public MyBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context instanceof FrmChronometer) {
            ((FrmChronometer) context).finish();
        }
    }
}
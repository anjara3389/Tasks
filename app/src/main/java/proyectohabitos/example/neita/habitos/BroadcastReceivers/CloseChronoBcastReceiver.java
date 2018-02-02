package proyectohabitos.example.neita.habitos.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import proyectohabitos.example.neita.habitos.FrmChronometer;


//Broadcast que sirve para cerrar la clase frmChronometer
public class CloseChronoBcastReceiver extends BroadcastReceiver {

    public CloseChronoBcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context instanceof FrmChronometer) {
            ((FrmChronometer) context).finish();
        }
    }
}
package proyectohabitos.example.neita.habitos.Chronometer.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import proyectohabitos.example.neita.habitos.Chronometer.FrmChronometer;

/**Broadcast que sirve para cerrar la clase frmChronometer
 * Un BroadcastReceiver es un componente Android que permite el registro de eventos del sistema.
 * Todos los Receivers registrados para un evento serán notificados por Android una vez que éstos ocurran.
 */
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
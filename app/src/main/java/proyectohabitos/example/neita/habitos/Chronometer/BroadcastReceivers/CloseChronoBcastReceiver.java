package proyectohabitos.example.neita.habitos.Chronometer.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import proyectohabitos.example.neita.habitos.Chronometer.FrmChronometer;


//Broadcast que sirve para cerrar la clase frmChronometer

/**
 * Un BroadcastReceiver es un componente Android que permite el registro de eventos del sistema.
 * Todos los Receivers registrados para un evento serán notificados por Android una vez que éstos ocurran.
 * Por ejemplo, Android permite que aplicaciones puedan registrarse al ACTION_BOOT_COMPLETED que es un evento que lanza el sistema una vez que ha completado el proceso de arranque.
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
package proyectohabitos.example.neita.habitos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {
    private Context mContext;
    private AlertDialog.Builder alertDialogBuilder;
    private boolean answer;

    public interface MyDialogDialogListener {
        void onFinishDialog(boolean ans);
    }

    public MyDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Eliminar");
        alertDialogBuilder.setMessage("¿Desea eliminar la actividad?");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                answer=true;
                sendBackResult();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return alertDialogBuilder.create();
    }

    public void setmContext(Context cont) {
        this.mContext = cont;
    }

    public void sendBackResult() {
        MyDialogDialogListener listener = (MyDialogDialogListener) getTargetFragment();
        listener.onFinishDialog(answer);
        dismiss();
    }
}
package proyectohabitos.example.neita.habitos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.NumberPicker;

public class NumPickersDialogFragment extends DialogFragment {


    private NumberPicker hrs;
    private NumberPicker min;
    private int hours, minutes;
    private boolean ans;


    public NumPickersDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dial_frg_num_pickers, null, false);

        hrs = (NumberPicker) v.findViewById(R.id.number_pickers_hrs);
        min = (NumberPicker) v.findViewById(R.id.number_pickers_min);
        hrs.setMinValue(0);
        hrs.setMaxValue(23);
        min.setMinValue(0);
        min.setMaxValue(59);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        hrs.setWrapSelectorWheel(true);
        min.setWrapSelectorWheel(true);

        final AlertDialog dlg = new AlertDialog.Builder(getActivity())
                .setTitle("Cronómetro")
                .setPositiveButton("ACEPTAR", null)
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ans = false;
                                AddTask act = (AddTask) getActivity();
                                act.onFinishNumbersDialog(ans, -1, -1);
                            }
                        }
                )
                .setView(v)
                .create();

        dlg.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                dlg.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hours = hrs.getValue();
                        minutes = min.getValue();
                        ans = true;
                        AddTask act = (AddTask) getActivity();
                        act.onFinishNumbersDialog(ans, hours, minutes);
                        dlg.dismiss();
                    }
                });
            }
        });
        return dlg;
    }

}

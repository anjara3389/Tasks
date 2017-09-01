package proyectohabitos.example.neita.habitos.Statistics.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import proyectohabitos.example.neita.habitos.R;

public class GlobalStatistics extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_global_statistics, container, false);
        return rootView;
    }
}

package com.example.monpetitespace;

import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PenseeAdapter extends ArrayAdapter<String> {
    private ArrayList<String> listeDates;
    private onPenseeListener listener;

    // Interface pour communiquer les clics (supprimer / modifier) à l'activité
    public interface onPenseeListener{
        void onModifierClick(int position);
        void onSupprimerClick(int position);
    }
    public PenseeAdapter(Context context, ArrayList<String> pensees, ArrayList<String> dates, onPenseeListener listener) {

        super(context, 0, pensees);
        this.listeDates=dates;
        this.listener=listener;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vue = convertView;

        if(vue==null) {
            vue=LayoutInflater.from(getContext()).inflate(R.layout.item_pensee, parent, false);
        }

        TextView tvTexte = vue.findViewById(R.id.tvTextePensee);
        TextView tvDate = vue.findViewById(R.id.tvDatePensee);
        TextView btnModifier = vue.findViewById(R.id.btnModifierPensee);
        TextView btnSupprimer = vue.findViewById(R.id.btnSupprimerPensee);

        tvTexte.setText(getItem(position));
        if(listeDates != null && position < listeDates.size()) {
            tvDate.setText(listeDates.get(position));
        }

        // Clic sur le bouton Supprimer
        btnSupprimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(listener != null) {
                    listener.onSupprimerClick(position);
                }
            }
        });

        // Clic sur le bouton Modifier
        btnModifier.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(listener != null) {
                    listener.onModifierClick(position);
                }
            }
        });

        return vue;
    }
}

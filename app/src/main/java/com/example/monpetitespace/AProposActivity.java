package com.example.monpetitespace;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AProposActivity extends AppCompatActivity {

    private RelativeLayout rootLayoutAPropos;
    private Button btnRetourAPropos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_a_propos);

        rootLayoutAPropos = findViewById(R.id.rootLayoutAPropos);
        btnRetourAPropos = findViewById(R.id.btnRetourAPropos);

        // Application du thème global
        Theme.appliquerTheme(this, rootLayoutAPropos);

        // Bouton de retour
        btnRetourAPropos.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Theme.appliquerTheme(this, rootLayoutAPropos);
    }
}
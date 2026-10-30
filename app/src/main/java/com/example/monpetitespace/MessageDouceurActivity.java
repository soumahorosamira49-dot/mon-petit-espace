package com.example.monpetitespace;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MessageDouceurActivity extends AppCompatActivity {

    private RelativeLayout rootLayoutMessage;
    private TextView tvMessageDouceur, tvSignatureDouceur, btnFavori;
    private Button btnAutreMessage, btnRetourMessage;
    private SharedPreferences preferences;

    // Ma collection de messages doux et lettres intimes
    private final String[] messagesDoux = {
            "« Tu as le droit d’avancer à ton rythme, sans pression ni comparaison. »",
            "« Même les petites victoires d’aujourd'hui méritent d’être célébrées, aussi infimes soient-elles. »",
            "« Tu n’as pas besoin d’avoir toutes les réponses maintenant. Laisse les choses se poser. »",
            "« C’est OK de se sentir fatigué(e). Prends le temps de t'accueillir avec bienveillance. »",
            "« Respire doucement. Relâche tes épaules. Une seule chose à la fois. »",
            "« Ce que tu ressens est valide, même quand tu n’arrives pas à l’expliquer avec des mots. »",
            "« Tu fais de ton mieux avec ce que tu as, et c'est déjà immense. »",
            "« Offre-toi la même douceur et la même patience que tu offrirais à ta meilleure amie. »"
    };

    private final String[] signaturesDouces = {
            "— Un souffle léger",
            "— Pensée pour toi",
            "— Douceur intérieure",
            "— Écoute de soi",
            "— Instant présent",
            "— Paix sur ton cœur",
            "— Petit pas",
            "— Bienveillance"
    };

    private int indexActuel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message_doux);

        preferences = getSharedPreferences("MonPetitEspace", MODE_PRIVATE);

        rootLayoutMessage = findViewById(R.id.rootLayoutMessage);
        tvMessageDouceur = findViewById(R.id.tvMessageDouceur);
        tvSignatureDouceur = findViewById(R.id.tvSignatureDouceur);
        btnFavori = findViewById(R.id.btnFavori);
        btnAutreMessage = findViewById(R.id.btnAutreMessage);
        btnRetourMessage = findViewById(R.id.btnRetourMessage);

        // Application du thème global de l'appli
        Theme.appliquerTheme(this, rootLayoutMessage);

        // Afficher un message aléatoire au démarrage de la page
        afficherNouveauMessage();

        // Changer de message au clic (correction ici)
        btnAutreMessage.setOnClickListener(v -> afficherNouveauMessage());

        // Gestion du cœur (Favoris)
        btnFavori.setOnClickListener(v -> {
            Toast.makeText(this, "Ajouté à tes messages préférés ♡", Toast.LENGTH_SHORT).show();
            btnFavori.setText("❤️");
        });

        // Bouton de retour
        btnRetourMessage.setOnClickListener(v -> finish());
    }

    private void afficherNouveauMessage() {
        Random random = new Random();
        indexActuel = random.nextInt(messagesDoux.length);

        tvMessageDouceur.setText(messagesDoux[indexActuel]);
        tvSignatureDouceur.setText(signaturesDouces[indexActuel]);

        // Remettre le cœur en blanc par défaut lors du changement
        btnFavori.setText("🤍");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Theme.appliquerTheme(this, rootLayoutMessage);
    }
}
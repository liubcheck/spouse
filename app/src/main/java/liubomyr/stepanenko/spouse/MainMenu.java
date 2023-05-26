package liubomyr.stepanenko.spouse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import liubomyr.stepanenko.spouse.authentication.Login;
import liubomyr.stepanenko.spouse.creation.CityChoice;
import liubomyr.stepanenko.spouse.redaction.EditOrder;

public class MainMenu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences =
                getSharedPreferences("USER_DATA", MODE_PRIVATE);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Button makeOrderButton = findViewById(R.id.makeOrderButton);
        makeOrderButton.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String email = sharedPreferences.getString("EMAIL", "");
                if (!email.isEmpty()) {
                    db.collection("orders")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        Toast.makeText(MainMenu.this,
                                                "Ви вже маєте замовлення",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent intent = new Intent(
                                                MainMenu.this, CityChoice.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(MainMenu.this,
                                            "Помилка при роботі з базою даних",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        Button editOrderButton = findViewById(R.id.editOrderButton);
        editOrderButton.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String email = sharedPreferences.getString("EMAIL", "");
                if (!email.isEmpty()) {
                    db.collection("orders")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    if (task.getResult().isEmpty()) {
                                        Toast.makeText(MainMenu.this,
                                                "Ви ще не оформлювали замовлення",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent intent = new Intent(
                                                MainMenu.this, EditOrder.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(MainMenu.this,
                                            "Помилка при роботі з базою даних",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        Button signOutButton = findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(v -> {
            mAuth.signOut();

            SharedPreferences preferences =
                    getSharedPreferences("USER_DATA", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("EMAIL");
            editor.remove("PASSWORD");
            editor.apply();

            Intent intent = new Intent(MainMenu.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }
}
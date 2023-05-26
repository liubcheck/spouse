package liubomyr.stepanenko.spouse.authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import liubomyr.stepanenko.spouse.MainMenu;
import liubomyr.stepanenko.spouse.R;

public class Login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        SharedPreferences preferences =
                getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String loggedInUserEmail =
                preferences.getString("EMAIL", "");
        String loggedInUserPassword =
                preferences.getString("PASSWORD", "");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (!TextUtils.isEmpty(loggedInUserEmail)
                && !TextUtils.isEmpty(loggedInUserPassword)) {
            mAuth.signInWithEmailAndPassword(loggedInUserEmail, loggedInUserPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(Login.this, MainMenu.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        } else {
                            Toast.makeText(Login.this,
                                    "Неправильна пошта чи пароль",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            setContentView(R.layout.activity_login);

            EditText emailEditText = findViewById(R.id.editTextLoginTextEmailAddress);
            EditText passwordEditText = findViewById(R.id.editTextLoginTextPassword);

            TextView registerSuggestionTextView =
                    findViewById(R.id.registerSuggestionTextView);
            registerSuggestionTextView.setOnClickListener(v -> {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            });

            Button loginButton = findViewById(R.id.loginButton);
            loginButton.setOnClickListener(v -> {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this,
                            "Введіть Вашу електронну пошту",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this,
                            "Введіть Ваш пароль",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                SharedPreferences preferences1 =
                                        getSharedPreferences(
                                                "USER_DATA", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences1.edit();
                                editor.putString("EMAIL", email);
                                editor.putString("PASSWORD", password);
                                editor.apply();
                                Toast.makeText(Login.this,
                                        "Автентифікація успішна",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(
                                        Login.this, MainMenu.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            } else {
                                Toast.makeText(Login.this,
                                        "Неправильна пошта чи пароль",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        }
    }
}
package liubomyr.stepanenko.spouse.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import liubomyr.stepanenko.spouse.R;

public class Register extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        passwordEditText = findViewById(R.id.editTextTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextTextConfirmPassword);

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmedPassword = confirmPasswordEditText.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Register.this,
                        "Введіть Вашу електронну пошту",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Register.this,
                        "Введіть Ваш пароль",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 8) {
                Toast.makeText(Register.this,
                        "Пароль повинен мати мінімум 8 символів",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(confirmedPassword)) {
                Toast.makeText(Register.this,
                        "Введіть підтвердження паролю",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmedPassword)) {
                Toast.makeText(Register.this,
                        "Паролі не збігаються",
                        Toast.LENGTH_SHORT).show();
                return;
            }


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this,
                                    "Реєстрація успішна",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(
                                    Register.this, Login.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        } else {
                            Exception exception = task.getException();
                            if (exception != null) {
                                Toast.makeText(Register.this,
                                        "Помилка реєстрації: "
                                                + exception.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Register.this,
                                        "Помилка реєстрації",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }
}
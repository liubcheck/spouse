package liubomyr.stepanenko.spouse.creation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import liubomyr.stepanenko.spouse.MainMenu;
import liubomyr.stepanenko.spouse.R;

public class PhoneNumberActivity extends AppCompatActivity {
    private SharedPreferences userSharedPreferences;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_phone_number);

        userSharedPreferences =
                getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();

        EditText phoneEditText = findViewById(R.id.phone_editText);

        Button confirmButton = findViewById(R.id.confirmPhone_button);
        confirmButton.setOnClickListener(v -> {
            String phoneNumber = phoneEditText.getText().toString();
            if (isRightNumber(phoneNumber)) {
                saveOrder(phoneNumber);
                Toast.makeText(PhoneNumberActivity.this,
                        "Ваше замовлення успішно доповнене",
                        Toast.LENGTH_SHORT).show();
                onBackPressed();
            } else {
                Toast.makeText(PhoneNumberActivity.this,
                        "Некоректний номер", Toast.LENGTH_SHORT).show();
            }
        });

        Button skipButton = findViewById(R.id.skipPhone_button);
        skipButton.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private boolean isRightNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("^0\\d{2}\\d{7}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    private void saveOrder(String phoneNumber) {
        CollectionReference ordersRef = db.collection("orders");
        Query query = ordersRef.whereEqualTo("email",
                userSharedPreferences.getString("EMAIL", "_"));
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    document.getReference().update("phone", phoneNumber);
                }
            } else {
                Toast.makeText(PhoneNumberActivity.this,
                        "Помилка при доповненні замовлення: "
                                + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
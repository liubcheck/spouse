package liubomyr.stepanenko.spouse.creation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import liubomyr.stepanenko.spouse.MainMenu;
import liubomyr.stepanenko.spouse.R;
import liubomyr.stepanenko.spouse.model.RoundedImageView;

public class CityChoice extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_city_choice);

        sharedPreferences = getSharedPreferences("WEDDING_DATA", MODE_PRIVATE);

        RoundedImageView kyivImageView = findViewById(R.id.kyiv_image);
        kyivImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCityAndContinue("Київ");
            }
        });

        ImageView lvivImageView = findViewById(R.id.lviv_image);
        lvivImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCityAndContinue("Львів");
            }
        });

        ImageView kharkivImageView = findViewById(R.id.kharkiv_image);
        kharkivImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCityAndContinue("Харків");
            }
        });

        ImageView odesaImageView = findViewById(R.id.odesa_image);
        odesaImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCityAndContinue("Одеса");
            }
        });

        ImageView dniproImageView = findViewById(R.id.dnipro_image);
        dniproImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCityAndContinue("Дніпро");
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
        finish();
    }

    private void chooseCityAndContinue(String selectedCity) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SELECTED_CITY", selectedCity);
        editor.apply();
        Intent intent = new Intent(CityChoice.this, PlaceChoice.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}

package liubomyr.stepanenko.spouse.creation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import liubomyr.stepanenko.spouse.R;

public class PlaceChoice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_place_choice);

        SharedPreferences preferences = getSharedPreferences("WEDDING_DATA", MODE_PRIVATE);

        TextView chosenCityTextView = findViewById(R.id.chosenCity_textView);
        String city = preferences.getString("SELECTED_CITY", "");
        chosenCityTextView.setText(chosenCityTextView.getText()
                .toString().replace("city", city));

        ImageView restaurantImageView = findViewById(R.id.restaurant_imageView);
        restaurantImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlaceAndContinue("ресторан");
            }
        });

        ImageView countrysideComplexImageView =
                findViewById(R.id.countrysideComplex_imageView);
        countrysideComplexImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlaceAndContinue("заміський комплекс");
            }
        });

        ImageView parkImageView = findViewById(R.id.park_imageView);
        parkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlaceAndContinue("парк");
            }
        });

        ImageView conferenceHallImageView = findViewById(R.id.conferenceHall_imageView);
        conferenceHallImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlaceAndContinue("конференц-зал");
            }
        });

        ImageView otherImageView = findViewById(R.id.other_imageView);
        otherImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlaceAndContinue("інше");
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CityChoice.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void setPlaceAndContinue(String place) {
        SharedPreferences sharedPreferences =
                getSharedPreferences("WEDDING_DATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SELECTED_PLACE", place);
        editor.apply();
        Intent intent = new Intent(PlaceChoice.this,
                ExactLocationChoice.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
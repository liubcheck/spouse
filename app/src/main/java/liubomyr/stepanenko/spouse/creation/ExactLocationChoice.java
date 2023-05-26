package liubomyr.stepanenko.spouse.creation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import liubomyr.stepanenko.spouse.fragment.LocationInfoFragment;
import liubomyr.stepanenko.spouse.R;
import liubomyr.stepanenko.spouse.model.Location;
import liubomyr.stepanenko.spouse.model.RoundedImageView;

public class ExactLocationChoice extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore db;
    private GridLayout gridLayout;
    private View overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_exact_location_choice);

        sharedPreferences = getSharedPreferences("WEDDING_DATA", MODE_PRIVATE);
        String place = sharedPreferences.getString("SELECTED_PLACE", "");
        String city = sharedPreferences.getString("SELECTED_CITY", "");

        TextView chosenOptionsTextView = findViewById(R.id.chosenOptions_textView);
        chosenOptionsTextView.setText(chosenOptionsTextView.getText()
                .toString()
                .replace("place", place)
                .replace("city", city));

        firebaseStorage = FirebaseStorage.getInstance();
        gridLayout = findViewById(R.id.gridLayout);
        db = FirebaseFirestore.getInstance();

        overlay = findViewById(R.id.overlay);
        overlay.setOnClickListener(v -> {});

        new LoadLocationsTask(city, place).execute();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            Intent intent = new Intent(this, PlaceChoice.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    public void hideOverlay() {
        if (overlay != null) {
            overlay.setVisibility(View.GONE);
        }
    }

    private void addImageViewToGridLayout(Location location, int position) {
        ImageView imageView = new RoundedImageView(this);
        int imageViewId = View.generateViewId();
        imageView.setId(imageViewId);

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
        layoutParams.height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        layoutParams.setMargins(30, 80, 30, 30);

        layoutParams.rowSpec = GridLayout.spec(position / 2);
        layoutParams.columnSpec = GridLayout.spec(position % 2);

        imageView.setLayoutParams(layoutParams);
        gridLayout.addView(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.overlay,
                                LocationInfoFragment.newInstance(location, imageView,
                                        sharedPreferences,
                                        true, false,
                                        false, false))
                        .addToBackStack(null)
                        .commit();
            }
        });

        loadLocationImage(location.getImagePath(), imageViewId);
    }

    private void addTextViewToGridLayout(String name, int position) {
        TextView textView = new TextView(this);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.setMargins(30, 0, 30, 30);

        layoutParams.rowSpec = GridLayout.spec((position / 2) + 1);
        layoutParams.columnSpec = GridLayout.spec(position % 2);

        textView.setLayoutParams(layoutParams);
        textView.setText(name);
        textView.setTextSize(12);
        textView.setTextColor(ContextCompat.getColor(this, R.color.red_500));
        textView.setGravity(Gravity.CENTER);
        gridLayout.addView(textView);
    }

    private void loadLocationImage(String imagePath, int imageViewId) {
        StorageReference storageRef = firebaseStorage.getReference().child(imagePath);
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            ImageView imageView = findViewById(imageViewId);
            Glide.with(ExactLocationChoice.this)
                    .load(uri)
                    .thumbnail(0.1f)
                    .into(imageView);
        });
    }

    private class LoadLocationsTask extends AsyncTask<Void, Void, List<Location>> {
        private final String city;
        private final String locationType;

        public LoadLocationsTask(String city, String locationType) {
            this.city = formatCity(city);
            this.locationType = formatLocationType(locationType);
        }

        @Override
        protected List<Location> doInBackground(Void... voids) {
            List<Location> locations = new ArrayList<>();
            CountDownLatch latch = new CountDownLatch(1);
            db.collection(locationType)
                    .whereEqualTo("city", city)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    locations.add(getLocationFromDocument(document));
                                }
                            }
                        }
                        latch.countDown();
                    }).addOnFailureListener(e -> {
                        throw new RuntimeException("Couldn't load the location image", e);
                    });
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException("Couldn't load the location image", e);
            }
            return locations;
        }

        @Override
        protected void onPostExecute(List<Location> locations) {
            for (int i = 0; i < locations.size(); i++) {
                Location location = locations.get(i);
                addImageViewToGridLayout(location, i);
                addTextViewToGridLayout(location.getName(), i);
            }
        }

        private Location getLocationFromDocument(QueryDocumentSnapshot document) {
            String name = document.getString("name");
            String city = sharedPreferences.getString("SELECTED_CITY", "");
            String address = document.getString("address");
            String description = document.getString("description");
            String imagePath = document.getString("imagePath");
            return new Location(name, city, address, description, imagePath);
        }

        private String formatCity(String city) {
            switch (city) {
                case "Київ" :
                    return "kyiv";
                case "Львів" :
                    return "lviv";
                case "Харків" :
                    return "kharkiv";
                case "Одеса" :
                    return "odesa";
                case "Дніпро" :
                    return "dnipro";
            }
            return null;
        }

        private String formatLocationType(String locationType) {
            switch (locationType) {
                case "ресторан" :
                    return "restaurants";
                case "заміський комплекс" :
                    return "countryside_complexes";
                case "парк" :
                    return "parks";
                case "конференц-зал" :
                    return "conference_halls";
                case "інше" :
                    return "other";
            }
            return null;
        }
    }
}

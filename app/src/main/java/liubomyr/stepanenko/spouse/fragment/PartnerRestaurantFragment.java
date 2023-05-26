package liubomyr.stepanenko.spouse.fragment;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
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
import liubomyr.stepanenko.spouse.R;
import liubomyr.stepanenko.spouse.creation.OtherOptionsChoice;
import liubomyr.stepanenko.spouse.handler.BackPressHandler;
import liubomyr.stepanenko.spouse.model.Location;
import liubomyr.stepanenko.spouse.model.RoundedImageView;

import static android.content.Context.MODE_PRIVATE;

public class PartnerRestaurantFragment extends Fragment implements BackPressHandler {
    public static List<Location> cachedRestaurants = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore db;
    private GridLayout gridLayout;
    private View overlay;

    public PartnerRestaurantFragment() {
    }

    @Override
    public boolean handleBackPress() {
        if (getActivity() instanceof OtherOptionsChoice) {
            ((OtherOptionsChoice) getActivity()).hideOverlay();
        }
        getParentFragmentManager().popBackStack();
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_partner_restaurant,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sharedPreferences = requireActivity()
                .getSharedPreferences("WEDDING_DATA", MODE_PRIVATE);
        String city = sharedPreferences.getString("SELECTED_CITY", "");

        firebaseStorage = FirebaseStorage.getInstance();
        gridLayout = view.findViewById(R.id.partnerRestaurantGridLayout);
        db = FirebaseFirestore.getInstance();

        overlay = view.findViewById(R.id.overlay);
        overlay.setOnClickListener(v -> {});

        if (cachedRestaurants.isEmpty()) {
            new LoadRestaurantsTask(city).execute();
        } else {
            for (int i = 0; i < cachedRestaurants.size(); i++) {
                Location restaurant = cachedRestaurants.get(i);
                addImageViewToGridLayout(restaurant, i);
                addTextViewToGridLayout(restaurant.getName(), i);
            }
        }

        Button noRestaurantButton = view.findViewById(R.id.noRestaurantButton);
        noRestaurantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("PARTNER_RESTAURANT", "_");
                editor.apply();
                handleBackPress();
            }
        });
    }

    private void addImageViewToGridLayout(Location location, int position) {
        ImageView imageView = new RoundedImageView(requireContext());
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
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("PARTNER_RESTAURANT", location.getName());
                editor.apply();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.overlay,
                                LocationInfoFragment.newInstance(location, imageView,
                                        sharedPreferences,
                                        false, true,
                                        false, false))
                        .addToBackStack(null)
                        .commit();
            }
        });

        loadLocationImage(location.getImagePath(), imageViewId);
    }

    private void addTextViewToGridLayout(String name, int position) {
        TextView textView = new TextView(requireContext());
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.setMargins(30, 0, 30, 30);

        layoutParams.rowSpec = GridLayout.spec((position / 2) + 1);
        layoutParams.columnSpec = GridLayout.spec(position % 2);

        textView.setLayoutParams(layoutParams);
        textView.setText(name);
        textView.setTextSize(12);
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_500));
        textView.setGravity(Gravity.CENTER);
        gridLayout.addView(textView);
    }

    private void loadLocationImage(String imagePath, int imageViewId) {
        StorageReference storageRef = firebaseStorage.getReference().child(imagePath);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageView imageView = getView().findViewById(imageViewId);
                Glide.with(PartnerRestaurantFragment.this)
                        .load(uri)
                        .thumbnail(0.1f)
                        .into(imageView);
            }
        });
    }

    private class LoadRestaurantsTask extends AsyncTask<Void, Void, List<Location>> {
        private final String city;
        private static final String locationType = "restaurants";

        public LoadRestaurantsTask(String city) {
            this.city = formatCity(city);
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
                                    cachedRestaurants.add(getLocationFromDocument(document));
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
            String address = document.getString("address");
            String description = document.getString("description");
            String imagePath = document.getString("imagePath");
            return new Location(name, city, address, description, imagePath);
        }
    }
}
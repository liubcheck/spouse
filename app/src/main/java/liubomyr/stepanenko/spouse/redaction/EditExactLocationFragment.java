package liubomyr.stepanenko.spouse.redaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import liubomyr.stepanenko.spouse.R;
import liubomyr.stepanenko.spouse.fragment.LocationInfoFragment;
import liubomyr.stepanenko.spouse.handler.BackPressHandler;
import liubomyr.stepanenko.spouse.model.Location;
import liubomyr.stepanenko.spouse.model.RoundedImageView;

public class EditExactLocationFragment extends Fragment implements BackPressHandler {
    private SharedPreferences updatedSharedPreferences;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore db;
    private GridLayout gridLayout;
    private View overlay;
    private boolean needsPartnerRestaurantUpdate = false;

    public EditExactLocationFragment() {
    }

    @Override
    public boolean handleBackPress() {
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
        return inflater.inflate(R.layout.fragment_edit_exact_location,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        updatedSharedPreferences = requireActivity()
                .getSharedPreferences("UPDATE_LOCATION_DATA", Context.MODE_PRIVATE);

        String place = updatedSharedPreferences.getString("SELECTED_PLACE", "");
        String city = updatedSharedPreferences.getString("SELECTED_CITY", "");

        TextView chosenOptionsTextView = requireView()
                .findViewById(R.id.chosenOptions_textViewEdit);
        chosenOptionsTextView.setText(chosenOptionsTextView.getText()
                .toString()
                .replace("place", place)
                .replace("city", city));

        firebaseStorage = FirebaseStorage.getInstance();
        gridLayout = requireView().findViewById(R.id.gridLayoutEdit);
        db = FirebaseFirestore.getInstance();

        overlay = view.findViewById(R.id.overlay);
        overlay.setOnClickListener(v -> {});

        new EditExactLocationFragment.LoadLocationsTask(city, place).execute();
    }

    private void addImageViewToGridLayout(Location location, int position) {
        ImageView imageView = new RoundedImageView(requireContext());
        int imageViewId = View.generateViewId(); // Generate a unique ID for the ImageView
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
                String place =
                        updatedSharedPreferences.getString("SELECTED_PLACE", "_");
                if (!place.equals("ресторан")
                        && !place.equals("заміський комплекс")
                        && !place.equals("інше")) {
                    needsPartnerRestaurantUpdate = true;
                }
                Gson gson = new Gson();
                String locationJson = gson.toJson(location);
                SharedPreferences.Editor editor = updatedSharedPreferences.edit();
                editor.putString("SELECTED_LOCATION", locationJson);
                editor.apply();
                overlay.setVisibility(View.VISIBLE);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.overlay,
                                LocationInfoFragment.newInstance(location, imageView,
                                        updatedSharedPreferences,
                                        false, false,
                                        true, needsPartnerRestaurantUpdate))
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
                ImageView imageView = requireView().findViewById(imageViewId);
                Glide.with(EditExactLocationFragment.this)
                        .load(uri)
                        .thumbnail(0.1f)
                        .into(imageView);
            }
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

        private Location getLocationFromDocument(QueryDocumentSnapshot document) {
            String name = document.getString("name");
            String city = updatedSharedPreferences.getString("SELECTED_CITY", "");
            String address = document.getString("address");
            String description = document.getString("description");
            String imagePath = document.getString("imagePath");
            return new Location(name, city, address, description, imagePath);
        }
    }
}
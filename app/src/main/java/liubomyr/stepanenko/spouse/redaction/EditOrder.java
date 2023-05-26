package liubomyr.stepanenko.spouse.redaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import liubomyr.stepanenko.spouse.MainMenu;
import liubomyr.stepanenko.spouse.R;
import liubomyr.stepanenko.spouse.fragment.AccessoriesChoiceFragment;
import liubomyr.stepanenko.spouse.handler.BackPressHandler;
import liubomyr.stepanenko.spouse.fragment.DjChoiceFragment;
import liubomyr.stepanenko.spouse.fragment.FlowersChoiceFragment;
import liubomyr.stepanenko.spouse.fragment.HostChoiceFragment;
import liubomyr.stepanenko.spouse.fragment.PartnerRestaurantFragment;
import liubomyr.stepanenko.spouse.fragment.PhotographerChoiceFragment;
import liubomyr.stepanenko.spouse.model.Location;

public class EditOrder extends AppCompatActivity {
    private EditCityFragment editCityFragment;
    private AccessoriesChoiceFragment accessoriesChoiceFragment;
    private FlowersChoiceFragment flowersChoiceFragment;
    private DjChoiceFragment djChoiceFragment;
    private PhotographerChoiceFragment photographerChoiceFragment;
    private HostChoiceFragment hostChoiceFragment;
    private Button editOrderButton;
    private Button deleteOrderButton;
    private TextView totalPriceTextView;
    private View overlay;
    private SharedPreferences userData;
    private SharedPreferences updateSharedPreferences;
    private SharedPreferences originalSharedPreferences;
    private SharedPreferences locationSharedPreferences;
    private Location location;
    private TextView chosenLocationTextView;
    private FirebaseFirestore db;
    private boolean fragmentOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_edit_order);

        userData = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        updateSharedPreferences =
                getSharedPreferences("UPDATE_WEDDING_DATA", MODE_PRIVATE);
        originalSharedPreferences =
                getSharedPreferences("WEDDING_DATA", MODE_PRIVATE);
        locationSharedPreferences =
                getSharedPreferences("UPDATE_LOCATION_DATA", MODE_PRIVATE);

        SharedPreferences.Editor editor = locationSharedPreferences.edit();
        editor.putString("SELECTED_CITY",
                originalSharedPreferences.getString("SELECTED_CITY", "_"));
        editor.putString("SELECTED_PLACE",
                originalSharedPreferences.getString("SELECTED_PLACE", "_"));
        editor.putString("SELECTED_LOCATION",
                originalSharedPreferences.getString("SELECTED_LOCATION", "_"));
        editor.apply();

        overlay = findViewById(R.id.overlay);

        db = FirebaseFirestore.getInstance();

        editor = updateSharedPreferences.edit();
        setUpValues(editor);

        String locationJson =
                updateSharedPreferences.getString("SELECTED_LOCATION", "");
        location = new Gson().fromJson(locationJson, Location.class);

        editCityFragment = new EditCityFragment();
        accessoriesChoiceFragment = new AccessoriesChoiceFragment(updateSharedPreferences);
        flowersChoiceFragment = new FlowersChoiceFragment(updateSharedPreferences);
        djChoiceFragment = new DjChoiceFragment(updateSharedPreferences);
        photographerChoiceFragment = new PhotographerChoiceFragment(updateSharedPreferences);
        hostChoiceFragment = new HostChoiceFragment(updateSharedPreferences);

        chosenLocationTextView = findViewById(R.id.chosenLocation_textViewEdit);
        chosenLocationTextView.setText(chosenLocationTextView.getText()
                .toString().replace("location",
                        location.getName() + ", " + location.getCity()));

        ImageView locationImageView = findViewById(R.id.locationEditImage);
        locationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(editCityFragment);
            }
        });

        ImageView accessoriesImageView = findViewById(R.id.accessoriesEditImage);
        accessoriesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(accessoriesChoiceFragment);
            }
        });

        ImageView flowersImageView = findViewById(R.id.flowersEditImage);
        flowersImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(flowersChoiceFragment);
            }
        });

        ImageView djsImageView = findViewById(R.id.djEditImage);
        djsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(djChoiceFragment);
            }
        });

        ImageView photographersImageView = findViewById(R.id.photoEditImage);
        photographersImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(photographerChoiceFragment);
            }
        });

        ImageView hostsImageView = findViewById(R.id.hostEditImage);
        hostsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(hostChoiceFragment);
            }
        });

        totalPriceTextView = findViewById(R.id.totalPriceEditText);
        totalPriceTextView.setText(getString(R.string.total_estimated_price_1_d,
                updateSharedPreferences.getInt("TOTAL_SUM", 0)));

        editOrderButton = findViewById(R.id.confirmEditButton);
        editOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
                updateOriginalSharedPreferences();
                Intent intent = new Intent(EditOrder.this, MainMenu.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        deleteOrderButton = findViewById(R.id.deleteOrderButton);
        deleteOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new AttentionFragment(true));
            }
        });
    }
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.overlay);
        if (currentFragment instanceof BackPressHandler) {
            if (((BackPressHandler) currentFragment).handleBackPress()) {
                return;
            }
        }
        if (fragmentOpen) {
            getSupportFragmentManager().popBackStack();
            fragmentOpen = false;
        } else {
            openFragment(new AttentionFragment(false));
        }
    }

    public void hideOverlay() {
        if (overlay != null) {
            String locationJson =
                    locationSharedPreferences.getString("SELECTED_LOCATION", "");
            location = new Gson().fromJson(locationJson, Location.class);
            String locationString = "Location: " + location.getName() + ", " + location.getCity();
            chosenLocationTextView.setText(locationString);
            totalPriceTextView.setText(getString(R.string.total_estimated_price_1_d_new,
                    updateSharedPreferences.getInt("TOTAL_SUM", 0)));
            overlay.setVisibility(View.GONE);
            overlay.setClickable(false);
            editOrderButton.setVisibility(View.VISIBLE);
            deleteOrderButton.setVisibility(View.VISIBLE);
        }
        fragmentOpen = false;
    }

    private void openFragment(Fragment fragment) {
        overlay.setVisibility(View.VISIBLE);
        overlay.setClickable(true);
        editOrderButton.setVisibility(View.GONE);
        deleteOrderButton.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.overlay, fragment)
                .addToBackStack(null)
                .commit();
        fragmentOpen = true;
    }

    public void saveChanges() {
        String user = userData.getString("EMAIL", "_");

        Gson gson = new Gson();
        String city = locationSharedPreferences.getString("SELECTED_CITY", "_");
        String place = locationSharedPreferences.getString("SELECTED_PLACE", "_");
        Location location = gson.fromJson(locationSharedPreferences
                .getString("SELECTED_LOCATION", "_"), Location.class);
        String name = location.getName();

        String partnerRestaurant =
                updateSharedPreferences.getString("PARTNER_RESTAURANT", "_");

        Map<String, Object> accessories = new HashMap<>();

        Map<String, Object> manyBalloons = new HashMap<>();
        int manyTotalNumber = updateSharedPreferences.getInt("MANY_NUMBER", 0);
        if (manyTotalNumber > 0) {
            manyBalloons.put("colors", getColors("many"));
            manyBalloons.put("quantity", manyTotalNumber);
            manyBalloons.put("total_price",
                    updateSharedPreferences.getInt("manyTotalPrice", 0));
        } else {
            manyBalloons.put("info", "_");
        }
        accessories.put("balloons", manyBalloons);

        Map<String, Object> heartBalloons = new HashMap<>();
        int heartTotalNumber = updateSharedPreferences.getInt("HEART_NUMBER", 0);
        if (heartTotalNumber > 0) {
            heartBalloons.put("colors", getColors("heart"));
            heartBalloons.put("quantity", heartTotalNumber);
            heartBalloons.put("total_price", updateSharedPreferences.getInt("heartTotalPrice", 0));
        } else {
            heartBalloons.put("info", "_");
        }
        accessories.put("heart_balloons", heartBalloons);

        Map<String, Object> archBalloons = new HashMap<>();
        int archTotalNumber = updateSharedPreferences.getInt("ARCH_NUMBER", 0);
        if (archTotalNumber > 0) {
            archBalloons.put("colors", getColors("arch"));
            archBalloons.put("quantity", archTotalNumber);
            archBalloons.put("total_price",
                    updateSharedPreferences.getInt("archTotalPrice", 0));
        } else {
            archBalloons.put("info", "_");
        }
        accessories.put("archBalloons", archBalloons);

        accessories.put("total_sum",
                updateSharedPreferences.getInt("TOTAL_BALLOONS_SUM", 0));

        String accessoriesComment =
                updateSharedPreferences.getString("ACCESSORIES_COMMENT", "_");

        Map<String, Object> flowers = new HashMap<>();

        Map<String, Object> roses = new HashMap<>();
        int rosesTotalNumber = updateSharedPreferences.getInt("ROSES_NUMBER", 0);
        if (rosesTotalNumber > 0) {
            roses.put("colors", getColors("roses"));
            roses.put("quantity", rosesTotalNumber);
            roses.put("total_price",
                    updateSharedPreferences.getInt("rosesTotalPrice", 0));
        } else {
            roses.put("info", "_");
        }
        flowers.put("roses", roses);

        Map<String, Object> peonies = new HashMap<>();
        int peoniesTotalNumber = updateSharedPreferences.getInt("PEONIES_NUMBER", 0);
        if (peoniesTotalNumber > 0) {
            peonies.put("colors", getColors("peonies"));
            peonies.put("quantity", peoniesTotalNumber);
            peonies.put("total_price", updateSharedPreferences.getInt("peoniesTotalPrice", 0));
        } else {
            peonies.put("info", "_");
        }
        flowers.put("peonies", peonies);

        Map<String, Object> freesias = new HashMap<>();
        int freesiasTotalNumber = updateSharedPreferences.getInt("FREESIAS_NUMBER", 0);
        if (freesiasTotalNumber > 0) {
            freesias.put("colors", getColors("freesias"));
            freesias.put("quantity", freesiasTotalNumber);
            freesias.put("total_price",
                    updateSharedPreferences.getInt("freesiasTotalPrice", 0));
        } else {
            freesias.put("info", "_");
        }
        flowers.put("freesias", freesias);

        flowers.put("total_sum",
                updateSharedPreferences.getInt("TOTAL_FLOWERS_SUM", 0));

        String flowersComment =
                updateSharedPreferences.getString("FLOWERS_COMMENT", "_");

        Map<String, Object> dj = new HashMap<>();
        String djName = updateSharedPreferences.getString("SELECTED_DJ", "_");
        if (!djName.equals("_")) {
            dj.put("name", djName);
            dj.put("price", updateSharedPreferences.getInt("DJ_PRICE", 0));
        } else {
            dj.put("info", djName);
        }

        Map<String, Object> photographer = new HashMap<>();
        String photographerName =
                updateSharedPreferences.getString("SELECTED_PHOTOGRAPHER", "_");
        if (!photographerName.equals("_")) {
            photographer.put("name", photographerName);
            photographer.put("hours", updateSharedPreferences.getInt("PHOTO_HOURS", 0));
            photographer.put("price", updateSharedPreferences.getInt("PHOTO_PRICE", 0));
            photographer.put("total_price",
                    updateSharedPreferences.getInt("PHOTOGRAPHER_TOTAL_PRICE", 0));
        } else {
            photographer.put("info", photographerName);
        }

        Map<String, Object> host = new HashMap<>();
        String hostName = updateSharedPreferences.getString("SELECTED_HOST", "_");
        if (!hostName.equals("_")) {
            host.put("name", hostName);
            host.put("price", updateSharedPreferences.getInt("HOST_PRICE", 0));
        } else {
            host.put("info", hostName);
        }

        int totalSum = updateSharedPreferences.getInt("TOTAL_SUM", 0);

        Map<String, Object> order = new HashMap<>();
        order.put("email", user);
        order.put("city", city);
        order.put("place", place);
        order.put("location_name", name);
        order.put("partner_restaurant", partnerRestaurant);
        order.put("accessories", accessories);
        order.put("accessories_comment",
                accessoriesComment.length() >= 1 ? accessoriesComment : "_");
        order.put("flowers", flowers);
        order.put("flowers_comment",
                flowersComment.length() >= 1 ? flowersComment : "_");
        order.put("dj", dj);
        order.put("photographer", photographer);
        order.put("host", host);
        order.put("total_sum", totalSum);

        CollectionReference ordersRef = db.collection("orders");

        Query query = ordersRef.whereEqualTo("email", user);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document
                        : Objects.requireNonNull(task.getResult())) {
                    ordersRef.document(document.getId()).set(order, SetOptions.merge())
                            .addOnSuccessListener(e -> Toast.makeText(EditOrder.this,
                                    "Ваше замовлення успішно оновлене",
                                    Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(EditOrder.this,
                                    "Помилка при оновленні замовлення",
                                    Toast.LENGTH_SHORT).show());
                }
            } else {
                Toast.makeText(EditOrder.this,
                        "Помилка при оновленні замовлення: " + task.getException(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> getColors(String type) {
        List<String> colors = new ArrayList<>();
        if (updateSharedPreferences.getBoolean(type + "Red", false)) {
            colors.add("Red");
        }
        if (updateSharedPreferences.getBoolean(type + "Orange", false)) {
            colors.add("Orange");
        }
        if (updateSharedPreferences.getBoolean(type + "Yellow", false)) {
            colors.add("Yellow");
        }
        if (updateSharedPreferences.getBoolean(type + "Green", false)) {
            colors.add("Green");
        }
        if (updateSharedPreferences.getBoolean(type + "Blue", false)) {
            colors.add("Blue");
        }
        if (updateSharedPreferences.getBoolean(type + "Violet", false)) {
            colors.add("Violet");
        }
        if (updateSharedPreferences.getBoolean(type + "Golden", false)) {
            colors.add("Golden");
        }
        if (updateSharedPreferences.getBoolean(type + "Silver", false)) {
            colors.add("Silver");
        }
        if (updateSharedPreferences.getBoolean(type + "Pink", false)) {
            colors.add("Pink");
        }
        if (updateSharedPreferences.getBoolean(type + "White", false)) {
            colors.add("White");
        }
        return colors;
    }

    public void deleteOrder() {
        CollectionReference ordersRef = db.collection("orders");

        String email = userData.getString("EMAIL", "_");

        Query query = ordersRef.whereEqualTo("email", email);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document
                        : Objects.requireNonNull(task.getResult())) {
                    ordersRef.document(document.getId()).delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(EditOrder.this,
                                    "Ваще замовлення видалене",
                                    Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(EditOrder.this,
                                    "Помилка при видаленні замовлення",
                                    Toast.LENGTH_SHORT).show());
                }
            } else {
                Toast.makeText(EditOrder.this,
                        "Помилка при видаленні замовлення: " + task.getException(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpValues(SharedPreferences.Editor editor) {
        editor.putString("SELECTED_CITY",
                originalSharedPreferences.getString("SELECTED_CITY", "_"));
        editor.putString("SELECTED_PLACE",
                originalSharedPreferences.getString("SELECTED_PLACE", "_"));
        editor.putString("SELECTED_LOCATION",
                originalSharedPreferences.getString("SELECTED_LOCATION", "_"));

        editor.putString("PARTNER_RESTAURANT",
                originalSharedPreferences.getString("PARTNER_RESTAURANT", "_"));
        PartnerRestaurantFragment.cachedRestaurants = new ArrayList<>();

        editor.putBoolean("manyRed",
                originalSharedPreferences.getBoolean("manyRed", false));
        editor.putBoolean("manyOrange",
                originalSharedPreferences.getBoolean("manyOrange", false));
        editor.putBoolean("manyYellow",
                originalSharedPreferences.getBoolean("manyYellow", false));
        editor.putBoolean("manyGreen",
                originalSharedPreferences.getBoolean("manyGreen", false));
        editor.putBoolean("manyBlue",
                originalSharedPreferences.getBoolean("manyBlue", false));
        editor.putBoolean("manyViolet",
                originalSharedPreferences.getBoolean("manyViolet", false));
        editor.putBoolean("manyGolden",
                originalSharedPreferences.getBoolean("manyGolden", false));
        editor.putBoolean("manySilver",
                originalSharedPreferences.getBoolean("manySilver", false));
        editor.putInt("manyTotalNumber",
                originalSharedPreferences.getInt("manyTotalNumber", 0));
        editor.putInt("manyTotalPrice",
                originalSharedPreferences.getInt("manyTotalPrice", 0));
        editor.putInt("MANY_NUMBER",
                originalSharedPreferences.getInt("MANY_NUMBER", 0));

        editor.putBoolean("heartRed",
                originalSharedPreferences.getBoolean("heartRed", false));
        editor.putBoolean("heartOrange",
                originalSharedPreferences.getBoolean("heartOrange", false));
        editor.putBoolean("heartYellow",
                originalSharedPreferences.getBoolean("heartYellow", false));
        editor.putBoolean("heartGreen",
                originalSharedPreferences.getBoolean("heartGreen", false));
        editor.putBoolean("heartBlue",
                originalSharedPreferences.getBoolean("heartBlue", false));
        editor.putBoolean("heartViolet",
                originalSharedPreferences.getBoolean("heartViolet", false));
        editor.putBoolean("heartGolden",
                originalSharedPreferences.getBoolean("heartGolden", false));
        editor.putBoolean("heartSilver",
                originalSharedPreferences.getBoolean("heartSilver", false));
        editor.putInt("heartTotalNumber",
                originalSharedPreferences.getInt("heartTotalNumber", 0));
        editor.putInt("heartTotalPrice",
                originalSharedPreferences.getInt("heartTotalPrice", 0));
        editor.putInt("HEART_NUMBER",
                originalSharedPreferences.getInt("HEART_NUMBER", 0));

        editor.putBoolean("archRed",
                originalSharedPreferences.getBoolean("archRed", false));
        editor.putBoolean("archOrange",
                originalSharedPreferences.getBoolean("archOrange", false));
        editor.putBoolean("archYellow",
                originalSharedPreferences.getBoolean("archYellow", false));
        editor.putBoolean("archGreen",
                originalSharedPreferences.getBoolean("archGreen", false));
        editor.putBoolean("archBlue",
                originalSharedPreferences.getBoolean("archBlue", false));
        editor.putBoolean("archViolet",
                originalSharedPreferences.getBoolean("archViolet", false));
        editor.putBoolean("archGolden",
                originalSharedPreferences.getBoolean("archGolden", false));
        editor.putBoolean("archSilver",
                originalSharedPreferences.getBoolean("archSilver", false));
        editor.putInt("archTotalNumber",
                originalSharedPreferences.getInt("archTotalNumber", 0));
        editor.putInt("archTotalPrice",
                originalSharedPreferences.getInt("archTotalPrice", 0));
        editor.putInt("ARCH_NUMBER",
                originalSharedPreferences.getInt("ARCH_NUMBER", 0));


        editor.putString("ACCESSORIES_COMMENT",
                originalSharedPreferences.getString("ACCESSORIES_COMMENT", "_"));

        editor.putInt("TOTAL_BALLOONS_SUM",
                originalSharedPreferences.getInt("TOTAL_BALLOONS_SUM", 0));

        editor.putBoolean("rosesRed",
                originalSharedPreferences.getBoolean("rosesRed", false));
        editor.putBoolean("rosesYellow",
                originalSharedPreferences.getBoolean("rosesYellow", false));
        editor.putBoolean("rosesPink",
                originalSharedPreferences.getBoolean("rosesPink", false));
        editor.putBoolean("rosesWhite",
                originalSharedPreferences.getBoolean("rosesWhite", false));
        editor.putInt("rosesTotalNumber",
                originalSharedPreferences.getInt("rosesTotalNumber", 0));
        editor.putInt("rosesTotalPrice",
                originalSharedPreferences.getInt("rosesTotalPrice", 0));
        editor.putInt("ROSES_NUMBER",
                originalSharedPreferences.getInt("ROSES_NUMBER", 0));

        editor.putBoolean("peoniesRed",
                originalSharedPreferences.getBoolean("peoniesRed", false));
        editor.putBoolean("peoniesYellow",
                originalSharedPreferences.getBoolean("peoniesYellow", false));
        editor.putBoolean("peoniesPink",
                originalSharedPreferences.getBoolean("peoniesPink", false));
        editor.putBoolean("peoniesWhite",
                originalSharedPreferences.getBoolean("peoniesWhite", false));
        editor.putInt("peoniesTotalNumber",
                originalSharedPreferences.getInt("peoniesTotalNumber", 0));
        editor.putInt("peoniesTotalPrice",
                originalSharedPreferences.getInt("peoniesTotalPrice", 0));
        editor.putInt("PEONIES_NUMBER",
                originalSharedPreferences.getInt("PEONIES_NUMBER", 0));

        editor.putBoolean("freesiasRed",
                originalSharedPreferences.getBoolean("freesiasRed", false));
        editor.putBoolean("freesiasYellow",
                originalSharedPreferences.getBoolean("freesiasYellow", false));
        editor.putBoolean("freesiasPink",
                originalSharedPreferences.getBoolean("freesiasPink", false));
        editor.putBoolean("freesiasWhite",
                originalSharedPreferences.getBoolean("freesiasWhite", false));
        editor.putInt("freesiasTotalNumber",
                originalSharedPreferences.getInt("freesiasTotalNumber", 0));
        editor.putInt("freesiasTotalPrice",
                originalSharedPreferences.getInt("freesiasTotalPrice", 0));
        editor.putInt("FREESIAS_NUMBER",
                originalSharedPreferences.getInt("FREESIAS_NUMBER", 0));

        editor.putString("FLOWERS_COMMENT",
                originalSharedPreferences.getString("FLOWERS_COMMENT", "_"));

        editor.putInt("TOTAL_FLOWERS_SUM",
                originalSharedPreferences.getInt("TOTAL_FLOWERS_SUM", 0));

        editor.putString("SELECTED_DJ",
                originalSharedPreferences.getString("SELECTED_DJ",
                        getResources().getString(R.string.none)));
        editor.putInt("DJ_PRICE", originalSharedPreferences.getInt("DJ_PRICE", 0));

        editor.putString("SELECTED_PHOTOGRAPHER",
                originalSharedPreferences.getString("SELECTED_PHOTOGRAPHER",
                        getResources().getString(R.string.none)));
        editor.putInt("PHOTO_HOURS",
                originalSharedPreferences.getInt("PHOTO_HOURS", 0));
        editor.putInt("PHOTO_PRICE",
                originalSharedPreferences.getInt("PHOTO_PRICE", 0));
        editor.putInt("PHOTOGRAPHER_TOTAL_PRICE",
                originalSharedPreferences.getInt("PHOTOGRAPHER_TOTAL_PRICE", 0));

        editor.putString("SELECTED_HOST",
                originalSharedPreferences.getString("SELECTED_HOST",
                        getResources().getString(R.string.none)));
        editor.putInt("HOST_PRICE",
                originalSharedPreferences.getInt("HOST_PRICE", 0));

        editor.putInt("TOTAL_SUM",
                originalSharedPreferences.getInt("TOTAL_SUM", 0));

        editor.apply();
    }

    private void updateOriginalSharedPreferences() {
        SharedPreferences.Editor editor = originalSharedPreferences.edit();

        editor.putString("SELECTED_CITY",
                updateSharedPreferences.getString("SELECTED_CITY", "_"));
        editor.putString("SELECTED_PLACE",
                updateSharedPreferences.getString("SELECTED_PLACE", "_"));
        editor.putString("SELECTED_LOCATION",
                updateSharedPreferences.getString("SELECTED_LOCATION", "_"));

        editor.putString("PARTNER_RESTAURANT",
                updateSharedPreferences.getString("PARTNER_RESTAURANT", "_"));
        PartnerRestaurantFragment.cachedRestaurants = new ArrayList<>();

        editor.putBoolean("manyRed",
                updateSharedPreferences.getBoolean("manyRed", false));
        editor.putBoolean("manyOrange",
                updateSharedPreferences.getBoolean("manyOrange", false));
        editor.putBoolean("manyYellow",
                updateSharedPreferences.getBoolean("manyYellow", false));
        editor.putBoolean("manyGreen",
                updateSharedPreferences.getBoolean("manyGreen", false));
        editor.putBoolean("manyBlue",
                updateSharedPreferences.getBoolean("manyBlue", false));
        editor.putBoolean("manyViolet",
                updateSharedPreferences.getBoolean("manyViolet", false));
        editor.putBoolean("manyGolden",
                updateSharedPreferences.getBoolean("manyGolden", false));
        editor.putBoolean("manySilver",
                updateSharedPreferences.getBoolean("manySilver", false));
        editor.putInt("manyTotalNumber",
                updateSharedPreferences.getInt("manyTotalNumber", 0));
        editor.putInt("manyTotalPrice",
                updateSharedPreferences.getInt("manyTotalPrice", 0));
        editor.putInt("MANY_NUMBER",
                updateSharedPreferences.getInt("MANY_NUMBER", 0));

        editor.putBoolean("heartRed",
                updateSharedPreferences.getBoolean("heartRed", false));
        editor.putBoolean("heartOrange",
                updateSharedPreferences.getBoolean("heartOrange", false));
        editor.putBoolean("heartYellow",
                updateSharedPreferences.getBoolean("heartYellow", false));
        editor.putBoolean("heartGreen",
                updateSharedPreferences.getBoolean("heartGreen", false));
        editor.putBoolean("heartBlue",
                updateSharedPreferences.getBoolean("heartBlue", false));
        editor.putBoolean("heartViolet",
                updateSharedPreferences.getBoolean("heartViolet", false));
        editor.putBoolean("heartGolden",
                updateSharedPreferences.getBoolean("heartGolden", false));
        editor.putBoolean("heartSilver",
                updateSharedPreferences.getBoolean("heartSilver", false));
        editor.putInt("heartTotalNumber",
                updateSharedPreferences.getInt("heartTotalNumber", 0));
        editor.putInt("heartTotalPrice",
                updateSharedPreferences.getInt("heartTotalPrice", 0));
        editor.putInt("HEART_NUMBER",
                updateSharedPreferences.getInt("HEART_NUMBER", 0));

        editor.putBoolean("archRed",
                updateSharedPreferences.getBoolean("archRed", false));
        editor.putBoolean("archOrange",
                updateSharedPreferences.getBoolean("archOrange", false));
        editor.putBoolean("archYellow",
                updateSharedPreferences.getBoolean("archYellow", false));
        editor.putBoolean("archGreen",
                updateSharedPreferences.getBoolean("archGreen", false));
        editor.putBoolean("archBlue",
                updateSharedPreferences.getBoolean("archBlue", false));
        editor.putBoolean("archViolet",
                updateSharedPreferences.getBoolean("archViolet", false));
        editor.putBoolean("archGolden",
                updateSharedPreferences.getBoolean("archGolden", false));
        editor.putBoolean("archSilver",
                updateSharedPreferences.getBoolean("archSilver", false));
        editor.putInt("archTotalNumber",
                updateSharedPreferences.getInt("archTotalNumber", 0));
        editor.putInt("archTotalPrice",
                updateSharedPreferences.getInt("archTotalPrice", 0));
        editor.putInt("ARCH_NUMBER",
                updateSharedPreferences.getInt("ARCH_NUMBER", 0));


        editor.putString("ACCESSORIES_COMMENT",
                updateSharedPreferences.getString("ACCESSORIES_COMMENT", "_"));

        editor.putInt("TOTAL_BALLOONS_SUM",
                updateSharedPreferences.getInt("TOTAL_BALLOONS_SUM", 0));

        editor.putBoolean("rosesRed",
                updateSharedPreferences.getBoolean("rosesRed", false));
        editor.putBoolean("rosesYellow",
                updateSharedPreferences.getBoolean("rosesYellow", false));
        editor.putBoolean("rosesPink",
                updateSharedPreferences.getBoolean("rosesPink", false));
        editor.putBoolean("rosesWhite",
                updateSharedPreferences.getBoolean("rosesWhite", false));
        editor.putInt("rosesTotalNumber",
                updateSharedPreferences.getInt("rosesTotalNumber", 0));
        editor.putInt("rosesTotalPrice",
                updateSharedPreferences.getInt("rosesTotalPrice", 0));
        editor.putInt("ROSES_NUMBER",
                updateSharedPreferences.getInt("ROSES_NUMBER", 0));

        editor.putBoolean("peoniesRed",
                updateSharedPreferences.getBoolean("peoniesRed", false));
        editor.putBoolean("peoniesYellow",
                updateSharedPreferences.getBoolean("peoniesYellow", false));
        editor.putBoolean("peoniesPink",
                updateSharedPreferences.getBoolean("peoniesPink", false));
        editor.putBoolean("peoniesWhite",
                updateSharedPreferences.getBoolean("peoniesWhite", false));
        editor.putInt("peoniesTotalNumber",
                updateSharedPreferences.getInt("peoniesTotalNumber", 0));
        editor.putInt("peoniesTotalPrice",
                updateSharedPreferences.getInt("peoniesTotalPrice", 0));
        editor.putInt("PEONIES_NUMBER",
                updateSharedPreferences.getInt("PEONIES_NUMBER", 0));

        editor.putBoolean("freesiasRed",
                updateSharedPreferences.getBoolean("freesiasRed", false));
        editor.putBoolean("freesiasYellow",
                updateSharedPreferences.getBoolean("freesiasYellow", false));
        editor.putBoolean("freesiasPink",
                updateSharedPreferences.getBoolean("freesiasPink", false));
        editor.putBoolean("freesiasWhite",
                updateSharedPreferences.getBoolean("freesiasWhite", false));
        editor.putInt("freesiasTotalNumber",
                updateSharedPreferences.getInt("freesiasTotalNumber", 0));
        editor.putInt("freesiasTotalPrice",
                updateSharedPreferences.getInt("freesiasTotalPrice", 0));
        editor.putInt("FREESIAS_NUMBER",
                updateSharedPreferences.getInt("FREESIAS_NUMBER", 0));

        editor.putString("FLOWERS_COMMENT",
                updateSharedPreferences.getString("FLOWERS_COMMENT", "_"));

        editor.putInt("TOTAL_FLOWERS_SUM",
                updateSharedPreferences.getInt("TOTAL_FLOWERS_SUM", 0));

        editor.putString("SELECTED_DJ",
                updateSharedPreferences.getString("SELECTED_DJ",
                        getResources().getString(R.string.none)));
        editor.putInt("DJ_PRICE", updateSharedPreferences.getInt("DJ_PRICE", 0));

        editor.putString("SELECTED_PHOTOGRAPHER",
                updateSharedPreferences.getString("SELECTED_PHOTOGRAPHER",
                        getResources().getString(R.string.none)));
        editor.putInt("PHOTO_HOURS",
                updateSharedPreferences.getInt("PHOTO_HOURS", 0));
        editor.putInt("PHOTO_PRICE",
                updateSharedPreferences.getInt("PHOTO_PRICE", 0));
        editor.putInt("PHOTOGRAPHER_TOTAL_PRICE",
                updateSharedPreferences.getInt("PHOTOGRAPHER_TOTAL_PRICE", 0));

        editor.putString("SELECTED_HOST",
                updateSharedPreferences.getString("SELECTED_HOST",
                        getResources().getString(R.string.none)));
        editor.putInt("HOST_PRICE",
                updateSharedPreferences.getInt("HOST_PRICE", 0));

        editor.putInt("TOTAL_SUM",
                updateSharedPreferences.getInt("TOTAL_SUM", 0));

        editor.apply();
    }
}

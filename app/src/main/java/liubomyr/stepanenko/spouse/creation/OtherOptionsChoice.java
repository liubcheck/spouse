package liubomyr.stepanenko.spouse.creation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import java.util.ArrayList;
import liubomyr.stepanenko.spouse.R;
import liubomyr.stepanenko.spouse.fragment.AccessoriesChoiceFragment;
import liubomyr.stepanenko.spouse.handler.BackPressHandler;
import liubomyr.stepanenko.spouse.fragment.CompleteOrderFragment;
import liubomyr.stepanenko.spouse.fragment.DjChoiceFragment;
import liubomyr.stepanenko.spouse.fragment.FlowersChoiceFragment;
import liubomyr.stepanenko.spouse.fragment.HostChoiceFragment;
import liubomyr.stepanenko.spouse.fragment.PartnerRestaurantFragment;
import liubomyr.stepanenko.spouse.fragment.PhotographerChoiceFragment;
import liubomyr.stepanenko.spouse.fragment.WarningFragment;
import liubomyr.stepanenko.spouse.model.Location;

public class OtherOptionsChoice extends AppCompatActivity {
    private WarningFragment warningFragment;
    private PartnerRestaurantFragment partnerRestaurantFragment;
    private AccessoriesChoiceFragment accessoriesChoiceFragment;
    private FlowersChoiceFragment flowersChoiceFragment;
    private DjChoiceFragment djChoiceFragment;
    private PhotographerChoiceFragment photographerChoiceFragment;
    private HostChoiceFragment hostChoiceFragment;
    private CompleteOrderFragment completeOrderFragment;
    private Button completeOrderButton;
    private TextView totalPriceTextView;
    private View overlay;
    private SharedPreferences sharedPreferences;
    private boolean fragmentOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_other_options_choice);

        overlay = findViewById(R.id.overlay);

        sharedPreferences =
                getSharedPreferences("WEDDING_DATA", MODE_PRIVATE);
        String locationJson =
                sharedPreferences.getString("SELECTED_LOCATION", "");
        Location location = new Gson().fromJson(locationJson, Location.class);

        String place = sharedPreferences.getString("SELECTED_PLACE", "");

        warningFragment = new WarningFragment();
        partnerRestaurantFragment = new PartnerRestaurantFragment();
        accessoriesChoiceFragment = new AccessoriesChoiceFragment(sharedPreferences);
        flowersChoiceFragment = new FlowersChoiceFragment(sharedPreferences);
        djChoiceFragment = new DjChoiceFragment(sharedPreferences);
        photographerChoiceFragment = new PhotographerChoiceFragment(sharedPreferences);
        hostChoiceFragment = new HostChoiceFragment(sharedPreferences);
        completeOrderFragment = new CompleteOrderFragment();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        setUpValues(editor);

        TextView chosenLocationTextView = findViewById(R.id.chosenLocation_textView);
        chosenLocationTextView.setText(chosenLocationTextView.getText()
                .toString().replace("location",
                        location.getName() + ", " + location.getCity()));

        ImageView dishesImageView = findViewById(R.id.dishes_imageView);
        dishesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (place.equals("ресторан")
                        || place.equals("заміський комплекс")
                        || place.equals("інше")) {
                    openFragment(warningFragment);
                } else {
                    openFragment(partnerRestaurantFragment);
                }
            }
        });

        ImageView accessoriesImageView =
                findViewById(R.id.accessories_imageView);
        accessoriesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(accessoriesChoiceFragment);
            }
        });

        ImageView flowersImageView = findViewById(R.id.flowers_imageView);
        flowersImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(flowersChoiceFragment);
            }
        });

        ImageView photographersImageView = findViewById(R.id.photographers_imageView);
        photographersImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(photographerChoiceFragment);
            }
        });

        ImageView djsImageView = findViewById(R.id.djs_imageView);
        djsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(djChoiceFragment);
            }
        });

        ImageView hostsImageView = findViewById(R.id.hosts_imageView);
        hostsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(hostChoiceFragment);
            }
        });

        totalPriceTextView = findViewById(R.id.totalEstimatedPrice_textView);
        editor = sharedPreferences.edit();
        editor.putInt("TOTAL_SUM", 0);
        editor.apply();
        totalPriceTextView.setText(getString(R.string.total_estimated_price_1_d,
                sharedPreferences.getInt("TOTAL_SUM", 0)));

        completeOrderButton = findViewById(R.id.completeOrder_button);
        completeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(completeOrderFragment);
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
            Intent intent = new Intent(this, ExactLocationChoice.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    public void hideOverlay() {
        if (overlay != null) {
            totalPriceTextView.setText(getString(R.string.total_estimated_price_1_d,
                    sharedPreferences.getInt("TOTAL_SUM", 0)));
            overlay.setVisibility(View.GONE);
            overlay.setClickable(false);
            completeOrderButton.setVisibility(View.VISIBLE);
        }
        fragmentOpen = false;
    }

    private void openFragment(Fragment fragment) {
        overlay.setVisibility(View.VISIBLE);
        overlay.setClickable(true);
        completeOrderButton.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.overlay, fragment)
                .addToBackStack(null)
                .commit();
        fragmentOpen = true;
    }

    private void setUpValues(SharedPreferences.Editor editor) {
        editor.putString("PARTNER_RESTAURANT", "_");
        PartnerRestaurantFragment.cachedRestaurants = new ArrayList<>();

        editor.putBoolean("manyRed", false);
        editor.putBoolean("manyOrange", false);
        editor.putBoolean("manyYellow", false);
        editor.putBoolean("manyGreen", false);
        editor.putBoolean("manyBlue", false);
        editor.putBoolean("manyViolet", false);
        editor.putBoolean("manyGolden", false);
        editor.putBoolean("manySilver", false);
        editor.putInt("manyTotalNumber", 0);
        editor.putInt("manyTotalPrice", 0);
        editor.putInt("MANY_NUMBER", 0);

        editor.putBoolean("heartRed", false);
        editor.putBoolean("heartOrange", false);
        editor.putBoolean("heartYellow", false);
        editor.putBoolean("heartGreen", false);
        editor.putBoolean("heartBlue", false);
        editor.putBoolean("heartViolet", false);
        editor.putBoolean("heartGolden", false);
        editor.putBoolean("heartSilver", false);
        editor.putInt("heartTotalNumber", 0);
        editor.putInt("heartTotalPrice", 0);
        editor.putInt("HEART_NUMBER", 0);

        editor.putBoolean("archRed", false);
        editor.putBoolean("archOrange", false);
        editor.putBoolean("archYellow", false);
        editor.putBoolean("archGreen", false);
        editor.putBoolean("archBlue", false);
        editor.putBoolean("archViolet", false);
        editor.putBoolean("archGolden", false);
        editor.putBoolean("archSilver", false);
        editor.putInt("archTotalNumber", 0);
        editor.putInt("archTotalPrice", 0);
        editor.putInt("ARCH_NUMBER", 0);

        editor.putString("ACCESSORIES_COMMENT", "");

        editor.putInt("TOTAL_BALLOONS_SUM", 0);

        editor.putBoolean("rosesRed", false);
        editor.putBoolean("rosesYellow", false);
        editor.putBoolean("rosesPink", false);
        editor.putBoolean("rosesWhite", false);
        editor.putInt("rosesTotalNumber", 0);
        editor.putInt("rosesTotalPrice", 0);
        editor.putInt("ROSES_NUMBER", 0);

        editor.putBoolean("peoniesRed", false);
        editor.putBoolean("peoniesYellow", false);
        editor.putBoolean("peoniesPink", false);
        editor.putBoolean("peoniesWhite", false);
        editor.putInt("peoniesTotalNumber", 0);
        editor.putInt("peoniesTotalPrice", 0);
        editor.putInt("PEONIES_NUMBER", 0);

        editor.putBoolean("freesiasRed", false);
        editor.putBoolean("freesiasYellow", false);
        editor.putBoolean("freesiasPink", false);
        editor.putBoolean("freesiasWhite", false);
        editor.putInt("freesiasTotalNumber", 0);
        editor.putInt("freesiasTotalPrice", 0);
        editor.putInt("FREESIAS_NUMBER", 0);

        editor.putString("FLOWERS_COMMENT", "");

        editor.putInt("TOTAL_FLOWERS_SUM", 0);

        editor.putString("SELECTED_DJ", getResources().getString(R.string.none));
        editor.putInt("DJ_PRICE", 0);

        editor.putString("SELECTED_PHOTOGRAPHER", getResources().getString(R.string.none));
        editor.putInt("PHOTO_HOURS", 0);
        editor.putInt("PHOTO_PRICE", 0);
        editor.putInt("PHOTOGRAPHER_TOTAL_PRICE", 0);

        editor.putString("SELECTED_HOST", getResources().getString(R.string.none));
        editor.putInt("HOST_PRICE", 0);

        editor.apply();
    }
}

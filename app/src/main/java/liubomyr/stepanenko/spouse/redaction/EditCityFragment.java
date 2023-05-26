package liubomyr.stepanenko.spouse.redaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import liubomyr.stepanenko.spouse.R;
import liubomyr.stepanenko.spouse.handler.BackPressHandler;

public class EditCityFragment extends Fragment implements BackPressHandler {
    private SharedPreferences updatedSharedPreferences;
    private EditPlaceFragment editPlaceFragment;

    public EditCityFragment() {
    }

    @Override
    public boolean handleBackPress() {
        if (getActivity() instanceof EditOrder) {
            ((EditOrder) getActivity()).hideOverlay();
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
        return inflater.inflate(R.layout.fragment_edit_city,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        updatedSharedPreferences = requireActivity()
                .getSharedPreferences("UPDATE_LOCATION_DATA", Context.MODE_PRIVATE);

        editPlaceFragment = new EditPlaceFragment();

        ImageView kyivImageView = view.findViewById(R.id.kyiv_imageEdit);
        kyivImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlaceFragment("Київ");
            }
        });

        ImageView lvivImageView = view.findViewById(R.id.lviv_imageEdit);
        lvivImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlaceFragment("Львів");
            }
        });

        ImageView kharkivImageView = view.findViewById(R.id.kharkiv_imageEdit);
        kharkivImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlaceFragment("Харків");
            }
        });

        ImageView odesaImageView = view.findViewById(R.id.odesa_imageEdit);
        odesaImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlaceFragment("Одеса");
            }
        });

        ImageView dniproImageView = view.findViewById(R.id.dnipro_imageEdit);
        dniproImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlaceFragment("Дніпро");
            }
        });
    }

    private void openPlaceFragment(String selectedCity) {
        SharedPreferences.Editor editor = updatedSharedPreferences.edit();
        editor.putString("SELECTED_CITY", selectedCity);
        editor.apply();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.overlay, editPlaceFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
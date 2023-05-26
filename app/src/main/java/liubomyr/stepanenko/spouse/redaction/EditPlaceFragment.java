package liubomyr.stepanenko.spouse.redaction;

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
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

import liubomyr.stepanenko.spouse.R;
import liubomyr.stepanenko.spouse.handler.BackPressHandler;

public class EditPlaceFragment extends Fragment implements BackPressHandler {
    private SharedPreferences updateSharedPreferences;
    private EditExactLocationFragment editExactLocationFragment;

    public EditPlaceFragment() {
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
        return inflater.inflate(R.layout.fragment_edit_place,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        updateSharedPreferences = requireActivity()
                .getSharedPreferences("UPDATE_LOCATION_DATA", MODE_PRIVATE);

        editExactLocationFragment = new EditExactLocationFragment();

        TextView chosenCityTextView = view.findViewById(R.id.chosenCity_textViewEdit);
        String city = updateSharedPreferences.getString("SELECTED_CITY", "");
        chosenCityTextView.setText(chosenCityTextView.getText()
                .toString().replace("city", city));

        ImageView restaurantImageView = view.findViewById(R.id.restaurant_imageViewEdit);
        restaurantImageView.setImageResource(R.drawable.restaurant);
        restaurantImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlaceAndContinue("ресторан");
            }
        });

        ImageView countrysideComplexImageView =
                view.findViewById(R.id.countrysideComplex_imageViewEdit);
        countrysideComplexImageView
                .setImageResource(R.drawable.countryside_complex);
        countrysideComplexImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlaceAndContinue("заміський комплекс");
            }
        });

        ImageView parkImageView = view.findViewById(R.id.park_imageViewEdit);
        parkImageView.setImageResource(R.drawable.park);
        parkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlaceAndContinue("парк");
            }
        });

        ImageView conferenceHallImageView = view.findViewById(R.id.conferenceHall_imageViewEdit);
        conferenceHallImageView.setImageResource(R.drawable.conference_hall);
        conferenceHallImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlaceAndContinue("конференц-зал");
            }
        });

        ImageView otherImageView = view.findViewById(R.id.other_imageViewEdit);
        otherImageView.setImageResource(R.drawable.restaurant);
        otherImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlaceAndContinue("інше");
            }
        });
    }

    private void setPlaceAndContinue(String place) {
        SharedPreferences.Editor editor = updateSharedPreferences.edit();
        editor.putString("SELECTED_PLACE", place);
        editor.apply();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.overlay, editExactLocationFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
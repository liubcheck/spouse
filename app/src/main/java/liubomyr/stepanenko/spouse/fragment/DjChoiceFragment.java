package liubomyr.stepanenko.spouse.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import liubomyr.stepanenko.spouse.redaction.EditOrder;
import liubomyr.stepanenko.spouse.R;
import liubomyr.stepanenko.spouse.creation.OtherOptionsChoice;
import liubomyr.stepanenko.spouse.handler.BackPressHandler;

public class DjChoiceFragment extends Fragment implements BackPressHandler {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RadioButton dj1RadioButton;
    private RadioButton dj2RadioButton;
    private RadioButton dj3RadioButton;
    private RadioButton noneRadioButton;
    private int price = 0;

    public DjChoiceFragment(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public boolean handleBackPress() {
        int previousDjPrice = sharedPreferences.getInt("DJ_PRICE", 0);
        editor.putInt("DJ_PRICE", price);
        editor.putInt("TOTAL_SUM",
                sharedPreferences.getInt("TOTAL_SUM", 0)
                        - previousDjPrice + price);
        editor.apply();
        if (getActivity() instanceof OtherOptionsChoice) {
            ((OtherOptionsChoice) getActivity()).hideOverlay();
        }
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
        return inflater.inflate(R.layout.fragment_dj_choice,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editor = sharedPreferences.edit();

        dj1RadioButton = view.findViewById(R.id.dj1_radioButton);
        if (dj1RadioButton.getText().toString()
                .equals(sharedPreferences.getString("SELECTED_DJ", "None"))) {
            dj1RadioButton.setChecked(true);
        }
        dj1RadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dj2RadioButton.setChecked(false);
                dj3RadioButton.setChecked(false);
                noneRadioButton.setChecked(false);
                price = 5500;
                editor.putInt("DJ_PRICE", price);
                editor.putString("SELECTED_DJ",
                        getResources().getString(R.string.maverick));
            }
        });

        dj2RadioButton = view.findViewById(R.id.dj2_radioButton);
        if (dj2RadioButton.getText().toString()
                .equals(sharedPreferences.getString("SELECTED_DJ", "None"))) {
            dj2RadioButton.setChecked(true);
        }
        dj2RadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dj1RadioButton.setChecked(false);
                dj3RadioButton.setChecked(false);
                noneRadioButton.setChecked(false);
                price = 4500;
                editor.putInt("DJ_PRICE", price);
                editor.putString("SELECTED_DJ",
                        getResources().getString(R.string.dj_anna));
            }
        });

        dj3RadioButton = view.findViewById(R.id.dj3_radioButton);
        if (dj3RadioButton.getText().toString()
                .equals(sharedPreferences.getString("SELECTED_DJ", "None"))) {
            dj3RadioButton.setChecked(true);
        }
        dj3RadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dj1RadioButton.setChecked(false);
                dj2RadioButton.setChecked(false);
                noneRadioButton.setChecked(false);
                price = 4000;
                editor.putInt("DJ_PRICE", price);
                editor.putString("SELECTED_DJ",
                        getResources().getString(R.string.sasha_topchik));
            }
        });

        noneRadioButton = view.findViewById(R.id.noDj_radioButton);
        if (!dj1RadioButton.isChecked()
                && !dj2RadioButton.isChecked()
                && !dj3RadioButton.isChecked()) {
            noneRadioButton.setChecked(true);
        }
        noneRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dj1RadioButton.setChecked(false);
                dj2RadioButton.setChecked(false);
                dj3RadioButton.setChecked(false);
                price = 0;
                editor.putInt("DJ_PRICE", price);
                editor.putString("SELECTED_DJ",
                        getResources().getString(R.string.none));
            }
        });
    }
}

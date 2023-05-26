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

public class HostChoiceFragment extends Fragment implements BackPressHandler {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RadioButton host1RadioButton;
    private RadioButton host2RadioButton;
    private RadioButton host3RadioButton;
    private RadioButton noneRadioButton;
    int price = 0;

    public HostChoiceFragment(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public boolean handleBackPress() {
        int previousHostPrice = sharedPreferences.getInt("HOST_PRICE", 0);
        editor.putInt("HOST_PRICE", price);
        editor.putInt("TOTAL_SUM",
                sharedPreferences.getInt("TOTAL_SUM", 0)
                        - previousHostPrice + price);
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
        return inflater.inflate(R.layout.fragment_host_choice,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editor = sharedPreferences.edit();

        host1RadioButton = view.findViewById(R.id.host1_radioButton);
        if (host1RadioButton.getText().toString()
                .equals(sharedPreferences.getString("SELECTED_HOST", "None"))) {
            host1RadioButton.setChecked(true);
        }
        host1RadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host2RadioButton.setChecked(false);
                host3RadioButton.setChecked(false);
                noneRadioButton.setChecked(false);
                price = 8000;
                editor.putString("SELECTED_HOST",
                        getResources().getString(R.string.oleksii));
            }
        });

        host2RadioButton = view.findViewById(R.id.host2_radioButton);
        if (host2RadioButton.getText().toString()
                .equals(sharedPreferences.getString("SELECTED_HOST", "None"))) {
            host2RadioButton.setChecked(true);
        }
        host2RadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host1RadioButton.setChecked(false);
                host3RadioButton.setChecked(false);
                noneRadioButton.setChecked(false);
                price = 8500;
                editor.putString("SELECTED_HOST",
                        getResources().getString(R.string.iryna));
            }
        });

        host3RadioButton = view.findViewById(R.id.host3_radioButton);
        if (host3RadioButton.getText().toString()
                .equals(sharedPreferences.getString("SELECTED_HOST", "None"))) {
            host3RadioButton.setChecked(true);
        }
        host3RadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host1RadioButton.setChecked(false);
                host2RadioButton.setChecked(false);
                noneRadioButton.setChecked(false);
                price = 10000;
                editor.putString("SELECTED_HOST",
                        getResources().getString(R.string.illia));
            }
        });

        noneRadioButton = view.findViewById(R.id.noHost_radioButton);
        if (!host1RadioButton.isChecked()
                && !host2RadioButton.isChecked()
                && !host3RadioButton.isChecked()) {
            noneRadioButton.setChecked(true);
        }
        noneRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host1RadioButton.setChecked(false);
                host2RadioButton.setChecked(false);
                host3RadioButton.setChecked(false);
                price = 0;
                editor.putString("SELECTED_HOST",
                        getResources().getString(R.string.none));
            }
        });
    }
}

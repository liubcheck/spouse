package liubomyr.stepanenko.spouse.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import liubomyr.stepanenko.spouse.redaction.EditOrder;
import liubomyr.stepanenko.spouse.R;
import liubomyr.stepanenko.spouse.creation.OtherOptionsChoice;
import liubomyr.stepanenko.spouse.handler.BackPressHandler;

public class PhotographerChoiceFragment extends Fragment implements BackPressHandler {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RadioButton photo1RadioButton;
    private RadioButton photo2RadioButton;
    private RadioButton photo3RadioButton;
    private RadioButton noneRadioButton;
    private TextView photoPriceTextView;
    private SeekBar photoSeekBar;
    private int price = 0;

    public PhotographerChoiceFragment(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public boolean handleBackPress() {
        int currentTotalPrice = getTotalPrice();
        int previousTotalPrice =
                sharedPreferences.getInt("PHOTOGRAPHER_TOTAL_PRICE", 0);
        editor.putInt("PHOTOGRAPHER_TOTAL_PRICE", currentTotalPrice);
        if (price != 0) {
            editor.putInt("PHOTO_HOURS", currentTotalPrice / price);
            editor.putInt("PHOTO_PRICE", price);
        }
        editor.putInt("TOTAL_SUM",
                sharedPreferences.getInt("TOTAL_SUM", 0)
                        - previousTotalPrice + currentTotalPrice);
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
        return inflater.inflate(R.layout.fragment_photographer_choice,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editor = sharedPreferences.edit();

        photoPriceTextView = view.findViewById(R.id.photographerPrice);
        photoPriceTextView.setText(getString(R.string.hours_0_price_0_uah,
                0, 0));

        photoSeekBar = view.findViewById(R.id.photographerPriceSeekBar);
        photoSeekBar.setMin(1);
        photoSeekBar.setMax(10);
        photoSeekBar.setProgress(sharedPreferences.getInt("PHOTO_HOURS", 0));

        price = sharedPreferences.getInt("PHOTO_PRICE", 0);

        photoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!noneRadioButton.isChecked()) {
                    int totalPrice = progress * price;
                    photoPriceTextView.setText(
                            getString(R.string.hours_0_price_0_uah, progress, totalPrice)
                    );
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        photo1RadioButton = view.findViewById(R.id.photo1_radioButton);
        if (photo1RadioButton.getText().toString()
                .equals(sharedPreferences.getString("SELECTED_PHOTOGRAPHER", "None"))) {
            photo1RadioButton.setChecked(true);
        }
        photo1RadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo2RadioButton.setChecked(false);
                photo3RadioButton.setChecked(false);
                noneRadioButton.setChecked(false);
                price = 500;
                photoSeekBar.setEnabled(true);
                int hours = photoSeekBar.getProgress();
                photoPriceTextView.setText(getString(R.string.hours_0_price_0_uah,
                        hours, hours * price));
                editor.putString("SELECTED_PHOTOGRAPHER",
                        getResources().getString(R.string.valeriia));
            }
        });

        photo2RadioButton = view.findViewById(R.id.photo2_radioButton);
        if (photo2RadioButton.getText().toString()
                .equals(sharedPreferences.getString("SELECTED_PHOTOGRAPHER", "None"))) {
            photo2RadioButton.setChecked(true);
        }
        photo2RadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo1RadioButton.setChecked(false);
                photo3RadioButton.setChecked(false);
                noneRadioButton.setChecked(false);
                price = 850;
                photoSeekBar.setEnabled(true);
                int hours = photoSeekBar.getProgress();
                photoPriceTextView.setText(getString(R.string.hours_0_price_0_uah,
                        hours, hours * price));
                editor.putString("SELECTED_PHOTOGRAPHER",
                        getResources().getString(R.string.dmytro));
            }
        });

        photo3RadioButton = view.findViewById(R.id.photo3_radioButton);
        if (photo3RadioButton.getText().toString()
                .equals(sharedPreferences.getString("SELECTED_PHOTOGRAPHER", "None"))) {
            photo3RadioButton.setChecked(true);
        }
        photo3RadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo1RadioButton.setChecked(false);
                photo2RadioButton.setChecked(false);
                noneRadioButton.setChecked(false);
                price = 750;
                photoSeekBar.setEnabled(true);
                int hours = photoSeekBar.getProgress();
                photoPriceTextView.setText(getString(R.string.hours_0_price_0_uah,
                        hours, hours * price));
                editor.putString("SELECTED_PHOTOGRAPHER",
                        getResources().getString(R.string.vlad));
            }
        });

        noneRadioButton = view.findViewById(R.id.noPhotographer_radioButton);
        if (!photo1RadioButton.isChecked()
                && !photo2RadioButton.isChecked()
                && !photo3RadioButton.isChecked()) {
            noneRadioButton.setChecked(true);
            photoSeekBar.setEnabled(false);
        }
        noneRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo1RadioButton.setChecked(false);
                photo2RadioButton.setChecked(false);
                photo3RadioButton.setChecked(false);
                price = 0;
                photoPriceTextView.setText(getString(R.string.hours_0_price_0_uah,
                        0, 0));
                photoSeekBar.setEnabled(false);
                photoSeekBar.setProgress(0);
                editor.putString("SELECTED_PHOTOGRAPHER",
                        getResources().getString(R.string.none));
            }
        });

        photoSeekBar.setClickable(!noneRadioButton.isChecked());

        setValues();
    }

    private int getTotalPrice() {
        return photoSeekBar.getProgress() * price;
    }

    private void setValues() {
        int totalPrice = sharedPreferences.getInt("PHOTOGRAPHER_TOTAL_PRICE", 0);
        photoSeekBar.setProgress(price != 0 ? (totalPrice / price) : price);
        photoPriceTextView.setText(getString(R.string.hours_0_price_0_uah,
                price != 0 ? (totalPrice / price) : price, totalPrice));
    }
}

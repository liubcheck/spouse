package liubomyr.stepanenko.spouse.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Arrays;
import java.util.List;
import liubomyr.stepanenko.spouse.R;
import liubomyr.stepanenko.spouse.handler.BackPressHandler;

public class BalloonsOptionsFragment extends Fragment implements BackPressHandler {
    private SharedPreferences sharedPreferences;
    private final ImageView imageView;
    private final int index;
    private final int price;
    private CheckBox redCheckBox;
    private CheckBox orangeCheckBox;
    private CheckBox yellowCheckBox;
    private CheckBox greenCheckBox;
    private CheckBox blueCheckBox;
    private CheckBox violetCheckBox;
    private CheckBox goldenCheckBox;
    private CheckBox silverCheckBox;
    private TextView amountPriceTextView;
    private SeekBar balloonSeekBar;
    private List<CheckBox> checkBoxes;
    private int totalCheckedBoxes;

    public BalloonsOptionsFragment(ImageView imageView, int index,
                                   SharedPreferences sharedPreferences) {
        this.imageView = imageView;
        this.index = index;
        this.price = index == 1 ? 20 : index == 2 ? 750 : 1000;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public boolean handleBackPress() {
        saveChanges();
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
        return inflater.inflate(R.layout.fragment_balloons_options,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView locationImageView = view.findViewById(R.id.balloonsImageView);
        locationImageView.setImageDrawable(imageView.getDrawable());

        amountPriceTextView = view.findViewById(R.id.balloonsAmountAndPrice);
        amountPriceTextView.setText(getString(R.string.amount_0_price_0_uah,
                0, 0));

        redCheckBox = view.findViewById(R.id.redCheckBox);
        orangeCheckBox = view.findViewById(R.id.orangeCheckBox);
        yellowCheckBox = view.findViewById(R.id.yellowCheckBox);
        greenCheckBox = view.findViewById(R.id.greenCheckBox);
        blueCheckBox = view.findViewById(R.id.blueCheckBox);
        violetCheckBox = view.findViewById(R.id.violetCheckBox);
        goldenCheckBox = view.findViewById(R.id.goldenCheckBox);
        silverCheckBox = view.findViewById(R.id.silverCheckBox);

        checkBoxes = Arrays.asList(
                redCheckBox, orangeCheckBox, yellowCheckBox,
                greenCheckBox, blueCheckBox, violetCheckBox,
                goldenCheckBox, silverCheckBox
        );

        balloonSeekBar = view.findViewById(R.id.amountPriceSeekBar);
        balloonSeekBar.setMin(0);
        balloonSeekBar.setMax(index == 1 ? 100 : 10);
        balloonSeekBar.setEnabled(totalCheckedBoxes > 0);
        balloonSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (index == 1 && progress < totalCheckedBoxes) {
                    balloonSeekBar.setProgress(totalCheckedBoxes);
                } else {
                    int totalPrice = progress * price;
                    amountPriceTextView.setText(
                            getString(R.string.amount_0_price_0_uah, progress, totalPrice)
                    );
                    updateSeekBarAvailability();
                }
                updateSeekBarAvailability();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        for (CheckBox checkBox : checkBoxes) {
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    totalCheckedBoxes++;
                } else {
                    totalCheckedBoxes--;
                }
                updateSeekBarProgress();
                updateSeekBarAvailability();
                if (index == 1 && balloonSeekBar.getProgress() < totalCheckedBoxes) {
                    balloonSeekBar.setProgress(totalCheckedBoxes);
                }
            });
        }

        setValues();
        updateSeekBarProgress();

        Button resetButton = view.findViewById(R.id.resetBalloonsButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CheckBox checkBox : checkBoxes) {
                    checkBox.setChecked(false);
                }
                balloonSeekBar.setMin(0);
                balloonSeekBar.setProgress(0);
                totalCheckedBoxes = 0;
                updateSeekBarAvailability();
                saveChanges();
            }
        });
    }

    private int getTotalPrice() {
        return balloonSeekBar.getProgress() * price;
    }

    private void setValues() {
        int totalPrice;
        switch (index) {
            case 1:
                redCheckBox.setChecked(sharedPreferences.getBoolean("manyRed", false));
                orangeCheckBox.setChecked(sharedPreferences.getBoolean("manyOrange", false));
                yellowCheckBox.setChecked(sharedPreferences.getBoolean("manyYellow", false));
                greenCheckBox.setChecked(sharedPreferences.getBoolean("manyGreen", false));
                blueCheckBox.setChecked(sharedPreferences.getBoolean("manyBlue", false));
                violetCheckBox.setChecked(sharedPreferences.getBoolean("manyViolet", false));
                goldenCheckBox.setChecked(sharedPreferences.getBoolean("manyGolden", false));
                silverCheckBox.setChecked(sharedPreferences.getBoolean("manySilver", false));
                totalPrice = sharedPreferences.getInt("manyTotalPrice", 0);
                balloonSeekBar.setProgress(totalPrice / price);
                amountPriceTextView.setText(getString(R.string.amount_0_price_0_uah,
                        totalPrice / price, totalPrice));
                break;
            case 2:
                redCheckBox.setChecked(sharedPreferences.getBoolean("heartRed", false));
                orangeCheckBox.setChecked(sharedPreferences.getBoolean("heartOrange", false));
                yellowCheckBox.setChecked(sharedPreferences.getBoolean("heartYellow", false));
                greenCheckBox.setChecked(sharedPreferences.getBoolean("heartGreen", false));
                blueCheckBox.setChecked(sharedPreferences.getBoolean("heartBlue", false));
                violetCheckBox.setChecked(sharedPreferences.getBoolean("heartViolet", false));
                goldenCheckBox.setChecked(sharedPreferences.getBoolean("heartGolden", false));
                silverCheckBox.setChecked(sharedPreferences.getBoolean("heartSilver", false));
                totalPrice = sharedPreferences.getInt("heartTotalPrice", 0);
                balloonSeekBar.setProgress(totalPrice / price);
                amountPriceTextView.setText(getString(R.string.amount_0_price_0_uah,
                        totalPrice / price, totalPrice));
                break;
            case 3:
                redCheckBox.setChecked(sharedPreferences.getBoolean("archRed", false));
                orangeCheckBox.setChecked(sharedPreferences.getBoolean("archOrange", false));
                yellowCheckBox.setChecked(sharedPreferences.getBoolean("archYellow", false));
                greenCheckBox.setChecked(sharedPreferences.getBoolean("archGreen", false));
                blueCheckBox.setChecked(sharedPreferences.getBoolean("archBlue", false));
                violetCheckBox.setChecked(sharedPreferences.getBoolean("archViolet", false));
                goldenCheckBox.setChecked(sharedPreferences.getBoolean("archGolden", false));
                silverCheckBox.setChecked(sharedPreferences.getBoolean("archSilver", false));
                totalPrice = sharedPreferences.getInt("archTotalPrice", 0);
                balloonSeekBar.setProgress(totalPrice / price);
                amountPriceTextView.setText(getString(R.string.amount_0_price_0_uah,
                        totalPrice / price, totalPrice));
                break;
        }
    }

    private void saveChanges() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int previousTotalPrice;
        int currentTotalPrice = getTotalPrice();
        switch (index) {
            case 1:
                editor.putBoolean("manyRed", redCheckBox.isChecked());
                editor.putBoolean("manyOrange", orangeCheckBox.isChecked());
                editor.putBoolean("manyYellow", yellowCheckBox.isChecked());
                editor.putBoolean("manyGreen", greenCheckBox.isChecked());
                editor.putBoolean("manyBlue", blueCheckBox.isChecked());
                editor.putBoolean("manyViolet", violetCheckBox.isChecked());
                editor.putBoolean("manyGolden", goldenCheckBox.isChecked());
                editor.putBoolean("manySilver", silverCheckBox.isChecked());
                previousTotalPrice = sharedPreferences.getInt("manyTotalPrice", 0);
                editor.putInt("manyTotalPrice", currentTotalPrice);
                editor.putInt("MANY_NUMBER", balloonSeekBar.getProgress());
                editor.putInt("TOTAL_BALLOONS_SUM",
                        sharedPreferences.getInt("TOTAL_BALLOONS_SUM", 0)
                                - previousTotalPrice + currentTotalPrice);
                editor.putInt("TOTAL_SUM",
                        sharedPreferences.getInt("TOTAL_SUM", 0)
                                - previousTotalPrice + currentTotalPrice);
                break;
            case 2:
                editor.putBoolean("heartRed", redCheckBox.isChecked());
                editor.putBoolean("heartOrange", orangeCheckBox.isChecked());
                editor.putBoolean("heartYellow", yellowCheckBox.isChecked());
                editor.putBoolean("heartGreen", greenCheckBox.isChecked());
                editor.putBoolean("heartBlue", blueCheckBox.isChecked());
                editor.putBoolean("heartViolet", violetCheckBox.isChecked());
                editor.putBoolean("heartGolden", goldenCheckBox.isChecked());
                editor.putBoolean("heartSilver", silverCheckBox.isChecked());
                previousTotalPrice = sharedPreferences.getInt("heartTotalPrice", 0);
                editor.putInt("heartTotalPrice", currentTotalPrice);
                editor.putInt("HEART_NUMBER", balloonSeekBar.getProgress());
                editor.putInt("TOTAL_BALLOONS_SUM",
                        sharedPreferences.getInt("TOTAL_BALLOONS_SUM", 0)
                                - previousTotalPrice + currentTotalPrice);
                editor.putInt("TOTAL_SUM",
                        sharedPreferences.getInt("TOTAL_SUM", 0)
                                - previousTotalPrice + currentTotalPrice);
                break;
            case 3:
                editor.putBoolean("archRed", redCheckBox.isChecked());
                editor.putBoolean("archOrange", orangeCheckBox.isChecked());
                editor.putBoolean("archYellow", yellowCheckBox.isChecked());
                editor.putBoolean("archGreen", greenCheckBox.isChecked());
                editor.putBoolean("archBlue", blueCheckBox.isChecked());
                editor.putBoolean("archViolet", violetCheckBox.isChecked());
                editor.putBoolean("archGolden", goldenCheckBox.isChecked());
                editor.putBoolean("archSilver", silverCheckBox.isChecked());
                previousTotalPrice = sharedPreferences.getInt("archTotalPrice", 0);
                editor.putInt("archTotalPrice", currentTotalPrice);
                editor.putInt("ARCH_NUMBER", balloonSeekBar.getProgress());
                editor.putInt("TOTAL_BALLOONS_SUM",
                        sharedPreferences.getInt("TOTAL_BALLOONS_SUM", 0)
                                - previousTotalPrice + currentTotalPrice);
                editor.putInt("TOTAL_SUM",
                        sharedPreferences.getInt("TOTAL_SUM", 0)
                                - previousTotalPrice + currentTotalPrice);
                break;
        }
        editor.apply();
    }

    private void updateSeekBarAvailability() {
        if (totalCheckedBoxes > 0) {
            balloonSeekBar.setEnabled(true);
            if (index != 1) {
                balloonSeekBar.setMin(1);
            }
        } else {
            balloonSeekBar.setEnabled(false);
        }
    }

    private void updateSeekBarProgress() {
        if (index == 1) {
            if (totalCheckedBoxes == 0) {
                balloonSeekBar.setProgress(0);
            }
        } else {
            if (totalCheckedBoxes == 0) {
                balloonSeekBar.setMin(0);
                balloonSeekBar.setProgress(0);
            }
        }
    }
}
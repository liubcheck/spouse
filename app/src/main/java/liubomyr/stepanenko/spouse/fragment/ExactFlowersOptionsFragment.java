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

public class ExactFlowersOptionsFragment extends Fragment implements BackPressHandler {
    private SharedPreferences sharedPreferences;
    private final ImageView imageView;
    private final int index;
    private final int price;
    private CheckBox redCheckBox;
    private CheckBox yellowCheckBox;
    private CheckBox pinkCheckBox;
    private CheckBox whiteCheckBox;
    private TextView amountPriceTextView;
    private SeekBar flowersSeekBar;
    private List<CheckBox> checkBoxes;
    private int totalCheckedBoxes;
    private int actualProgress;
    private int totalPrice;
    private boolean valueSetUp = false;
    private static final int STEP_SIZE = 2;

    public ExactFlowersOptionsFragment(ImageView imageView, int index,
                                       SharedPreferences sharedPreferences) {
        this.imageView = imageView;
        this.index = index;
        this.price = index == 1 ? 200 : index == 2 ? 300 : 110;
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
        return inflater.inflate(R.layout.fragment_exact_flowers_options,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView locationImageView = view.findViewById(R.id.exactFlowersImageView);
        locationImageView.setImageDrawable(imageView.getDrawable());

        amountPriceTextView = view.findViewById(R.id.flowersAmountAndPrice);
        amountPriceTextView.setText(getString(R.string.amount_0_price_0_uah,
                0, 0));

        redCheckBox = view.findViewById(R.id.redFlowerCheckBox);
        yellowCheckBox = view.findViewById(R.id.yellowFlowerCheckBox);
        pinkCheckBox = view.findViewById(R.id.pinkFlowerCheckBox);
        whiteCheckBox = view.findViewById(R.id.whiteFlowerCheckBox);

        checkBoxes = Arrays.asList(
                redCheckBox, yellowCheckBox, pinkCheckBox, whiteCheckBox
        );

        flowersSeekBar = view.findViewById(R.id.flowersAmountPriceSeekBar);
        flowersSeekBar.setMin(0);
        flowersSeekBar.setMax(101);
        flowersSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (valueSetUp) {
                    flowersSeekBar.setMax(51);
                }
                actualProgress = progress * STEP_SIZE - ((progress < 1) ? 0 : 1);
                totalPrice = actualProgress * price;
                amountPriceTextView.setText(
                        getString(R.string.amount_0_price_0_uah,
                                actualProgress, totalPrice)
                );
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
                updateSeekBarMin();
            });
        }

        setValues();
        updateSeekBarMin();

        Button resetButton = view.findViewById(R.id.resetFlowersButton);
        resetButton.setOnClickListener(v -> {
            for (CheckBox checkBox : checkBoxes) {
                checkBox.setChecked(false);
            }
            updateSeekBarMin();
            totalCheckedBoxes = 0;
            saveChanges();
        });
    }

    private void updateSeekBarMin() {
        int min = getMinValue();
        flowersSeekBar.setMin(min);
        if (totalCheckedBoxes == 0) {
            flowersSeekBar.setProgress(0);
        }
        flowersSeekBar.setEnabled(totalCheckedBoxes != 0);
    }

    private int getMinValue() {
        switch (totalCheckedBoxes) {
            case 1:
            case 2:
                return totalCheckedBoxes;
            case 3:
            case 4:
                return totalCheckedBoxes - 1;
            default:
                return 0;
        }
    }

    private void setValues() {
        int totalPrice;
        switch (index) {
            case 1:
                redCheckBox.setChecked(sharedPreferences.getBoolean("rosesRed", false));
                yellowCheckBox.setChecked(sharedPreferences.getBoolean("rosesYellow", false));
                pinkCheckBox.setChecked(sharedPreferences.getBoolean("rosesPink", false));
                whiteCheckBox.setChecked(sharedPreferences.getBoolean("rosesWhite", false));
                totalPrice = sharedPreferences.getInt("rosesTotalPrice", 0);
                flowersSeekBar.setProgress(totalPrice / price);
                valueSetUp = true;
                amountPriceTextView.setText(getString(R.string.amount_0_price_0_uah,
                        totalPrice / price, totalPrice));
                break;
            case 2:
                redCheckBox.setChecked(sharedPreferences.getBoolean("peoniesRed", false));
                yellowCheckBox.setChecked(sharedPreferences.getBoolean("peoniesYellow", false));
                pinkCheckBox.setChecked(sharedPreferences.getBoolean("peoniesPink", false));
                whiteCheckBox.setChecked(sharedPreferences.getBoolean("peoniesWhite", false));
                totalPrice = sharedPreferences.getInt("peoniesTotalPrice", 0);
                flowersSeekBar.setProgress(totalPrice / price);
                valueSetUp = true;
                amountPriceTextView.setText(getString(R.string.amount_0_price_0_uah,
                        totalPrice / price, totalPrice));
                break;
            case 3:
                redCheckBox.setChecked(sharedPreferences.getBoolean("freesiasRed", false));
                yellowCheckBox.setChecked(sharedPreferences.getBoolean("freesiasYellow", false));
                pinkCheckBox.setChecked(sharedPreferences.getBoolean("freesiasPink", false));
                whiteCheckBox.setChecked(sharedPreferences.getBoolean("freesiasWhite", false));
                totalPrice = sharedPreferences.getInt("freesiasTotalPrice", 0);
                flowersSeekBar.setProgress(totalPrice / price);
                valueSetUp = true;
                amountPriceTextView.setText(getString(R.string.amount_0_price_0_uah,
                        totalPrice / price, totalPrice));
                break;
        }
    }

    private void saveChanges() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int previousTotalPrice;
        switch (index) {
            case 1:
                editor.putBoolean("rosesRed", redCheckBox.isChecked());
                editor.putBoolean("rosesYellow", yellowCheckBox.isChecked());
                editor.putBoolean("rosesPink", pinkCheckBox.isChecked());
                editor.putBoolean("rosesWhite", whiteCheckBox.isChecked());
                previousTotalPrice = sharedPreferences.getInt("rosesTotalPrice", 0);
                editor.putInt("rosesTotalPrice", totalPrice);
                editor.putInt("ROSES_NUMBER", actualProgress);
                editor.putInt("TOTAL_FLOWERS_SUM",
                        sharedPreferences.getInt("TOTAL_FLOWERS_SUM", 0)
                                - previousTotalPrice + totalPrice);
                editor.putInt("TOTAL_SUM",
                        sharedPreferences.getInt("TOTAL_SUM", 0)
                                - previousTotalPrice + totalPrice);
                break;
            case 2:
                editor.putBoolean("peoniesRed", redCheckBox.isChecked());
                editor.putBoolean("peoniesYellow", yellowCheckBox.isChecked());
                editor.putBoolean("peoniesPink", pinkCheckBox.isChecked());
                editor.putBoolean("peoniesWhite", whiteCheckBox.isChecked());
                previousTotalPrice = sharedPreferences.getInt("peoniesTotalPrice", 0);
                editor.putInt("peoniesTotalPrice", totalPrice);
                editor.putInt("PEONIES_NUMBER", actualProgress);
                editor.putInt("TOTAL_FLOWERS_SUM",
                        sharedPreferences.getInt("TOTAL_FLOWERS_SUM", 0)
                                - previousTotalPrice + totalPrice);
                editor.putInt("TOTAL_SUM",
                        sharedPreferences.getInt("TOTAL_SUM", 0)
                                - previousTotalPrice + totalPrice);
                break;
            case 3:
                editor.putBoolean("freesiasRed", redCheckBox.isChecked());
                editor.putBoolean("freesiasYellow", yellowCheckBox.isChecked());
                editor.putBoolean("freesiasPink", pinkCheckBox.isChecked());
                editor.putBoolean("freesiasWhite", whiteCheckBox.isChecked());
                previousTotalPrice = sharedPreferences.getInt("freesiasTotalPrice", 0);
                editor.putInt("freesiasTotalPrice", totalPrice);
                editor.putInt("FREESIAS_NUMBER", actualProgress);
                editor.putInt("TOTAL_FLOWERS_SUM",
                        sharedPreferences.getInt("TOTAL_FLOWERS_SUM", 0)
                                - previousTotalPrice + totalPrice);
                editor.putInt("TOTAL_SUM",
                        sharedPreferences.getInt("TOTAL_SUM", 0)
                                - previousTotalPrice + totalPrice);
                break;
        }
        editor.apply();
    }
}

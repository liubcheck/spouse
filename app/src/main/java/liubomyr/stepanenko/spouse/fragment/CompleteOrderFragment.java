package liubomyr.stepanenko.spouse.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import liubomyr.stepanenko.spouse.R;
import liubomyr.stepanenko.spouse.creation.PhoneNumberActivity;
import liubomyr.stepanenko.spouse.creation.OtherOptionsChoice;
import liubomyr.stepanenko.spouse.model.Location;

public class CompleteOrderFragment extends Fragment {
    private LinearLayout linearLayout;
    private SharedPreferences sharedPreferences;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore db;
    private boolean isTotalPriceShown;

    public CompleteOrderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseStorage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_complete_order,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sharedPreferences = requireActivity()
                .getSharedPreferences("WEDDING_DATA", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        isTotalPriceShown = false;

        Location orderLocation = gson.fromJson(sharedPreferences
                .getString("SELECTED_LOCATION", null), Location.class);

        ImageView orderLocationImageView = view.findViewById(R.id.orderLocation_imageView);
        String imagePath = sharedPreferences.getString("IMAGE_PATH", "");
        StorageReference storageRef = firebaseStorage.getReference().child(imagePath);
        storageRef.getDownloadUrl().addOnSuccessListener(uri ->
                Glide.with(requireContext())
                .load(uri)
                .thumbnail(0.1f)
                .into(orderLocationImageView));

        TextView orderLocationNameTextView = view.findViewById(R.id.orderLocationName_textView);
        orderLocationNameTextView.setText(orderLocation.getName());

        TextView orderLocationDescriptionTextView =
                view.findViewById(R.id.orderLocationDescription_textView);
        orderLocationDescriptionTextView.setText(orderLocation.getDescription());


        linearLayout = view.findViewById(R.id.linearLayout);

        int locationIndex = 0;

        addTextViewToGridLayout("Місто", locationIndex++);
        addTextViewToGridLayout(orderLocation.getCity(), locationIndex++);

        addTextViewToGridLayout("Ресторан-партнер", locationIndex++);
        addTextViewToGridLayout(sharedPreferences
                .getString("PARTNER_RESTAURANT", "_"), locationIndex++);

        addTextViewToGridLayout("Прикраси", locationIndex++);
        addTextViewToGridLayout(formatAccessoriesInfo(), locationIndex++);

        addTextViewToGridLayout("Коментарі до прикрас", locationIndex++);
        String accessoriesComment = sharedPreferences
                .getString("ACCESSORIES_COMMENT", "_");
        if (accessoriesComment.length() >= 1) {
            addTextViewToGridLayout(accessoriesComment, locationIndex++);
        } else {
            addTextViewToGridLayout("_", locationIndex++);
        }

        addTextViewToGridLayout("Квіти", locationIndex++);
        addTextViewToGridLayout(formatFlowersInfo(), locationIndex++);

        addTextViewToGridLayout("Коментарі до квітів", locationIndex++);
        String flowersComment = sharedPreferences
                .getString("FLOWERS_COMMENT", "_");
        if (flowersComment.length() >= 1) {
            addTextViewToGridLayout(flowersComment, locationIndex++);
        } else {
            addTextViewToGridLayout("_", locationIndex++);
        }

        addTextViewToGridLayout("Діджей", locationIndex++);
        addTextViewToGridLayout(writePersonWithPrice(
                sharedPreferences.getString("SELECTED_DJ", "_"),
                sharedPreferences.getInt("DJ_PRICE", 0), null
        ), locationIndex++);

        addTextViewToGridLayout("Фотограф", locationIndex++);
        addTextViewToGridLayout(writePersonWithPrice(
                sharedPreferences.getString("SELECTED_PHOTOGRAPHER", "_"),
                sharedPreferences.getInt("PHOTOGRAPHER_TOTAL_PRICE", 0),
                sharedPreferences.getInt("PHOTO_HOURS", 0)
        ), locationIndex++);

        addTextViewToGridLayout("Ведучий", locationIndex++);
        addTextViewToGridLayout(writePersonWithPrice(
                sharedPreferences.getString("SELECTED_HOST", "_"),
                sharedPreferences.getInt("HOST_PRICE", 0), null
        ), locationIndex++);

        isTotalPriceShown = true;
        addTextViewToGridLayout("Усього", locationIndex++);
        String totalSum = sharedPreferences.getInt("TOTAL_SUM", 0) + " грн";
        addTextViewToGridLayout(totalSum, locationIndex);

        Button completeOrderButton = view.findViewById(R.id.order_button);
        completeOrderButton.setOnClickListener(v -> {
            saveOrder();
            Intent intent = new Intent(getActivity(), PhoneNumberActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof OtherOptionsChoice) {
            ((OtherOptionsChoice) getActivity()).hideOverlay();
        }
    }

    private void addTextViewToGridLayout(String name, int position) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(30, 0, 30, 35);

        TextView textView = new TextView(requireContext());
        textView.setLayoutParams(layoutParams);
        textView.setText(name);
        if (isTotalPriceShown) {
            textView.setTextSize(28);
            textView.setTypeface(null, Typeface.BOLD);
        } else {
            textView.setTextSize(position % 2 == 0 ? 20 : 14);
            textView.setTypeface(null, position % 2 == 0 ? Typeface.BOLD : Typeface.NORMAL);
        }
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_500));
        if (position % 2 == 0) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        linearLayout.addView(textView);
    }

    private String formatAccessoriesInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        int manyNumber = sharedPreferences.getInt("MANY_NUMBER", 0);
        if (manyNumber != 0) {
            stringBuilder.append("Поштучні кульки: ").append(manyNumber).append(" шт.\n");
            stringBuilder.append("Кольори: ");
            boolean isFirst = true;
            if (sharedPreferences.getBoolean("manyRed", false)) {
                stringBuilder.append("червоний");
                isFirst = false;
            }
            if (sharedPreferences.getBoolean("manyOrange", false)) {
                if (isFirst) {
                    stringBuilder.append("помаранчевий");
                    isFirst = false;
                } else {
                    stringBuilder.append(", помаранчевий");
                }
            }
            if (sharedPreferences.getBoolean("manyYellow", false)) {
                if (isFirst) {
                    stringBuilder.append("жовтий");
                    isFirst = false;
                } else {
                    stringBuilder.append(", жовтий");
                }
            }
            if (sharedPreferences.getBoolean("manyGreen", false)) {
                if (isFirst) {
                    stringBuilder.append("зелений");
                    isFirst = false;
                } else {
                    stringBuilder.append(", зелений");
                }
            }
            if (sharedPreferences.getBoolean("manyBlue", false)) {
                if (isFirst) {
                    stringBuilder.append("блакитний");
                    isFirst = false;
                } else {
                    stringBuilder.append(", блакитний");
                }
            }
            if (sharedPreferences.getBoolean("manyViolet", false)) {
                if (isFirst) {
                    stringBuilder.append("фіолетовий");
                    isFirst = false;
                } else {
                    stringBuilder.append(", фіолетовий");
                }
            }
            if (sharedPreferences.getBoolean("manyGolden", false)) {
                if (isFirst) {
                    stringBuilder.append("золотий");
                    isFirst = false;
                } else {
                    stringBuilder.append(", золотий");
                }
            }
            if (sharedPreferences.getBoolean("manySilver", false)) {
                if (isFirst) {
                    stringBuilder.append("сріблястий");
                } else {
                    stringBuilder.append(", сріблястий");
                }
            }
            stringBuilder.append("\n").append(sharedPreferences
                    .getInt("manyTotalPrice", 0)).append(" грн");
        }
        int heartNumber = sharedPreferences.getInt("HEART_NUMBER", 0);
        if (heartNumber != 0) {
            if (manyNumber != 0) {
                stringBuilder.append("\n\n");
            }
            stringBuilder.append("Кулькові серця: ").append(heartNumber).append(" шт.\n");
            stringBuilder.append("Кольори: ");
            boolean isFirst = true;
            if (sharedPreferences.getBoolean("heartRed", false)) {
                stringBuilder.append("червоний");
                isFirst = false;
            }
            if (sharedPreferences.getBoolean("heartOrange", false)) {
                if (isFirst) {
                    stringBuilder.append("помаранчевий");
                    isFirst = false;
                } else {
                    stringBuilder.append(", помаранчевий");
                }
            }
            if (sharedPreferences.getBoolean("heartYellow", false)) {
                if (isFirst) {
                    stringBuilder.append("жовтий");
                    isFirst = false;
                } else {
                    stringBuilder.append(", жовтий");
                }
            }
            if (sharedPreferences.getBoolean("heartGreen", false)) {
                if (isFirst) {
                    stringBuilder.append("зелений");
                    isFirst = false;
                } else {
                    stringBuilder.append(", зелений");
                }
            }
            if (sharedPreferences.getBoolean("heartBlue", false)) {
                if (isFirst) {
                    stringBuilder.append("блакитний");
                    isFirst = false;
                } else {
                    stringBuilder.append(", блакитний");
                }
            }
            if (sharedPreferences.getBoolean("heartViolet", false)) {
                if (isFirst) {
                    stringBuilder.append("фіолетовий");
                    isFirst = false;
                } else {
                    stringBuilder.append(", фіолетовий");
                }
            }
            if (sharedPreferences.getBoolean("heartGolden", false)) {
                if (isFirst) {
                    stringBuilder.append("золотий");
                    isFirst = false;
                } else {
                    stringBuilder.append(", золотий");
                }
            }
            if (sharedPreferences.getBoolean("heartSilver", false)) {
                if (isFirst) {
                    stringBuilder.append("сріблястий");
                } else {
                    stringBuilder.append(", сріблястий");
                }
            }
            stringBuilder.append("\n").append(sharedPreferences
                    .getInt("heartTotalPrice", 0)).append(" грн");
        }
        int archNumber = sharedPreferences.getInt("ARCH_NUMBER", 0);
        if (archNumber != 0) {
            if (manyNumber != 0 || heartNumber != 0) {
                stringBuilder.append("\n\n");
            }
            stringBuilder.append("Кулькові арки: ").append(archNumber).append(" шт.\n");
            stringBuilder.append("Кольори: ");
            boolean isFirst = true;
            if (sharedPreferences.getBoolean("archRed", false)) {
                stringBuilder.append("червоний");
                isFirst = false;
            }
            if (sharedPreferences.getBoolean("archOrange", false)) {
                if (isFirst) {
                    stringBuilder.append("помаранчевий");
                    isFirst = false;
                } else {
                    stringBuilder.append(", помаранчевий");
                }
            }
            if (sharedPreferences.getBoolean("archYellow", false)) {
                if (isFirst) {
                    stringBuilder.append("жовтий");
                    isFirst = false;
                } else {
                    stringBuilder.append(", жовтий");
                }
            }
            if (sharedPreferences.getBoolean("archGreen", false)) {
                if (isFirst) {
                    stringBuilder.append("зелений");
                    isFirst = false;
                } else {
                    stringBuilder.append(", зелений");
                }
            }
            if (sharedPreferences.getBoolean("archBlue", false)) {
                if (isFirst) {
                    stringBuilder.append("блакитний");
                    isFirst = false;
                } else {
                    stringBuilder.append(", блакитний");
                }
            }
            if (sharedPreferences.getBoolean("archViolet", false)) {
                if (isFirst) {
                    stringBuilder.append("фіолетовий");
                    isFirst = false;
                } else {
                    stringBuilder.append(", фіолетовий");
                }
            }
            if (sharedPreferences.getBoolean("archGolden", false)) {
                if (isFirst) {
                    stringBuilder.append("золотий");
                    isFirst = false;
                } else {
                    stringBuilder.append(", золотий");
                }
            }
            if (sharedPreferences.getBoolean("archSilver", false)) {
                if (isFirst) {
                    stringBuilder.append("сріблястий");
                } else {
                    stringBuilder.append(", сріблястий");
                }
            }
            stringBuilder.append("\n").append(sharedPreferences
                    .getInt("archTotalPrice", 0)).append(" грн");
        }
        if (manyNumber + heartNumber + archNumber == 0) {
            stringBuilder.append("_");
        }
        return stringBuilder.toString();
    }

    private String formatFlowersInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        int rosesNumber = sharedPreferences.getInt("ROSES_NUMBER", 0);
        if (rosesNumber != 0) {
            stringBuilder.append("Троянди: ").append(rosesNumber).append(" шт.\n");
            stringBuilder.append("Кольори: ");
            boolean isFirst = true;
            if (sharedPreferences.getBoolean("rosesRed", false)) {
                stringBuilder.append("червоний");
                isFirst = false;
            }
            if (sharedPreferences.getBoolean("rosesYellow", false)) {
                if (isFirst) {
                    stringBuilder.append("жовтий");
                    isFirst = false;
                } else {
                    stringBuilder.append(", жовтий");
                }
            }
            if (sharedPreferences.getBoolean("rosesPink", false)) {
                if (isFirst) {
                    stringBuilder.append("рожевий");
                    isFirst = false;
                } else {
                    stringBuilder.append(", рожевий");
                }
            }
            if (sharedPreferences.getBoolean("rosesWhite", false)) {
                if (isFirst) {
                    stringBuilder.append("білий");
                } else {
                    stringBuilder.append(", білий");
                }
            }
            stringBuilder.append("\n").append(sharedPreferences
                    .getInt("rosesTotalPrice", 0)).append(" грн");
        }
        int peoniesNumber = sharedPreferences.getInt("PEONIES_NUMBER", 0);
        if (peoniesNumber != 0) {
            if (rosesNumber != 0) {
                stringBuilder.append("\n\n");
            }
            stringBuilder.append("Піонії: ").append(peoniesNumber).append(" шт.\n");
            stringBuilder.append("Кольори: ");
            boolean isFirst = true;
            if (sharedPreferences.getBoolean("peoniesRed", false)) {
                stringBuilder.append("червоний");
                isFirst = false;
            }
            if (sharedPreferences.getBoolean("peoniesYellow", false)) {
                if (isFirst) {
                    stringBuilder.append("жовтий");
                    isFirst = false;
                } else {
                    stringBuilder.append(", жовтий");
                }
            }
            if (sharedPreferences.getBoolean("peoniesPink", false)) {
                if (isFirst) {
                    stringBuilder.append("рожевий");
                    isFirst = false;
                } else {
                    stringBuilder.append(", рожевий");
                }
            }
            if (sharedPreferences.getBoolean("peoniesWhite", false)) {
                if (isFirst) {
                    stringBuilder.append("білий");
                } else {
                    stringBuilder.append(", білий");
                }
            }
            stringBuilder.append("\n").append(sharedPreferences
                    .getInt("peoniesTotalPrice", 0)).append(" грн");
        }
        int freesiasNumber = sharedPreferences.getInt("FREESIAS_NUMBER", 0);
        if (freesiasNumber != 0) {
            if (rosesNumber != 0 || peoniesNumber != 0) {
                stringBuilder.append("\n\n");
            }
            stringBuilder.append("Фрезії: ").append(freesiasNumber).append(" шт.\n");
            stringBuilder.append("Кольори: ");
            boolean isFirst = true;
            if (sharedPreferences.getBoolean("freesiasRed", false)) {
                stringBuilder.append("червоний");
                isFirst = false;
            }
            if (sharedPreferences.getBoolean("freesiasYellow", false)) {
                if (isFirst) {
                    stringBuilder.append("жовтий");
                    isFirst = false;
                } else {
                    stringBuilder.append(", жовтий");
                }
            }
            if (sharedPreferences.getBoolean("freesiasPink", false)) {
                if (isFirst) {
                    stringBuilder.append("рожевий");
                    isFirst = false;
                } else {
                    stringBuilder.append(", рожевий");
                }
            }
            if (sharedPreferences.getBoolean("freesiasWhite", false)) {
                if (isFirst) {
                    stringBuilder.append("білий");
                } else {
                    stringBuilder.append(", білий");
                }
            }
            stringBuilder.append("\n").append(sharedPreferences
                    .getInt("freesiasTotalPrice", 0)).append(" грн");
        }
        if (rosesNumber + peoniesNumber + freesiasNumber == 0) {
            stringBuilder.append("_");
        }
        return stringBuilder.toString();
    }

    private String writePersonWithPrice(String person, Integer price, Integer hours) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!person.equals("None")) {
            stringBuilder.append(person).append("\n");
            if (hours != null) {
                stringBuilder.append(hours).append(" год").append("\n");
            }
            stringBuilder.append(price).append(" грн");
        } else {
            return "_";
        }
        return stringBuilder.toString();
    }

    private void saveOrder() {
        SharedPreferences userData = requireActivity()
                .getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        String user = userData.getString("EMAIL", "_");

        Gson gson = new Gson();
        String city = sharedPreferences.getString("SELECTED_CITY", "_");
        String place = sharedPreferences.getString("SELECTED_PLACE", "_");
        Location location = gson.fromJson(sharedPreferences
                .getString("SELECTED_LOCATION", "_"), Location.class);
        String name = location.getName();

        String partnerRestaurant =
                sharedPreferences.getString("PARTNER_RESTAURANT", "_");

        Map<String, Object> accessories = new HashMap<>();

        Map<String, Object> manyBalloons = new HashMap<>();
        int manyTotalNumber = sharedPreferences.getInt("MANY_NUMBER", 0);
        if (manyTotalNumber > 0) {
            manyBalloons.put("colors", getColors("many"));
            manyBalloons.put("quantity", manyTotalNumber);
            manyBalloons.put("total_price",
                    sharedPreferences.getInt("manyTotalPrice", 0));
        } else {
            manyBalloons.put("info", "_");
        }
        accessories.put("balloons", manyBalloons);

        Map<String, Object> heartBalloons = new HashMap<>();
        int heartTotalNumber = sharedPreferences.getInt("HEART_NUMBER", 0);
        if (heartTotalNumber > 0) {
            heartBalloons.put("colors", getColors("heart"));
            heartBalloons.put("quantity", heartTotalNumber);
            heartBalloons.put("total_price", sharedPreferences.getInt("heartTotalPrice", 0));
        } else {
            heartBalloons.put("info", "_");
        }
        accessories.put("heart_balloons", heartBalloons);

        Map<String, Object> archBalloons = new HashMap<>();
        int archTotalNumber = sharedPreferences.getInt("ARCH_NUMBER", 0);
        if (archTotalNumber > 0) {
            archBalloons.put("colors", getColors("arch"));
            archBalloons.put("quantity", archTotalNumber);
            archBalloons.put("total_price",
                    sharedPreferences.getInt("archTotalPrice", 0));
        } else {
            archBalloons.put("info", "_");
        }
        accessories.put("archBalloons", archBalloons);

        accessories.put("total_sum",
                sharedPreferences.getInt("TOTAL_BALLOONS_SUM", 0));

        String accessoriesComment =
                sharedPreferences.getString("ACCESSORIES_COMMENT", "_");

        Map<String, Object> flowers = new HashMap<>();

        Map<String, Object> roses = new HashMap<>();
        int rosesTotalNumber = sharedPreferences.getInt("ROSES_NUMBER", 0);
        if (rosesTotalNumber > 0) {
            roses.put("colors", getColors("roses"));
            roses.put("quantity", rosesTotalNumber);
            roses.put("total_price",
                    sharedPreferences.getInt("rosesTotalPrice", 0));
        } else {
            roses.put("info", "_");
        }
        flowers.put("roses", roses);

        Map<String, Object> peonies = new HashMap<>();
        int peoniesTotalNumber = sharedPreferences.getInt("PEONIES_NUMBER", 0);
        if (peoniesTotalNumber > 0) {
            peonies.put("colors", getColors("peonies"));
            peonies.put("quantity", peoniesTotalNumber);
            peonies.put("total_price", sharedPreferences.getInt("peoniesTotalPrice", 0));
        } else {
            peonies.put("info", "_");
        }
        flowers.put("peonies", peonies);

        Map<String, Object> freesias = new HashMap<>();
        int freesiasTotalNumber = sharedPreferences.getInt("FREESIAS_NUMBER", 0);
        if (freesiasTotalNumber > 0) {
            freesias.put("colors", getColors("freesias"));
            freesias.put("quantity", freesiasTotalNumber);
            freesias.put("total_price",
                    sharedPreferences.getInt("freesiasTotalPrice", 0));
        } else {
            freesias.put("info", "_");
        }
        flowers.put("freesias", freesias);

        flowers.put("total_sum",
                sharedPreferences.getInt("TOTAL_FLOWERS_SUM", 0));

        String flowersComment =
                sharedPreferences.getString("FLOWERS_COMMENT", "_");

        Map<String, Object> dj = new HashMap<>();
        String djName = sharedPreferences.getString("SELECTED_DJ", "_");
        if (!djName.equals("_")) {
            dj.put("name", djName);
            dj.put("price", sharedPreferences.getInt("DJ_PRICE", 0));
        } else {
            dj.put("info", djName);
        }

        Map<String, Object> photographer = new HashMap<>();
        String photographerName =
                sharedPreferences.getString("SELECTED_PHOTOGRAPHER", "_");
        if (!photographerName.equals("_")) {
            photographer.put("name", photographerName);
            photographer.put("hours", sharedPreferences.getInt("PHOTO_HOURS", 0));
            photographer.put("price", sharedPreferences.getInt("PHOTO_PRICE", 0));
            photographer.put("total_price",
                    sharedPreferences.getInt("PHOTOGRAPHER_TOTAL_PRICE", 0));
        } else {
            photographer.put("info", photographerName);
        }

        Map<String, Object> host = new HashMap<>();
        String hostName = sharedPreferences.getString("SELECTED_HOST", "_");
        if (!hostName.equals("_")) {
            host.put("name", hostName);
            host.put("price", sharedPreferences.getInt("HOST_PRICE", 0));
        } else {
            host.put("info", hostName);
        }

        int totalSum = sharedPreferences.getInt("TOTAL_SUM", 0);

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

        db.collection("orders")
                .add(order)
                .addOnSuccessListener(e -> Toast.makeText(requireContext(),
                        "Ваше замовлення успішно оформлене",
                        Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(requireContext(),
                        "Помилка при оформленні замовлення",
                        Toast.LENGTH_SHORT).show());

    }

    private List<String> getColors(String type) {
        List<String> colors = new ArrayList<>();
        if (sharedPreferences.getBoolean(type + "Red", false)) {
            colors.add("Red");
        }
        if (sharedPreferences.getBoolean(type + "Orange", false)) {
            colors.add("Orange");
        }
        if (sharedPreferences.getBoolean(type + "Yellow", false)) {
            colors.add("Yellow");
        }
        if (sharedPreferences.getBoolean(type + "Green", false)) {
            colors.add("Green");
        }
        if (sharedPreferences.getBoolean(type + "Blue", false)) {
            colors.add("Blue");
        }
        if (sharedPreferences.getBoolean(type + "Violet", false)) {
            colors.add("Violet");
        }
        if (sharedPreferences.getBoolean(type + "Golden", false)) {
            colors.add("Golden");
        }
        if (sharedPreferences.getBoolean(type + "Silver", false)) {
            colors.add("Silver");
        }
        if (sharedPreferences.getBoolean(type + "Pink", false)) {
            colors.add("Pink");
        }
        if (sharedPreferences.getBoolean(type + "White", false)) {
            colors.add("White");
        }
        return colors;
    }
}

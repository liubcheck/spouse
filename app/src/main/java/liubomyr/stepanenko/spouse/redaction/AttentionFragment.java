package liubomyr.stepanenko.spouse.redaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import liubomyr.stepanenko.spouse.redaction.EditOrder;
import liubomyr.stepanenko.spouse.MainMenu;
import liubomyr.stepanenko.spouse.R;
import liubomyr.stepanenko.spouse.handler.BackPressHandler;

public class AttentionFragment extends Fragment implements BackPressHandler {
    private final boolean isDelete;

    public AttentionFragment(boolean isDelete) {
        this.isDelete = isDelete;
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
        return inflater.inflate(R.layout.fragment_attention,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView textView = view.findViewById(R.id.attentionTextView);
        textView.setText(isDelete
                ? "Остаточно видалити Ваше замовлення?" : "Перервати редагування?");

        Button yesButton = view.findViewById(R.id.yesButton);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDelete) {
                    if (getActivity() instanceof EditOrder) {
                        ((EditOrder) getActivity()).deleteOrder();
                        clearSharedPreferences();
                    }
                }
                Intent intent = new Intent(getActivity(), MainMenu.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        Button noButton = view.findViewById(R.id.noButton);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBackPress();
            }
        });
    }

    private void clearSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity()
                .getSharedPreferences("WEDDING_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        SharedPreferences updatedSharedPreferences = requireActivity()
                .getSharedPreferences("UPDATE_WEDDING_DATA", Context.MODE_PRIVATE);
        editor = updatedSharedPreferences.edit();
        editor.clear();
        editor.apply();

        SharedPreferences locationSharedPreferences = requireActivity()
                .getSharedPreferences("UPDATE_LOCATION_DATA", Context.MODE_PRIVATE);
        editor = locationSharedPreferences.edit();
        editor.clear();
        editor.apply();;
    }
}
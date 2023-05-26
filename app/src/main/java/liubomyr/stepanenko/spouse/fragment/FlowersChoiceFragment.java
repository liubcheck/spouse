package liubomyr.stepanenko.spouse.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import liubomyr.stepanenko.spouse.redaction.EditOrder;
import liubomyr.stepanenko.spouse.R;
import liubomyr.stepanenko.spouse.creation.OtherOptionsChoice;
import liubomyr.stepanenko.spouse.handler.BackPressHandler;

public class FlowersChoiceFragment extends Fragment implements BackPressHandler {
    private SharedPreferences sharedPreferences;

    private ExactFlowersOptionsFragment rosesFragment;
    private ExactFlowersOptionsFragment peoniesFragment;
    private ExactFlowersOptionsFragment freesiasFragment;
    private EditText commentsEditText;

    public FlowersChoiceFragment(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public boolean handleBackPress() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("FLOWERS_COMMENT", commentsEditText.getText().toString());
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
        return inflater.inflate(R.layout.fragment_flowers_choice,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView rosesImageView = view.findViewById(R.id.roses_imageView);
        rosesFragment = new ExactFlowersOptionsFragment(rosesImageView,
                1, sharedPreferences);
        rosesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFlowersFragment(rosesFragment);
            }
        });

        ImageView peoniesImageView = view.findViewById(R.id.peonies_imageView);
        peoniesFragment = new ExactFlowersOptionsFragment(peoniesImageView,
                2, sharedPreferences);
        peoniesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFlowersFragment(peoniesFragment);
            }
        });

        ImageView freesiasImageView = view.findViewById(R.id.freesias_imageView);
        freesiasFragment = new ExactFlowersOptionsFragment(freesiasImageView,
                3, sharedPreferences);
        freesiasImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFlowersFragment(freesiasFragment);
            }
        });

        TextView totalSumTextView = view.findViewById(R.id.totalSum_flowers_textView);
        totalSumTextView.setText(getString(R.string.total_flowers_sum,
                sharedPreferences.getInt("TOTAL_FLOWERS_SUM", 0)));

        commentsEditText = view.findViewById(R.id.flowersComments_editText);
    }

    private void openFlowersFragment(ExactFlowersOptionsFragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.overlay, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
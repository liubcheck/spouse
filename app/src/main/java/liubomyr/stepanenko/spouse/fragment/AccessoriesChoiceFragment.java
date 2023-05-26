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

public class AccessoriesChoiceFragment extends Fragment implements BackPressHandler {
    private SharedPreferences sharedPreferences;
    private BalloonsOptionsFragment manyBalloonsOptionsFragment;
    private BalloonsOptionsFragment heartBalloonsOptionsFragment;
    private BalloonsOptionsFragment archBalloonsOptionsFragment;
    private EditText commentsEditText;

    public AccessoriesChoiceFragment(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public boolean handleBackPress() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ACCESSORIES_COMMENT", commentsEditText.getText().toString());
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
        return inflater.inflate(R.layout.fragment_accessories_choice,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView manyBalloonsImageView = view.findViewById(R.id.manyBalloons_imageView);
        manyBalloonsOptionsFragment =
                new BalloonsOptionsFragment(manyBalloonsImageView,
                        1, sharedPreferences);
        manyBalloonsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBalloonsFragment(manyBalloonsOptionsFragment);
            }
        });

        ImageView heartBalloonsImageView = view.findViewById(R.id.heartBalloons_imageView);
        heartBalloonsOptionsFragment =
                new BalloonsOptionsFragment(heartBalloonsImageView,
                        2, sharedPreferences);
        heartBalloonsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBalloonsFragment(heartBalloonsOptionsFragment);
            }
        });

        ImageView archBalloonsImageView = view.findViewById(R.id.archBalloons_imageView);
        archBalloonsOptionsFragment =
                new BalloonsOptionsFragment(archBalloonsImageView,
                        3, sharedPreferences);
        archBalloonsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBalloonsFragment(archBalloonsOptionsFragment);
            }
        });

        TextView totalSumTextView = view.findViewById(R.id.totalSum_accessories_textView);
        totalSumTextView.setText(getString(R.string.total_sum,
                sharedPreferences.getInt("TOTAL_BALLOONS_SUM", 0)));

        commentsEditText = view.findViewById(R.id.accessoriesComments_editText);
    }

    private void openBalloonsFragment(BalloonsOptionsFragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.overlay, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
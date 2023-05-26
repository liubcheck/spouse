package liubomyr.stepanenko.spouse.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import liubomyr.stepanenko.spouse.R;
import liubomyr.stepanenko.spouse.creation.OtherOptionsChoice;
import liubomyr.stepanenko.spouse.handler.BackPressHandler;

public class WarningFragment extends Fragment implements BackPressHandler {

    public WarningFragment() {
    }

    @Override
    public boolean handleBackPress() {
        if (getActivity() instanceof OtherOptionsChoice) {
            ((OtherOptionsChoice) getActivity()).hideOverlay();
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
        return inflater.inflate(R.layout.fragment_warning,
                container, false);
    }
}

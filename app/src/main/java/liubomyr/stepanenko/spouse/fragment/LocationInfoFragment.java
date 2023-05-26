package liubomyr.stepanenko.spouse.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.gson.Gson;

import liubomyr.stepanenko.spouse.redaction.EditOrder;
import liubomyr.stepanenko.spouse.R;
import liubomyr.stepanenko.spouse.creation.ExactLocationChoice;
import liubomyr.stepanenko.spouse.creation.OtherOptionsChoice;
import liubomyr.stepanenko.spouse.handler.BackPressHandler;
import liubomyr.stepanenko.spouse.model.Location;
import liubomyr.stepanenko.spouse.model.RoundedImageView;
import liubomyr.stepanenko.spouse.redaction.EditPartnerRestaurantFragment;

import static android.content.Context.MODE_PRIVATE;

public class LocationInfoFragment extends Fragment implements BackPressHandler {
    private final Location location;
    private final ImageView imageView;
    private final SharedPreferences sharedPreferences;
    private final boolean isForOrder;
    private final boolean choosingPartnerRestaurant;
    private final boolean updating;
    private boolean partnerRestaurantUpdate;
    private EditPartnerRestaurantFragment editPartnerRestaurantFragment;

    private LocationInfoFragment(Location location, ImageView imageView,
                                 SharedPreferences sharedPreferences, boolean isForOrder,
                                 boolean choosingPartnerRestaurant, boolean updating,
                                 boolean partnerRestaurantUpdate) {
        this.location = location;
        this.imageView = imageView;
        this.sharedPreferences = sharedPreferences;
        this.isForOrder = isForOrder;
        this.choosingPartnerRestaurant = choosingPartnerRestaurant;
        this.updating = updating;
        this.partnerRestaurantUpdate = partnerRestaurantUpdate;
    }

    public static LocationInfoFragment newInstance(Location location, ImageView imageView,
                                                   SharedPreferences sharedPreferences,
                                                   boolean isForOrder,
                                                   boolean choosingPartnerRestaurant,
                                                   boolean updating,
                                                   boolean partnerRestaurantUpdate) {
        return new LocationInfoFragment(location, imageView,
                sharedPreferences, isForOrder,
                choosingPartnerRestaurant, updating, partnerRestaurantUpdate);
    }

    @Override
    public boolean handleBackPress() {
        if (!isForOrder || choosingPartnerRestaurant) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("PARTNER_RESTAURANT", "_");
            editor.apply();
            getParentFragmentManager().popBackStack();
            return true;
        }
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getActivity() instanceof ExactLocationChoice) {
                    ((ExactLocationChoice) getActivity()).hideOverlay();
                }
                getParentFragmentManager().popBackStack();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editPartnerRestaurantFragment = new EditPartnerRestaurantFragment();

        RoundedImageView locationImageView = view.findViewById(R.id.location_imageView);
        locationImageView.setImageDrawable(imageView.getDrawable());

        TextView locationNameTextView = view.findViewById(R.id.locationName_textView);
        locationNameTextView.setText(location.getName());

        TextView locationDescriptionTextView =
                view.findViewById(R.id.locationDescription_textView);
        locationDescriptionTextView.setText(location.getDescription());

        TextView locationCityTextView = view.findViewById(R.id.locationCity_textView);
        locationCityTextView.setText(locationCityTextView.getText()
                .toString()
                .replace("city", location.getCity()));

        TextView locationAddressTextView = view.findViewById(R.id.locationAddress_textView);
        locationAddressTextView.setText(locationAddressTextView.getText()
                .toString()
                .replace("address", location.getAddress()));

        Button chooseLocationButton = view.findViewById(R.id.chooseLocationButton);
        chooseLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String locationJson = gson.toJson(location);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isForOrder) {
                    editor.putString("SELECTED_LOCATION", locationJson);
                    editor.putString("IMAGE_PATH", location.getImagePath());
                    editor.apply();
                    Intent intent = new Intent(getActivity(), OtherOptionsChoice.class);
                    startActivity(intent);
                    getActivity().finish();
                } else if (choosingPartnerRestaurant) {
                    editor.putString("PARTNER_RESTAURANT", location.getName());
                    editor.apply();
                    getParentFragmentManager().popBackStack();
                    if (getActivity() instanceof OtherOptionsChoice) {
                        ((OtherOptionsChoice) getActivity()).hideOverlay();
                    }
                    getParentFragmentManager().popBackStack();
                } else if (updating && partnerRestaurantUpdate) {
                    editor.apply();
                    FragmentTransaction transaction =
                            getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.overlay, editPartnerRestaurantFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else if (updating) {
                    getParentFragmentManager().popBackStack();
                    getParentFragmentManager().popBackStack();
                    if (getActivity() instanceof EditOrder) {
                        ((EditOrder) getActivity()).hideOverlay();
                    }
                    getParentFragmentManager().popBackStack();
                } else {
                    SharedPreferences updatedSharedPreferences = requireActivity()
                            .getSharedPreferences("UPDATE_WEDDING_DATA", MODE_PRIVATE);
                    editor = updatedSharedPreferences.edit();
                    editor.putString("SELECTED_CITY",
                            sharedPreferences.getString("SELECTED_CITY", "_"));
                    editor.putString("SELECTED_PLACE",
                            sharedPreferences.getString("SELECTED_PLACE", "_"));
                    editor.putString("SELECTED_LOCATION",
                            sharedPreferences.getString("SELECTED_LOCATION", "_"));
                    editor.putString("PARTNER_RESTAURANT",
                            sharedPreferences.getString("PARTNER_RESTAURANT", "_"));
                    editor.apply();
                    getParentFragmentManager().popBackStack();
                    getParentFragmentManager().popBackStack();
                    getParentFragmentManager().popBackStack();
                    if (getActivity() instanceof EditOrder) {
                        ((EditOrder) getActivity()).hideOverlay();
                    }
                    getParentFragmentManager().popBackStack();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof ExactLocationChoice) {
            ((ExactLocationChoice) getActivity()).hideOverlay();
        }
    }
}
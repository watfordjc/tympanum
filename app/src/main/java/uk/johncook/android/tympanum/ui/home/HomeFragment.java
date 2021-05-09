package uk.johncook.android.tympanum.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import uk.johncook.android.tympanum.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        final TextView textViewWGS84CoordinatesDMS = root.findViewById(R.id.wgs84_coordinates_degrees);
        homeViewModel.getWGS84CoordinatesDMS().observe(getViewLifecycleOwner(), textViewWGS84CoordinatesDMS::setText);
        final TextView textViewWGS84CoordinatesDecimal = root.findViewById(R.id.wgs84_coordinates_decimal);
        homeViewModel.getWGS84CoordinatesDecimal().observe(getViewLifecycleOwner(), textViewWGS84CoordinatesDecimal::setText);
        final TextView textViewCoordinates = root.findViewById(R.id.iaru_location);
        homeViewModel.getIARULocation().observe(getViewLifecycleOwner(), textViewCoordinates::setText);
        final TextView textViewOpenLocationCode = root.findViewById(R.id.olc_text);
        homeViewModel.getOpenLocationCode().observe(getViewLifecycleOwner(), textViewOpenLocationCode::setText);
        final TextView textViewOsgb36Location = root.findViewById(R.id.os_grid_unimplemented);
        homeViewModel.getOsgb36Location().observe(getViewLifecycleOwner(), textViewOsgb36Location::setText);
        final TextView textViewCoordinatesAccuracy = root.findViewById(R.id.text_coordinates_accuracy);
        homeViewModel.getCoordinatesAccuracy().observe(getViewLifecycleOwner(), textViewCoordinatesAccuracy::setText);
        final TextView textViewAltitude = root.findViewById(R.id.text_altitude);
        homeViewModel.getAltitude().observe(getViewLifecycleOwner(), textViewAltitude::setText);
        final TextView textViewAltitudeAccuracy = root.findViewById(R.id.text_altitude_accuracy);
        homeViewModel.getAltitudeAccuracy().observe(getViewLifecycleOwner(), textViewAltitudeAccuracy::setText);
        final TextView textViewGeocoder = root.findViewById(R.id.text_geocoder);
        homeViewModel.getGeocoder().observe(getViewLifecycleOwner(), textViewGeocoder::setText);
        return root;
    }
}
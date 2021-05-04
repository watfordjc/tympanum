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
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        final TextView textViewCoordinates = root.findViewById(R.id.text_coordinates);
        homeViewModel.getCoordinates().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textViewCoordinates.setText(s);
            }
        });
        final TextView textViewCoordinatesAccuracy = root.findViewById(R.id.text_coordinates_accuracy);
        homeViewModel.getCoordinatesAccuracy().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textViewCoordinatesAccuracy.setText(s);
            }
        });
        final TextView textViewAltitude = root.findViewById(R.id.text_altitude);
        homeViewModel.getAltitude().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textViewAltitude.setText(s);
            }
        });
        final TextView textViewAltitudeAccuracy = root.findViewById(R.id.text_altitude_accuracy);
        homeViewModel.getAltitudeAccuracy().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textViewAltitudeAccuracy.setText(s);
            }
        });
        return root;
    }
}
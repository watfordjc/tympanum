package uk.johncook.android.tympanum.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<String> mCoordinates;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Tympanum is an algorithm for converting a 3D location into words.");
        mCoordinates = new MutableLiveData<>();
        mCoordinates.setValue("Coordinates: Not Implemented");
        UpdateCoordinates();
    }

    public void UpdateCoordinates() {
        mCoordinates.setValue("Coordinates: " + "Unknown");
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<String> getCoordinates() { return mCoordinates; }
}
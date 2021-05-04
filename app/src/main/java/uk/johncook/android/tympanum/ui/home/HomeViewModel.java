package uk.johncook.android.tympanum.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> mCoordinates;
    private final MutableLiveData<String> mCoordinatesAccuracy;
    private final MutableLiveData<String> mAltitude;
    private final MutableLiveData<String> mAltitudeAccuracy;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Tympanum is an algorithm for converting a 3D location into words.");
        mCoordinates = new MutableLiveData<>();
        mCoordinatesAccuracy = new MutableLiveData<>();
        mAltitude = new MutableLiveData<>();
        mAltitudeAccuracy = new MutableLiveData<>();
        UpdateCoordinates("Unknown", -1);
        UpdateAltitude(-1, -1);
    }

    public void UpdateCoordinates(String coordinates, double accuracy) {
        mCoordinates.setValue("IARU Grid Location: " + coordinates);
        String stringAccuracy = accuracy == -1 ? "Not Available" : "Within " + String.format("%.0fm", accuracy + 2.4) + " *";
        mCoordinatesAccuracy.setValue("Location Accuracy: " + stringAccuracy);
    }

    public void UpdateAltitude(double altitude, double accuracy) {
        String stringAltitude = altitude == -1 ? "Unknown" : String.format("%.0fm", altitude);
        mAltitude.setValue("Altitude (WGS-84 ellipsoid): " + stringAltitude);
        String stringAltitudeAccuracy = accuracy == -1 ? "Not Available" : String.format("± %.0fm", accuracy) + " *";
        mAltitudeAccuracy.setValue("Altitude Accuracy: " + stringAltitudeAccuracy);
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<String> getCoordinates() { return mCoordinates; }
    public LiveData<String> getCoordinatesAccuracy() { return mCoordinatesAccuracy; }
    public LiveData<String> getAltitude() { return mAltitude; }
    public LiveData<String> getAltitudeAccuracy() { return mAltitudeAccuracy; }
}
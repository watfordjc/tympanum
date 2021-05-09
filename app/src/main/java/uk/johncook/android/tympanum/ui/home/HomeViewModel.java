package uk.johncook.android.tympanum.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Locale;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> mWGS84CoordinatesDMS;
    private final MutableLiveData<String> mWGS84CoordinatesDecimal;
    private final MutableLiveData<String> mIARULocation;
    private final MutableLiveData<String> mOpenLocationCode;
    private final MutableLiveData<String> mOsgb36Location;
    private final MutableLiveData<String> mCoordinatesAccuracy;
    private final MutableLiveData<String> mAltitude;
    private final MutableLiveData<String> mAltitudeAccuracy;
    private final MutableLiveData<String> mGeocoder;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Tympanum is an algorithm for converting a 3D location into words.");
        mWGS84CoordinatesDMS = new MutableLiveData<>();
        mWGS84CoordinatesDecimal = new MutableLiveData<>();
        mIARULocation = new MutableLiveData<>();
        mOpenLocationCode = new MutableLiveData<>();
        mOsgb36Location = new MutableLiveData<>();
        mCoordinatesAccuracy = new MutableLiveData<>();
        mAltitude = new MutableLiveData<>();
        mAltitudeAccuracy = new MutableLiveData<>();
        mGeocoder = new MutableLiveData<>();
        UpdateCoordinates("Unknown", "Unknown", "Unknown", "Unknown", -1);
        UpdateOsgb36Location("Unknown");
        UpdateGeocoder("Unknown");
        UpdateAltitude(-1, -1);
    }

    public void UpdateCoordinates(String wgs84CoordinatesDMS, String wgs84CoordinatesDecimal, String iaruLocation, String openLocationCode, double accuracy) {
        mWGS84CoordinatesDMS.setValue(wgs84CoordinatesDMS);
        mWGS84CoordinatesDecimal.setValue(wgs84CoordinatesDecimal);
        mIARULocation.setValue(iaruLocation);
        mOpenLocationCode.setValue(openLocationCode);
        String stringAccuracy = accuracy == -1 ? "Not Available" : "Within " + String.format(Locale.getDefault(), "%.0fm", accuracy + 2.4) + " *";
        mCoordinatesAccuracy.setValue("Location Accuracy: " + stringAccuracy);
    }

    public void UpdateOsgb36Location(String osgb36Location) {
        mOsgb36Location.setValue(osgb36Location);
    }

    public void UpdateAltitude(double altitude, double accuracy) {
        String stringAltitude = altitude == -1 ? "Unknown" : String.format(Locale.getDefault(), "%.0fm", altitude);
        mAltitude.setValue(stringAltitude);
        String stringAltitudeAccuracy = accuracy == -1 ? "Not Available" : String.format(Locale.getDefault(), "± %.0fm", accuracy) + " *";
        mAltitudeAccuracy.setValue("Altitude Accuracy: " + stringAltitudeAccuracy);
    }

    public void UpdateGeocoder(String location) {
        mGeocoder.setValue(location);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getWGS84CoordinatesDMS() {
        return mWGS84CoordinatesDMS;
    }

    public LiveData<String> getWGS84CoordinatesDecimal() {
        return mWGS84CoordinatesDecimal;
    }

    public LiveData<String> getIARULocation() {
        return mIARULocation;
    }

    public LiveData<String> getOpenLocationCode() {
        return mOpenLocationCode;
    }

    public LiveData<String> getOsgb36Location() {
        return mOsgb36Location;
    }

    public LiveData<String> getCoordinatesAccuracy() {
        return mCoordinatesAccuracy;
    }

    public LiveData<String> getAltitude() {
        return mAltitude;
    }

    public LiveData<String> getAltitudeAccuracy() {
        return mAltitudeAccuracy;
    }

    public LiveData<String> getGeocoder() {
        return mGeocoder;
    }
}
package uk.johncook.android.tympanum;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import uk.johncook.android.tympanum.data.Ostn15Point;
import uk.johncook.android.tympanum.data.Ostn15Repository;
import uk.johncook.android.tympanum.ui.home.HomeViewModel;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private static final int REQUEST_CHECK_SETTINGS = 1000;
    private boolean canAccessLocation = false;
    private boolean requestingLocationUpdates = false;
    private long lastUpdate = -1;
    private double lastUpdateAccuracy = -1;
    private HomeViewModel homeViewModel;
    private Geocoder geocoder;

    /*
    This block of constants contain transformations that are licensed BSD-2-Clause. The following required notice is in addition to those in files LICENSE and DATABASE-LICENSE and must be displayed in the software.
    © Copyright and database rights Ordnance Survey Limited 2016, © Crown copyright and database rights Land & Property Services 2016 and/or © Ordnance Survey Ireland, 2016. All rights reserved.
    */
    private static final double AIRY1830_SEMI_MAJOR_AXIS = 6377563.396; // a
    private static final double AIRY1830_SEMI_MINOR_AXIS = 6356256.909; // b
    private static final double AIRY1830_SQUARED_ECCENTRICITY = (Math.pow(AIRY1830_SEMI_MAJOR_AXIS, 2.0) - Math.pow(AIRY1830_SEMI_MINOR_AXIS, 2.0)) / Math.pow(AIRY1830_SEMI_MAJOR_AXIS, 2.0); // e^2
    private static final double GRS80_SEMI_MAJOR_AXIS = 6378137.000000; // a
    private static final double GRS80_SEMI_MINOR_AXIS = 6356752.314140; // b
    private static final double GRS80_SQUARED_ECCENTRICITY = (Math.pow(GRS80_SEMI_MAJOR_AXIS, 2.0) - Math.pow(GRS80_SEMI_MINOR_AXIS, 2.0)) / Math.pow(GRS80_SEMI_MAJOR_AXIS, 2.0); // e^2
    private static final double WGS84_SEMI_MAJOR_AXIS = 6378137.000000; // a
    private static final double WGS84_SEMI_MINOR_AXIS = 6356752.314245; // b
    private static final double WGS84_SQUARED_ECCENTRICITY = (Math.pow(WGS84_SEMI_MAJOR_AXIS, 2.0) - Math.pow(WGS84_SEMI_MINOR_AXIS, 2.0)) / Math.pow(WGS84_SEMI_MAJOR_AXIS, 2.0); // e^2
    private static final double NG_SCALE_FACTOR = 0.9996012717;
    private static final double NG_TRUE_ORIGIN_LATITUDE = 49.0 / 180 * Math.PI; // 49 degrees N in radians
    private static final double NG_TRUE_ORIGIN_LONGITUDE = -2.0 / 180 * Math.PI; // 2 degrees W in radians
    private static final double NG_TRUE_ORIGIN_EASTINGS = 400000;
    private static final double NG_TRUE_ORIGIN_NORTHINGS = -100000;
    private Ostn15Repository ostn15Repository = null;
    private MutableLiveData<List<Ostn15Point>> ostn15Points;
    private double osgb36Dx = -1;
    private double osgb36Dy = -1;

    private static String GetOSGB36GridSquare(int eastingsGrid, int northingsGrid) {
        switch (northingsGrid) {
            case 12:
                if (eastingsGrid == 4) {
                    return "HP";
                }
                return null;
            case 11:
                switch (eastingsGrid) {
                    case 3:
                        return "HT";
                    case 4:
                        return "HU";
                    default:
                        return null;
                }
            case 10:
                switch (eastingsGrid) {
                    case 1:
                        return "HW";
                    case 2:
                        return "HX";
                    case 3:
                        return "HY";
                    case 4:
                        return "HZ";
                    default:
                        return null;
                }
            case 9:
                switch (eastingsGrid) {
                    case 0:
                        return "NA";
                    case 1:
                        return "NB";
                    case 2:
                        return "NC";
                    case 3:
                        return "ND";
                    default:
                        return null;
                }
            case 8:
                switch (eastingsGrid) {
                    case 0:
                        return "NF";
                    case 1:
                        return "NG";
                    case 2:
                        return "NH";
                    case 3:
                        return "NJ";
                    case 4:
                        return "NK";
                    default:
                        return null;
                }
            case 7:
                switch (eastingsGrid) {
                    case 0:
                        return "NL";
                    case 1:
                        return "NM";
                    case 2:
                        return "NN";
                    case 3:
                        return "NO";
                    default:
                        return null;
                }
            case 6:
                switch (eastingsGrid) {
                    case 1:
                        return "NR";
                    case 2:
                        return "NS";
                    case 3:
                        return "NT";
                    case 4:
                        return "NU";
                    default:
                        return null;
                }
            case 5:
                switch (eastingsGrid) {
                    case 1:
                        return "NW";
                    case 2:
                        return "NX";
                    case 3:
                        return "NY";
                    case 4:
                        return "NZ";
                    case 5:
                        return "OV";
                    default:
                        return null;
                }
            case 4:
                switch (eastingsGrid) {
                    case 2:
                        return "SC";
                    case 3:
                        return "SD";
                    case 4:
                        return "SE";
                    case 5:
                        return "TA";
                    default:
                        return null;
                }
            case 3:
                switch (eastingsGrid) {
                    case 2:
                        return "SH";
                    case 3:
                        return "SJ";
                    case 4:
                        return "SK";
                    case 5:
                        return "TF";
                    case 6:
                        return "TG";
                    default:
                        return null;
                }
            case 2:
                switch (eastingsGrid) {
                    case 1:
                        return "SM";
                    case 2:
                        return "SN";
                    case 3:
                        return "SO";
                    case 4:
                        return "SP";
                    case 5:
                        return "TL";
                    case 6:
                        return "TM";
                    default:
                        return null;
                }
            case 1:
                switch (eastingsGrid) {
                    case 1:
                        return "SR";
                    case 2:
                        return "SS";
                    case 3:
                        return "ST";
                    case 4:
                        return "SU";
                    case 5:
                        return "TQ";
                    case 6:
                        return "TR";
                    default:
                        return null;
                }
            case 0:
                switch (eastingsGrid) {
                    case 0:
                        return "SV";
                    case 1:
                        return "SW";
                    case 2:
                        return "SX";
                    case 3:
                        return "SY";
                    case 4:
                        return "SZ";
                    case 5:
                        return "TV";
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    private void ETRS89ToOSGB36Grid(Location location) {
        /*
        This function contains transformations that are licensed BSD-2-Clause. The following required notice is in addition to those in files LICENSE and DATABASE-LICENSE and must be displayed in the software.
        © Copyright and database rights Ordnance Survey Limited 2016, © Crown copyright and database rights Land & Property Services 2016 and/or © Ordnance Survey Ireland, 2016. All rights reserved.
         */
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double altitude = location.getAltitude();
        if (latitude <= 49 || latitude >= 62 || longitude <= -10 || longitude >= 4) {
            Log.d("ETRS89", "Outside OSGB36 range.");
            homeViewModel.UpdateOsgb36Location("Outside OSGB36 range.");
            return;
        } else {
            Log.d("ETRS89", "Potentially inside OSGB36 range.");
            if (ostn15Repository == null) {
                homeViewModel.UpdateOsgb36Location("Potentially inside OSGB36 range.\nOSTN15 database is being prepared...");
                ostn15Repository = new Ostn15Repository(getApplication());
                ostn15Points = ostn15Repository.getSearchResults();
                ostn15Points.observe(this, ostn15Points -> {
                    Log.d("ETRS89", "OSGB36 Shift Points have been updated.");
                    Log.d("ETRS89", "se0, sn0, sg0 = " + ostn15Points.get(0).eastShift + "," + ostn15Points.get(0).northShift + "," + ostn15Points.get(0).heightShift);
                    Log.d("ETRS89", "Offset (dx,dy): (" + osgb36Dx + "," + osgb36Dy + ")");
                    double t = osgb36Dx / 1000;
                    Log.d("ETRS89", "t = " + t);
                    double u = osgb36Dy / 1000;
                    Log.d("ETRS89", "u = " + u);
                    double se = ((1 - t) * (1 - u) * ostn15Points.get(0).eastShift) + (t * (1 - u) * ostn15Points.get(1).eastShift) + (t * u * ostn15Points.get(2).eastShift) + ((1 - t) * u * ostn15Points.get(3).eastShift);
                    Log.d("ETRS89", "se = " + se);
                    double sn = ((1 - t) * (1 - u) * ostn15Points.get(0).northShift) + (t * (1 - u) * ostn15Points.get(1).northShift) + (t * u * ostn15Points.get(2).northShift) + ((1 - t) * u * ostn15Points.get(3).northShift);
                    Log.d("ETRS89", "sn = " + sn);
                    double sg = ((1 - t) * (1 - u) * ostn15Points.get(0).heightShift) + (t * (1 - u) * ostn15Points.get(1).heightShift) + (t * u * ostn15Points.get(2).heightShift) + ((1 - t) * u * ostn15Points.get(3).heightShift);
                    Log.d("ETRS89", "sg = " + sg);
                    double eastings = ostn15Points.get(0).easting + osgb36Dx + se;
                    Log.d("ETRS89", "OSGB36 Eastings: " + eastings);
                    double northings = ostn15Points.get(0).northing + osgb36Dy + sn;
                    Log.d("ETRS89", "OSGB36 Northings: " + northings);
                    double orthometric_height = altitude - sg;
                    Log.d("ETRS89", "OSGB36 Orthometric Height: " + orthometric_height);
                    int eastingsGrid = (int) (eastings / 100000);
                    int northingsGrid = (int) (northings / 100000);
                    String gridSquare = GetOSGB36GridSquare(eastingsGrid, northingsGrid);
                    if (gridSquare == null) {
                        homeViewModel.UpdateOsgb36Location("Outside OSGB36 range.");
                        return;
                    }
                    homeViewModel.UpdateOsgb36Location("Eastings (Decimal): " + Math.round(eastings) + "\nNorthings (Decimal): " + Math.round(northings) + "\nAltitude: " + Math.round(orthometric_height) + "m\nGrid Square: " + gridSquare + "\nEastings: " + Math.round(eastings % 100000) + "\nNorthings: " + Math.round(northings % 100000) + "\n\n© Copyright and database rights Ordnance Survey Limited 2016, © Crown copyright and database rights Land & Property Services 2016 and/or © Ordnance Survey Ireland, 2016. All rights reserved.");
                    Log.d("ETRS89", "(east,north) grid: (" + eastingsGrid + "," + northingsGrid + ")");
                });
            } else if (osgb36Dx == -1 && osgb36Dy == -1) {
                homeViewModel.UpdateOsgb36Location("Potentially inside OSGB36 range.");
            }
        }
        double phi = latitude / 180 * Math.PI; // latitude in radians
        Log.d("ETRS89", "phi = " + phi);
        double lambda = longitude / 180 * Math.PI; //longitude in radians
        Log.d("ETRS89", "lambda = " + lambda);
        Log.d("ETRS89", "© Copyright and database rights Ordnance Survey Limited 2016, © Crown copyright and database rights Land & Property Services 2016 and/or © Ordnance Survey Ireland, 2016. All rights reserved.");
        double n = (GRS80_SEMI_MAJOR_AXIS - GRS80_SEMI_MINOR_AXIS) / (GRS80_SEMI_MAJOR_AXIS + GRS80_SEMI_MINOR_AXIS);
        Log.d("ETRS89", "n = " + n);
        double v = (GRS80_SEMI_MAJOR_AXIS * NG_SCALE_FACTOR) * Math.pow((1 - GRS80_SQUARED_ECCENTRICITY * Math.pow(Math.sin(phi), 2.0)), -0.5);
        Log.d("ETRS89", "v = " + v);
        double rho = (GRS80_SEMI_MAJOR_AXIS * NG_SCALE_FACTOR) * (1 - GRS80_SQUARED_ECCENTRICITY) * Math.pow((1 - GRS80_SQUARED_ECCENTRICITY * Math.pow(Math.sin(phi), 2.0)), -1.5);
        Log.d("ETRS89", "rho = " + rho);
        double eta_squared = (v / rho) - 1;
        Log.d("ETRS89", "eta^2 = " + eta_squared);
        double mu = (GRS80_SEMI_MINOR_AXIS * NG_SCALE_FACTOR) *
                (((1 + n + (5.0 / 4 * Math.pow(n, 2.0)) + (5.0 / 4 * Math.pow(n, 3.0))) * (phi - NG_TRUE_ORIGIN_LATITUDE)) -
                        (((3 * n) + (3 * Math.pow(n, 2.0)) + (21.0 / 8 * Math.pow(n, 3.0))) * Math.sin(phi - NG_TRUE_ORIGIN_LATITUDE) * Math.cos(phi + NG_TRUE_ORIGIN_LATITUDE)) +
                        (((15.0 / 8 * Math.pow(n, 2.0)) + (15.0 / 8 * Math.pow(n, 3.0))) * Math.sin(2 * (phi - NG_TRUE_ORIGIN_LATITUDE)) * Math.cos(2 * (phi + NG_TRUE_ORIGIN_LATITUDE))) -
                        ((35.0 / 24 * Math.pow(n, 3.0)) * Math.sin(3 * (phi - NG_TRUE_ORIGIN_LATITUDE)) * Math.cos(3 * (phi + NG_TRUE_ORIGIN_LATITUDE))));
        Log.d("ETRS89", "mu = " + mu);
        double numeral_i = mu + NG_TRUE_ORIGIN_NORTHINGS;
        Log.d("ETRS89", "I = " + numeral_i);
        double numeral_ii = (v / 2) * Math.sin(phi) * Math.cos(phi);
        Log.d("ETRS89", "II = " + numeral_ii);
        double numeral_iii = (v / 24) * Math.sin(phi) * (Math.pow(Math.cos(phi), 3.0) * (5 - Math.pow(Math.tan(phi), 2.0) + (9 * eta_squared)));
        Log.d("ETRS89", "III = " + numeral_iii);
        double numeral_iiia = (v / 720) * Math.sin(phi) * (Math.pow(Math.cos(phi), 5.0) * (61 - (58 * Math.pow(Math.tan(phi), 2.0)) + Math.pow(Math.tan(phi), 4.0)));
        Log.d("ETRS89", "IIIA = " + numeral_iiia);
        double numeral_iv = v * Math.cos(phi);
        Log.d("ETRS89", "IV = " + numeral_iv);
        double numeral_v = (v / 6) * (Math.pow(Math.cos(phi), 3.0) * (v / rho - Math.pow(Math.tan(phi), 2.0)));
        Log.d("ETRS89", "V = " + numeral_v);
        double numeral_vi = (v / 120) * (Math.pow(Math.cos(phi), 5.0) * (5 - (18 * Math.pow(Math.tan(phi), 2.0)) + Math.pow(Math.tan(phi), 4.0) + (14 * eta_squared) - ((58 * eta_squared) * (Math.pow(Math.tan(phi), 2.0)))));
        Log.d("ETRS89", "VI = " + numeral_vi);
        double northings = numeral_i + numeral_ii * (Math.pow(lambda - NG_TRUE_ORIGIN_LONGITUDE, 2.0)) + numeral_iii * (Math.pow(lambda - NG_TRUE_ORIGIN_LONGITUDE, 4.0)) + numeral_iiia * (Math.pow(lambda - NG_TRUE_ORIGIN_LONGITUDE, 6.0));
        Log.d("ETRS89", "Northings: " + northings + "m");
        double eastings = NG_TRUE_ORIGIN_EASTINGS + numeral_iv * (lambda - NG_TRUE_ORIGIN_LONGITUDE) + numeral_v * (Math.pow(lambda - NG_TRUE_ORIGIN_LONGITUDE, 3.0)) + numeral_vi * (Math.pow(lambda - NG_TRUE_ORIGIN_LONGITUDE, 5.0));
        Log.d("ETRS89", "Eastings: " + eastings + "m");

        if (northings < 0 || northings > 1250000 || eastings < 0 || eastings > 700000) {
            homeViewModel.UpdateOsgb36Location("Outside OSGB36 range.");
            return;
        }

        int east_index = (int) (eastings / 1000);
        osgb36Dx = eastings - (east_index * 1000);
        int north_index = (int) (northings / 1000);
        osgb36Dy = northings - (north_index * 1000);
        Log.d("ETRS89", "(east_index,north_index) = (" + east_index + "," + north_index + ")");
        int south_west_corner_record = (east_index + (north_index * 701) + 1);
        Log.d("ETRS89", "south_west_corner_record: " + south_west_corner_record);
        int south_east_corner_record = (east_index + 1 + (north_index * 701) + 1);
        Log.d("ETRS89", "south_east_corner_record: " + south_east_corner_record);
        int north_east_corner_record = (east_index + 1 + ((north_index + 1) * 701) + 1);
        Log.d("ETRS89", "north_east_corner_record: " + north_east_corner_record);
        int north_west_corner_record = (east_index + ((north_index + 1) * 701) + 1);
        Log.d("ETRS89", "north_west_corner_record: " + north_west_corner_record);
        Integer[] pointIds = new Integer[]{south_west_corner_record, south_east_corner_record, north_east_corner_record, north_west_corner_record};
        ostn15Repository.getOstn15Points(pointIds);
    }

    private void OSGB36ToOSGB36Grid() {
        /*
        This function contains transformations that are licensed BSD-2-Clause. The following required notice is in addition to those in files LICENSE and DATABASE-LICENSE and must be displayed in the software.
        © Copyright and database rights Ordnance Survey Limited 2016, © Crown copyright and database rights Land & Property Services 2016 and/or © Ordnance Survey Ireland, 2016. All rights reserved.
         */
        double phi = (52.0 + (39.0 / 60) + (27.2531 / 3600)) / 180 * Math.PI; // latitude in radians for worked example in Annex B, Transformations and OSGM15 user guide [v.1.3], Ordnance Survey Limited
        Log.d("OSGB36", "phi = " + phi);
        double lambda = (1.0 + (43.0 / 60) + (4.5177 / 3600)) / 180 * Math.PI; //longitude in radians for worked example in Annex B, Transformations and OSGM15 user guide [v.1.3], Ordnance Survey Limited
        Log.d("OSGB36", "lambda = " + lambda);
        Log.d("OSGB36", "© Copyright and database rights Ordnance Survey Limited 2016, © Crown copyright and database rights Land & Property Services 2016 and/or © Ordnance Survey Ireland, 2016. All rights reserved.");
        double n = (AIRY1830_SEMI_MAJOR_AXIS - AIRY1830_SEMI_MINOR_AXIS) / (AIRY1830_SEMI_MAJOR_AXIS + AIRY1830_SEMI_MINOR_AXIS);
        Log.d("OSGB36", "n = " + n);
        double v = (AIRY1830_SEMI_MAJOR_AXIS * NG_SCALE_FACTOR) * Math.pow((1 - AIRY1830_SQUARED_ECCENTRICITY * Math.pow(Math.sin(phi), 2.0)), -0.5);
        Log.d("OSGB36", "v = " + v);
        double rho = (AIRY1830_SEMI_MAJOR_AXIS * NG_SCALE_FACTOR) * (1 - AIRY1830_SQUARED_ECCENTRICITY) * Math.pow((1 - AIRY1830_SQUARED_ECCENTRICITY * Math.pow(Math.sin(phi), 2.0)), -1.5);
        Log.d("OSGB36", "rho = " + rho);
        double eta_squared = (v / rho) - 1;
        Log.d("OSGB36", "eta^2 = " + eta_squared);
        double mu = (AIRY1830_SEMI_MINOR_AXIS * NG_SCALE_FACTOR) *
                (((1 + n + (5.0 / 4 * Math.pow(n, 2.0)) + (5.0 / 4 * Math.pow(n, 3.0))) * (phi - NG_TRUE_ORIGIN_LATITUDE)) -
                        (((3 * n) + (3 * Math.pow(n, 2.0)) + (21.0 / 8 * Math.pow(n, 3.0))) * Math.sin(phi - NG_TRUE_ORIGIN_LATITUDE) * Math.cos(phi + NG_TRUE_ORIGIN_LATITUDE)) +
                        (((15.0 / 8 * Math.pow(n, 2.0)) + (15.0 / 8 * Math.pow(n, 3.0))) * Math.sin(2 * (phi - NG_TRUE_ORIGIN_LATITUDE)) * Math.cos(2 * (phi + NG_TRUE_ORIGIN_LATITUDE))) -
                        ((35.0 / 24 * Math.pow(n, 3.0)) * Math.sin(3 * (phi - NG_TRUE_ORIGIN_LATITUDE)) * Math.cos(3 * (phi + NG_TRUE_ORIGIN_LATITUDE))));
        Log.d("OSGB36", "mu = " + mu);
        double numeral_i = mu + NG_TRUE_ORIGIN_NORTHINGS;
        Log.d("OSGB36", "I = " + numeral_i);
        double numeral_ii = (v / 2) * Math.sin(phi) * Math.cos(phi);
        Log.d("OSGB36", "II = " + numeral_ii);
        double numeral_iii = (v / 24) * Math.sin(phi) * (Math.pow(Math.cos(phi), 3.0) * (5 - Math.pow(Math.tan(phi), 2.0) + (9 * eta_squared)));
        Log.d("OSGB36", "III = " + numeral_iii);
        double numeral_iiia = (v / 720) * Math.sin(phi) * (Math.pow(Math.cos(phi), 5.0) * (61 - (58 * Math.pow(Math.tan(phi), 2.0)) + Math.pow(Math.tan(phi), 4.0)));
        Log.d("OSGB36", "IIIA = " + numeral_iiia);
        double numeral_iv = v * Math.cos(phi);
        Log.d("OSGB36", "IV = " + numeral_iv);
        double numeral_v = (v / 6) * (Math.pow(Math.cos(phi), 3.0) * (v / rho - Math.pow(Math.tan(phi), 2.0)));
        Log.d("OSGB36", "V = " + numeral_v);
        double numeral_vi = (v / 120) * (Math.pow(Math.cos(phi), 5.0) * (5 - (18 * Math.pow(Math.tan(phi), 2.0)) + Math.pow(Math.tan(phi), 4.0) + (14 * eta_squared) - ((58 * eta_squared) * (Math.pow(Math.tan(phi), 2.0)))));
        Log.d("OSGB36", "VI = " + numeral_vi);
        double northings = numeral_i + numeral_ii * (Math.pow(lambda - NG_TRUE_ORIGIN_LONGITUDE, 2.0)) + numeral_iii * (Math.pow(lambda - NG_TRUE_ORIGIN_LONGITUDE, 4.0)) + numeral_iiia * (Math.pow(lambda - NG_TRUE_ORIGIN_LONGITUDE, 6.0));
        Log.d("OSGB36", "Northings: " + northings + "m");
        double eastings = NG_TRUE_ORIGIN_EASTINGS + numeral_iv * (lambda - NG_TRUE_ORIGIN_LONGITUDE) + numeral_v * (Math.pow(lambda - NG_TRUE_ORIGIN_LONGITUDE, 3.0)) + numeral_vi * (Math.pow(lambda - NG_TRUE_ORIGIN_LONGITUDE, 5.0));
        Log.d("OSGB36", "Eastings: " + eastings + "m");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        geocoder = new Geocoder(this, Locale.getDefault());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                long compareTime = System.currentTimeMillis() / 1000;
                Location location = locationResult.getLastLocation();
                if (lastUpdate == -1 || compareTime - lastUpdate >= 60 || lastUpdateAccuracy == -1 || location.getAccuracy() < lastUpdateAccuracy) {
                    lastUpdate = compareTime;
                    lastUpdateAccuracy = location.getAccuracy();
                    UpdatePosition(location);
                    try {
                        List<Address> geocoderLocations = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);
                        StringBuilder geocoderString = new StringBuilder();
                        for (int i = 0; i < geocoderLocations.size(); i++) {
                            geocoderString.append("* ").append(geocoderLocations.get(i).getAddressLine(0));
                            if (i + 1 < geocoderLocations.size()) {
                                geocoderString.append("\n");
                            }
                        }
                        homeViewModel.UpdateGeocoder(geocoderString.toString());
                    } catch (IOException e) {
                        homeViewModel.UpdateGeocoder("Not available.");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                if (locationAvailability.isLocationAvailable()) {
                    Toast.makeText(MainActivity.this, "Location is updating.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Location is not updating.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        createLocationRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        lastUpdateAccuracy = -1;
    }

    private void UpdatePosition(Location location) {
        if (location != null) {
            String latitude = Location.convert(location.getLatitude(), Location.FORMAT_SECONDS);
            String equatorHemisphere = latitude.startsWith("-") ? "S" : "N";
            double[] latitudeDMS = GetCoordinates(latitude);
            ETRS89ToOSGB36Grid(location);
            char maidenheadFieldLatitude = (char) ((equatorHemisphere.equals("S") && (latitudeDMS[0] > -90 || latitudeDMS[1] > 0 || latitudeDMS[2] > 0) ? (int) latitudeDMS[0] + 89 : (int) latitudeDMS[0] + 90) / 10 + 65);
            int maidenheadSquareLatitude = (int) (equatorHemisphere.equals("S") && (latitudeDMS[0] > -90 || latitudeDMS[1] > 0 || latitudeDMS[2] > 0) ? 9 - (latitudeDMS[0] % 10) : latitudeDMS[0] % 10);
            char maidenheadSubSquareLatitude = (char) ((equatorHemisphere.equals("S") && (latitudeDMS[0] > -90 || latitudeDMS[1] > 0 || latitudeDMS[2] > 0) ? 23 - ((latitudeDMS[1] * 60 + (int) latitudeDMS[2]) / 150) : (latitudeDMS[1] * 60 + (int) latitudeDMS[2]) / 150) + 65);
            int maidenheadExtendedSquareLatitude = (int) (equatorHemisphere.equals("S") && (latitudeDMS[0] > -90 || latitudeDMS[1] > 0 || latitudeDMS[2] > 0) ? 9 - (int) ((latitudeDMS[1] * 60 + (int) latitudeDMS[2]) % 150 / 15) : (int) ((latitudeDMS[1] * 60 + (int) latitudeDMS[2]) % 150 / 15));
            char maidenheadExtendedSubSquareLatitude = (char) ((equatorHemisphere.equals("S") && (latitudeDMS[0] > -90 || latitudeDMS[1] > 0 || latitudeDMS[2] > 0) ? 23 - (int) (latitudeDMS[2] % 15 / 0.625) : (int) (latitudeDMS[2] % 15 / 0.625)) + 65);
            int maidenheadExtendedExtendedSquareLatitude = (int) (equatorHemisphere.equals("S") && (latitudeDMS[0] > -90 || latitudeDMS[1] > 0 || latitudeDMS[2] > 0) ? 9 - (int) (latitudeDMS[2] % 0.625 / 0.0625) : (int) (latitudeDMS[2] % 0.625 / 0.0625));
            String longitude = Location.convert(location.getLongitude(), Location.FORMAT_SECONDS);
            String meridianHemisphere = longitude.startsWith("-") ? "W" : "E";
            double[] longitudeDMS = GetCoordinates(longitude);
            char maidenheadFieldLongitude = (char) ((meridianHemisphere.equals("W") && (longitudeDMS[0] > -180 || longitudeDMS[1] > 0 || longitudeDMS[2] > 0) ? (int) longitudeDMS[0] + 179 : (int) longitudeDMS[0] + 180 % 360) / 20 + 65);
            int maidenheadSquareLongitude = (int) (meridianHemisphere.equals("W") && (longitudeDMS[0] > -180 || longitudeDMS[1] > 0 || longitudeDMS[2] > 0) ? 9 - (longitudeDMS[0] % 20 / 2 * 2) : longitudeDMS[0] % 20 / 2 * 2);
            char maidenheadSubSquareLongitude = (char) ((meridianHemisphere.equals("W") && (longitudeDMS[0] > -180 || longitudeDMS[1] > 0 || longitudeDMS[2] > 0) ? 23 - ((int) longitudeDMS[1] / 5) : longitudeDMS[2] / 5) + 65);
            int maidenheadExtendedSquareLongitude = (int) (meridianHemisphere.equals("W") && (longitudeDMS[0] > -180 || longitudeDMS[1] > 0 || longitudeDMS[2] > 0) ? 9 - (int) ((longitudeDMS[1] * 60 + (int) longitudeDMS[2]) % 300 / 30) : (int) ((longitudeDMS[1] * 60 + (int) longitudeDMS[2]) % 300 / 30));
            char maidenheadExtendedSubSquareLongitude = (char) ((meridianHemisphere.equals("W") && (longitudeDMS[0] > -180 || longitudeDMS[1] > 0 || longitudeDMS[2] > 0) ? 23 - (int) (longitudeDMS[2] % 30 / 1.25) : (int) (longitudeDMS[2] % 30 / 1.25)) + 65);
            int maidenheadExtendedExtendedSquareLongitude = (int) (meridianHemisphere.equals("W") && (longitudeDMS[0] > -180 || longitudeDMS[1] > 0 || longitudeDMS[2] > 0) ? 9 - (int) (longitudeDMS[2] % 1.25 / 0.125) : (int) (longitudeDMS[2] % 1.25 / 0.125));
            String altitude = location.hasAltitude() ? location.getAltitude() + "m" : "N/A";
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(maidenheadFieldLongitude);
            stringBuilder.append(maidenheadFieldLatitude);
            stringBuilder.append(maidenheadSquareLongitude);
            stringBuilder.append(maidenheadSquareLatitude);
            stringBuilder.append(maidenheadSubSquareLongitude);
            stringBuilder.append(maidenheadSubSquareLatitude);
            stringBuilder.append(maidenheadExtendedSquareLongitude);
            stringBuilder.append(maidenheadExtendedSquareLatitude);
            stringBuilder.append(maidenheadExtendedSubSquareLongitude);
            stringBuilder.append(maidenheadExtendedSubSquareLatitude);
            stringBuilder.append(maidenheadExtendedExtendedSquareLongitude);
            stringBuilder.append(maidenheadExtendedExtendedSquareLatitude);
            Log.d("LOC", "Location: " +
                    (int) latitudeDMS[0] + "° " + (int) latitudeDMS[1] + "' " + latitudeDMS[2] + "\" " + equatorHemisphere + ", " +
                    (int) longitudeDMS[0] + "° " + (int) longitudeDMS[1] + "' " + longitudeDMS[2] + "\" " + meridianHemisphere + ", (" +
                    stringBuilder.toString() + "), " + altitude + ", " + location.getAccuracy());
            String coordinatesDMS = Math.abs((int) latitudeDMS[0]) + "° " + (int) latitudeDMS[1] + "' " + latitudeDMS[2] + "\" " + equatorHemisphere + ",\n" +
                    Math.abs((int) longitudeDMS[0]) + "° " + (int) longitudeDMS[1] + "' " + longitudeDMS[2] + "\" " + meridianHemisphere;
            String coordinatesDecimal = Math.abs(location.getLatitude()) + "° " + equatorHemisphere + ",\n" + Math.abs(location.getLongitude()) + "° " + meridianHemisphere;
            char[] plusCodeAlphabet = {'2', '3', '4', '5', '6', '7', '8', '9', 'C', 'F', 'G', 'H', 'J', 'M', 'P', 'Q', 'R', 'V', 'W', 'X'};
            StringBuilder plusCode = new StringBuilder();
            plusCode.append(plusCodeAlphabet[(equatorHemisphere.equals("S") && (latitudeDMS[0] > -90 || latitudeDMS[1] > 0 || latitudeDMS[2] > 0) ? (int) latitudeDMS[0] + 89 : (int) latitudeDMS[0] + 90) / 20]);
            plusCode.append(plusCodeAlphabet[(meridianHemisphere.equals("W") && (longitudeDMS[0] > -180 || longitudeDMS[1] > 0 || longitudeDMS[2] > 0) ? (int) longitudeDMS[0] + 179 : (int) longitudeDMS[0] + 180 % 360) / 20]);
            plusCode.append(plusCodeAlphabet[(equatorHemisphere.equals("S") && (latitudeDMS[0] > -90 || latitudeDMS[1] > 0 || latitudeDMS[2] > 0) ? (int) latitudeDMS[0] + 89 : (int) latitudeDMS[0] + 90) % 20]);
            plusCode.append(plusCodeAlphabet[(meridianHemisphere.equals("W") && (longitudeDMS[0] > -180 || longitudeDMS[1] > 0 || longitudeDMS[2] > 0) ? (int) longitudeDMS[0] + 179 : (int) longitudeDMS[0] + 180 % 360) % 20]);
            plusCode.append(plusCodeAlphabet[equatorHemisphere.equals("S") && (latitudeDMS[0] > -90 || latitudeDMS[1] > 0 || latitudeDMS[2] > 0) ? 19 - (int) (latitudeDMS[1] / 3) : (int) (latitudeDMS[1] / 3)]);
            plusCode.append(plusCodeAlphabet[meridianHemisphere.equals("W") && (longitudeDMS[0] > -180 || longitudeDMS[1] > 0 || longitudeDMS[2] > 0) ? 19 - (int) (longitudeDMS[1] / 3) : (int) longitudeDMS[2] / 3]);
            plusCode.append(plusCodeAlphabet[equatorHemisphere.equals("S") && (latitudeDMS[0] > -90 || latitudeDMS[1] > 0 || latitudeDMS[2] > 0) ? 19 - (int) ((latitudeDMS[1] * 60 + (int) latitudeDMS[2]) % 180 / 9) : (int) ((latitudeDMS[1] * 60 + (int) latitudeDMS[2]) % 180 / 9)]);
            plusCode.append(plusCodeAlphabet[meridianHemisphere.equals("W") && (longitudeDMS[0] > -180 || longitudeDMS[1] > 0 || longitudeDMS[2] > 0) ? 19 - (int) ((longitudeDMS[1] * 60 + (int) longitudeDMS[2]) % 180 / 9) : (int) ((longitudeDMS[1] * 60 + (int) longitudeDMS[2]) % 180 / 9)]);
            plusCode.append('+');
            plusCode.append(plusCodeAlphabet[equatorHemisphere.equals("S") && (latitudeDMS[0] > -90 || latitudeDMS[1] > 0 || latitudeDMS[2] > 0) ? 19 - (int) ((latitudeDMS[1] * 60 + latitudeDMS[2]) % 9 / 0.45) : (int) ((latitudeDMS[1] * 60 + latitudeDMS[2]) % 9 / 0.45)]);
            plusCode.append(plusCodeAlphabet[meridianHemisphere.equals("W") && (longitudeDMS[0] > -180 || longitudeDMS[1] > 0 || longitudeDMS[2] > 0) ? 19 - (int) ((longitudeDMS[1] * 60 + longitudeDMS[2]) % 9 / 0.45) : (int) ((longitudeDMS[1] * 60 + longitudeDMS[2]) % 9 / 0.45)]);
            plusCode.append(plusCodeAlphabet[
                    4 * (equatorHemisphere.equals("S") && (latitudeDMS[0] > -90 || latitudeDMS[1] > 0 || latitudeDMS[2] > 0) ? 4 - (int) ((latitudeDMS[1] * 60 + latitudeDMS[2]) % 0.45 / 0.09) : (int) ((latitudeDMS[1] * 60 + latitudeDMS[2]) % 0.45 / 0.09))
                            + (meridianHemisphere.equals("W") && (longitudeDMS[0] > -180 || longitudeDMS[1] > 0 || longitudeDMS[2] > 0) ? 3 - (int) ((longitudeDMS[1] * 60 + longitudeDMS[2]) % 0.45 / 0.1125) : (int) ((longitudeDMS[1] * 60 + longitudeDMS[2]) % 0.45 / 0.1125))
                    ]);
            Log.d("LOC", "PlusCode: " + plusCode.toString());
            homeViewModel.UpdateCoordinates(coordinatesDMS, coordinatesDecimal, stringBuilder.toString(), plusCode.toString(), location.getAccuracy());
            double altitudeDouble = location.hasAltitude() ? location.getAltitude() : -1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (location.hasVerticalAccuracy()) {
                    homeViewModel.UpdateAltitude(altitudeDouble, location.getVerticalAccuracyMeters());
                } else {
                    homeViewModel.UpdateAltitude(altitudeDouble, -1);
                }
            } else {
                homeViewModel.UpdateAltitude(altitudeDouble, -1);
            }
        } else {
            Log.d("LOC", "NULL");
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            canAccessLocation = false;
            requestingLocationUpdates = false;
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        requestingLocationUpdates = true;
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    canAccessLocation = true;
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    canAccessLocation = false;
                    Toast.makeText(this, "Tympanum cannot access location data.", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setMaxWaitTime(15000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setWaitForAccurateLocation(true);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(locationSettingsResponse -> {
            Log.d("LOC", "Location usable: " + locationSettingsResponse.getLocationSettingsStates().isLocationUsable());
            canAccessLocation = true;
            startLocationUpdates();
        });
        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    resolvableApiException.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendIntentException) {
                    sendIntentException.printStackTrace();
                }
            }
        });
    }

    private double[] GetCoordinates(String coordinateString) {
        String[] coordinateStrings = coordinateString.split(":");
        double[] coordinateValues = new double[coordinateStrings.length];
        for (int i = 0; i < coordinateStrings.length; i++) {
            coordinateValues[i] = Double.parseDouble(coordinateStrings[i]);
        }
        return coordinateValues;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        UpdatePosition(location);
    }
}
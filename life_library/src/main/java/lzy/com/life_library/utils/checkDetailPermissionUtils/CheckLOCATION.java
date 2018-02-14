package lzy.com.life_library.utils.checkDetailPermissionUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.List;

/**
 * Created by lizhiyun on 2018/2/13.
 */

public class CheckLOCATION implements Check {
    @SuppressLint("MissingPermission")
    @Override
    public Boolean check(Context context) throws Throwable {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> list = locationManager.getProviders(true);

        if (list.contains(LocationManager.GPS_PROVIDER)) {
            return true;
        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            return true;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, new MLocationListener(locationManager));
        }
        return true;
    }
    private static class MLocationListener implements LocationListener {
        private LocationManager mManager;

        public MLocationListener(LocationManager manager) {
            mManager = manager;
        }

        @Override
        public void onLocationChanged(Location location) {
            mManager.removeUpdates(this);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            mManager.removeUpdates(this);
        }

        @Override
        public void onProviderEnabled(String provider) {
            mManager.removeUpdates(this);
        }

        @Override
        public void onProviderDisabled(String provider) {
            mManager.removeUpdates(this);
        }
    }
}

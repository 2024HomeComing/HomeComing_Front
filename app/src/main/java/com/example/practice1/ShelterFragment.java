package com.example.practice1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ShelterFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "ShelterFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int SEARCH_RADIUS = 1000000; // 검색 반경 (미터)
    private GoogleMap mMap;

    // 현재 위치 좌표
    private LatLng currentLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shelter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            showMap();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void showMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "MapFragment is null");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // 현재 위치를 가져와서 지도의 가운데로 이동합니다.
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15)); // 15는 줌 레벨, 1~20까지 가능
                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("내 위치"));

                        // API 호출하여 보호소 데이터 가져오기
                        fetchTotalCountAndShelterData();
                    } else {
                        Log.e(TAG, "Location is null");
                    }
                }).addOnFailureListener(e -> Log.e(TAG, "Failed to get location", e));
    }


    private void fetchTotalCountAndShelterData() {
        String apiUrl = getString(R.string.api_url);
        String apiKey = getString(R.string.api_key);
        String url = apiUrl + "?serviceKey=" + apiKey + "&numOfRows=1&pageNo=1"; // 전체 결과 수만 가져오기 위해 numOfRows=1

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int totalCount = parseTotalCount(response);
                        fetchAllShelterData(totalCount);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "API Error: " + error.getMessage());
            }
        });

        queue.add(stringRequest);
    }

    private int parseTotalCount(String xml) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                if (eventType == XmlPullParser.START_TAG && tagName.equalsIgnoreCase("totalCount")) {
                    return Integer.parseInt(parser.nextText());
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void fetchAllShelterData(int totalCount) {
        String apiUrl = getString(R.string.api_url);
        String apiKey = getString(R.string.api_key);
        String url = apiUrl + "?serviceKey=" + apiKey + "&numOfRows=" + totalCount + "&pageNo=1";

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "API Response: " + response); // API 호출 성공 로그
                        List<Shelter> shelters = parseXML(response);
                        Log.d(TAG, "Parsed shelters: " + shelters); // 파싱된 데이터 로그
                        if (shelters != null && shelters.size() > 0) {
                            for (Shelter shelter : shelters) {
                                if (shelter.lat != null && shelter.lng != null) { // 위치 데이터가 있는 경우에만 처리
                                    LatLng shelterLocation = new LatLng(Double.parseDouble(shelter.lat), Double.parseDouble(shelter.lng));
                                    double distance = getDistance(currentLocation.latitude, currentLocation.longitude, shelterLocation.latitude, shelterLocation.longitude);
                                    Log.d(TAG, "Shelter: " + shelter.name + ", Distance: " + distance); // 거리 계산 로그
                                    if (distance <= SEARCH_RADIUS) {
                                        mMap.addMarker(new MarkerOptions().position(shelterLocation).title(shelter.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                                    }
                                }
                            }
                        } else {
                            Log.e(TAG, "No shelter data found");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null && error.networkResponse != null && error.networkResponse.data != null) {
                    String errorMsg = new String(error.networkResponse.data);
                    Log.e(TAG, "API Error: " + errorMsg);
                } else {
                    Log.e(TAG, "API Error: Unknown error occurred");
                }
            }
        });

        queue.add(stringRequest);
    }

    private List<Shelter> parseXML(String xml) {
        List<Shelter> shelters = new ArrayList<>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();
            Shelter currentShelter = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase("item")) {
                            currentShelter = new Shelter();
                        } else if (currentShelter != null) {
                            if (tagName.equalsIgnoreCase("careNm")) {
                                currentShelter.name = parser.nextText();
                            } else if (tagName.equalsIgnoreCase("careAddr")) {
                                currentShelter.address = parser.nextText();
                            } else if (tagName.equalsIgnoreCase("lat")) {
                                currentShelter.lat = parser.nextText();
                            } else if (tagName.equalsIgnoreCase("lng")) {
                                currentShelter.lng = parser.nextText();
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase("item") && currentShelter != null) {
                            shelters.add(currentShelter);
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return shelters;
    }

    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c; // Distance in km
        return d * 1000; // Convert to meters
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showMap(); // 권한이 허용되면 지도를 초기화
            } else {
                Toast.makeText(requireContext(), "Location permission is required", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            }
        }
    }

    static class Shelter {
        String name;
        String address;
        String lat;
        String lng;
    }
}
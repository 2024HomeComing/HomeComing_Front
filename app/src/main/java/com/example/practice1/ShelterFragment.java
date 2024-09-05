package com.example.practice1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ShelterFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "ShelterFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private List<Shelter> shelters;
    private LatLng currentLocation;

    private EditText searchEditText;
    private Button searchButton;  // Change to Button

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shelter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchEditText = view.findViewById(R.id.searchEditText);
        searchButton = view.findViewById(R.id.searchButton);  // Now matches Button

        // Set onClickListener for the button
        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString();
            searchShelter(query);
        });

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            showMap();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Show an explanation to the user
            Toast.makeText(requireContext(), "Location permission is needed to show your location on the map.", Toast.LENGTH_LONG).show();
        }
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showMap();
            } else {
                Toast.makeText(requireContext(), "Permission denied. Unable to access location.", Toast.LENGTH_SHORT).show();
            }
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

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("내 위치"));

                    fetchShelterData();
                } else {
                    Log.e(TAG, "Location is null");
                }
            }).addOnFailureListener(e -> Log.e(TAG, "Failed to get location", e));
        } else {
            // Permission not granted
            Log.e(TAG, "Location permission not granted");
        }
    }

    private void fetchShelterData() {
        String apiUrl = getString(R.string.api_url);
        String apiKey = getString(R.string.api_key);
        String url = apiUrl + "?serviceKey=" + apiKey + "&numOfRows=1000&pageNo=1"; // API 요청 URL

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        shelters = parseXML(response);
                        addSheltersToMap();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "API Error: " + error.getMessage());
            }
        });

        queue.add(stringRequest);
    }

    private void addSheltersToMap() {
        if (shelters != null && !shelters.isEmpty()) {
            for (Shelter shelter : shelters) {
                if (shelter.lat != null && shelter.lng != null) {
                    try {
                        LatLng location = new LatLng(Double.parseDouble(shelter.lat), Double.parseDouble(shelter.lng));
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(location)
                                .title(shelter.name)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        );
                        marker.setTag(shelter);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Invalid lat/lng format: " + shelter.lat + ", " + shelter.lng);
                    }
                } else {
                    Log.e(TAG, "Latitude or Longitude is null for shelter: " + shelter.name);
                }
            }

            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View infoView = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                    Shelter shelter = (Shelter) marker.getTag();

                    if (shelter != null) {
                        TextView nameTextView = infoView.findViewById(R.id.nameTextView);
                        TextView organizationTextView = infoView.findViewById(R.id.organizationTextView);
                        TextView targetAnimalsTextView = infoView.findViewById(R.id.targetAnimalsTextView);
                        TextView addressTextView = infoView.findViewById(R.id.addressTextView);
                        TextView jibunAddressTextView = infoView.findViewById(R.id.jibunAddressTextView);
                        TextView phoneNumberTextView = infoView.findViewById(R.id.phoneNumberTextView);
                        TextView openingHoursTextView = infoView.findViewById(R.id.openingHoursTextView);
                        TextView closingHoursTextView = infoView.findViewById(R.id.closingHoursTextView);

                        nameTextView.setText(shelter.name);
                        organizationTextView.setText(shelter.organization);
                        targetAnimalsTextView.setText(shelter.targetAnimals);
                        addressTextView.setText(shelter.address);
                        jibunAddressTextView.setText(shelter.jibunAddress);
                        phoneNumberTextView.setText(shelter.phoneNumber);
                        openingHoursTextView.setText("Open: " + shelter.openingHours);
                        closingHoursTextView.setText("Close: " + shelter.closingHours);
                    }

                    return infoView;
                }
            });

            mMap.setOnInfoWindowClickListener(marker -> {
                Shelter shelter = (Shelter) marker.getTag();
                if (shelter != null) {
                    Log.d(TAG, "Shelter Info: ");
                    Log.d(TAG, "Name: " + shelter.name);
                    Log.d(TAG, "Organization: " + shelter.organization);
                    Log.d(TAG, "Target Animals: " + shelter.targetAnimals);
                    Log.d(TAG, "Address: " + shelter.address);
                    Log.d(TAG, "Jibun Address: " + shelter.jibunAddress);
                    Log.d(TAG, "Phone Number: " + shelter.phoneNumber);
                    Log.d(TAG, "Opening Hours: " + shelter.openingHours);
                    Log.d(TAG, "Closing Hours: " + shelter.closingHours);
                }
            });
        } else {
            Log.e(TAG, "No shelters found");
        }
    }


    private void searchShelter(String query) {
        if (shelters != null && !shelters.isEmpty()) {
            for (Shelter shelter : shelters) {
                if (shelter.name != null && shelter.name.contains(query)) {
                    LatLng location = new LatLng(Double.parseDouble(shelter.lat), Double.parseDouble(shelter.lng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                    return;
                }
            }
            Toast.makeText(requireContext(), "보호소를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
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
                            } else if (tagName.equalsIgnoreCase("orgNm")) {
                                currentShelter.organization = parser.nextText();
                            } else if (tagName.equalsIgnoreCase("saveTrgtAnimal")) {
                                currentShelter.targetAnimals = parser.nextText();
                            } else if (tagName.equalsIgnoreCase("careAddr")) {
                                currentShelter.address = parser.nextText();
                            } else if (tagName.equalsIgnoreCase("jibunAddr")) {
                                currentShelter.jibunAddress = parser.nextText();
                            } else if (tagName.equalsIgnoreCase("lat")) {
                                currentShelter.lat = parser.nextText();
                            } else if (tagName.equalsIgnoreCase("lng")) {
                                currentShelter.lng = parser.nextText();
                            } else if (tagName.equalsIgnoreCase("careTel")) {
                                currentShelter.phoneNumber = parser.nextText();
                            } else if (tagName.equalsIgnoreCase("openTime")) {
                                currentShelter.openingHours = parser.nextText(); // 운영 시작 시간
                            } else if (tagName.equalsIgnoreCase("closeTime")) {
                                currentShelter.closingHours = parser.nextText(); // 운영 종료 시간
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

        } catch (Exception e) {
            Log.e(TAG, "XML Parsing Error", e);
        }
        return shelters;
    }


    public class Shelter {
        String name;
        String organization;
        String targetAnimals;
        String address;
        String jibunAddress;
        String lat;
        String lng;
        String phoneNumber;
        String openingHours; // 운영 시작 시간
        String closingHours; // 운영 종료 시간
    }
}
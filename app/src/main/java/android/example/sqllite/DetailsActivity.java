package android.example.sqllite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    DatabaseHelper db;

    ListView userList;
    ArrayList<String> listItem;
    ArrayAdapter adapter;

    private String number;
    private String currentLocation = "";
    private static final int REQUEST_CALL = 1;
    final int SEND_SMS_REQUEST_CODE = 1;
    int CODE = 0;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        userList = findViewById(R.id.list_view);

        db = new DatabaseHelper(this);

        listItem = new ArrayList<>();
        viewData();
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                number = userList.getItemAtPosition(position).toString();

                long i = Long.parseLong(number);
                if(i <= 999999999){
                    Toast.makeText(DetailsActivity.this, "Select Again", Toast.LENGTH_SHORT).show();
                }else{
                    makePhoneCall(number);
                    getLocationPermission();
                    Toast.makeText(DetailsActivity.this, number, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // INITIALIZE FUSED LOCATION
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if(checkPermission(Manifest.permission.SEND_SMS)){
            CODE = 1;
        }else{
            ActivityCompat.requestPermissions(this,
            new String[] {Manifest.permission.SEND_SMS},SEND_SMS_REQUEST_CODE);
        }
    }

    private void viewData(){
        Cursor cursor = db.viewDataList();

        if(cursor.getCount() == 0){
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }
        else{
            while (cursor.moveToNext()){
                listItem.add(cursor.getString(0));
                listItem.add(cursor.getString(2));
            }

            adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listItem);
            userList.setAdapter(adapter);
        }
    }

    private void makePhoneCall(final String no){
        String ContactNumber = no;
        if (ContextCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + ContactNumber;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    private void getLocationPermission(){
        if(ActivityCompat.checkSelfPermission(DetailsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // WHEN PERMISSION GRANTED
            getLocationData();
        }
        else{
            ActivityCompat.requestPermissions(DetailsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
    }

    private void getLocationData(){
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                // INITIALIZE LOCATION
                Location location = task.getResult();
                if(location != null){

                    try {
                        // INITIALIZE GEO-CODER
                        Geocoder geocoder = new Geocoder(DetailsActivity.this, Locale.getDefault());

                        //INITIALIZE ADDRESS LIST
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(),location.getLongitude(),1);

                        // set latitude
                        //currentLocation += addresses.get(0).getLatitude();
                        //set longitude
                        //currentLocation += addresses.get(0).getLongitude();
                        //set country name
                        //currentLocation += addresses.get(0).getCountryName();
                        //set locality
                        //currentLocation += addresses.get(0).getLocality();
                        //set address

                        currentLocation += addresses.get(0).getAddressLine(0);
                        sendSms();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void sendSms(){
        String phone = number;
        String message = "Need HELP! Address \n" + currentLocation;
        if(checkPermission(Manifest.permission.SEND_SMS)){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone,null,message,null,null);
            Toast.makeText(this, "Message Sent!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this,permission);
        return(check==PackageManager.PERMISSION_GRANTED);
    }
}

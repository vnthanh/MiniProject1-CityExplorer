package com.example.user.cityexplorer;

import android.content.Intent;
import android.content.res.AssetManager;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class PlacesActivity extends AppCompatActivity {

    // Load from file again, entire places info
    ArrayList<Place> PlacesFromFile = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        AssetManager am = this.getAssets();
        try {
            LoadPlacesFromFile(am.open("places.txt")); // Load from file and store in ArrayList<Place>
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add scroll view, and ll inside wrap all orther view
        LinearLayout ll = (LinearLayout) findViewById(R.id.LinearLayoutInsideScrollView);


        for(int i=0;i<PlacesFromFile.size();i++)
        {
            Button bt = new Button(this);
            bt.setText(PlacesFromFile.get(i).name);
            bt.setId(i); // Set id = i each button added -> important for later event

            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            bt.setLayoutParams(p);

            ll.addView(bt);

            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Return value (button clicked id to parent activity)
                    // Go back to parent activity
                    Intent intent = new Intent();
                    intent.putExtra("buttonClickedId",v.getId());
                    setResult(RESULT_OK,intent);
                    finish();
                    //============================================ go back
                }
            });
        }
    }

    public void LoadPlacesFromFile(InputStream inputStream)
    {
        com.example.user.cityexplorer.Place tempPlace;
        LatLng tempPosition;
        double tempLat, tempLng; // Lat and Lng
        String tempName;
        String tempPhone;
        String tempWebsite;
        String tempDes;
        boolean tempBookmark;


        Scanner scan  = new Scanner(inputStream);

        // Be carefull , scanner type: double, int, -> mismatch bug
        int nPlaces = scan.nextInt();

        for(int i=0;i<nPlaces;i++)
        {
            tempLat = scan.nextDouble();
            tempLng = scan.nextDouble();
            tempPosition = new LatLng(tempLat,tempLng);
            scan.nextLine();
            tempName = scan.nextLine();
            tempPhone = scan.nextLine();
            tempWebsite = scan.nextLine();
            tempDes = scan.nextLine();
            tempBookmark = scan.nextBoolean();

            tempPlace = new com.example.user.cityexplorer.Place(tempPosition,tempName,tempPhone,tempWebsite,tempDes,tempBookmark);
            PlacesFromFile.add(tempPlace);
        }

        scan.close();
    }
}

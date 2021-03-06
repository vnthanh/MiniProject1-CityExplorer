package com.example.user.cityexplorer;

import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class BookmarkActivity extends AppCompatActivity {

    // Load from file again, entire places info
    ArrayList<Place> PlacesFromFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        AssetManager am = this.getAssets();
        // first time launch: check if there is a file in getFileDir, if not, open in assets
        // Dummy code for BookMark Act and ScrollingAct only
        try {
            PlacesFromFile = FileManager.LoadPlacesFromFile(openFileInput("places.txt")); // Load from file and store in ArrayList<Place>
        } catch (IOException e) {
            //e.printStackTrace();
            try {
                PlacesFromFile = FileManager.LoadPlacesFromFile(am.open("places.txt")); // if not found (not create) in fileDir, go on this file
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        // Add scroll view, and ll inside wrap all orther view
        LinearLayout ll = (LinearLayout) findViewById(R.id.LinearLayoutInsideScrollView);


        for(int i=0;i<PlacesFromFile.size();i++)
        {
            // Only pick place which has isBookmark == tru to display as Button
            if(PlacesFromFile.get(i).isBookmark == true){
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
    }

}

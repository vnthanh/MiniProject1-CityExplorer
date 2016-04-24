package com.example.user.cityexplorer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Field;

public class ScrollingActivity extends AppCompatActivity {

    Button bt_Call,bt_GoWeb,bt_Bookmark;
    ImageView iv_PlaceImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        // set activity name : to place name
        this.setTitle(name);

        // Set text: place decription
        String description = intent.getStringExtra("description");
        TextView tv = (TextView) findViewById(R.id.ScrollActText);
        tv.setText(description);

        String phone = intent.getStringExtra("phone");
        String website = intent.getStringExtra("website");

        bt_Call = (Button) findViewById(R.id.bt_Call);
        bt_GoWeb = (Button) findViewById(R.id.bt_GoWeb);
        bt_Bookmark = (Button) findViewById(R.id.bt_Bookmark);

        bt_Call.setText("Call: " + phone);
        bt_GoWeb.setText("Go to Web: " + website);


        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx   Load image to image view by place name - place image name
        iv_PlaceImage = (ImageView) findViewById(R.id.iv_PlaceImage);
        // Process name and get res id, set
        String placeNameToGetImage = PlaceNameStringProcess(name);
        int picId = getResources().getIdentifier(placeNameToGetImage, "drawable", getApplicationContext().getPackageName());
        iv_PlaceImage.setImageResource(picId);
    }

    // Process: clear space, toLowercase to get place image from res/drawable
    public String PlaceNameStringProcess(String before){
        String after = before.replaceAll(" ","");
        after = after.toLowerCase(); // Note: It doesn't work because strings are immutable. You need to reassign
        return after;
    }

    // Transform string to res id
    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


}

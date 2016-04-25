package com.example.user.cityexplorer;

import com.google.android.gms.maps.model.LatLng;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by USER on 4/25/2016.
 */
public class FileManager {
    public static ArrayList<Place> LoadPlacesFromFile(InputStream inputStream){
        ArrayList<Place> PlacesFromFile = new ArrayList<>();

        com.example.user.cityexplorer.Place tempPlace;
        LatLng tempPosition;
        double tempLat, tempLng; // Lat and Lng
        String tempName;
        String tempPhone;
        String tempWebsite;
        String tempDes;
        boolean tempBookmark;
        // input Stream from assets
        Scanner scan = new Scanner(inputStream);

        // Be carefull , scanner type: double, int, -> mismatch bug

        /*String n = scan.nextLine();
        int nPlaces = Integer.parseInt(n);*/
        int nPlaces = scan.nextInt();

        for (int i = 0; i < nPlaces; i++) {
            tempLat = scan.nextDouble();
            tempLng = scan.nextDouble();
            tempPosition = new LatLng(tempLat, tempLng);
            scan.nextLine();
            tempName = scan.nextLine();
            tempPhone = scan.nextLine();
            tempWebsite = scan.nextLine();
            tempDes = scan.nextLine();
            tempBookmark = scan.nextBoolean();

            tempPlace = new com.example.user.cityexplorer.Place(tempPosition, tempName, tempPhone, tempWebsite, tempDes,tempBookmark);
            PlacesFromFile.add(tempPlace);
        }

        scan.close();

        return PlacesFromFile;
    }

    public static void SavePlacesToFile(ArrayList<Place> places, PrintStream output) {
        output.println(places.size());

        for (int i = 0; i < places.size(); i++) {
            output.println(places.get(i).postion.latitude);
            output.println(places.get(i).postion.longitude); // bug copy, changed
            output.println(places.get(i).name);
            output.println(places.get(i).phone);
            output.println(places.get(i).website);
            output.println(places.get(i).description);
            output.println(places.get(i).isBookmark);
        }

        output.close();
    }
}

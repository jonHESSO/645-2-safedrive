package ch.safedrive.safedrive.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import ch.safedrive.safedrive.model.Location;

public class PopulateFirebase {

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public PopulateFirebase() {
        database = FirebaseDatabase.getInstance();
    }

    public void populateLocations() {
        myRef = database.getReference("locations");

        List<Location> locationList = new ArrayList<>();

        Location l1 = new Location("Monthey", 46.252370, 6.947540, true, true);
        Location l2 = new Location("Sion", 46.233124, 7.360626, true, false);
        Location l3 = new Location("Sierre", 46.293999, 7.533220, false, true);
        Location l4 = new Location("Conthey", 46.233420, 7.308320, true, false);
        Location l5 = new Location("Martigny", 46.101710, 7.073480, true, true);

        locationList.add(l1);
        locationList.add(l2);
        locationList.add(l3);
        locationList.add(l4);
        locationList.add(l5);

        for (Location l : locationList) {
            myRef.push().setValue(l);
        }
    }
}

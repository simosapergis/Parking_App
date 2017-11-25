package helperClasses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Geocoder;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sapergis.parking.R;

import java.util.ArrayList;
import java.util.List;

import database.ParkingDBHelper;
import database.ParkingLocationDBContract;
import database.StoreToDatabase;
import objects.ParkingPositionObject;

public class FireBaseTestData {
    private static final String ENTRIES = "entries";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private Context context;

    public FireBaseTestData(Context context){
        this.context = context;
        initiateFirebaseConnection();
    }

    public void requestTestListFromFireBase(){
        myRef = firebaseDatabase.getReference(ENTRIES);
    }

    public void postTestListToFireBase(List<ParkingPositionObject> entries){
        for(ParkingPositionObject ppo : entries){
            myRef.child(ENTRIES).child(String.valueOf(ppo.getId())).setValue(ppo);
        }
    }

    private void initiateFirebaseConnection(){
      myRef = firebaseDatabase.getReference();
      myRef.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              List<ParkingPositionObject> testEntries = new ArrayList<>();
              DataSnapshot entriesSnapshot = dataSnapshot.child(ENTRIES);
              Iterable<DataSnapshot> entriesChildren = entriesSnapshot.getChildren();
              for(DataSnapshot firebaseEntry : entriesChildren){
                  ParkingPositionObject ppo = firebaseEntry.getValue(ParkingPositionObject.class);
                  testEntries.add(ppo);
              }
              SQLiteDatabase readableDatabase = new ParkingDBHelper(context).getWritableDatabase();
              StoreToDatabase.storeFirebaseTestData(testEntries , readableDatabase);
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });
      myRef.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              //String value = dataSnapshot.getValue(String.class);
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {
              Log.w(Helper.TAG , "Error", databaseError.toException());
          }
      });
      myRef.addChildEventListener(new ChildEventListener() {
          @Override
          public void onChildAdded(DataSnapshot dataSnapshot, String s) {
              System.out.print(s);
          }

          @Override
          public void onChildChanged(DataSnapshot dataSnapshot, String s) {

          }

          @Override
          public void onChildRemoved(DataSnapshot dataSnapshot) {

          }

          @Override
          public void onChildMoved(DataSnapshot dataSnapshot, String s) {

          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });
    }
}

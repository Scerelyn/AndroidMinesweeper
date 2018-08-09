package neumont.minesweeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Firebase.setAndroidContext(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void FirebaseRefExample(){
        Firebase myFirebaseRef = new Firebase("https://<YOUR-FIREBASE-APP>.firebaseio.com/");

        //Write data to Firebase
        myFirebaseRef.child("message").setValue("Do you have data? You'll love Firebase.");

        //Read data from FireBase
        myFirebaseRef.child("message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());
            }
            @Override public void onCancelled(FirebaseError error) { }
        });
        // Tutorial Link
        // https://www.firebase.com/docs/android/quickstart.html
    }

}

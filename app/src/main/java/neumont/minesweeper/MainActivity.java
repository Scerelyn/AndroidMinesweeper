package neumont.minesweeper;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

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

        Minefield m = new Minefield(10, 10, 10);
        buildButtonGrid(10,10, m);
        TableLayout ty = findViewById(R.id.MinefieldTableLayout);
    }

    /**
     * Builds a grid of buttons with a given row and column count into the minefield TableLayout instance
     * @param rowCount The amount of rows to build
     * @param colCount The amount of columns to build
     */
    private void buildButtonGrid(int rowCount, int colCount, final Minefield m){
        TableLayout ty = findViewById(R.id.MinefieldTableLayout); //get the table
        for(int i = 0; i < rowCount; i++){
            TableRow tr = new TableRow(this); // new row
            tr.setBackgroundColor(getResources().getColor(R.color.cellColorNormal)); // set color
            for(int j = 0; j < colCount; j++){
                final Button b = new Button(this); // new button
                b.setText(m.GetCells()[i][j].getDisplay()); // set text
                b.setBackgroundTintList(getResources().getColorStateList(R.color.cellColorNormal)); // set its color
                final int row = i;
                final int col = j;
                b.setOnClickListener(new View.OnClickListener() { // now for an on click
                    @Override
                    public void onClick(View v) {
                        m.FlipCell(m.GetCells()[row][col]);
                        b.setText(m.GetCells()[row][col].getDisplay());  // change text
                        Log.i("event handler", b.getText()+"");
                        b.setBackgroundTintList(getResources().getColorStateList(R.color.cellColorClicked)); // change color
                    }
                });
                tr.addView(b); // add the button
                ViewGroup.LayoutParams params = b.getLayoutParams(); // get its layout parameters
                params.height = dpToPixel(40); // change height and width using a dp to pixel converter method
                params.width = dpToPixel(35);
                b.setLayoutParams(params);
                b.requestLayout(); // update layouts
                tr.requestLayout();
            }
            ty.addView(tr);
            ty.requestLayout();
        }
    }

    /**
     * Converts a given number in dp into its respective pixel amount
     * @param dp The dp to use
     * @return The pixels that the given amount of dp gives
     */
    public int dpToPixel(int dp){
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);
        return pixels;
    }

    public void FirebaseRefExample(){
        Firebase myFirebaseRef = new Firebase("https://androidminesweeper.firebaseio.com/");

        //Write data to Firebase
        myFirebaseRef.child("Cell").setValue("Real");

        //Read data from FireBase
//        myFirebaseRef.child("Cell").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                System.out.println(snapshot.getValue());
//            }
//            @Override public void onCancelled(FirebaseError error) { }
//        });
        // Tutorial Link
        // https://www.firebase.com/docs/android/quickstart.html
    }

}

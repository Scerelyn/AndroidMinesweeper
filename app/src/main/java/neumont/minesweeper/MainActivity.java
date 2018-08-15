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
    boolean isFlagMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Firebase.setAndroidContext(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Button ngButton = findViewById(R.id.NewGameButton);
        ngButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableLayout ty = findViewById(R.id.MinefieldTableLayout);
                ty.removeAllViews();
                Minefield m = new Minefield(10, 10, 10);
                buildButtonGrid(10,10, m);
            }
        });
        final Button flagButton = findViewById(R.id.FlagModeButton);
        flagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagButton.setText(isFlagMode ? "Flag Mode On" : "Flag Mode Off");
                isFlagMode = !isFlagMode;
            }
        });
    }

    /**
     * Builds a grid of buttons with a given row and column count into the minefield TableLayout instance
     * @param rowCount The amount of rows to build
     * @param colCount The amount of columns to build
     * @return Returns a 2D button array with all the buttons on the TableLayout instance
     */
    private Button[][] buildButtonGrid(final int rowCount, final int colCount, final Minefield m){
        TableLayout ty = findViewById(R.id.MinefieldTableLayout); //get the table
        final Button[][] buttonArr = new Button[rowCount][colCount];
        for(int i = 0; i < rowCount; i++){
            TableRow tr = new TableRow(this); // new row
            tr.setBackgroundColor(getResources().getColor(R.color.cellColorNormal)); // set color
            for(int j = 0; j < colCount; j++){
                final Button b = new Button(this); // new button
                buttonArr[i][j] = b;
                b.setText(m.GetCells()[i][j].getDisplay()); // set text
                b.setBackgroundTintList(getResources().getColorStateList(R.color.cellColorNormal)); // set its color
                final int row = i;
                final int col = j;
                b.setOnClickListener(new View.OnClickListener() { // now for an on click
                    @Override
                    public void onClick(View v) {
                        if(isFlagMode){
                            m.GetCells()[row][col].Flag();
                            FlipButton(b, m, row, col);
                        }
                        else {
                            TidalButtonFlip(row,col, buttonArr, m);
                        }
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
        return buttonArr;
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

    public void TidalButtonFlip(int row, int col, Button[][] buttonArr, Minefield m){
        Button b = buttonArr[row][col];
        FlipButton(b, m, row, col);
        if(m.GetCells()[row][col].getNumBombs() == 0){
            for(int rowOffset = -1; rowOffset < 2; rowOffset++){
                for(int colOffset = -1; colOffset < 2; colOffset++){
                    if(row+rowOffset < m.GetCells().length
                            && row+rowOffset >= 0
                            && col+colOffset < m.GetCells()[0].length
                            && col+colOffset >= 0
                            && m.GetCells()[row+rowOffset][col+colOffset].getDisplay() == "_"
                    ) {
                        TidalButtonFlip(row+rowOffset, col+colOffset, buttonArr, m);
                    }
                }
            }
        }
    }

    public void FlipButton(Button b, Minefield m, int row, int col){
        boolean gameDone = m.FlipCell(m.GetCells()[row][col]);
        b.setText(m.GetCells()[row][col].getDisplay());  // change text
        //Log.i("event handler", b.getText()+"");
        b.setBackgroundTintList(getResources().getColorStateList(R.color.cellColorClicked)); // change color
        if(gameDone){
            //do something on win
        }
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

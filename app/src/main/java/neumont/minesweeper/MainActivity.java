package neumont.minesweeper;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    boolean isFlagMode = false;
    Button easyButton, mediumButton, hardButton, cancelButton;
    Dialog newGameDialog, gameOverDialog;
    Minefield minefield;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Firebase.setAndroidContext(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newGameDialog = new Dialog(this);
        newGameDialog.setContentView(R.layout.new_game_dialog);
        gameOverDialog = new Dialog(this);
        gameOverDialog.setContentView(R.layout.game_over_dialog);

        Button ngButton = findViewById(R.id.NewGameButton);
        ngButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGameDialog.show();
            }
        });

        final Button flagButton = findViewById(R.id.FlagModeButton);
        flagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView flagStatusTextView = findViewById(R.id.FlagModeStatusTextView);
                if(isFlagMode){
                    flagStatusTextView.setText(R.string.flag_mode_off_label);
                } else {
                    flagStatusTextView.setText(R.string.flag_mode_on_label);
                }
                isFlagMode = !isFlagMode;
            }
        });

        easyButton = newGameDialog.findViewById(R.id.EasyDifficultyButton);
        easyButton.setOnClickListener(this);
        mediumButton = newGameDialog.findViewById(R.id.MediumDifficultyButton);
        mediumButton.setOnClickListener(this);
        hardButton = newGameDialog.findViewById(R.id.HardDifficultyButton);
        hardButton.setOnClickListener(this);
        cancelButton = newGameDialog.findViewById(R.id.CancelButton);
        cancelButton.setOnClickListener(this);

        gameOverDialog.findViewById(R.id.GameOverYesButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.EasyDifficultyButton:
                TableLayout ty = findViewById(R.id.MinefieldTableLayout);
                ty.removeAllViews();
                Minefield m = new Minefield(10, 10, 12);
                buildButtonGrid(10,10, m);
                newGameDialog.dismiss();
                break;
            case R.id.MediumDifficultyButton:
                TableLayout ty2 = findViewById(R.id.MinefieldTableLayout);
                ty2.removeAllViews();
                Minefield m2 = new Minefield(20, 20, 60);
                buildButtonGrid(20,20, m2);
                newGameDialog.dismiss();
                break;
            case R.id.HardDifficultyButton:
                TableLayout ty3 = findViewById(R.id.MinefieldTableLayout);
                ty3.removeAllViews();
                Minefield m3 = new Minefield(30, 30, 120);
                buildButtonGrid(30,30, m3);
                newGameDialog.dismiss();
                break;
            case R.id.CancelButton:
                newGameDialog.dismiss();
                break;
            case R.id.GameOverYesButton: //using fallthrough to my advantage here
                newGameDialog.show();
            case R.id.GameOverNoButton:
                gameOverDialog.dismiss();
                break;
        }
    }

    /**
     * Builds a grid of buttons with a given row and column count into the minefield TableLayout instance
     * @param rowCount The amount of rows to build
     * @param colCount The amount of columns to build
     * @return Returns a 2D button array with all the buttons on the TableLayout instance
     */
    private Button[][] buildButtonGrid(final int rowCount, final int colCount, final Minefield m){
        this.minefield = m;
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
                            FlagButton(b,m,row,col);
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
        if(m.GetCells()[row][col].getDisplay().equals("B")){
            b.setBackgroundTintList(getResources().getColorStateList(R.color.bombColor));
        }
        else {
            b.setBackgroundTintList(getResources().getColorStateList(R.color.cellColorClicked)); // change color
        }
        //Log.i("event handler", b.getText()+"");
        if(gameDone){
            SetFieldEnabled(false);
            if(m.GetCells()[row][col].getDisplay().equals("B")){
                ((TextView)gameOverDialog.findViewById(R.id.GameOverHeaderTextView)).setText(R.string.game_over_lose);
            }
            else {
                ((TextView)gameOverDialog.findViewById(R.id.GameOverHeaderTextView)).setText(R.string.game_over_won);
            }
            gameOverDialog.show();
        }
    }

    public void FlagButton(Button b, Minefield m, int row, int col){
        m.GetCells()[row][col].Flag();
        b.setText(m.GetCells()[row][col].getDisplay());  // change text
        if(b.getText().toString().equals("F")){
            b.setBackgroundTintList(getResources().getColorStateList(R.color.flagColor)); // change color
        }
        else if(b.getText().toString().equals("_")){
            b.setBackgroundTintList(getResources().getColorStateList(R.color.cellColorNormal)); // change color
        }
    }

    public void SetFieldEnabled(boolean enabled){
        TableLayout tl = findViewById(R.id.MinefieldTableLayout);
        for(int row = 0; row < minefield.GetCells().length; row++){
            TableRow tr = (TableRow)tl.getChildAt(row);
            for(int col = 0; col < minefield.GetCells()[0].length; col++){
                Button b = (Button)tr.getChildAt(col);
                b.setEnabled(enabled);
            }
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

    private void UpdateBombCount()
    {
        TextView BombCounter = (TextView)findViewById(R.id.BombCountTextView);
        //BombCounter.setText(minefield.NumBoms() - flaggedCells);
    }

}

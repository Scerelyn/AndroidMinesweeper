package neumont.minesweeper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    boolean isFlagMode = false;
    Button easyButton, mediumButton, hardButton, cancelButton;
    Dialog newGameDialog, gameOverDialog, loadGameDialog;
    Minefield minefield;
    private int BombsRemaining = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Firebase.setAndroidContext(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newGameDialog = new Dialog(this);
        newGameDialog.setContentView(R.layout.new_game_dialog);
        gameOverDialog = new Dialog(this);
        gameOverDialog.setContentView(R.layout.game_over_dialog);
        loadGameDialog = new Dialog(this);
        loadGameDialog.setContentView(R.layout.load_game_layout);

        Button ngButton = findViewById(R.id.NewGameButton);
        ngButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGameDialog.show();
            }
        });

        final Switch flagSwitch = findViewById(R.id.FlagModeSwitch);
        flagSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean b){
                isFlagMode = b;
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

        newGameDialog.findViewById(R.id.LoadGameButton).setOnClickListener(this);

        gameOverDialog.findViewById(R.id.GameOverYesButton).setOnClickListener(this);
        gameOverDialog.findViewById(R.id.GameOverNoButton).setOnClickListener(this);
        loadGameDialog.findViewById(R.id.LoadGameLoadButton).setOnClickListener(this);
        loadGameDialog.findViewById(R.id.LoadGameCancelButton).setOnClickListener(this);

    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.EasyDifficultyButton:
                TableLayout ty = findViewById(R.id.MinefieldTableLayout);
                ty.removeAllViews();
                Minefield m = new Minefield(10, 10, 12);
                BombsRemaining = 12;
                buildButtonGrid(10,10, m);
                newGameDialog.dismiss();
                SetFieldEnabled(true);
                break;
            case R.id.MediumDifficultyButton:
                TableLayout ty2 = findViewById(R.id.MinefieldTableLayout);
                ty2.removeAllViews();
                Minefield m2 = new Minefield(20, 20, 60);
                BombsRemaining = 60;
                buildButtonGrid(20,20, m2);
                SetFieldEnabled(true);
                newGameDialog.dismiss();
                break;
            case R.id.HardDifficultyButton:
                TableLayout ty3 = findViewById(R.id.MinefieldTableLayout);
                ty3.removeAllViews();
                Minefield m3 = new Minefield(30, 30, 120);
                BombsRemaining = 120;
                buildButtonGrid(30,30, m3);
                SetFieldEnabled(true);
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
            case R.id.LoadGameButton:
                loadGameDialog.show();
                newGameDialog.dismiss();
                break;
            case R.id.LoadGameLoadButton:
                String loadName = ((TextView)loadGameDialog.findViewById(R.id.LoadGameSaveName)).getText().toString();

                loadGameDialog.show();
                TableLayout ty4 = findViewById(R.id.MinefieldTableLayout);
                ty4.removeAllViews();
                Log.i("firebasedebug","1");
                Firebase myFirebaseRef = new Firebase("https://androidminesweeper.firebaseio.com/");

                myFirebaseRef.child(loadName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("firebasedebug","2");
                        ArrayList<DataSnapshot> array = new ArrayList<>();
                        for (DataSnapshot cell: dataSnapshot.getChildren()
                                ) {
                            array.add(cell);
                        }
                        int length= 0;
                        int width = 0;
                        switch(array.size()){
                            case 100:
                                Log.i("firebasedebug","3-100");
                                length = 10;
                                width = 10;
                                break;
                            case 400:
                                Log.i("firebasedebug","3-400");
                                length = 20;
                                width = 20;
                                break;
                            case 900:
                                Log.i("firebasedebug","3-900");
                                length = 30;
                                width = 30;
                                break;
                        }

                        Minefield newMineField = new Minefield(width,length,0);
                        Log.i("firebasedebug","4");
                        for(int i = 0; i < length; i++){
                            for(int j = 0;j < width;j++){
                                Cell c = newMineField.GetCells()[i][j];
                                c.setBomb((boolean)array.get(j+i * width).child("bomb").getValue());
                                c.setNumBombs((int)((long)array.get(j+i * width).child("numBombs").getValue()+0.0));
                                if(!array.get(j+i * width).child("display").getValue().equals("_")){
                                    c.Flip(false);
                                }
                                c.y = i;
                                c.x = j;
                                newMineField.GetCells()[i][j] = c;
                            }
                        }
                        buildButtonGrid(length, width, newMineField);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                loadGameDialog.dismiss();
                break;
            case R.id.LoadGameCancelButton:
                loadGameDialog.dismiss();
                break;
        }
    }

    public void SaveGame(View view){
        final AlertDialog.Builder Dialogue = new AlertDialog.Builder(MainActivity.this);

        Dialogue.setTitle("Save Game");

        Dialogue.setMessage("Save Game?");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        Dialogue.setView(input);

        Dialogue.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Firebase ref = new Firebase("https://androidminesweeper.firebaseio.com/");

                Cell[][] MineFieldToStore = minefield.GetCells();

                List<Cell> list = new ArrayList<>();

                for (Cell[] array : MineFieldToStore) {
                    list.addAll(Arrays.asList(array));
                }
                ref.child(input.getText().toString()).setValue(list);

                dialog.dismiss();

            }

        });
        Dialogue.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        Dialogue.show();
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
                switch(m.GetCells()[i][j].getDisplay()){// set its color
                    case "F":
                        b.setBackgroundTintList(getResources().getColorStateList(R.color.flagColor));
                        break;
                    case "_":
                        b.setBackgroundTintList(getResources().getColorStateList(R.color.cellColorNormal));
                        break;
                    case "B":
                        b.setBackgroundTintList(getResources().getColorStateList(R.color.bombColor));
                        break;
                    default:
                        b.setBackgroundTintList(getResources().getColorStateList(R.color.cellColorClicked));
                        break;
                }

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
        if(!b.getText().equals("F")){
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
    }

    public void FlipButton(Button b, Minefield m, int row, int col){
        boolean gameDone = m.FlipCell(m.GetCells()[row][col]);
        b.setText(m.GetCells()[row][col].getDisplay());  // change text
        if(m.GetCells()[row][col].getDisplay().equals("B")){
            b.setBackgroundTintList(getResources().getColorStateList(R.color.bombColor));
            b.setText("💣");
        }
        else {
            b.setBackgroundTintList(getResources().getColorStateList(R.color.cellColorClicked)); // change color
        }
        //Log.i("event handler", b.getText()+"");
        if(gameDone){
            SetFieldEnabled(false);
            if(!m.isGameWon()){
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
            b.setText("🚩");
            BombsRemaining--;
        }
        else if(b.getText().toString().equals("_")){
            b.setBackgroundTintList(getResources().getColorStateList(R.color.cellColorNormal)); // change color
            BombsRemaining++;
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
        findViewById(R.id.save_button).setEnabled(enabled);
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

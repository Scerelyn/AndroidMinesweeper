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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildButtonGrid(25,25);
        TableLayout ty = findViewById(R.id.MinefieldTableLayout);
        Minefield m = new Minefield(10, 10, 10);

    }

    /**
     * Builds a grid of buttons with a given row and column count into the minefield TableLayout instance
     * @param rowCount The amount of rows to build
     * @param colCount The amount of columns to build
     */
    private void buildButtonGrid(int rowCount, int colCount){
        TableLayout ty = findViewById(R.id.MinefieldTableLayout); //get the table
        for(int i = 0; i < rowCount; i++){
            TableRow tr = new TableRow(this); // new row
            tr.setBackgroundColor(getResources().getColor(R.color.cellColorNormal)); // set color
            for(int j = 0; j < colCount; j++){
                final Button b = new Button(this); // new button
                b.setText("a"); // set text
                b.setBackgroundTintList(getResources().getColorStateList(R.color.cellColorNormal)); // set its color
                b.setOnClickListener(new View.OnClickListener() { // now for an on click
                    @Override
                    public void onClick(View v) {
                        Log.i("event handler", b.getText()+"");
                        b.setText("nice");  // change text
                        b.setBackgroundTintList(getResources().getColorStateList(R.color.cellColorClicked)); // change color
                    }
                });
                tr.addView(b); // add the button
                ViewGroup.LayoutParams params = b.getLayoutParams(); // get its layout parameters
                params.height = dpToPixel(50); // change height and width using a dp to pixel converter method
                params.width = dpToPixel(50);
                b.setLayoutParams(params);
                b.requestLayout(); // update layouts
                tr.requestLayout();
            }
            ty.addView(tr);
            ty.requestLayout();
        }
        Log.i("a","a");
    }

    public int dpToPixel(int dp){
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);
        return pixels;
    }
}

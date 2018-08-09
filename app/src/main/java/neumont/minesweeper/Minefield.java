package neumont.minesweeper;

import android.util.Log;

import java.io.Console;
import java.util.Random;

public class Minefield {
    private Cell[][] minefield;

    public Minefield(int width, int height, int numBombs){
        minefield = new Cell[width][height];
        for(int x =0; x < width; x++)
        {
            for(int y = 0; y < height; y++)
            {
                minefield[y][x] = new Cell();
            }
        }

        placeBombs(numBombs);
    }

    private void placeBombs(int numbombs){
        for(int i = numbombs; i > 0; i--)
        {
            Random rand  = new Random();
            int y = rand.nextInt(minefield.length);
            int x = rand.nextInt(minefield[y].length);
            minefield[y][x].setBomb(true);

            for(int j = y-1; j <= y+1; j++)
        {
            if(j >= 0 && j < minefield.length)
            {
                for(int k = x-1; k <= x+1; k++)
                {
                    if(k >= 0 && k < minefield[j].length)
                    {
                        minefield[j][k].setNumBombs(minefield[j][k].getNumBombs() + 1);
                    }
                }
            }
        }
        }
        for (Cell[] a:minefield) {
            String s = "";
            for (Cell c:a) {
                if(c.getBomb())
                {
                    s += "B";
                }
                else{
                    int num = c.getNumBombs();
                    s += num;
                }
            }
            Log.i("MineLine", s);
        }
    }


    /**
     * Method flips the passed in cell and if cell is blank calls the tidal flip method
     * to flip all adjacent cells
     * @param c
     * The cell to be flipped
     * @return
     * returns if a bomb was flipped
     */
    public boolean FlipCell(Cell c)
    {
        boolean bomb = c.Flip(false);
        if(bomb) {
            return bomb;
        }
        if(c.getNumBombs() == 0)
        {
            TidalFlip(c);
        }
        return false;

    }

}

package neumont.minesweeper;

public class Cell {

    public Cell(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int x;

    public int y;

    private String Display = "_";

    public String getDisplay()
    {
        return Display;
    }

    private boolean isBomb = false;

    public boolean getBomb()
    {
        return isBomb;
    }

    public void setBomb(boolean b)
    {
        isBomb = b;
    }

    private int numBombs = 0;

    public int getNumBombs()
    {
        return numBombs;
    }

    public void setNumBombs(int surroundingBombs)
    {
            numBombs = surroundingBombs;
    }

    public void Flag() {
        if(Display == "F") {
            Display = "_";
        }
        else if(Display == "_"){
            Display = "F";
        }
    }

    /**
     *  Flips the given tile, setting its display to what the tile actually is
     *
     * @return
     * Returns true if the tile is a bomb
     */
    public boolean Flip(boolean tidalFlip){
        if(Display == "F" && !tidalFlip)
        {
            return false;
        }

        if(isBomb)
        {
            Display = "B";
            return true;
        }
        else {
            String num = "";
            num += numBombs;
            Display = num;
            return false;
        }

    }
}

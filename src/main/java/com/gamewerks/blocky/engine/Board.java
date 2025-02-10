package com.gamewerks.blocky.engine;

import java.util.LinkedList;
import java.util.List;

import com.gamewerks.blocky.util.Constants;
import com.gamewerks.blocky.util.Position;

public class Board {

    private boolean[][] well;

    public Board() {
        well = new boolean[Constants.BOARD_HEIGHT][Constants.BOARD_WIDTH];
    }

    /**
     * Returns whether given position is valid on the board
     * @param row
     * @param col
     * @return true is valid else false
     */
    public boolean isValidPosition(int row, int col) {
        //SNEAKY OSERA
        //Off by one error
        return row >= 0 && row < well.length - 3 && col >= 0 && col <= well[0].length - 1 ;
    }

    /**
     * REturns true if piece collides with anything
     * @param p
     * @return 
     */
    public boolean collides(Piece p) {
        return collides(p.getLayout(), p.getPosition());
    }

    /**
     * Second collides function called by the one  above that does collision testing
     * @param layout
     * @param pos
     * @return 
     */
    public boolean collides(boolean[][] layout, Position pos) {
        for (int row = 0; row < layout.length; row++) {
            int wellRow = pos.row - row;
            for (int col = 0; col < layout[row].length; col++) {
                int wellCol = col + pos.col;
                if (layout[row][col]) {
                    if (!isValidPosition(wellRow, wellCol)) {
                        return true;
                    } else if (well[wellRow][wellCol]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Function adds a piece to the well
     * @param p 
     */
    public void addToWell(Piece p) {
        boolean[][] layout = p.getLayout();
        Position pos = p.getPosition();
        for (int row = 0; row < layout.length; row++) {
            int wellRow = pos.row - row;
            for (int col = 0; col < layout[row].length; col++) {
                int wellCol = pos.col + col;
                if (isValidPosition(wellRow, wellCol) && layout[row][col]) {
                    well[wellRow][wellCol] = true;
                }
            }
        }
    }

    /**
     * This function deletes a row from the well 
     * @param n 
     */
    public void deleteRow(int n) {
        for (int row = 0; row < n - 1; row++) {
            for (int col = 0; col < Constants.BOARD_WIDTH; col++) {
                well[row][col] = well[row + 1][col];
            }
        }
        for (int col = 0; col < Constants.BOARD_WIDTH; col++) {
            well[n][col] = false;
        }
    }

    //credit: https://stackoverflow.com/a/28954225
    /**
     * This function deletes a list of rows 
     * @param rows 
     */
    public void deleteRows(List<Integer> rows) {
        for (int i = 0; i < rows.size(); i++) {

            int row = rows.get(i);

            //delete all rows in array
            for (int j = 0; j < row; j++) {
                //use ternary to 
                deleteRow(row);
            }

        }
    }

    /**
     * Checks if a row is completed and has all its spaces filled
     * @param row
     * @return 
     */
    public boolean isCompletedRow(int row) {
        boolean isCompleted = true;
        for (int col = 0; col < Constants.BOARD_WIDTH; col++) {
            isCompleted = isCompleted && well[row][col];
        }
        return isCompleted;
    }

    /**
     * Returns a list containing all completed rows
     * @return 
     */
    public List getCompletedRows() {
        List completedRows = new LinkedList();
        for (int row = 0; row < Constants.BOARD_HEIGHT; row++) {
            if (isCompletedRow(row)) {
                //VERY SNEAKY OSERA
                //must be row index
                completedRows.add(row);
            }
        }
        return completedRows;
    }

    /**
     * Returns the current well
     * @return 
     */
    public boolean[][] getWell() {
        return well;
    }
}

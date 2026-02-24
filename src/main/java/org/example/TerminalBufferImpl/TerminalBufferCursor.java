package org.example.TerminalBufferImpl;

public class TerminalBufferCursor {
    private int currentRow;
    private int currentColumn;
    private int screenWidth;
    private int screenHeight;

    public TerminalBufferCursor(int screenWidth, int screenHeight) {
        this.currentRow = 0;
        this.currentColumn = 0;

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public int getCurrentColumn() {
        return currentColumn;
    }

    public void setCurrentRow(int newRow) {
        if(newRow < 0) {
            newRow = 0;
        }

        if(newRow > screenHeight - 1) {
            newRow = screenHeight - 1;
        }

        this.currentRow = newRow;
    }

    public void setCurrentColumn(int newColumn) {
        if(newColumn < 0) {
            newColumn = 0;
        }

        if(newColumn > screenWidth - 1) {
            newColumn = screenWidth - 1;
        }

        this.currentColumn = newColumn;
    }

    public void setPosition(int newRow, int newColumn) {
        setCurrentRow(newRow);
        setCurrentColumn(newColumn);
    }

    public void moveUp(int numOfCells) {
        setCurrentRow(getCurrentRow() + numOfCells);
    }

    public void moveDown(int numOfCells) {
        setCurrentRow(getCurrentRow() - numOfCells);
    }

    public void moveRight(int numOfCells) {
        setCurrentColumn(getCurrentColumn() + numOfCells);
    }

    public void moveLeft(int numOfCells) {
        setCurrentColumn(getCurrentColumn() - numOfCells);
    }


}

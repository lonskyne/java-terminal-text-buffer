package org.example.TerminalBufferImpl;

public class TerminalBufferCursor {
    private int currentRow;
    private int currentColumn;
    private int screenWidth;
    private int screenHeight;

    /**
     * Constructs a TerminalBufferCursor object.
     *
     * @param screenWidth the current screen width
     * @param screenHeight the current screen height
     */
    public TerminalBufferCursor(int screenWidth, int screenHeight) {
        this.currentRow = 0;
        this.currentColumn = 0;

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    /**
     * Gets the current cursor row index.
     *
     * @return the current cursor row index
     */
    public int getCurrentRow() {
        return currentRow;
    }

    /**
     * Gets the current cursor column index.
     *
     * @return the current cursor column index
     */
    public int getCurrentColumn() {
        return currentColumn;
    }

    /**
     * Sets the cursors current row index. Clamps the row index
     * so the cursor does not end up off-screen.
     *
     * @param newRow the new row index of the cursor
     */
    public void setCurrentRow(int newRow) {
        if(newRow < 0) {
            newRow = 0;
        }

        if(newRow > screenHeight - 1) {
            newRow = screenHeight - 1;
        }

        this.currentRow = newRow;
    }

    /**
     * Sets the cursors current column index. Clamps the column index
     * so the cursor does not end up off-screen.
     *
     * @param newColumn the new column index of the cursor
     */
    public void setCurrentColumn(int newColumn) {
        if(newColumn < 0) {
            newColumn = 0;
        }

        if(newColumn > screenWidth - 1) {
            newColumn = screenWidth - 1;
        }

        this.currentColumn = newColumn;
    }

    /**
     * Sets the cursors current row and column indexes.
     * Clamps the row and column index so the cursor does not end up off-screen.
     *
     * @param newRow the new row index of the cursor
     * @param newColumn the new column index of the cursor
     */
    public void setPosition(int newRow, int newColumn) {
        setCurrentRow(newRow);
        setCurrentColumn(newColumn);
    }

    /**
     * Moves the cursor up by the given number of cells.
     * Cursor position is clamped so it does not end up off-screen
     *
     * @param numOfCells the number of cells for the cursor to be moved up for
     */
    public void moveUp(int numOfCells) {
        setCurrentRow(getCurrentRow() + numOfCells);
    }

    /**
     * Moves the cursor down by the given number of cells.
     * Cursor position is clamped so it does not end up off-screen
     *
     * @param numOfCells the number of cells for the cursor to be moved down for
     */
    public void moveDown(int numOfCells) {
        setCurrentRow(getCurrentRow() - numOfCells);
    }

    /**
     * Moves the cursor right by the given number of cells.
     * Cursor position is clamped so it does not end up off-screen
     *
     * @param numOfCells the number of cells for the cursor to be moved right for
     */
    public void moveRight(int numOfCells) {
        setCurrentColumn(getCurrentColumn() + numOfCells);
    }

    /**
     * Moves the cursor left by the given number of cells.
     * Cursor position is clamped so it does not end up off-screen
     *
     * @param numOfCells the number of cells for the cursor to be moved left for
     */
    public void moveLeft(int numOfCells) {
        setCurrentColumn(getCurrentColumn() - numOfCells);
    }


}

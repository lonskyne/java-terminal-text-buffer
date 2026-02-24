package org.example.TerminalBufferImpl;

public class TerminalBufferCursor {
    private int screenWidth;
    private int screenHeight;
    private int currentIndex;

    /**
     * Constructs a TerminalBufferCursor object.
     *
     * @param screenWidth the current screen width
     * @param screenHeight the current screen height
     */
    public TerminalBufferCursor(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.currentIndex = 0;
    }

    /**
     * Gets the current cursor row index.
     *
     * @return the current cursor row index
     */
    public int getCurrentRow() {
        return this.currentIndex / screenWidth;
    }

    /**
     * Gets the current cursor column index.
     *
     * @return the current cursor column index
     */
    public int getCurrentColumn() {
        return this.currentIndex % screenWidth;
    }

    /**
     * Gets the current cursor index.
     *
     * @return the current cursor index
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Sets the cursors row index. Clamped between 0 and screen height - 1.
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

        this.currentIndex = newRow * screenWidth + getCurrentColumn();
    }

    /**
     * Sets the cursors column index. Clamped between 0 and screen width - 1.
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

        this.currentIndex = getCurrentRow() * screenWidth + newColumn;
    }

    /**
     * Sets the cursors current index. Clamps the index
     * so the cursor does not end up off-screen.
     * Updates the cursor row and column index.
     *
     * @param newIndex the new index of the cursor
     */
    public void setCurrentIndex(int newIndex) {
        if(newIndex < 0) {
            newIndex = 0;
        }

        if(newIndex > screenWidth * screenHeight - 1) {
            newIndex = screenWidth * screenHeight - 1;
        }

        this.currentIndex = newIndex;
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
        setCurrentRow(getCurrentRow() - numOfCells);
    }

    /**
     * Moves the cursor down by the given number of cells.
     * Cursor position is clamped so it does not end up off-screen
     *
     * @param numOfCells the number of cells for the cursor to be moved down for
     */
    public void moveDown(int numOfCells) {
        setCurrentRow(getCurrentRow() + numOfCells);
    }

    /**
     * Moves the cursor right by the given number of cells.
     * Cursor moves to the beginning of next row after the end of the row.
     *
     * @param numOfCells the number of cells for the cursor to be moved right for
     */
    public void moveRight(int numOfCells) {
        setCurrentIndex(currentIndex + numOfCells);
    }

    /**
     * Moves the cursor left by the given number of cells.
     * Cursor moves to the end of the previous row after the end of the row.
     *
     * @param numOfCells the number of cells for the cursor to be moved left for
     */
    public void moveLeft(int numOfCells) {
        setCurrentIndex(currentIndex - numOfCells);
    }


}

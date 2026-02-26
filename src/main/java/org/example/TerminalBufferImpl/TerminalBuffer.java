package org.example.TerminalBufferImpl;

import org.example.TerminalBufferImpl.Model.TerminalBufferCell;
import org.example.TerminalBufferImpl.Model.TerminalBufferCellStyle;
import org.example.TerminalBufferImpl.Model.TerminalBufferColor;

import java.util.EnumSet;


public class TerminalBuffer {
    private final int screenWidth;
    private final int screenHeight;
    private final int cellCount;

    private final TerminalBufferCell[] screenBuffer;
    private final ScrollbackBuffer scrollbackBuffer;
    private final TerminalBufferCursor cursor;

    private TerminalBufferColor currentBackgroundColor;
    private TerminalBufferColor currentForegroundColor;
    private EnumSet<TerminalBufferCellStyle> currentStyles;

    private int lastCharacterIndex;

    /**
     * Writes a single character at the current cursor position
     * and moves the cursor one cell to the right.
     * Using the current colors and styles.
     *
     * @param c the character to be written
     */
    private void writeCharacterOnScreenBuffer(char c) {
        int cursorIndex = cursor.getCurrentIndex();

        screenBuffer[cursorIndex].setCharacter(c);
        screenBuffer[cursorIndex].setForegroundColor(currentForegroundColor);
        screenBuffer[cursorIndex].setBackgroundColor(currentBackgroundColor);
        screenBuffer[cursorIndex].setStyles(EnumSet.copyOf(currentStyles));

        lastCharacterIndex = Math.max(lastCharacterIndex, cursorIndex);

        // Scroll if at end of line.
        if (cursorIndex >= cellCount - 1) {
            scrollUp(1);
        }

        cursor.moveRight(1);
    }

    /**
     * Scrolls the screen buffer up, clearing the bottom row.
     * Updates the cursor position and lastCharacterIndex.
     *
     * @param rows amount of rows to scroll up
     */
    private void scrollUp(int rows) {
        if(rows <= 0) {
            return;
        }

        if(rows > screenHeight) {
            rows = screenHeight;
        }

        // Insert the scrolled lines into the scrollback buffer
        scrollbackBuffer.insertLastScreenLinesToScrollback(screenBuffer, rows);

        int totalCells = cellCount + screenWidth;
        int shift = rows * screenWidth;

        // Shift rows up
        for (int i = 0; i < totalCells - shift; i++) {
            screenBuffer[i].copyFrom(screenBuffer[i + shift]);
        }

        // Clear new rows
        for (int i = totalCells - shift; i < totalCells; i++) {
            screenBuffer[i].clear();
        }

        int newIndex = cursor.getCurrentIndex() - shift;
        lastCharacterIndex = lastCharacterIndex - shift;
        cursor.setCurrentIndex(Math.max(newIndex, 0));
    }

    /**
     * Recalculates the lastCharacterIndex as the first non-empty cell from
     * beginning of buffer.
     */
    private void recalculateLastCharacterIndex() {
        lastCharacterIndex = cellCount - 1;

        while(lastCharacterIndex >= 0 && screenBuffer[lastCharacterIndex].isEmpty()) {
            lastCharacterIndex--;
        }
    }

    /**
     * Creates the TerminalBuffer object
     *
     * @param screenWidth width of terminal screen
     * @param screenHeight height of terminal screen
     * @param maxScrollbackLines number of lines saved in the scrollback buffer
     */
    public TerminalBuffer(int screenWidth, int screenHeight, int maxScrollbackLines) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.cellCount = screenWidth * screenHeight;
        this.screenBuffer = new TerminalBufferCell[cellCount + screenWidth];

        for(int i = 0; i < cellCount + screenWidth; i++) {
            this.screenBuffer[i] = new TerminalBufferCell();
        }

        this.scrollbackBuffer = new ScrollbackBuffer(maxScrollbackLines, screenWidth);
        this.cursor = new TerminalBufferCursor(screenWidth, screenHeight);

        this.currentBackgroundColor = TerminalBufferColor.DEFAULT;
        this.currentForegroundColor = TerminalBufferColor.DEFAULT;
        this.currentStyles = EnumSet.noneOf(TerminalBufferCellStyle.class);
    }

    public TerminalBufferCursor getCursor() {
        return cursor;
    }

    /**
     * Changes the current used background color.
     *
     * @param newBackgroundColor the new background color to be used
     */
    public void setCurrentBackgroundColor(TerminalBufferColor newBackgroundColor) {
        this.currentBackgroundColor = newBackgroundColor;
    }

    /**
     * Changes the current used foreground color.
     *
     * @param newForegroundColor the new background color to be used
     */
    public void setCurrentForegroundColor(TerminalBufferColor newForegroundColor) {
        this.currentForegroundColor = newForegroundColor;
    }

    /**
     * Changes the current used text styles.
     *
     * @param newStyles the new background color to be used
     */
    public void setCurrentStyles(EnumSet<TerminalBufferCellStyle> newStyles) {
        this.currentStyles = EnumSet.copyOf(newStyles);
    }

    /**
     * Writes a text on a line, overriding the current content. Moves the cursor.
     *
     * @param text the text to be written on the line
     */
    public void writeTextOnLine(String text) {
        int numOfCharactersToWrite = Math.min(text.length(), screenWidth - cursor.getCurrentColumn());

        for (int i = 0; i < numOfCharactersToWrite; i++) {
            writeCharacterOnScreenBuffer(text.charAt(i));
        }
    }

    /**
     * Inserts text on a line, possibly wrapping the line.
     * Moves the cursor.
     *
     * @param text the text to be inserted
     */
    public void insertTextOnLine(String text) {
        int textLen = text.length();
        int textIndex = 0;

        // Move text in current row right if there is space
        int lastInRow = ((cursor.getCurrentRow() + 1) * screenWidth) - 1;
        while(lastInRow >= 0 && screenBuffer[lastInRow].isEmpty()) {
            lastInRow--;
        }
        if(lastInRow >= cursor.getCurrentIndex()) {
            int amountToMove = Math.min(textLen, screenWidth - lastInRow - 1);
            for (int i = lastInRow + amountToMove; i > lastInRow; i--) {
                screenBuffer[i].copyFrom(screenBuffer[i - amountToMove]);
                screenBuffer[i - amountToMove].clear();
                lastCharacterIndex = Math.max(i, lastCharacterIndex);
            }
        }
        // If we are inserting into empty space, no shifting needed
        while(textIndex < textLen && screenBuffer[cursor.getCurrentIndex()].isEmpty()) {
            writeCharacterOnScreenBuffer(text.charAt(textIndex));
            textIndex++;
        }

        int leftoverLen = textLen - textIndex;
        if(leftoverLen <= 0) {
            return;
        }

        // Add full row lengths first
        while(textLen - textIndex >= screenWidth) {
            for(int i = lastCharacterIndex + screenWidth; i >= cursor.getCurrentIndex() + screenWidth; i--) {
                screenBuffer[i].copyFrom(screenBuffer[i - screenWidth]);
                screenBuffer[i - screenWidth].clear();

                if(!screenBuffer[i].isEmpty()) {
                    lastCharacterIndex = Math.max(lastCharacterIndex, i);
                }
            }

            for(int i = 0; i < screenWidth; i++) {
                writeCharacterOnScreenBuffer(text.charAt(textIndex));
                lastCharacterIndex = Math.max(lastCharacterIndex, i);
                textIndex++;
            }

            if(lastCharacterIndex > cellCount - 1) {
                scrollUp(1);
            }
        }

        // Add what is left of the text
        int notRowPart = leftoverLen % screenWidth;

        // Free up row below cursor by shifting content down
        int lastToCopy = ((cursor.getCurrentIndex() / screenWidth) + 1) * screenWidth;
        for(int i = lastCharacterIndex + screenWidth; i >= lastToCopy + screenWidth; i--) {
            screenBuffer[i].copyFrom(screenBuffer[i - screenWidth]);
            screenBuffer[i - screenWidth].clear();

            if(!screenBuffer[i].isEmpty()) {
                lastCharacterIndex = Math.max(lastCharacterIndex, i);
            }
        }

        // Make just enough space for the notRowPart
        for(int i = lastToCopy + notRowPart - 1; i >= cursor.getCurrentIndex() + notRowPart; i--) {
            screenBuffer[i].copyFrom(screenBuffer[i - notRowPart]);
            screenBuffer[i - notRowPart].clear();

            if(!screenBuffer[i].isEmpty()) {
                lastCharacterIndex = Math.max(lastCharacterIndex, i);
            }
        }

        for(int i = 0; i < notRowPart; i++) {
            writeCharacterOnScreenBuffer(text.charAt(textIndex));
            textIndex++;
        }

        if(lastCharacterIndex / screenWidth == screenHeight) {
            scrollUp(1);
        }
    }

    /**
     * Fills line with the given character.
     * Moves the cursor to start of next line.
     *
     * @param c the character the line will be filled with
     */
    public void fillLineWithCharacter(char c) {
        cursor.setCurrentColumn(0);

        for(int i = 0; i < screenWidth; i++) {
            writeCharacterOnScreenBuffer(c);
        }
    }

    /**
     * Fills line with empty cells.
     * Moves the cursor to beginning of the line.
     */
    public void fillLineWithCharacter() {
        cursor.setCurrentColumn(0);

        for(int i = 0; i < screenWidth; i++) {
            screenBuffer[i + cursor.getCurrentIndex()].clear();
        }

        if(cursor.getCurrentRow() == lastCharacterIndex / screenWidth) {
            recalculateLastCharacterIndex();
        }
    }

    /**
     * Inserts an empty line at the bottom of the screen
     */
    public void insertEmptyLineAtBottomOfScreen() {
        scrollUp(1);
    }

    /**
     * Clears the entire screen, moves cursor to the beginning.
     */
    public void clearScreen() {
        scrollUp(screenHeight);
    }

    /**
     * Clears the entire screen and scrollback, moves cursor to the beginning.
     */
    public void clearScreenAndScrollback() {
        scrollUp(screenHeight);
        scrollbackBuffer.clearScrollbackBuffer();
    }

    /**
     * Returns a character that is in the given position in the screen buffer.
     *
     * @param row row index
     * @param column column index
     * @return character that is on the given position on the screen
     * @throws IndexOutOfBoundsException when x and y are invalid
     */
    public char getCharacterAtPositionScreen(int row, int column) {
        if(row < 0 || column < 0 || row >= screenHeight || column >= screenWidth) {
            throw new IndexOutOfBoundsException("Row or column index out of bounds: row=" + row + ", column=" + column);
        }

        return screenBuffer[row * screenWidth + column].getCharacter();
    }

    /**
     * Returns a character that is in the given position in the screen buffer.
     *
     * @param row row index
     * @param column column index
     * @return character that is on the given position on the scrollback
     */
    public char getCharacterAtPositionScrollback(int row, int column) {
        if(row < 0 || column < 0 || row >= scrollbackBuffer.getMaxScrollbackLines() || column >= screenWidth) {
            throw new IndexOutOfBoundsException("Row or column index out of bounds: row=" + row + ", column=" + column);
        }

        return scrollbackBuffer.getBuffer()[row * screenWidth + column].getCharacter();
    }

    /**
     * Returns the foreground color of a character in the given position
     * in the screen buffer.
     *
     * @param row row index
     * @param column column index
     * @return the foreground color of a character in the given position on the screen
     */
    public TerminalBufferColor getForegroundColorAtPositionScreen(int row, int column) {
        if(row < 0 || column < 0 || row >= screenHeight || column >= screenWidth) {
            throw new IndexOutOfBoundsException("Row or column index out of bounds: row=" + row + ", column=" + column);
        }

        return screenBuffer[row * screenWidth + column].getForegroundColor();
    }

    /**
     * Returns the foreground color of a character in the given position
     * in the scrollback buffer.
     *
     * @param row row index
     * @param column column index
     * @return foreground color of a character in the given position on the scrollback
     */
    public TerminalBufferColor getForegroundColorAtPositionScrollback(int row, int column) {
        if(row < 0 || column < 0 || row >= scrollbackBuffer.getMaxScrollbackLines() || column >= screenWidth) {
            throw new IndexOutOfBoundsException("Row or column index out of bounds: row=" + row + ", column=" + column);
        }

        return scrollbackBuffer.getBuffer()[row * screenWidth + column].getForegroundColor();
    }

    /**
     * Returns the background color of a character in the given position
     * in the screen buffer.
     *
     * @param row row index
     * @param column column index
     * @return the background color of a character in the given position on the screen
     */
    public TerminalBufferColor getBackgroundColorAtPositionScreen(int row, int column) {
        if(row < 0 || column < 0 || row >= screenHeight || column >= screenWidth) {
            throw new IndexOutOfBoundsException("Row or column index out of bounds: row=" + row + ", column=" + column);
        }

        return screenBuffer[row * screenWidth + column].getBackgroundColor();
    }

    /**
     * Returns the background color of a character in the given position
     * in the scrollback buffer.
     *
     * @param row row index
     * @param column column index
     * @return background color of a character in the given position on the scrollback
     */
    public TerminalBufferColor getBackgroundColorAtPositionScrollback(int row, int column) {
        if(row < 0 || column < 0 || row >= scrollbackBuffer.getMaxScrollbackLines() || column >= screenWidth) {
            throw new IndexOutOfBoundsException("Row or column index out of bounds: row=" + row + ", column=" + column);
        }

        return scrollbackBuffer.getBuffer()[row * screenWidth + column].getBackgroundColor();
    }

    /**
     * Returns the applied styles to a character in the given position
     * in the screen buffer.
     *
     * @param row row index
     * @param column column index
     * @return the applied styles to a character on the given position on the screen
     */
    public EnumSet<TerminalBufferCellStyle> getStylesAtPositionScreen(int row, int column) {
        if(row < 0 || column < 0 || row >= screenHeight || column >= screenWidth) {
            throw new IndexOutOfBoundsException("Row or column index out of bounds: row=" + row + ", column=" + column);
        }

        return screenBuffer[row * screenWidth + column].getStyles();
    }

    /**
     * Returns the applied styles to a character in the given position
     * in the scrollback buffer.
     *
     * @param row row index
     * @param column column index
     * @return the applied styles to a character on the given position on the scrollback
     */
    public EnumSet<TerminalBufferCellStyle> getStylesAtPositionScrollback(int row, int column) {
        if(row < 0 || column < 0 || row >= scrollbackBuffer.getMaxScrollbackLines() || column >= screenWidth) {
            throw new IndexOutOfBoundsException("Row or column index out of bounds: row=" + row + ", column=" + column);
        }

        return scrollbackBuffer.getBuffer()[row * screenWidth + column].getStyles();
    }

    /**
     * Returns the asked row of the screen as a string.
     *
     * @param row row index
     * @return the row of the screen as a string
     */
    public String getScreenLineAsString(int row) {
        StringBuilder sb = new StringBuilder();
        for(int y = 0; y < screenWidth; y++) {
            sb.append(screenBuffer[row * screenWidth + y].getCharacter());
        }

        return sb.toString();
    }

    /**
     * Returns the asked row of the scrollback as a string.
     *
     * @param row row index
     * @return the row of the screen as a string
     */
    public String getScrollbackLineAsString(int row) {
        StringBuilder sb = new StringBuilder();
        for(int y = 0; y < screenWidth; y++) {
            sb.append(scrollbackBuffer.getBuffer()[row * screenWidth + y].getCharacter());
        }

        return sb.toString();
    }

    /**
     * Returns a string representing the terminal screen buffer.
     *
     * @return the string representation of the screen buffer
     */
    public String getScreenAsString() {
        StringBuilder sb = new StringBuilder();
        int index = 0;

        for(int i = 0; i < screenWidth * screenHeight; i++) {

            if(screenBuffer[i].isEmpty()) {
                sb.append('·');
            }
            else {
                sb.append(screenBuffer[i].getCharacter());
            }

            index++;

            if(index % screenWidth == 0) {
                sb.append('\n');
            }
        }

        return sb.toString();
    }

    /**
     * Returns a string representing the terminal scrollback and screen buffer.
     * First maxScrollbackLines lines are lines of scrollback, others are of the screen.
     *
     * @return the string representation of the scrollback and screen buffer
     */
    public String getScreenAndScrollbackAsString() {
        return scrollbackBuffer.getScrollbackAsString() + getScreenAsString();
    }
}

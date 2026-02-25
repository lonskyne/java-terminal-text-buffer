package org.example.TerminalBufferImpl;

import java.util.Arrays;
import java.util.EnumSet;


public class TerminalBuffer {
    private int screenWidth;
    private int screenHeight;
    private int cellCount;

    private TerminalBufferCell[] screenBuffer;
    private ScrollbackBuffer scrollbackBuffer;
    private TerminalBufferCursor cursor;

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
        if (cursorIndex >= screenWidth * screenHeight - 1) {
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

        int totalCells = screenWidth * screenHeight + screenWidth;
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

    private void recalculateLastCharacterIndex() {
        lastCharacterIndex = screenWidth * screenHeight - 1;

        while(lastCharacterIndex >= 0 && screenBuffer[lastCharacterIndex].isEmpty()) {
            lastCharacterIndex--;
        }
    }

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
        this.currentStyles = newStyles;
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
        while((textLen - textIndex - 1) >= screenWidth) {
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

            if(lastCharacterIndex > screenWidth * screenHeight - 1) {
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

    public void clearScreen() {
        scrollUp(screenHeight);
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

    public char getCharacterAtPositionScreen(int x, int y) {
        if(screenBuffer[x * screenWidth + y].isEmpty()) {
            return '·';
        }
        return screenBuffer[x * screenWidth + y].getCharacter();
    }
}

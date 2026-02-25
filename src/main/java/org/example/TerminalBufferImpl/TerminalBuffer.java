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

        // Scroll if at end of line.
        if (cursorIndex >= screenWidth * screenHeight - 1) {
            scrollUp(1);
        }

        screenBuffer[cursorIndex].setCharacter(c);
        screenBuffer[cursorIndex].setForegroundColor(currentForegroundColor);
        screenBuffer[cursorIndex].setBackgroundColor(currentBackgroundColor);
        screenBuffer[cursorIndex].setStyles(EnumSet.copyOf(currentStyles));

        lastCharacterIndex = Math.max(lastCharacterIndex, cursorIndex);

        cursor.moveRight(1);
    }

    public TerminalBuffer(int screenWidth, int screenHeight, int maxScrollbackLines) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.cellCount = screenWidth * screenHeight;
        this.screenBuffer = new TerminalBufferCell[cellCount];

        this.scrollbackBuffer = new ScrollbackBuffer(maxScrollbackLines, screenWidth);
        this.cursor = new TerminalBufferCursor(screenWidth, screenHeight);

        this.currentBackgroundColor = TerminalBufferColor.DEFAULT;
        this.currentForegroundColor = TerminalBufferColor.DEFAULT;
        this.currentStyles = EnumSet.noneOf(TerminalBufferCellStyle.class);
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

        int totalCells = screenWidth * screenHeight;
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
     * Inserts text on a line, possibly wrapping the line.
     * Moves the cursor.
     *
     * @param text the text to be inserted
     */
    public void insertTextOnLine(String text) {
        int textLen = text.length();
        int textIndex = 0;

        // Insert text line by line
        while(textIndex < textLen) {
            int lengthToInsert = Math.min(textLen - textIndex, screenWidth);

            // If inserting would go over the buffer, scroll up.
            if(lastCharacterIndex + lengthToInsert >= screenWidth * screenHeight) {
                scrollUp(1);
            }

            // Shift text to the right
            for(int j = lastCharacterIndex + lengthToInsert; j >= cursor.getCurrentIndex() + lengthToInsert; j--) {
                screenBuffer[j].copyFrom(screenBuffer[j - lengthToInsert]);
            }
            lastCharacterIndex = lastCharacterIndex + lengthToInsert;

            // Write the text in the emptied space
            for(int j = 0; j < lengthToInsert; j++) {
                writeCharacterOnScreenBuffer(text.charAt(textIndex));
                textIndex++;
            }
        }
    }
}

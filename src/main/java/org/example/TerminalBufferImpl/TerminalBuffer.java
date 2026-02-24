package org.example.TerminalBufferImpl;

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

    /**
     * Writes a single character at the current cursor position
     * and moves the cursor one cell to the right.
     * Using the current colors and styles.
     *
     * @param c the character to be written
     */
    private void WriteCharacterOnScreenBuffer(char c) {
        screenBuffer[cursor.getCurrentIndex()].setCharacter(c);
        screenBuffer[cursor.getCurrentIndex()].setForegroundColor(currentForegroundColor);
        screenBuffer[cursor.getCurrentIndex()].setBackgroundColor(currentBackgroundColor);
        screenBuffer[cursor.getCurrentIndex()].setStyles(currentStyles);
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
    public void WriteTextOnLine(String text) {
        int numOfCharactersToWrite = Math.min(text.length(), screenWidth - cursor.getCurrentIndex() + 1);

        for(int i = 0; i < numOfCharactersToWrite; i++) {
            WriteCharacterOnScreenBuffer(text.charAt(i));
        }
    }
}

package org.example.TerminalBufferImpl;

import java.util.EnumSet;

public class TerminalBuffer {
    private int screenWidth;
    private int screenHeight;
    private int cellCount;

    private TerminalBufferCell[] screen;
    private ScrollbackBuffer scrollback;
    private TerminalBufferCursor cursor;

    private TerminalBufferColor currentBackgroundColor;
    private TerminalBufferColor currentForegroundColor;
    private EnumSet<TerminalBufferCellStyle> currentStyles;


    public TerminalBuffer(int screenWidth, int screenHeight, int maxScrollbackLines) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.cellCount = screenWidth * screenHeight;
        this.screen = new TerminalBufferCell[cellCount];

        this.scrollback = new ScrollbackBuffer(maxScrollbackLines, screenWidth);
        this.cursor = new TerminalBufferCursor(screenWidth, screenHeight);

        this.currentBackgroundColor = TerminalBufferColor.DEFAULT;
        this.currentForegroundColor = TerminalBufferColor.DEFAULT;
        this.currentStyles = EnumSet.noneOf(TerminalBufferCellStyle.class);
    }

    public void setCurrentBackgroundColor(TerminalBufferColor currentBackgroundColor) {
        this.currentBackgroundColor = currentBackgroundColor;
    }

    public void setCurrentForegroundColor(TerminalBufferColor currentForegroundColor) {
        this.currentForegroundColor = currentForegroundColor;
    }

    public void setCurrentStyles(EnumSet<TerminalBufferCellStyle> currentStyles) {
        this.currentStyles = currentStyles;
    }
}

package org.example.TerminalBufferImpl;

public class TerminalBuffer {
    private int screenWidth;
    private int screenHeight;
    private int cellCount;
    private TerminalBufferCell[] screen;

    private ScrollbackBuffer scrollback;


    public TerminalBuffer(int screenWidth, int screenHeight, int maxScrollbackLines) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.cellCount = screenWidth * screenHeight;
        this.screen = new TerminalBufferCell[cellCount];

        scrollback = new ScrollbackBuffer(maxScrollbackLines, screenWidth);
    }
}

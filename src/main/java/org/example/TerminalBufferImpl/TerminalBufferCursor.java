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
}

package org.example.TerminalBufferImpl;

public class ScrollbackBuffer {
    private int maxScrollbackLines;
    private int screenWidth;

    private TerminalBufferCell[] buffer;

    public ScrollbackBuffer(int maxScrollbackLines, int screenWidth) {
        this.maxScrollbackLines = maxScrollbackLines;
        this.screenWidth = screenWidth;
        buffer = new TerminalBufferCell[screenWidth * maxScrollbackLines];
    }
}

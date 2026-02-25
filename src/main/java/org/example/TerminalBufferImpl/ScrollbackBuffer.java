package org.example.TerminalBufferImpl;

public class ScrollbackBuffer {
    private final int maxScrollbackLines;
    private final int screenWidth;

    private final TerminalBufferCell[] buffer;

    public ScrollbackBuffer(int maxScrollbackLines, int screenWidth) {
        this.maxScrollbackLines = maxScrollbackLines;
        this.screenWidth = screenWidth;
        buffer = new TerminalBufferCell[screenWidth * maxScrollbackLines];
    }
}

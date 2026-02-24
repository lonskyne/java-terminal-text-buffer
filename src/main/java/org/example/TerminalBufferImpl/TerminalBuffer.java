package org.example.TerminalBufferImpl;

public class TerminalBuffer {
    private int screenWidth;
    private int screenHeight;
    private TerminalBufferCell[] screen;

    public TerminalBuffer(int screenWidth, int screenHeight, TerminalBufferCell[] screen) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screen = screen;
    }
}

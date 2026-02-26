package org.example.TerminalBufferImpl;

public class ScrollbackBuffer {
    private final int maxScrollbackLines;
    private final int screenWidth;
    private int startIndex;
    private int curIndex;
    private boolean anyWritten;

    private final TerminalBufferCell[] buffer;

    public ScrollbackBuffer(int maxScrollbackLines, int screenWidth) {
        this.maxScrollbackLines = maxScrollbackLines;
        this.screenWidth = screenWidth;
        this.startIndex = 0;
        this.curIndex = 0;
        this.anyWritten = false;

        buffer = new TerminalBufferCell[screenWidth * maxScrollbackLines];

        for(int i = 0; i < screenWidth * maxScrollbackLines; i++) {
            buffer[i] = new TerminalBufferCell();
        }
    }

    private void moveCurIndexOneCellLeft() {
        if(maxScrollbackLines == 0) {
            return;
        }
        curIndex = (curIndex - 1 + buffer.length) % buffer.length;
    }

    private void moveStartIndexOneCellLeft() {
        if(maxScrollbackLines == 0) {
            return;
        }
        startIndex = (startIndex - 1 + buffer.length) % buffer.length;
    }

    public void insertLastScreenLinesToScrollback(TerminalBufferCell[] screenBuffer, int numOfLines) {
        if(maxScrollbackLines == 0) {
            return;
        }
        for(int i = (numOfLines * screenWidth) - 1; i >= 0; i--) {
            if(startIndex == curIndex && anyWritten) {
                moveStartIndexOneCellLeft();
            }

            moveCurIndexOneCellLeft();
            buffer[curIndex].copyFrom(screenBuffer[i]);
        }
        anyWritten = true;
    }

    public void clearScrollbackBuffer() {
        if(maxScrollbackLines == 0) {
            return;
        }
        int localIndex = startIndex;
        int endIndex = (startIndex - 1 + buffer.length) % buffer.length;

        while(localIndex != endIndex) {
            buffer[localIndex].clear();
            localIndex = (localIndex + 1) % buffer.length;
        }

        buffer[localIndex].clear();

        startIndex = 0;
        curIndex = 0;
        anyWritten = false;
    }

    public String getScrollbackAsString() {
        if(maxScrollbackLines == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        int charsAdded = 0;
        int localIndex = startIndex;
        int endIndex = (startIndex - 1 + buffer.length) % buffer.length;

        while(localIndex != endIndex) {
            sb.append(buffer[localIndex].getCharacter());
            charsAdded++;
            localIndex = (localIndex + 1) % buffer.length;

            if(charsAdded % screenWidth == 0) {
                sb.append("\n");
            }
        }

        sb.append(buffer[localIndex].getCharacter());
        sb.append("\n");

        return sb.toString();
    }

    public TerminalBufferCell[] getBuffer() {
        return buffer;
    }

    public int getMaxScrollbackLines() {
        return maxScrollbackLines;
    }
}

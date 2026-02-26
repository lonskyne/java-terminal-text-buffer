package org.example.TerminalBufferImpl;

public class ScrollbackBuffer {
    private final int maxScrollbackLines;
    private final int screenWidth;
    private int startIndex;

    private final TerminalBufferCell[] buffer;

    public ScrollbackBuffer(int maxScrollbackLines, int screenWidth) {
        this.maxScrollbackLines = maxScrollbackLines;
        this.screenWidth = screenWidth;
        this.startIndex = 0;
        buffer = new TerminalBufferCell[screenWidth * maxScrollbackLines];

        for(int i = 0; i < screenWidth * maxScrollbackLines; i++) {
            buffer[i] = new TerminalBufferCell();
        }
    }

    private void moveStartRowIndexOneCellLeft() {
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
            moveStartRowIndexOneCellLeft();
            buffer[startIndex].copyFrom(screenBuffer[i]);
        }
    }

    public void clearScrollbackBuffer() {
        if(maxScrollbackLines == 0) {
            return;
        }
        int curIndex = startIndex;
        int endIndex = (startIndex - 1 + buffer.length) % buffer.length;

        while(curIndex != endIndex) {
            buffer[curIndex].clear();
            curIndex = (curIndex + 1) % buffer.length;
        }

        buffer[curIndex].clear();

        startIndex = 0;
    }

    public String getScrollbackAsString() {
        if(maxScrollbackLines == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        int charsAdded = 0;
        int curIndex = startIndex;
        int endIndex = (startIndex - 1 + buffer.length) % buffer.length;

        while(curIndex != endIndex) {
            sb.append(buffer[curIndex].getCharacter());
            charsAdded++;
            curIndex = (curIndex + 1) % buffer.length;

            if(charsAdded % screenWidth == 0) {
                sb.append("\n");
            }
        }

        sb.append(buffer[curIndex].getCharacter());
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

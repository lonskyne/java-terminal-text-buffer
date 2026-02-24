package org.example.TerminalBufferImpl;

import java.util.EnumSet;

public class TerminalBufferCell {
    private char character;
    private TerminalBufferColor backgroundColor;
    private TerminalBufferColor foregroundColor;
    private EnumSet<TerminalBufferCellStyle> styles;

    public TerminalBufferCell() {
        this.character = ' ';
        backgroundColor = TerminalBufferColor.DEFAULT;
        foregroundColor = TerminalBufferColor.DEFAULT;
        styles = EnumSet.noneOf(TerminalBufferCellStyle.class);
    }

    public TerminalBufferCell(char character, TerminalBufferColor backgroundColor, TerminalBufferColor foregroundColor, EnumSet<TerminalBufferCellStyle> styles) {
        this.character = character;
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        this.styles = styles;
    }
}

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

    public void setCharacter(char character) {
        this.character = character;
    }

    public void setBackgroundColor(TerminalBufferColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setForegroundColor(TerminalBufferColor foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public void setStyles(EnumSet<TerminalBufferCellStyle> styles) {
        this.styles = styles;
    }

    public void copyFrom(TerminalBufferCell other) {
        this.character = other.character;
        this.backgroundColor = other.backgroundColor;
        this.foregroundColor = other.foregroundColor;
        this.styles = EnumSet.copyOf(other.styles);
    }

    public void clear() {
        this.character = ' ';
        this.backgroundColor = TerminalBufferColor.DEFAULT;
        this.foregroundColor = TerminalBufferColor.DEFAULT;
        this.styles = EnumSet.noneOf(TerminalBufferCellStyle.class);
    }
}

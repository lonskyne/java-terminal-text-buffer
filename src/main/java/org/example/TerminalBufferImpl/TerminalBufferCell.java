package org.example.TerminalBufferImpl;

import java.util.EnumSet;

public class TerminalBufferCell {
    private boolean isEmpty;
    private char character;
    private TerminalBufferColor backgroundColor;
    private TerminalBufferColor foregroundColor;
    private EnumSet<TerminalBufferCellStyle> styles;

    public TerminalBufferCell() {
        this.isEmpty = true;
        backgroundColor = TerminalBufferColor.DEFAULT;
        foregroundColor = TerminalBufferColor.DEFAULT;
        styles = EnumSet.noneOf(TerminalBufferCellStyle.class);
    }

    public TerminalBufferCell(char character, TerminalBufferColor backgroundColor, TerminalBufferColor foregroundColor, EnumSet<TerminalBufferCellStyle> styles) {
        this.isEmpty = false;
        this.character = character;
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        this.styles = styles;
    }

    public void setCharacter(char character) {
        this.isEmpty = false;
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
        this.isEmpty = other.isEmpty;
        this.character = other.character;
        this.backgroundColor = other.backgroundColor;
        this.foregroundColor = other.foregroundColor;
        this.styles = EnumSet.copyOf(other.styles);
    }

    public void clear() {
        this.isEmpty = true;
        this.backgroundColor = TerminalBufferColor.DEFAULT;
        this.foregroundColor = TerminalBufferColor.DEFAULT;
        this.styles = EnumSet.noneOf(TerminalBufferCellStyle.class);
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}

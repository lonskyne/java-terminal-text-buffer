package org.example.TerminalBufferImpl;

import java.util.EnumSet;

public class TerminalBufferCell {
    private boolean isEmpty;
    private char character;
    private TerminalBufferColor backgroundColor;
    private TerminalBufferColor foregroundColor;
    private EnumSet<TerminalBufferCellStyle> styles;

    /**
     * Constructs an empty, styless cell with default colors
     */
    public TerminalBufferCell() {
        this.isEmpty = true;
        backgroundColor = TerminalBufferColor.DEFAULT;
        foregroundColor = TerminalBufferColor.DEFAULT;
        styles = EnumSet.noneOf(TerminalBufferCellStyle.class);
    }

    /**
     * Sets a character to a cell, making it not empty.
     *
     * @param character new character of the cell
     */
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

    /**
     * Copies the character, color, styles and empty status from another cell.
     *
     * @param other the cell from which content should be copied from.
     */
    public void copyFrom(TerminalBufferCell other) {
        this.isEmpty = other.isEmpty;
        this.character = other.character;
        this.backgroundColor = other.backgroundColor;
        this.foregroundColor = other.foregroundColor;
        this.styles = EnumSet.copyOf(other.styles);
    }

    /**
     * Clears a cell making it empty without styles and reverting colors to default.
     */
    public void clear() {
        this.isEmpty = true;
        this.backgroundColor = TerminalBufferColor.DEFAULT;
        this.foregroundColor = TerminalBufferColor.DEFAULT;
        this.styles = EnumSet.noneOf(TerminalBufferCellStyle.class);
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    /**
     * Returns the character in the cell. Returns '·' if cell is empty.
     *
     * @return the character in the cell or '·' if cell is empty.
     */
    public char getCharacter() {
        if(isEmpty) {
            return '·';
        }

        return character;
    }

    public EnumSet<TerminalBufferCellStyle> getStyles() {
        return styles;
    }

    public TerminalBufferColor getBackgroundColor() {
        return backgroundColor;
    }

    public TerminalBufferColor getForegroundColor() {
        return foregroundColor;
    }
}

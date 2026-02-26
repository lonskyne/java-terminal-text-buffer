import org.example.TerminalBufferImpl.TerminalBuffer;
import org.example.TerminalBufferImpl.TerminalBufferCellStyle;
import org.example.TerminalBufferImpl.TerminalBufferColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class TerminalBufferTest {

    private TerminalBuffer terminal;

    @BeforeEach
    void setUp() {
        terminal = new TerminalBuffer(5, 3, 3); // width=5, height=3
    }

    @Test
    void testMoveRightAndLeft() {
        terminal.clearScreen();

        terminal.getCursor().moveRight(3);
        assertEquals(3, terminal.getCursor().getCurrentColumn());

        terminal.getCursor().moveLeft(2);
        assertEquals(1, terminal.getCursor().getCurrentColumn());

        // Cursor expected to remain at beginning of line after moving left however many times.
        terminal.getCursor().moveLeft(5);
        terminal.getCursor().moveLeft(1);
        assertEquals(0, terminal.getCursor().getCurrentColumn());
        assertEquals(0, terminal.getCursor().getCurrentRow());

        // Cursor expected to move down and right until hitting the end of the screen buffer, then stop moving.
        terminal.getCursor().moveRight(14);
        assertEquals(4, terminal.getCursor().getCurrentColumn());
        assertEquals(2, terminal.getCursor().getCurrentRow());
    }

    @Test
    void testMoveUpAndDown() {
        terminal.clearScreen();

        terminal.getCursor().setPosition(1, 2);
        terminal.getCursor().moveUp(1);
        assertEquals(0, terminal.getCursor().getCurrentRow());

        // Expected to clamp to top
        terminal.getCursor().moveUp(5);
        assertEquals(0, terminal.getCursor().getCurrentRow());

        terminal.getCursor().moveDown(2);
        assertEquals(2, terminal.getCursor().getCurrentRow());

        // Expected to clamp to bottom
        terminal.getCursor().moveDown(10); // clamp to bottom
        assertEquals(2, terminal.getCursor().getCurrentRow());
    }

    @Test
    void testCursorSetPositionBounds() {
        terminal.clearScreen();

        // Expected to clamp left and top
        terminal.getCursor().setPosition(-5, -5);
        assertEquals(0, terminal.getCursor().getCurrentRow());
        assertEquals(0, terminal.getCursor().getCurrentColumn());

        // Expected to clamp right and bottom
        terminal.getCursor().setPosition(10, 10);
        assertEquals(2, terminal.getCursor().getCurrentRow());
        assertEquals(4, terminal.getCursor().getCurrentColumn());
    }

    @Test
    void testWriteSingleCharacter() {
        terminal.clearScreen();

        terminal.writeTextOnLine("A");

        assertEquals('A', terminal.getCharacterAtPositionScreen(0, 0));
        assertEquals(1, terminal.getCursor().getCurrentColumn());

        // Cursor expected to go to next line after writing until end of line thus scrolling.
        terminal.getCursor().setPosition(2, 4);
        terminal.writeTextOnLine("A");
        assertEquals('A', terminal.getCharacterAtPositionScreen(1, 4));
        assertEquals('·', terminal.getCharacterAtPositionScreen(2, 4));
    }

    @Test
    void testWriteText() {
        terminal.clearScreen();

        terminal.writeTextOnLine("ABC");

        assertEquals("ABC··", terminal.getScreenLineAsString(0));
        assertEquals("ABC··\n·····\n·····\n", terminal.getScreenAsString()); // fill with spaces
        assertEquals(3, terminal.getCursor().getCurrentColumn());

        // Expected to stop writing at end of line and for the cursor to go to the next line.
        terminal.writeTextOnLine("ABC");

        assertEquals("ABCAB", terminal.getScreenLineAsString(0));
        assertEquals("ABCAB\n·····\n·····\n", terminal.getScreenAsString()); // fill with spaces
        assertEquals(0, terminal.getCursor().getCurrentColumn());

        // Expected to write in the next line
        terminal.writeTextOnLine("ABC");

        assertEquals("ABC··", terminal.getScreenLineAsString(1));
    }

    @Test
    void testInsertTextWithWrapWithoutScroll() {
        terminal.clearScreen();
        terminal.getCursor().setPosition(0, 3);

        // Expected to wrap line to next one
        terminal.insertTextOnLine("XYZ");
        assertEquals("···XY\nZ····\n·····\n", terminal.getScreenAsString());

        terminal.insertTextOnLine("XYZ");
        assertEquals("···XY\nZXYZ·\n·····\n", terminal.getScreenAsString());

        terminal.getCursor().setPosition(0, 0);

        // Expected to insert new text in the beginning, shifting other text to the right and down.
        // Expected to only wrap the content of the current row, leaving lines below only shifted, but unchanged.
        terminal.insertTextOnLine("WXYZ");
        assertEquals("WXYZX\nY····\nZXYZ·\n", terminal.getScreenAsString());

        terminal.clearScreen();

        // Expected to insert text between characters, wrapping the current line.
        terminal.insertTextOnLine("ABC");
        terminal.getCursor().setPosition(0, 1);
        terminal.insertTextOnLine("XYZ");
        assertEquals("C····", terminal.getScreenLineAsString(1));
        assertEquals("AXYZB\nC····\n·····\n", terminal.getScreenAsString());

        // Expected to maintain lines below the current as they are, just shift them down.
        terminal.getCursor().setPosition(0, 2);
        terminal.insertTextOnLine("1234");
        assertEquals("AX123\n4YZB·\nC····\n", terminal.getScreenAsString());


    }

    @Test
    void testInsertTextWithWrapWithScroll() {
        terminal.clearScreen();

        terminal.insertTextOnLine("ABC");

        terminal.getCursor().setPosition(0, 1);
        terminal.insertTextOnLine("XYZ");

        terminal.getCursor().setPosition(0, 2);
        terminal.insertTextOnLine("1234");

        // Expected to maintain lines after the current, just shift them.
        // Expected to scroll the screen when a line would exit the screen at the bottom.
        terminal.getCursor().setPosition(0, 0);
        terminal.insertTextOnLine("123456789");
        assertEquals("X123·\n4YZB·\nC····\n", terminal.getScreenAsString());

        // Expected correct wrapping of text.
        terminal.clearScreen();
        terminal.insertTextOnLine("1234567891011121314");
        assertEquals("1314·", terminal.getScreenLineAsString(2));
        assertEquals("67891\n01112\n1314·\n", terminal.getScreenAsString());
    }

    @Test
    void testFillLine() {
        terminal.getCursor().setPosition(1, 0);
        terminal.fillLineWithCharacter('a');

        // Expected filled line and cursor at the beginning of the next.
        assertEquals("·····\naaaaa\n·····\n", terminal.getScreenAsString());
        assertEquals(0, terminal.getCursor().getCurrentColumn());

        terminal.fillLineWithCharacter('b');
        assertEquals("aaaaa\nbbbbb\n·····\n", terminal.getScreenAsString());
        assertEquals(0, terminal.getCursor().getCurrentColumn());

        // Expected to fill out the line with empty cells and maintain the cursor at the beginning of the line.
        terminal.fillLineWithCharacter();
        assertEquals("aaaaa\nbbbbb\n·····\n", terminal.getScreenAsString());
        assertEquals(0, terminal.getCursor().getCurrentColumn());

        terminal.getCursor().setPosition(0, 0);
        terminal.fillLineWithCharacter();
        assertEquals("·····\nbbbbb\n·····\n", terminal.getScreenAsString());
        assertEquals(0, terminal.getCursor().getCurrentColumn());

        terminal.getCursor().setPosition(1, 0);
        terminal.fillLineWithCharacter();
        assertEquals("·····\n·····\n·····\n", terminal.getScreenAsString());
        assertEquals(0, terminal.getCursor().getCurrentColumn());
    }

    @Test
    void testCellAttributes() {
        terminal.clearScreen();

        // Expected default cells when terminal is cleared.
        assertEquals(TerminalBufferColor.DEFAULT, terminal.getBackgroundColorAtPositionScreen(0, 0));
        assertEquals(TerminalBufferColor.DEFAULT, terminal.getForegroundColorAtPositionScreen(0, 0));
        assertEquals(EnumSet.noneOf(TerminalBufferCellStyle.class), terminal.getStylesAtPositionScreen(0, 0));

        terminal.fillLineWithCharacter('A');

        assertEquals(TerminalBufferColor.DEFAULT, terminal.getBackgroundColorAtPositionScreen(0, 0));
        assertEquals(TerminalBufferColor.DEFAULT, terminal.getForegroundColorAtPositionScreen(0, 0));
        assertEquals(EnumSet.noneOf(TerminalBufferCellStyle.class), terminal.getStylesAtPositionScreen(0, 0));

        // Expected to use the new colors and styles when writing on line.
        terminal.setCurrentBackgroundColor(TerminalBufferColor.BLACK);
        terminal.setCurrentForegroundColor(TerminalBufferColor.WHITE);
        terminal.setCurrentStyles(EnumSet.allOf(TerminalBufferCellStyle.class));

        terminal.fillLineWithCharacter('B');

        assertEquals(TerminalBufferColor.BLACK, terminal.getBackgroundColorAtPositionScreen(1, 0));
        assertEquals(TerminalBufferColor.WHITE, terminal.getForegroundColorAtPositionScreen(1, 0));
        assertEquals(EnumSet.allOf(TerminalBufferCellStyle.class), terminal.getStylesAtPositionScreen(1, 0));

        // Expected new styles and colors only on the newly inserted part of the screen.
        terminal.setCurrentBackgroundColor(TerminalBufferColor.BLUE);
        terminal.setCurrentForegroundColor(TerminalBufferColor.RED);
        terminal.setCurrentStyles(EnumSet.of(TerminalBufferCellStyle.BOLD));

        terminal.getCursor().setPosition(1, 1);
        terminal.insertTextOnLine("ABAB");

        assertEquals(TerminalBufferColor.BLACK, terminal.getBackgroundColorAtPositionScreen(1, 0));
        assertEquals(TerminalBufferColor.WHITE, terminal.getForegroundColorAtPositionScreen(1, 0));
        assertEquals(EnumSet.allOf(TerminalBufferCellStyle.class), terminal.getStylesAtPositionScreen(1, 0));

        assertEquals(TerminalBufferColor.BLUE, terminal.getBackgroundColorAtPositionScreen(1, 1));
        assertEquals(TerminalBufferColor.RED, terminal.getForegroundColorAtPositionScreen(1, 1));
        assertEquals(EnumSet.of(TerminalBufferCellStyle.BOLD), terminal.getStylesAtPositionScreen(1, 1));

        assertEquals(TerminalBufferColor.BLUE, terminal.getBackgroundColorAtPositionScreen(1, 4));
        assertEquals(TerminalBufferColor.RED, terminal.getForegroundColorAtPositionScreen(1, 4));
        assertEquals(EnumSet.of(TerminalBufferCellStyle.BOLD), terminal.getStylesAtPositionScreen(1, 4));

        // Expected that cells are scrolled up and with them their color/styles.
        terminal.insertTextOnLine("CDCDC");

        assertEquals(TerminalBufferColor.BLUE, terminal.getBackgroundColorAtPositionScreen(0, 1));
        assertEquals(TerminalBufferColor.RED, terminal.getForegroundColorAtPositionScreen(0, 1));
        assertEquals(EnumSet.of(TerminalBufferCellStyle.BOLD), terminal.getStylesAtPositionScreen(0, 1));
    }

    @Test
    void testCellAttributesScrollback() {
        TerminalBuffer localTerminal = new TerminalBuffer(6, 2, 5);

        localTerminal.setCurrentBackgroundColor(TerminalBufferColor.GREEN);
        localTerminal.writeTextOnLine("AAAAAA");

        localTerminal.setCurrentBackgroundColor(TerminalBufferColor.BLUE);
        localTerminal.writeTextOnLine("BBBBBB");

        // Expected to scroll and maintain color in scrollback
        localTerminal.writeTextOnLine("CCCCCC");

        assertEquals(
                TerminalBufferColor.GREEN,
                localTerminal.getBackgroundColorAtPositionScrollback(4, 0)
        );

        localTerminal.clearScreenAndScrollback();

        localTerminal.setCurrentStyles(EnumSet.of(
                TerminalBufferCellStyle.BOLD,
                TerminalBufferCellStyle.UNDERLINE
        ));

        localTerminal.writeTextOnLine("AAAAAA");
        localTerminal.writeTextOnLine("BBBBBB");

        assertEquals(
                EnumSet.of(TerminalBufferCellStyle.BOLD, TerminalBufferCellStyle.UNDERLINE),
                localTerminal.getStylesAtPositionScrollback(4, 0)
        );
    }

    @Test
    void testScrollbackStoresScrolledLines() {
        terminal.clearScreen();

        terminal.writeTextOnLine("AAAAA");
        terminal.writeTextOnLine("BBBBB");
        // Expected to cause scrolling
        terminal.writeTextOnLine("CCCCC");

        assertEquals("AAAAA", terminal.getScrollbackLineAsString(2));
        assertEquals("BBBBB", terminal.getScreenLineAsString(0));
        assertEquals("CCCCC", terminal.getScreenLineAsString(1));
    }

    @Test
    void testScrollbackPreservesAttributes() {
        terminal.clearScreen();
        terminal.setCurrentForegroundColor(TerminalBufferColor.RED);
        terminal.writeTextOnLine("AAAAA");

        terminal.setCurrentForegroundColor(TerminalBufferColor.BLUE);
        terminal.writeTextOnLine("BBBBB");

        terminal.writeTextOnLine("CCCCC"); // scroll

        assertEquals(
                TerminalBufferColor.RED,
                terminal.getForegroundColorAtPositionScrollback(2, 0)
        );
    }

    @Test
    void testClearScreenAndScrollback() {
        terminal.clearScreen();

        terminal.writeTextOnLine("AAAAA");
        terminal.writeTextOnLine("BBBBB");
        terminal.writeTextOnLine("CCCCC");

        terminal.clearScreenAndScrollback();

        assertEquals("·····\n·····\n·····\n·····\n·····\n·····\n", terminal.getScreenAndScrollbackAsString());
    }

    @Test
    void testGetCharacterAtPositionScrollback() {
        TerminalBuffer localTerminal = new TerminalBuffer(5, 2, 5);

        localTerminal.writeTextOnLine("HELLO");
        localTerminal.writeTextOnLine("WORLD");
        localTerminal.writeTextOnLine("!!!!!");

        assertEquals('H', localTerminal.getCharacterAtPositionScrollback(4, 0));
        assertEquals('O', localTerminal.getCharacterAtPositionScrollback(4, 4));
    }

    @Test
    void testInsertEmptyString() {
        terminal.clearScreen();
        terminal.insertTextOnLine("");

        assertEquals("·····\n·····\n·····\n", terminal.getScreenAsString());
    }

    @Test
    void testWriteEmptyString() {
        terminal.clearScreen();
        terminal.writeTextOnLine("");

        assertEquals("·····\n·····\n·····\n", terminal.getScreenAsString());
    }

    @Test
    void testInsertEmptyLineAtBottomOfScreen() {
        TerminalBuffer localTerminal = new TerminalBuffer(5, 4, 5);

        localTerminal.writeTextOnLine("AAAAA");
        localTerminal.writeTextOnLine("BBBBB");
        localTerminal.writeTextOnLine("CCCCC");

        localTerminal.insertEmptyLineAtBottomOfScreen();

        // First line should be scrolled into scrollback
        assertEquals("AAAAA", localTerminal.getScrollbackLineAsString(4));

        // Screen should shift up and bottom line be empty
        assertEquals("BBBBB", localTerminal.getScreenLineAsString(0));
        assertEquals("CCCCC", localTerminal.getScreenLineAsString(1));
        assertEquals("·····", localTerminal.getScreenLineAsString(2));
    }

    @Test
    void testOutOfBoundsAccess() {
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> terminal.getCharacterAtPositionScreen(-1, 0)
        );

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> terminal.getCharacterAtPositionScreen(0, 10)
        );
    }
}
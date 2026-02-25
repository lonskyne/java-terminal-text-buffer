import org.example.TerminalBufferImpl.TerminalBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TerminalBufferTest {

    private TerminalBuffer terminal;

    @BeforeEach
    void setUp() {
        terminal = new TerminalBuffer(5, 3, 0); // width=5, height=3
    }

    @Test
    void testMoveRightAndLeft() {
        terminal.clearScreen();

        terminal.getCursor().moveRight(3);
        assertEquals(3, terminal.getCursor().getCurrentColumn());

        terminal.getCursor().moveLeft(2);
        assertEquals(1, terminal.getCursor().getCurrentColumn());

        terminal.getCursor().moveLeft(5);
        assertEquals(0, terminal.getCursor().getCurrentColumn());

        terminal.getCursor().moveRight(14);
        assertEquals(4, terminal.getCursor().getCurrentColumn());
        assertEquals(2, terminal.getCursor().getCurrentRow());
    }

    @Test
    void testWriteSingleCharacter() {
        terminal.clearScreen();

        terminal.writeTextOnLine("A");

        assertEquals('A', terminal.getCharacterAtPositionScreen(0, 0));
        assertEquals(1, terminal.getCursor().getCurrentColumn());
    }

    @Test
    void testWriteText() {
        terminal.clearScreen();

        terminal.writeTextOnLine("ABC");

        assertEquals("ABC··\n·····\n·····\n", terminal.getScreenAsString()); // fill with spaces
        assertEquals(3, terminal.getCursor().getCurrentColumn());

        terminal.writeTextOnLine("ABC");

        assertEquals("ABCAB\n·····\n·····\n", terminal.getScreenAsString()); // fill with spaces
        assertEquals(0, terminal.getCursor().getCurrentColumn());
    }

    @Test
    void testInsertTextWithWrapWithoutScroll() {
        terminal.clearScreen();
        terminal.getCursor().setPosition(0, 3);

        terminal.insertTextOnLine("XYZ");
        assertEquals("···XY\nZ····\n·····\n", terminal.getScreenAsString());

        terminal.insertTextOnLine("XYZ");
        assertEquals("···XY\nZXYZ·\n·····\n", terminal.getScreenAsString());

        terminal.getCursor().setPosition(0, 0);

        terminal.insertTextOnLine("WXYZ");
        assertEquals("WXYZX\nY····\nZXYZ·\n", terminal.getScreenAsString());

        terminal.clearScreen();

        terminal.insertTextOnLine("ABC");
        assertEquals("ABC··\n·····\n·····\n", terminal.getScreenAsString());

        terminal.getCursor().setPosition(0, 1);
        terminal.insertTextOnLine("XYZ");
        assertEquals("AXYZB\nC····\n·····\n", terminal.getScreenAsString());

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

        terminal.getCursor().setPosition(0, 0);
        terminal.insertTextOnLine("123456789");
        assertEquals("X123·\n4YZB·\nC····\n", terminal.getScreenAsString());

        terminal.clearScreen();
        terminal.insertTextOnLine("1234567891011121314");
        assertEquals("67891\n01112\n1314·\n", terminal.getScreenAsString());
    }

    @Test
    void testFillLine() {
        terminal.getCursor().setPosition(1, 0);
        terminal.fillLineWithCharacter('a');

        assertEquals("·····\naaaaa\n·····\n", terminal.getScreenAsString());
        assertEquals(0, terminal.getCursor().getCurrentColumn());

        terminal.fillLineWithCharacter('b');
        assertEquals("aaaaa\nbbbbb\n·····\n", terminal.getScreenAsString());
        assertEquals(0, terminal.getCursor().getCurrentColumn());

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
}
package simple.escp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
public class StringUtilTest {

    @Test
    public void alignLeft() {
        assertEquals("1234    ", StringUtil.alignLeft("1234", 8));
        assertEquals("12345678", StringUtil.alignLeft("1234567890", 8));
    }

    @Test
    public void alignCenter() {
        assertEquals("  1234  ", StringUtil.alignCenter("1234", 8));
        assertEquals("  123   ", StringUtil.alignCenter("123", 8));
        assertEquals("12345678", StringUtil.alignCenter("1234567890", 8));
    }

    @Test
    public void alignRight() {
        assertEquals("    1234", StringUtil.alignRight("1234", 8));
        assertEquals("12345678", StringUtil.alignRight("1234567890", 8));
    }

    @Test
    public void align() {
        assertEquals("1234    ", StringUtil.align("1234", 8, StringUtil.ALIGNMENT.LEFT));
        assertEquals("  1234  ", StringUtil.align("1234", 8, StringUtil.ALIGNMENT.CENTER));
        assertEquals("    1234", StringUtil.align("1234", 8, StringUtil.ALIGNMENT.RIGHT));
    }
}

package simple.escp.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static simple.escp.util.EscpUtil.CRFF;
import static simple.escp.util.EscpUtil.CRLF;
import static simple.escp.util.EscpUtil.escCancelBoldFont;
import static simple.escp.util.EscpUtil.escCancelDoubleStrikeFont;
import static simple.escp.util.EscpUtil.escCancelItalicFont;
import static simple.escp.util.EscpUtil.escCancelSuperscriptOrSubscript;
import static simple.escp.util.EscpUtil.escCancelUnderline;
import static simple.escp.util.EscpUtil.escSelectBoldFont;
import static simple.escp.util.EscpUtil.escSelectDoubleStrikeFont;
import static simple.escp.util.EscpUtil.escSelectItalicFont;
import static simple.escp.util.EscpUtil.escSelectSubscript;
import static simple.escp.util.EscpUtil.escSelectSuperscript;
import static simple.escp.util.EscpUtil.escSelectUnderline;

import java.util.regex.Matcher;

import org.junit.jupiter.api.Test;

import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import simple.escp.fill.FillJob;
import simple.escp.fill.function.Function;
import simple.escp.util.EscpUtil;

public class FunctionTest {

    private final String INIT = EscpUtil.escInitalize();

    @Test
    public void pageNo() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"detail\": [" +
                    "\"Page %{PAGE_NO}\"," +
                    "\"Page %{ PAGE_NO}\"," +
                    "\"Page %{PAGE_NO }\"," +
                    "\"Page %{ PAGE_NO }\"," +
                    "\"Page %{PAGE_NO}\"," +
                    "\"Page %{PAGE_NO}\"," +
                    "\"Page %{   PAGE_NO   }\"]" +
                "}" +
            "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "Page 1" + CRLF + "Page 1" + CRLF + "Page 1" + CRLF + CRFF +
            "Page 2" + CRLF + "Page 2" + CRLF + "Page 2" + CRLF + CRFF +
            "Page 3" + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void pageNoWithHeader() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"header\": [\"Halaman %{PAGE_NO}\"]," +
                "\"detail\": [" +
                "\"Detail 2\"," +
                "\"Detail 3\"," +
                "\"Detail 4\"," +
                "\"Detail 5\"]" +
            "}" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "Halaman 1" + CRLF + "Detail 2" + CRLF + "Detail 3" + CRLF + CRFF +
            "Halaman 2" + CRLF + "Detail 4" + CRLF + "Detail 5" + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void ascii() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"detail\": [" +
                    "\"Result: %{65}%{66}%{67}\"," +
                    "\"Result: %{176}%{177}%{178}\"]" +
                "}" +
            "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "Result: ABC" + CRLF +
            "Result: " + (char) 176 + (char) 177 + (char) 178 + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void asciiWithRepeat() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"detail\": [" +
                    "\"Result: %{177 R10}\"," +
                    "\"Result: %{176 R 5}\"]" +
                "}" +
            "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "Result: " + (char) 177 + (char) 177 + (char) 177 + (char) 177 + (char) 177 + (char) 177 + (char) 177 + (char) 177 + (char) 177 + (char) 177 + CRLF +
            "Result: " + (char) 176 + (char) 176 + (char) 176 + (char) 176 + (char) 176 + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void bold() {
        final String jsonString =
        "{" +
            "\"template\": [" +
                "\"%{BOLD}This is bold%{BOLD}\"," +
                "\"This is normal\"" +
            "]" +
        "}";

        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            escSelectBoldFont() + "This is bold" + escCancelBoldFont() + CRLF +
            "This is normal" + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void italic() {
        final String jsonString =
        "{" +
            "\"template\": [" +
                "\"%{ITALIC}This is italic%{ITALIC}\"," +
                "\"This is normal\"" +
            "]" +
        "}";

        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            escSelectItalicFont() + "This is italic" + escCancelItalicFont() + CRLF +
            "This is normal" + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void doubleStrike() {
        final String jsonString =
        "{" +
            "\"template\": [" +
                "\"%{DOUBLE}This is double-strike%{DOUBLE}\"," +
                "\"This is normal\"" +
            "]" +
        "}";

        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            escSelectDoubleStrikeFont() + "This is double-strike" + escCancelDoubleStrikeFont() + CRLF +
            "This is normal" + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void underline() {
        final String jsonString =
        "{" +
            "\"template\": [" +
                "\"%{UNDERLINE}This is underline%{UNDERLINE}\"," +
                "\"This is normal\"" +
            "]" +
        "}";

        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            escSelectUnderline() + "This is underline" + escCancelUnderline() + CRLF +
            "This is normal" + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void superscript() {
        final String jsonString =
        "{" +
            "\"template\": [" +
                "\"This is normal%{SUPER}This is superscript%{SUPER}\"" +
            "]" +
        "}";

        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "This is normal" + escSelectSuperscript() + "This is superscript" + escCancelSuperscriptOrSubscript() +
            CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void subscript() {
        final String jsonString =
        "{" +
            "\"template\": [" +
                "\"This is normal%{SUB}This is subscript%{SUB}\"" +
            "]" +
        "}";

        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "This is normal" + escSelectSubscript() + "This is subscript" + escCancelSuperscriptOrSubscript() +
            CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void autoIncrement() {
        final String jsonString =
        "{" +
            "\"template\": [" +
                "\"Result: %{INC AUTO_NO}\"," +
                "\"Result: %{INC AUTO_A}\"," +
                "\"Result: %{INC AUTO_NO}\"," +
                "\"Result: %{INC AUTO_NO}\"," +
                "\"Result: %{INC AUTO_A}\"" +
            "]" +
        "}";

        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "Result: 1" + CRLF +
            "Result: 1" + CRLF +
            "Result: 2" + CRLF +
            "Result: 3" + CRLF +
            "Result: 2" + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void autoIncrementDuplicate() {
        final String jsonString =
        "{" +
            "\"template\": [" +
                "\"Result: %{INC AUTO_NO}\"," +
                "\"Result: %{INC AUTO_A}\"," +
                "\"Result: %{INC AUTO_NO}\"," +
                "\"Result: %{INC AUTO_NO}\"," +
                "\"Result: %{INC AUTO_A}\"" +
            "]" +
        "}";

        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "Result: 1" + CRLF +
            "Result: 1" + CRLF +
            "Result: 2" + CRLF +
            "Result: 3" + CRLF +
            "Result: 2" + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void lineNo() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"header\": [\"Halaman %{PAGE_NO}\"]," +
                "\"detail\": [" +
                "\"Line %{LINE_NO}\"," +
                "\"Line %{LINE_NO}\"," +
                "\"Line %{LINE_NO}\"," +
                "\"Line %{LINE_NO}\"," +
                "\"Line %{LINE_NO}\"," +
                "\"Line %{LINE_NO}\"]" +
            "}" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "Halaman 1" + CRLF + "Line 2" + CRLF + "Line 3" + CRLF + CRFF +
            "Halaman 2" + CRLF + "Line 2" + CRLF + "Line 3" + CRLF + CRFF +
            "Halaman 3" + CRLF + "Line 2" + CRLF + "Line 3" + CRLF + CRFF +
            INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void globalLineNo() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"header\": [\"Halaman %{PAGE_NO}\"]," +
                "\"detail\": [" +
                "\"Line %{GLOBAL_LINE_NO}\"," +
                "\"Line %{GLOBAL_LINE_NO}\"," +
                "\"Line %{GLOBAL_LINE_NO}\"," +
                "\"Line %{GLOBAL_LINE_NO}\"," +
                "\"Line %{GLOBAL_LINE_NO}\"," +
                "\"Line %{GLOBAL_LINE_NO}\"]" +
            "}" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(
            INIT +
            "Halaman 1" + CRLF + "Line 2" + CRLF + "Line 3" + CRLF + CRFF +
            "Halaman 2" + CRLF + "Line 5" + CRLF + "Line 6" + CRLF + CRFF +
            "Halaman 3" + CRLF + "Line 8" + CRLF + "Line 9" + CRLF + CRFF +
            INIT,
            new FillJob(jsonTemplate.parse()).fill()
        );
    }

    @Test
    public void customFunction() {
        final Function function = new CustomFunction();
        FillJob.addFunction(function);
        final String jsonString =
        "{" +
            "\"template\": [\"Result: %{MY_CUSTOM}\"]" +
        "}";

        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        assertEquals(INIT + "Result: MyCustomResult" + CRLF + CRFF + INIT, new FillJob(jsonTemplate.parse()).fill());

        FillJob.removeFunction(function);
        assertEquals(INIT + "Result: %{MY_CUSTOM}" + CRLF + CRFF + INIT, new FillJob(jsonTemplate.parse()).fill());
    }

    private static class CustomFunction extends Function {

        public CustomFunction() {
            super("%\\{\\s*(MY_CUSTOM)\\s*\\}");
        }
        @Override
        public String process(final Matcher matcher, final Report report, final Page page, final Line line) {
            return "MyCustomResult";
        }

        @Override
        public void reset() {
            // do nothing
        }
    }

}

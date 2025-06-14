/*
 * Copyright © 2003 - 2024 The eFaps Team (-)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package simple.escp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static simple.escp.util.EscpUtil.CR;
import static simple.escp.util.EscpUtil.CRFF;
import static simple.escp.util.EscpUtil.CRLF;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.line.EmptyLine;
import simple.escp.dom.line.ListLine;
import simple.escp.dom.line.TableLine;
import simple.escp.dom.line.TextLine;

public class PageTest
{

    @Test
    public void append()
    {
        final List<Line> content = new ArrayList<>();
        final Page page = new Page(content, null, null, 1, 3);
        page.append("This is line 1");
        assertEquals(1, page.getContent().size());
        assertEquals("This is line 1", page.getContent().get(0).toString());
        assertEquals("This is line 1", page.getLine(1).toString());
        page.append("This is line 2");
        assertEquals(2, page.getContent().size());
        assertEquals("This is line 2", page.getContent().get(1).toString());
        assertEquals("This is line 2", page.getLine(2).toString());
        page.append("This is line 3");
        assertEquals(3, page.getContent().size());
        assertEquals("This is line 3", page.getContent().get(2).toString());
        assertEquals("This is line 3", page.getLine(3).toString());

    }

    @Test
    public void appendFull()
    {
        assertThrows(IllegalStateException.class, () -> {
            final List<Line> content = new ArrayList<>();
            final Page page = new Page(content, null, null, 1, 3);
            page.append("This is line 1");
            page.append("This is line 2");
            page.append("This is line 3");
            page.append("This is line 4");
        });
    }

    @Test
    public void appendFullWithHeaderAndFooter()
    {
        assertThrows(IllegalStateException.class, () -> {
            final List<Line> content = new ArrayList<>();
            final TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
            final TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
            final Page page = new Page(content, header, footer, 1, 3);
            page.append("This is line 1");
            page.append("This is line 2");
        });
    }

    @Test
    public void getWithHeaderAndFooter()
    {
        final List<Line> content = new ArrayList<>();
        content.add(new TextLine("This is content"));
        final TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
        final Page page = new Page(content, header, footer, 1, 3);
        assertEquals("This is header 1", page.getLine(1).toString());
        assertEquals("This is content", page.getLine(2).toString());
        assertEquals("This is footer 1", page.getLine(3).toString());
    }

    @Test
    public void getNumberOfLines()
    {
        // With content only
        List<Line> content = new ArrayList<>();
        content.add(new TextLine("This is content"));
        Page page = new Page(content, null, null, 1, 3);
        assertEquals(1, page.getNumberOfLines());

        // With header and footer
        content = new ArrayList<>();
        content.add(new TextLine("This is content"));
        final TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
        page = new Page(content, header, footer, 1, 3);
        assertEquals(3, page.getNumberOfLines());
    }

    @Test
    public void getLines()
    {
        // With content only
        List<Line> content = new ArrayList<>();
        content.add(new TextLine("This is content"));
        Page page = new Page(content, null, null, 1, 3);
        assertEquals(1, page.getLines().length);
        assertEquals("This is content", page.getLines()[0].toString());

        // With header and footer
        content = new ArrayList<>();
        content.add(new TextLine("This is content"));
        final TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
        page = new Page(content, header, footer, 1, 3);
        assertEquals(3, page.getLines().length);
        assertEquals("This is header 1", page.getLines()[0].toString());
        assertEquals("This is content", page.getLines()[1].toString());
        assertEquals("This is footer 1", page.getLines()[2].toString());
        assertEquals(1, page.getLines()[0].getLineNumber().intValue());
        assertEquals(2, page.getLines()[1].getLineNumber().intValue());
        assertEquals(3, page.getLines()[2].getLineNumber().intValue());
    }

    @Test
    public void convertToString()
    {
        final List<Line> content = new ArrayList<>();
        content.add(new TextLine("This is content"));
        final TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
        final Page page = new Page(content, header, footer, 1, 3);
        assertEquals("This is header 1" + CRLF + "This is content" + CRLF + "This is footer 1" + CRLF + CRFF,
                        page.convertToString(false, true));
        assertEquals("This is header 1" + CR + "This is content" + CR + "This is footer 1" + CR + CRFF,
                        page.convertToString(true, true));
    }

    @Test
    public void insert()
    {
        final List<Line> content = new ArrayList<>();
        content.add(new TextLine("This is content 1"));
        content.add(new TextLine("This is content 2"));
        final TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
        final Page page = new Page(content, header, footer, 1, 5);
        final Line result = page.insert(new TextLine("Inserted line"), 4);

        assertNull(result);
        assertEquals(5, page.getNumberOfLines());
        assertEquals("This is header 1", page.getLine(1).toString());
        assertEquals("This is content 1", page.getLine(2).toString());
        assertEquals("This is content 2", page.getLine(3).toString());
        assertEquals("Inserted line", page.getLine(4).toString());
        assertEquals("This is footer 1", page.getLine(5).toString());
    }

    @Test
    public void insertOverflow()
    {
        final List<Line> content = new ArrayList<>();
        content.add(new TextLine("This is content 1"));
        content.add(new TextLine("This is content 2"));
        content.add(new TextLine("This is content 3"));
        final TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
        final Page page = new Page(content, header, footer, 1, 5);
        final Line result = page.insert(new TextLine("Inserted line"), 3);

        assertEquals("This is content 3", result.toString());
        assertEquals(5, page.getNumberOfLines());
        assertEquals("This is header 1", page.getLine(1).toString());
        assertEquals("This is content 1", page.getLine(2).toString());
        assertEquals("Inserted line", page.getLine(3).toString());
        assertEquals("This is content 2", page.getLine(4).toString());
        assertEquals("This is footer 1", page.getLine(5).toString());
    }

    @Test
    public void removeByObject()
    {
        final List<Line> content = new ArrayList<>();
        final TextLine line1 = new TextLine("This is content 1");
        final TextLine line2 = new TextLine("This is content 2");
        final TextLine line3 = new TextLine("This is content 3");
        content.add(line1);
        content.add(line2);
        content.add(line3);
        final TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
        final Page page = new Page(content, header, footer, 1, 5);

        assertTrue(page.removeLine(line2));
        assertEquals(4, page.getNumberOfLines());
        assertEquals("This is header 1", page.getLine(1).toString());
        assertEquals("This is content 1", page.getLine(2).toString());
        assertEquals("This is content 3", page.getLine(3).toString());
        assertEquals("This is footer 1", page.getLine(4).toString());

        assertTrue(page.removeLine(line1));
        assertEquals(3, page.getNumberOfLines());
        assertEquals("This is header 1", page.getLine(1).toString());
        assertEquals("This is content 3", page.getLine(2).toString());
        assertEquals("This is footer 1", page.getLine(3).toString());

        assertFalse(page.removeLine(new TextLine("This is content 3")));
    }

    @Test
    public void removeByLineNumberHeader()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            final List<Line> content = new ArrayList<>();
            final TextLine line1 = new TextLine("This is content 1");
            final TextLine line2 = new TextLine("This is content 2");
            final TextLine line3 = new TextLine("This is content 3");
            content.add(line1);
            content.add(line2);
            content.add(line3);
            final TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
            final TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
            final Page page = new Page(content, header, footer, 1, 5);
            page.removeLine(1);
        });
    }

    @Test
    public void removeByLineNumberFooter()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            final List<Line> content = new ArrayList<>();
            final TextLine line1 = new TextLine("This is content 1");
            final TextLine line2 = new TextLine("This is content 2");
            final TextLine line3 = new TextLine("This is content 3");
            content.add(line1);
            content.add(line2);
            content.add(line3);
            final TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
            final TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
            final Page page = new Page(content, header, footer, 1, 5);
            page.removeLine(5);
        });
    }

    @Test
    public void removeByLineNumber()
    {
        final List<Line> content = new ArrayList<>();
        final TextLine line1 = new TextLine("This is content 1");
        final TextLine line2 = new TextLine("This is content 2");
        final TextLine line3 = new TextLine("This is content 3");
        content.add(line1);
        content.add(line2);
        content.add(line3);
        final TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
        final Page page = new Page(content, header, footer, 1, 5);

        assertEquals(line2, page.removeLine(3));
        assertEquals(4, page.getNumberOfLines());
        assertEquals(line3, page.removeLine(3));
    }

    @Test
    public void hasDynamicLine()
    {
        final List<Line> content = new ArrayList<>();
        content.add(new TextLine("This is content 1"));
        final Page page = new Page(content, null, null, 1, 3);
        assertFalse(page.hasDynamicLine());
        page.append(new TableLine("test"));
        assertTrue(page.hasDynamicLine());
    }

    @Test
    public void getTableLines()
    {
        final List<Line> content = new ArrayList<>();
        content.add(new TextLine("This is content 1"));
        final TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
        final Page page = new Page(content, header, footer, 1, 5);
        assertEquals(0, page.getTableLines().size());

        page.append(new TableLine("test"));
        assertEquals(1, page.getTableLines().size());
        assertEquals(3, page.getTableLines().get(0).getLineNumber().intValue());
        page.append(new TableLine("test"));
        assertEquals(2, page.getTableLines().size());
        assertEquals(3, page.getTableLines().get(0).getLineNumber().intValue());
        assertEquals(4, page.getTableLines().get(1).getLineNumber().intValue());
    }

    @Test
    public void getListLines()
    {
        final List<Line> content = new ArrayList<>();
        content.add(new TextLine("This is content 1"));
        final TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
        final Page page = new Page(content, header, footer, 1, 5);
        assertEquals(0, page.getListLines().size());

        page.append(new ListLine("test", "every line", null, null));
        assertEquals(1, page.getListLines().size());
        assertEquals(3, page.getListLines().get(0).getLineNumber().intValue());
        page.append(new ListLine("test", "every line", null, null));
        assertEquals(2, page.getListLines().size());
        assertEquals(3, page.getListLines().get(0).getLineNumber().intValue());
        assertEquals(4, page.getListLines().get(1).getLineNumber().intValue());
    }

    @Test
    public void appendEmptyLineUntil()
    {
        List<Line> content = new ArrayList<>();
        final TextLine line1 = new TextLine("This is content 1");
        content.add(line1);
        TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
        TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
        Page page = new Page(content, header, footer, 1, 5);
        page.appendEmptyLineUntil(4);

        assertEquals(2, page.getContent().size());
        assertEquals("This is header 1", ((TextLine) page.getLine(1)).getText());
        assertEquals("This is content 1", ((TextLine) page.getLine(2)).getText());
        assertEquals(EmptyLine.class, page.getLine(3).getClass());
        assertEquals("This is footer 1", ((TextLine) page.getLine(4)).getText());

        content = new ArrayList<>();
        header = new TextLine[] { new TextLine("This is header 1") };
        footer = new TextLine[] { new TextLine("This is footer 1") };
        page = new Page(content, header, footer, 1, 5);
        page.appendEmptyLineUntil(4);

        assertEquals(2, page.getContent().size());
        assertEquals("This is header 1", ((TextLine) page.getLine(1)).getText());
        assertEquals(EmptyLine.class, page.getLine(2).getClass());
        assertEquals(EmptyLine.class, page.getLine(3).getClass());
        assertEquals("This is footer 1", ((TextLine) page.getLine(4)).getText());
    }

    @Test
    public void setLine()
    {
        final List<Line> content = new ArrayList<>();
        final TextLine line1 = new TextLine("This is content 1");
        final TextLine line2 = new TextLine("This is content 2");
        final TextLine line3 = new TextLine("This is content 3");
        content.add(line1);
        content.add(line2);
        content.add(line3);
        final TextLine[] header = new TextLine[] { new TextLine("This is header 1") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer 1") };
        final Page page = new Page(content, header, footer, 1, 5);

        page.setLine(1, new TextLine("This is new header"));
        page.setLine(2, new TextLine("This is new content 1"));
        page.setLine(3, new TextLine("This is new content 2"));
        page.setLine(4, new TextLine("This is new content 3"));
        page.setLine(5, new TextLine("This is new footer"));

        assertEquals("This is new header", ((TextLine) page.getLine(1)).getText());
        assertEquals("This is new content 1", ((TextLine) page.getLine(2)).getText());
        assertEquals("This is new content 2", ((TextLine) page.getLine(3)).getText());
        assertEquals("This is new content 3", ((TextLine) page.getLine(4)).getText());
        assertEquals("This is new footer", ((TextLine) page.getLine(5)).getText());
    }

    @Test
    public void setLineWithoutHeaderAndFooter()
    {
        final List<Line> content = new ArrayList<>();
        final TextLine line1 = new TextLine("This is content 1");
        final TextLine line2 = new TextLine("This is content 2");
        final TextLine line3 = new TextLine("This is content 3");
        content.add(line1);
        content.add(line2);
        content.add(line3);
        final Page page = new Page(content, null, null, 1, 5);

        page.setLine(1, new TextLine("This is new content 1"));
        page.setLine(2, new TextLine("This is new content 2"));
        page.setLine(3, new TextLine("This is new content 3"));

        assertEquals("This is new content 1", ((TextLine) page.getLine(1)).getText());
        assertEquals("This is new content 2", ((TextLine) page.getLine(2)).getText());
        assertEquals("This is new content 3", ((TextLine) page.getLine(3)).getText());
    }

}

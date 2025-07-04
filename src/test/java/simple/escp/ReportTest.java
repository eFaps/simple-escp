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
import static simple.escp.util.EscpUtil.CRFF;
import static simple.escp.util.EscpUtil.CRLF;
import static simple.escp.util.EscpUtil.escInitalize;
import static simple.escp.util.EscpUtil.escPageLength;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import simple.escp.data.MapDataSource;
import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.PageFormat;
import simple.escp.dom.Report;
import simple.escp.dom.line.EmptyLine;
import simple.escp.dom.line.ListLine;
import simple.escp.dom.line.TableLine;
import simple.escp.dom.line.TextLine;
import simple.escp.fill.FillJob;

public class ReportTest {

    @Test
    public void appendPage() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        final TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        final Report report = new Report(pageFormat, header, footer);

        final List<Line> content = new ArrayList<>();
        content.add(new TextLine("This is detail 1."));
        Page page = report.appendSinglePage(content, false);
        assertEquals(1, report.getNumberOfPages());
        assertEquals(1, page.getPageNumber().intValue());
        assertEquals(3, page.getNumberOfLines());

        page = report.appendSinglePage(content, false);
        assertEquals(2, report.getNumberOfPages());
        assertEquals(2, page.getPageNumber().intValue());
        assertEquals(3, page.getNumberOfLines());

        page = report.appendSinglePage(new ArrayList<Line>(), false);
        assertEquals(3, report.getNumberOfPages());
        assertEquals(3, page.getPageNumber().intValue());
        assertEquals(2, page.getNumberOfLines());

        page = report.appendSinglePage(content, true);
        assertEquals(4, report.getNumberOfPages());
        assertEquals(4, page.getPageNumber().intValue());
        assertEquals(1, page.getNumberOfLines());
    }

    @Test
    public void appendFullPage() {
        assertThrows(IllegalArgumentException.class, () -> {
            final PageFormat pageFormat = new PageFormat();
            pageFormat.setPageLength(3);
            final TextLine[] header = new TextLine[] { new TextLine("This is header.") };
            final TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
            final Report report = new Report(pageFormat, header, footer);

            final List<Line> content = new ArrayList<>();
            content.add(new TextLine("This is detail 1."));
            content.add(new TextLine("This is detail 2."));
            content.add(new TextLine("This is detail 3."));
            report.appendSinglePage(content, false);
        });
    }

    @Test
    public void newPage() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        final TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        final Report report = new Report(pageFormat, header, footer);

        Page page = report.newPage(true);
        assertEquals(0, page.getNumberOfLines());
        assertEquals(1, page.getPageNumber().intValue());
        assertEquals(3, page.getPageLength().intValue());
        assertFalse(page.isFull());
        assertEquals(1, report.getNumberOfPages());
        assertEquals(page, report.getCurrentPage());

        page = report.newPage(false);
        assertEquals(2, page.getNumberOfLines());
        assertEquals(2, page.getPageNumber().intValue());
        assertEquals(3, page.getPageLength().intValue());
        assertFalse(page.isFull());
        assertEquals(2, report.getNumberOfPages());
        assertEquals(page, report.getCurrentPage());
    }

    @Test
    public void append() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        final TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        final Report report = new Report(pageFormat, header, footer);

        report.append(new TextLine("This is line 1"), false);
        assertEquals(1, report.getLastPageNumber());
        assertEquals(1, report.getNumberOfPages());
        assertEquals(3, report.getCurrentPage().getNumberOfLines());

        report.append(new TextLine("This is line 2"), false);
        assertEquals(2, report.getLastPageNumber());
        assertEquals(2, report.getNumberOfPages());
        assertEquals(3, report.getCurrentPage().getNumberOfLines());

        report.append(new TextLine("This is line 3"), false);
        assertEquals(3, report.getLastPageNumber());
        assertEquals(3, report.getNumberOfPages());
        assertEquals(3, report.getCurrentPage().getNumberOfLines());
    }

    @Test
    public void appendBasic() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        final TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        final Report report = new Report(pageFormat, header, footer);

        report.append(new TextLine("This is line 1"), true);
        assertEquals(1, report.getLastPageNumber());
        assertEquals(1, report.getNumberOfPages());
        assertEquals(1, report.getCurrentPage().getNumberOfLines());

        report.append(new TextLine("This is line 2"), true);
        assertEquals(1, report.getLastPageNumber());
        assertEquals(1, report.getNumberOfPages());
        assertEquals(2, report.getCurrentPage().getNumberOfLines());

        report.append(new TextLine("This is line 3"), true);
        assertEquals(1, report.getLastPageNumber());
        assertEquals(1, report.getNumberOfPages());
        assertEquals(3, report.getCurrentPage().getNumberOfLines());

        report.append(new TextLine("This is line 4"), true);
        assertEquals(2, report.getLastPageNumber());
        assertEquals(2, report.getNumberOfPages());
        assertEquals(1, report.getCurrentPage().getNumberOfLines());
    }

    @Test
    public void fill() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        pageFormat.setUsePrinterPageLength(false);
        final TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        final Report report = new Report(pageFormat, header, footer);
        report.append(new TextLine("Name: ${name}"), false);
        report.append(new TextLine("Result: ${score}"), false);

        final Map<String, Object> mapSource = new HashMap<>();
        mapSource.put("name", "Solid Snake");
        mapSource.put("score", 80);

        assertEquals(escInitalize() + escPageLength(3) +
            "This is header." + CRLF +
            "Name: Solid Snake" + CRLF +
            "This is footer." + CRLF + CRFF +
            "This is header." + CRLF +
            "Result: 80" + CRLF +
            "This is footer." + CRLF + CRFF + escInitalize(),
            new FillJob(report, new MapDataSource(mapSource)).fill()
        );
    }

    @Test
    public void nextPage() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        pageFormat.setUsePrinterPageLength(false);
        final TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        final Report report = new Report(pageFormat, header, footer);
        report.append(new TextLine("This is in page 1."), false);
        report.lineBreak();
        report.append(new TextLine("This is in page 2."), false);
        report.lineBreak();
        report.append(new TextLine("This is in page 3."), false);

        assertEquals(3, report.getLastPageNumber());
        final Page page1 = report.getPage(1);
        final Page page2 = report.getPage(2);
        final Page page3 = report.getPage(3);
        assertEquals(page2, report.nextPage(page1));
        assertEquals(page3, report.nextPage(page2));
    }

    @Test
    public void previousPage() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        pageFormat.setUsePrinterPageLength(false);
        final TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        final Report report = new Report(pageFormat, header, footer);
        report.append(new TextLine("This is in page 1."), false);
        report.lineBreak();
        report.append(new TextLine("This is in page 2."), false);
        report.lineBreak();
        report.append(new TextLine("This is in page 3."), false);

        assertEquals(3, report.getLastPageNumber());
        final Page page1 = report.getPage(1);
        final Page page2 = report.getPage(2);
        final Page page3 = report.getPage(3);
        assertEquals(page1, report.previousPage(page2));
        assertEquals(page2, report.previousPage(page3));
    }

    @Test
    public void insert() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(5);
        pageFormat.setUsePrinterPageLength(false);
        final TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        final Report report = new Report(pageFormat, header, footer);
        report.append(new TextLine("This is line 1 in page 1"), false);
        report.append(new TextLine("This is line 2 in page 1"), false);
        report.lineBreak();
        report.append(new TextLine("This is line 1 in page 2"), false);
        report.append(new TextLine("This is line 2 in page 2"), false);
        report.append(new TextLine("This is line 3 in page 2"), false);

        report.insert(new TextLine("This is inserted line."), 1, 4);

        assertEquals(2, report.getNumberOfPages());
        final Page page1 = report.getPage(1);
        assertEquals(5, page1.getNumberOfLines());
        assertEquals("This is header.", page1.getLine(1).toString());
        assertEquals("This is line 1 in page 1", page1.getLine(2).toString());
        assertEquals("This is line 2 in page 1", page1.getLine(3).toString());
        assertEquals("This is inserted line.", page1.getLine(4).toString());
        assertEquals("This is footer.", page1.getLine(5).toString());
        final Page page2 = report.getPage(2);
        assertEquals(5, page2.getNumberOfLines());
        assertEquals("This is header.", page2.getLine(1).toString());
        assertEquals("This is line 1 in page 2", page2.getLine(2).toString());
        assertEquals("This is line 2 in page 2", page2.getLine(3).toString());
        assertEquals("This is line 3 in page 2", page2.getLine(4).toString());
        assertEquals("This is footer.", page2.getLine(5).toString());
    }

    @Test
    public void insertWithNewPageFirstLines() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(10);
        pageFormat.setUsePrinterPageLength(false);
        final TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        final Report report = new Report(pageFormat, header, footer);
        for (int i=0; i<8; i++) {
            report.append(new TextLine("This is line in page 1."), false);
        }

        final TextLine[] forNewPages = new TextLine[] { new TextLine("Line 2 in new page."),
            new TextLine("Line 3 in new page."), new TextLine("Line 4 in new page.")};
        report.insert(new TextLine("This is inserted line."), 1, 2, Arrays.asList(forNewPages));

        assertEquals(2, report.getNumberOfPages());
        final Page page1 = report.getPage(1);
        assertEquals(10, page1.getNumberOfLines());
        assertEquals("This is header.", page1.getLine(1).toString());
        assertEquals("This is inserted line.", page1.getLine(2).toString());
        assertEquals("This is line in page 1.", page1.getLine(3).toString());
        assertEquals("This is line in page 1.", page1.getLine(4).toString());
        assertEquals("This is line in page 1.", page1.getLine(5).toString());
        assertEquals("This is line in page 1.", page1.getLine(6).toString());
        assertEquals("This is line in page 1.", page1.getLine(7).toString());
        assertEquals("This is line in page 1.", page1.getLine(8).toString());
        assertEquals("This is line in page 1.", page1.getLine(9).toString());
        assertEquals("This is footer.", page1.getLine(10).toString());

        final Page page2 = report.getPage(2);
        assertEquals(6, page2.getNumberOfLines());
        assertEquals("This is header.", page2.getLine(1).toString());
        assertEquals("Line 2 in new page.", page2.getLine(2).toString());
        assertEquals("Line 3 in new page.", page2.getLine(3).toString());
        assertEquals("Line 4 in new page.", page2.getLine(4).toString());
        assertEquals("This is line in page 1.", page2.getLine(5).toString());
        assertEquals("This is footer.", page2.getLine(6).toString());
    }

    @Test
    public void insertLast() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(5);
        pageFormat.setUsePrinterPageLength(false);
        final TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        final Report report = new Report(pageFormat, header, footer);
        report.append(new TextLine("This is line 1 in page 1"), false);
        report.append(new TextLine("This is line 2 in page 1"), false);
        report.append(new TextLine("This is line 3 in page 1"), false);
        report.append(new TextLine("This is line 1 in page 2"), false);
        report.append(new TextLine("This is line 2 in page 2"), false);

        report.insert(new TextLine("This is inserted line."), 2, 4);

        assertEquals(2, report.getNumberOfPages());
        final Page page1 = report.getPage(1);
        assertEquals(5, page1.getNumberOfLines());
        assertEquals("This is header.", page1.getLine(1).toString());
        assertEquals("This is line 1 in page 1", page1.getLine(2).toString());
        assertEquals("This is line 2 in page 1", page1.getLine(3).toString());
        assertEquals("This is line 3 in page 1", page1.getLine(4).toString());
        assertEquals("This is footer.", page1.getLine(5).toString());
        final Page page2 = report.getPage(2);
        assertEquals(5, page2.getNumberOfLines());
        assertEquals("This is header.", page2.getLine(1).toString());
        assertEquals("This is line 1 in page 2", page2.getLine(2).toString());
        assertEquals("This is line 2 in page 2", page2.getLine(3).toString());
        assertEquals("This is inserted line.", page2.getLine(4).toString());
        assertEquals("This is footer.", page2.getLine(5).toString());
    }

    @Test
    public void insertOverflow() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(5);
        pageFormat.setUsePrinterPageLength(false);
        final TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        final Report report = new Report(pageFormat, header, footer);
        report.append(new TextLine("This is line 1 in page 1"), false);
        report.append(new TextLine("This is line 2 in page 1"), false);
        report.append(new TextLine("This is line 3 in page 1"), false);
        report.append(new TextLine("This is line 1 in page 2"), false);
        report.append(new TextLine("This is line 2 in page 2"), false);
        report.append(new TextLine("This is line 3 in page 2"), false);

        report.insert(new TextLine("This is inserted line."), 1, 3);

        assertEquals(3, report.getNumberOfPages());
        final Page page1 = report.getPage(1);
        assertEquals(5, page1.getNumberOfLines());
        assertEquals("This is header.", page1.getLine(1).toString());
        assertEquals("This is line 1 in page 1", page1.getLine(2).toString());
        assertEquals("This is inserted line.", page1.getLine(3).toString());
        assertEquals("This is line 2 in page 1", page1.getLine(4).toString());
        assertEquals("This is footer.", page1.getLine(5).toString());
        final Page page2 = report.getPage(2);
        assertEquals(5, page2.getNumberOfLines());
        assertEquals("This is header.", page2.getLine(1).toString());
        assertEquals("This is line 3 in page 1", page2.getLine(2).toString());
        assertEquals("This is line 1 in page 2", page2.getLine(3).toString());
        assertEquals("This is line 2 in page 2", page2.getLine(4).toString());
        assertEquals("This is footer.", page2.getLine(5).toString());
        final Page page3 = report.getPage(3);
        assertEquals(3, page3.getNumberOfLines());
        assertEquals("This is header.", page3.getLine(1).toString());
        assertEquals("This is line 3 in page 2", page3.getLine(2).toString());
        assertEquals("This is footer.", page3.getLine(3).toString());
    }

    @Test
    public void insertCausesNewPage() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        pageFormat.setUsePrinterPageLength(false);
        final TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        final Report report = new Report(pageFormat, header, footer);
        report.append(new TextLine("This is line 1 in page 1"), false);

        report.insert(new TextLine("This is inserted line."), 1, 2);

        assertEquals(2, report.getNumberOfPages());
        final Page page1 = report.getPage(1);
        assertEquals(3, page1.getNumberOfLines());
        assertEquals("This is header.", page1.getLine(1).toString());
        assertEquals("This is inserted line.", page1.getLine(2).toString());
        assertEquals("This is footer.", page1.getLine(3).toString());
        final Page page2 = report.getPage(2);
        assertEquals(3, page2.getNumberOfLines());
        assertEquals("This is header.", page2.getLine(1).toString());
        assertEquals("This is line 1 in page 1", page2.getLine(2).toString());
        assertEquals("This is footer.", page2.getLine(3).toString());
    }

    @Test
    public void dynamicLine() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        pageFormat.setUsePrinterPageLength(false);
        final TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        final Report report = new Report(pageFormat, header, footer);
        report.append(new TextLine("This is line 1 in page 1"), false);
        assertFalse(report.hasDynamicLine());
        report.append(new TableLine("test"), true);
        assertTrue(report.hasDynamicLine());
    }

    @Test
    public void cloneReport() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        pageFormat.setUsePrinterPageLength(false);
        final TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        final Report report = new Report(pageFormat, header, footer);
        report.append(new TextLine("This is line 1 in page 1."), false);

        final Report cloneReport = new Report(report);

        // Change cloned report.
        assertEquals(1, cloneReport.getNumberOfPages());
        assertEquals("This is header.", cloneReport.getPage(1).getLine(1).toString());
        assertEquals("This is line 1 in page 1.", cloneReport.getPage(1).getLine(2).toString());
        assertEquals("This is footer.", cloneReport.getPage(1).getLine(3).toString());

        // Make sure cloned report is modified
        cloneReport.getPage(1).removeLine(2);
        assertEquals(1, cloneReport.getNumberOfPages());
        assertEquals(2, cloneReport.getPage(1).getNumberOfLines());
        assertEquals("This is header.", cloneReport.getPage(1).getLine(1).toString());
        assertEquals("This is footer.", cloneReport.getPage(1).getLine(2).toString());

        // Make sure original report is not modified.
        assertEquals(1, report.getNumberOfPages());
        assertEquals(3, report.getPage(1).getNumberOfLines());
        assertEquals("This is header.", report.getPage(1).getLine(1).toString());
        assertEquals("This is line 1 in page 1.", report.getPage(1).getLine(2).toString());
        assertEquals("This is footer.", report.getPage(1).getLine(3).toString());
    }

    @Test
    public void getFirstPageWithTableLines() {
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        pageFormat.setUsePrinterPageLength(false);
        Report report = new Report(pageFormat, null, null);
        report.append(new TextLine("This is line 1 in page 1."), false);
        assertNull(report.getFirstPageWithTableLines());

        pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        pageFormat.setUsePrinterPageLength(false);
        report = new Report(pageFormat, null, null);
        report.append(new TableLine("test"), false);
        assertEquals(report.getPage(1), report.getFirstPageWithTableLines());

        pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        pageFormat.setUsePrinterPageLength(false);
        report = new Report(pageFormat, null, null);
        report.append(new TextLine("This is line 1 in page 1."), false);
        report.lineBreak();
        report.append(new TableLine("source"), false);
        assertEquals(report.getPage(2), report.getFirstPageWithTableLines());
    }

    @Test
    public void getFirstPageWithListLines() {
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        pageFormat.setUsePrinterPageLength(false);
        Report report = new Report(pageFormat, null, null);
        report.append(new TextLine("This is line 1 in page 1."), false);
        assertNull(report.getFirstPageWithListLines());

        pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        pageFormat.setUsePrinterPageLength(false);
        report = new Report(pageFormat, null, null);
        report.append(new ListLine("test", "line text", null, null), false);
        assertEquals(report.getPage(1), report.getFirstPageWithListLines());

        pageFormat = new PageFormat();
        pageFormat.setPageLength(3);
        pageFormat.setUsePrinterPageLength(false);
        report = new Report(pageFormat, null, null);
        report.append(new TextLine("This is line 1 in page 1."), false);
        report.lineBreak();
        report.append(new ListLine("test", "line text", null, null), false);
        assertEquals(report.getPage(2), report.getFirstPageWithListLines());
    }


    @Test
    public void newPageStartAtLineNumber() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(5);
        pageFormat.setUsePrinterPageLength(false);
        final TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        final Report report = new Report(pageFormat, header, footer);
        report.newPage(false, 4);
        report.append(new TextLine("This is line 4"), false);
        report.append(new TextLine("This is in new page"), false);

        assertEquals(2, report.getNumberOfPages());
        assertEquals(5, report.getPage(1).getNumberOfLines());
        assertEquals("This is header.", ((TextLine)report.getPage(1).getLine(1)).getText());
        assertTrue(report.getPage(1).getLine(2) instanceof EmptyLine);
        assertTrue(report.getPage(1).getLine(3) instanceof EmptyLine);
        assertEquals("This is line 4", ((TextLine)report.getPage(1).getLine(4)).getText());
        assertEquals("This is footer.", ((TextLine)report.getPage(1).getLine(5)).getText());

        assertEquals(3, report.getPage(2).getNumberOfLines());
        assertEquals("This is header.", ((TextLine)report.getPage(2).getLine(1)).getText());
        assertEquals("This is in new page", ((TextLine)report.getPage(2).getLine(2)).getText());
        assertEquals("This is footer.", ((TextLine)report.getPage(2).getLine(3)).getText());
    }

    @Test
    public void getFlatLines() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(5);
        pageFormat.setUsePrinterPageLength(false);
        final TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        final Report report = new Report(pageFormat, header, footer);
        report.newPage(false, 4);
        report.append(new TextLine("This is line 4"), false);
        report.append(new TextLine("This is in new page"), false);

        final List<Line> results = report.getFlatLines();
        assertEquals(6, results.size());
        assertEquals("This is header.", ((TextLine)results.get(0)).getText());
        assertEquals("This is line 4", ((TextLine)results.get(1)).getText());
        assertEquals("This is footer.", ((TextLine)results.get(2)).getText());
        assertEquals("This is header.", ((TextLine)results.get(3)).getText());
        assertEquals("This is in new page", ((TextLine)results.get(4)).getText());
        assertEquals("This is footer.", ((TextLine)results.get(5)).getText());
    }

    @Test
    public void getGlobalLineNumber() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(4);
        pageFormat.setUsePrinterPageLength(false);
        final TextLine[] header = new TextLine[] { new TextLine("This is header.") };
        final TextLine[] footer = new TextLine[] { new TextLine("This is footer.") };
        final Report report = new Report(pageFormat, header, footer);
        report.append(new TextLine("This is line"), false);
        report.newPage(false);
        report.append(new TextLine("This is line"), false);

        assertEquals(2, report.getNumberOfPages());
        for (final Page page : report) {
            if (page.getPageNumber() == 1) {
                assertEquals(3, page.getNumberOfLines());
                assertEquals(1, page.getLine(1).getGlobalLineNumber().intValue());
                assertEquals(2, page.getLine(2).getGlobalLineNumber().intValue());
                assertEquals(3, page.getLine(3).getGlobalLineNumber().intValue());
            } else if (page.getPageNumber() == 2) {
                assertEquals(3, page.getNumberOfLines());
                assertEquals(4, page.getLine(1).getGlobalLineNumber().intValue());
                assertEquals(5, page.getLine(2).getGlobalLineNumber().intValue());
                assertEquals(6, page.getLine(3).getGlobalLineNumber().intValue());
            }
        }
    }

}

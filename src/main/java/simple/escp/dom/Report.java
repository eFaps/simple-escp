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
package simple.escp.dom;

import simple.escp.dom.line.EmptyLine;
import simple.escp.dom.line.TextLine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * DOM class to represent a print result in form of collection of empty, one or more <code>Page</code>.
 * <code>Report</code> is usually the output of parsing stage.  To initiate filling stage,
 * call <code>fill()</code> method of this class.
 */
public class Report implements Iterable<Page> {

    private static final Logger LOG = Logger.getLogger("simple.escp");

    private List<Page> pages = new ArrayList<>();
    private int lastPageNumber = 0;
    private PageFormat pageFormat;
    private Page currentPage;
    private TextLine[] header;
    private TextLine[] footer;
    private TextLine[] lastPageFooter;
    private boolean lineBreak;

    /**
     * Create a clone from another report.
     *
     * @param anotherReport a <code>Report</code> to clone.
     */
    public Report(Report anotherReport) {
        init(anotherReport.getPageFormat(), anotherReport.getHeader(), anotherReport.getFooter(),
            anotherReport.getLastPageFooter());
        pages = new ArrayList<>();
        for (Page page : anotherReport) {
            pages.add(new Page(page, anotherReport.getPageFormat().getPageLength()));
        }
        if (!pages.isEmpty()) {
            currentPage = pages.get(pages.size() - 1);
        }
        lastPageNumber = anotherReport.getLastPageNumber();
    }

    /**
     * Create a new instance of <code>Report</code>.
     *
     * @see #Report(PageFormat, simple.escp.dom.line.TextLine[],
            simple.escp.dom.line.TextLine[], simple.escp.dom.line.TextLine[])
     * @param pageFormat the <code>PageFormat</code> for this <code>Report</code>.
     * @param header header for all pages in this <code>Report</code>.
     * @param footer footer for all pages in this <code>Report</code>.
     *
     */
    public Report(PageFormat pageFormat, TextLine[] header, TextLine[] footer) {
        this(pageFormat, header, footer, null);
    }

    /**
     * Create a new instance of <code>Report</code>.
     *
     * @param pageFormat the <code>PageFormat</code> for this <code>Report</code>.
     * @param header header for all pages in this <code>Report</code>.
     * @param footer footer for all pages in this <code>Report</code>.
     * @param lastPageFooter footer for the last page in this <code>Report</code>.
     */
    public Report(PageFormat pageFormat, TextLine[] header, TextLine[] footer, TextLine[] lastPageFooter) {
        init(pageFormat, header, footer, lastPageFooter);
    }

    /**
     * Create a new instance of <code>Report</code>.
     *
     * @see #Report(int, simple.escp.dom.line.TextLine[], simple.escp.dom.line.TextLine[],
                    simple.escp.dom.line.TextLine[])
     *
     * @param pageLength length of every pages in this report in number of lines.
     * @param header header for all pages in this <code>Report</code>.
     * @param footer footer for all pages in this <code>Report</code>.
     */
    public Report(int pageLength, TextLine[] header, TextLine[] footer) {
        this(pageLength, header, footer, null);
    }

    /**
     * Create a new instance of <code>Report</code>.
     *
     * @param pageLength length of every pages in this report in number of lines.
     * @param header header for all pages in this <code>Report</code>.
     * @param footer footer for all pages in this <code>Report</code>.
     * @param lastPageFooter footer for the last page in this <code>Report</code>.
     */
    public Report(int pageLength, TextLine[] header, TextLine[] footer, TextLine[] lastPageFooter) {
        pageFormat = new PageFormat();
        pageFormat.setPageLength(pageLength);
        init(pageFormat, header, footer, lastPageFooter);
    }

    /**
     * Initialization setup this report.
     *
     * @param pageFormat the <code>PageFormat</code> for this <code>Report</code>.
     * @param header header for all of pages in this <code>Report</code>.
     * @param footer footer for all of pages in this <code>Report</code>.
     * @param lastPageFooter footer that will be displayed only in the last page of this <code>Report</code>.
     */
    private void init(PageFormat pageFormat, TextLine[] header, TextLine[] footer, TextLine[] lastPageFooter) {
        this.pageFormat = pageFormat;
        if ((pageFormat.getPageLength() == null) && (!pageFormat.isUsePageLengthFromPrinter())) {
            throw new IllegalArgumentException("Invalid page format with pageLength undefined when " +
                    "isUsePageLengthFromPrinter is false.");
        }
        this.header = (header == null) ? new TextLine[0] : Arrays.copyOf(header, header.length);
        this.footer = (footer == null) ? new TextLine[0] : Arrays.copyOf(footer, footer.length);
        this.lastPageFooter = (lastPageFooter == null) ? new TextLine[0] :
            Arrays.copyOf(lastPageFooter, lastPageFooter.length);
        this.lineBreak = false;
    }

    /**
     * Get the <code>PageFormat</code> for this report.
     *
     * @return <code>PageFormat</code> for this report.
     */
    public PageFormat getPageFormat() {
        return pageFormat;
    }

    /**
     * Get the header for this page.
     *
     * @return header for this page.
     */
    public TextLine[] getHeader() {
        return Arrays.copyOf(header, header.length);
    }

    /**
     * Create a copy of every <code>TextLine</code> in header.  The modification to the copy will not
     * affect the original header stored in this report.
     *
     * @return the copy of current header.
     */
    public TextLine[] copyHeader() {
        TextLine[] result = new TextLine[header.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new TextLine(header[i]);
        }
        return result;
    }

    /**
     * Get the footer for this page.
     *
     * @return footer for this page.
     */
    public TextLine[] getFooter() {
        return Arrays.copyOf(footer, footer.length);
    }

    /**
     * Create a copy of every <code>TextLine</code> in footer.  The modification to the copy will not
     * affect the original footer stored in this report.
     *
     * @return the copy of current footer.
     */
    public TextLine[] copyFooter() {
        TextLine[] result = new TextLine[footer.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new TextLine(footer[i]);
        }
        return result;
    }

    /**
     * Get the footer for the last page of this <code>Report</code>.
     *
     * @return last page footer.
     */
    public TextLine[] getLastPageFooter() {
        return Arrays.copyOf(lastPageFooter, lastPageFooter.length);
    }

    /**
     * Create a copy of every <code>TextLine</code> in last page footer.  The modification to the copy will not
     * affect the original footer stored in this report.
     *
     * @return the copy of current last page footer.
     */
    public TextLine[] copyLastPageFooter() {
        TextLine[] result = new TextLine[lastPageFooter.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new TextLine(lastPageFooter[i]);
        }
        return result;
    }

    /**
     * Get current page number for this report.
     *
     * @return current page number in this report starting from 1.
     */
    public int getLastPageNumber() {
        return lastPageNumber;
    }

    /**
     * Get current page in this report.
     *
     * @return the latest <code>Page</code> for this report.
     */
    public Page getCurrentPage() {
        return currentPage;
    }

    /**
     * Get a <code>Page</code> based on its <code>pageNumber</code>.
     *
     * @param pageNumber the page number for the page that will be retrieved.
     * @return a <code>Page</code> or throws <code>IllegalArgumentException</code> if <code>pageNumber</code>
     *         is not valid.
     */
    public Page getPage(int pageNumber) {
        if (pageNumber < 1 || pageNumber > getLastPageNumber()) {
            throw new IllegalArgumentException("Invalid page: " + pageNumber);
        }
        return pages.get(pageNumber - 1);
    }

    /**
     * Get the first <code>Page</code> in this report that has <code>TableLine</code>.
     *
     * @return a <code>Page</code> that has at least one <code>TableLine</code>, or <code>null</code> if no page
     *         in this report has <code>TableLine</code>.
     */
    public Page getFirstPageWithTableLines() {
        for (Page page : pages) {
            if (!page.getTableLines().isEmpty()) {
                return page;
            }
        }
        return null;
    }

    /**
     * Get the first <code>Page</code> in this report that has <code>ListLine</code>.
     *
     * @return a <code>Page</code> that has at least one <code>ListLine</code>, or <code>null</code> if no page
     *         in this report has <code>ListLine</code>.
     */
    public Page getFirstPageWithListLines() {
        for (Page page : pages) {
            if (!page.getListLines().isEmpty()) {
                return page;
            }
        }
        return null;
    }

    /**
     * Get number of pages in this report.
     *
     * @return number of pages in this report.
     */
    public int getNumberOfPages() {
        return pages.size();
    }

    /**
     * Get next page of the specified page.
     *
     * @param  page find the next page of this <code>page</code>.
     * @return a <code>Page</code> or <code>null</code> if <code>page</code> is the last page.
     */
    public Page nextPage(Page page) {
        if (page.getPageNumber() == pages.size()) {
            return null;
        }
        // Note that page is 1-based while the list is 0-based.
        // page + 1 to list index is: page + 1 - 1 = page + 0.
        return pages.get(page.getPageNumber());
    }

    /**
     * Get previous page of the specified page.
     *
     * @param  page find the previous page of this <code>page</code>.
     * @return a <code>Page</code> or <code>null</code> if <code>page</code> is the first page.
     */
    public Page previousPage(Page page) {
        if (page.getPageNumber() == 1) {
            return null;
        }
        // Note that page is 1-based while the list is 0-based.
        // page - 1 to list index is: page - 1 - 1 = page - 2.
        return pages.get(page.getPageNumber() - 2);
    }

    /**
     * Create a new page for this report.
     *
     * @param plain if <code>true</code> will ignore header and footer.  Set this to <code>false</code> to create
     *              a <code>Page</code> that doesn't have header and footer.
     * @return the created <code>Page</code>.
     */
    public Page newPage(boolean plain) {
        lineBreak = false;
        lastPageNumber++;
        Page page;
        if (plain) {
            LOG.fine("Creating a new page without any header and footer.");
            page = new Page(new ArrayList<Line>(), null, null, lastPageNumber, pageFormat.getPageLength());
        } else {
            LOG.fine("Creating a new page that has report's header and footer.");
            page = new Page(new ArrayList<Line>(), copyHeader(), copyFooter(), lastPageNumber,
                pageFormat.getPageLength());
        }
        pages.add(page);
        currentPage = page;
        return page;
    }

    /**
     * Create a new page for this report that start at specified line.  The line before the specified line
     * will be filled by an <code>EmptyLine</code>.
     *
     * @param plain if <code>true</code> will ignore header and footer.  Set this to <code>false</code> to create
     *              a <code>Page</code> that doesn't have header and footer.
     * @param startAtLineNumber start appending from this line number and ignore lines before this line number.
     * @return the created <code>Page</code>.
     */
    public Page newPage(boolean plain, int startAtLineNumber) {
        Page page = newPage(plain);
        page.appendEmptyLineUntil(startAtLineNumber);
        return page;
    }

    /**
     * Start a line break.  The next call of <code>append()</code> will create a new page and write to this new
     * page instead of current page.
     */
    public void lineBreak() {
        this.lineBreak = true;
    }

    /**
     * Add a new single page to the last page of this report.
     *
     * @param content the content of the page.
     * @param plain set <code>true</code> to include header and footer, or <code>false</code> if otherwise.
     * @return the created <code>Page</code>.
     */
    public Page appendSinglePage(List<Line> content, boolean plain) {
        Page page = newPage(plain);
        page.setContent(content);
        return page;
    }

    /**
     * Add a new single page to the last page of this report.
     *
     * @param content the content of the page.
     * @param plain set <code>true</code> to include header and footer, or <code>false</code> if otherwise.
     * @return the created <code>Page</code>.
     */
    public Page appendSinglePage(Line[] content, boolean plain) {
        List<Line> contentInList = new ArrayList<>(content.length);
        for (Line s : content) {
            contentInList.add(s);
        }
        return appendSinglePage(contentInList, plain);
    }

    /**
     * Add a new line to this report.  If current page is full, this method will create a new page and write
     * to the new page.
     *
     * @param line a new line to be inserted to the last page of this report.
     * @param plain set <code>true</code> to include header and footer, or <code>false</code> if otherwise.
     */
    public void append(Line line, boolean plain) {
        if (lineBreak || (currentPage == null) || currentPage.isFull()) {
            newPage(plain);
        }
        currentPage.append(line);
    }

    /**
     * Insert a new line at certain page and certain position.  This may causes a new page to be created if necessary.
     *
     * @param line a new line to be inserted to this report.
     * @param pageNumber the page number in which the new line will be inserted.
     * @param lineNumber the line number in the page where the new line will be inserted.
     * @param newPageFirstLines these lines will be added to the new page if this insertion creates new page.
     */
    public void insert(Line line, int pageNumber, int lineNumber, List<? extends Line> newPageFirstLines) {
        if (pageNumber < 1 || pageNumber > pages.size()) {
            throw new IllegalArgumentException("Invalid page number: " + pageNumber);
        }
        Page startPage = pages.get(pageNumber - 1);
        Line discardedLine = startPage.insert(line, lineNumber);
        Page currentPage = nextPage(startPage);
        while (discardedLine != null) {
            if (currentPage == null) {
                currentPage = newPage(false);
                if (newPageFirstLines != null) {
                    currentPage.append(newPageFirstLines);
                }
            }
            discardedLine = currentPage.insert(discardedLine, header.length + 1  +
                (newPageFirstLines == null ? 0 : newPageFirstLines.size()));
            LOG.fine("Discarded line for next page is [" + discardedLine + "]");
            currentPage = nextPage(currentPage);
        }
    }

    /**
     * Insert a new line at certain page and certain position.  This may causes a new page to be created if necessary.
     *
     * @param line a new line to be inserted to this report.
     * @param pageNumber the page number in which the new line will be inserted.
     * @param lineNumber the line number in the page where the new line will be inserted.
     */
    public void insert(Line line, int pageNumber, int lineNumber) {
        insert(line, pageNumber, lineNumber, null);
    }

    /**
     * Determine if one or more pages in this report has one or more dynamic lines.
     *
     * @return <code>true</code> if this report contains dynamic line or <code>false</code> if otherwise.
     */
    public boolean hasDynamicLine() {
        for (Page page : pages) {
            if (page.hasDynamicLine()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all lines in this report combined as one big page.  <code>EmptyLine</code> will be ignored.
     *
     * @return all lines in this report.
     */
    public List<Line> getFlatLines() {
        List<Line> result = new ArrayList<>();
        for (Page page : pages) {
            for (Line line : page.getLines()) {
                if (line instanceof EmptyLine) {
                    continue;
                }
                result.add(line);
            }
        }
        return result;
    }

    /**
     * Get the length of content for each page.  Content is the area within the page that doesn't include
     * header and footer.  Methods such as {@link #append(Line, boolean)} or
     * {@link #insert(Line, int, int)} works only within this area.
     *
     * @return the length of content page in number of lines.
     */
    public int getContentLinesPerPage() {
        return pageFormat.getPageLength() - header.length - footer.length;
    }

    /**
     * Get line number that indicates the first line of footer.
     *
     * @return line number for the first line of footer.  If this report doesn't have footer, the returned value
     *         is line number for the last line.
     */
    public int getStartOfFooter() {
        return header.length + getContentLinesPerPage();
    }

    @Override
    public Iterator<Page> iterator() {
        int globalLineNumber = 1;
        for (Page page : pages) {
            for (Line line : page.getLines()) {
                if (line != null) {
                    line.setGlobalLineNumber(globalLineNumber++);
                }
            }
        }
        return pages.iterator();
    }
}

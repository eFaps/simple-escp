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
package simple.escp.json;

import java.util.logging.Logger;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import simple.escp.dom.Line;
import simple.escp.dom.PageFormat;
import simple.escp.dom.Report;
import simple.escp.dom.TableColumn;
import simple.escp.dom.line.ListLine;
import simple.escp.dom.line.TableLine;
import simple.escp.dom.line.TextLine;

/**
 * A helper class for parsing.
 */
public class Parser {

    private static final Logger LOG = Logger.getLogger("simple.escp");

    private Report result;
    private final Integer pageLength;
    private final PageFormat pageFormat;
    private JsonArray firstPage;
    private JsonArray lastPage;
    private JsonArray header;
    private JsonArray footer;
    private JsonArray lastPageFooter;
    private JsonArray detail;

    /**
     * Create a new instance of this class.
     *
     * @param pageFormat a <code>PageFormat</code>.
     */
    public Parser(PageFormat pageFormat) {
        this.pageFormat = pageFormat;
        this.pageLength = pageFormat.getPageLength();
    }

    /**
     * Set the "firstPage" section.
     *
     * @param firstPage a <code>JsonArray</code> or <code>null</code> if it is not available.
     */
    public void setFirstPage(JsonArray firstPage) {
        this.firstPage = firstPage;
    }

    /**
     * Set the "header" section.
     *
     * @param header a <code>JsonArray</code> or <code>null</code> if it is not available.
     */
    public void setHeader(JsonArray header) {
        if (pageLength == null) {
            throw new IllegalArgumentException("Can't use 'header' if 'pageLength' is not defined.");
        }
        this.header = header;
    }

    /**
     * Set the "footer" section.
     *
     * @param footer a <code>JsonArray</code> or <code>null</code> if it is not available.
     */
    public void setFooter(JsonArray footer) {
        if (pageLength == null) {
            throw new IllegalArgumentException("Can't use 'footer' if 'pageLength' is not defined.");
        }
        this.footer = footer;
    }

    /**
     * Set the "lastPageFooter" section.
     *
     * @param lastPageFooter a <code>JsonArray</code> or <code>null</code> if it is not available.
     */
    public void setLastPageFooter(JsonArray lastPageFooter) {
        if (pageLength == null) {
            throw new IllegalArgumentException("Can't use 'lastPageFooter' if 'pageLength' is not defined.");
        }
        this.lastPageFooter = lastPageFooter;
    }

    /**
     * Set the "lastPage" section.
     *
     * @param lastPage a <code>JsonArray</code> or <code>null</code> if it is not available.
     */
    public void setLastPage(JsonArray lastPage) {
        this.lastPage = lastPage;
    }

    /**
     * Set the "detail" section.  If this method is called more than once, it will append the
     * <code>JsonArray</code> to previous detail.
     *
     * @param detail a <code>JsonArray</code>.
     */
    public void setDetail(JsonArray detail) {
        if (this.detail == null) {
            this.detail = detail;
        } else {
            this.detail.addAll(detail);
        }
    }

    /**
     * Convert <code>JsonArray</code> into <code>String[]</code>.
     *
     * @param text is the JSON array to convert.
     * @return result in <code>String[]</code>.
     */
    private String[] jsonToString(JsonArray text) {
        final int size = text == null ? 0 : text.size();
        final String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            result[i] = text.getString(i);
        }
        return result;
    }

    /**
     * Convert <code>JsonArray</code> into <code>TextLine[]</code>.  This method will <strong>ignore</strong>
     * non-text line or dynamic line such as <code>TableLine</code>.
     *
     * <p>See also {@link #jsonToLine(javax.json.JsonArray)} for converting to generic <code>Line[]</code>.
     *
     * @param text is the JSON array to convert.
     * @return result in <code>TextLine[]</code>.
     */

    private TextLine[] jsonToTextLine(JsonArray text) {
        LOG.fine("Converting [" + text + "] into TextLine.");
        final int size = text == null ? 0 : text.size();
        final TextLine[] result = new TextLine[size];
        for (int i = 0; i < size; i++) {
            final JsonValue value = text.get(i);
            if (value.getValueType() == JsonValue.ValueType.STRING) {
                result[i] = new TextLine(text.getString(i));
            } else {
                LOG.warning("[" + value + "] is not a string and will be skipped.");
            }
        }
        return result;
    }

    /**
     * Convert <code>JsonObject</code> into <code>TableLine</code>.
     *
     * @param table is the JSON object to convert.
     * @return result in <code>TableLine</code>.
     */

    private TableLine jsonToTableLine(JsonObject table) {
        LOG.fine("Converting [" + table + "] into TableLine.");
        final TableLine tableLine = new TableLine(table.getString("table"));
        if (table.containsKey("border")) {
            tableLine.setDrawBorder(table.getBoolean("border", false));
        }
        if (table.containsKey("lineSeparator")) {
            tableLine.setDrawLineSeparator(table.getBoolean("lineSeparator", false));
        }
        if (table.containsKey("underlineSeparator")) {
            tableLine.setDrawUnderlineSeparator(table.getBoolean("underlineSeparator", false));
        }
        final JsonArray columns = table.getJsonArray("columns");
        if (columns == null) {
            throw new IllegalArgumentException("Table must have 'columns'.");
        } else {
            for (int i = 0; i < columns.size(); i++) {
                final JsonObject column = columns.getJsonObject(i);
                if (!column.containsKey("source")) {
                    throw new IllegalArgumentException("Can't find 'source' for column " + i);
                }
                if (!column.containsKey("width")) {
                    throw new IllegalArgumentException("Can't find 'width' for column " + i);
                }
                final TableColumn tableColumn = tableLine.addColumn(column.getString("source"), column.getInt("width"));
                if (column.containsKey("caption")) {
                    tableColumn.setCaption(column.getString("caption"));
                }
                if (column.containsKey("wrap")) {
                    tableColumn.setWrap(column.getBoolean("wrap", false));
                }
            }
        }
        return tableLine;
    }

    /**
     * Convert <code>JsonObject</code> into <code>ListLine</code>.
     *
     * @param list is the JSON object to convert.
     * @return result in <code>ListLine</code>.
     */
    private ListLine jsonToListLine(JsonObject list) {
        LOG.fine("Converting [" + list + "] into ListLine.");
        final String source = list.getString("list");
        if (!list.containsKey("line")) {
            throw new IllegalArgumentException("List must have 'line'.");
        }
        final String line = list.getString("line");
        TextLine[] header = null, footer = null;
        if (list.containsKey("header")) {
            header = jsonToTextLine(list.getJsonArray("header"));
        }
        if (list.containsKey("footer")) {
            footer = jsonToTextLine(list.getJsonArray("footer"));
        }
        return new ListLine(source, line, header, footer);
    }

    /**
     * Convert <code>JsonArray</code> into <code>Line[]</code>.
     *
     * @param text is the JSON array to convert.
     * @return result in <code>Line[]</code>.
     */
    private Line[] jsonToLine(JsonArray text) {
        LOG.fine("Converting [" + text + "] into Line.");
        final int size = text == null ? 0 : text.size();
        final Line[] result = new Line[size];
        for (int i = 0; i < size; i++) {
            final JsonValue value = text.get(i);
            if (value.getValueType() == JsonValue.ValueType.STRING) {
                result[i] = new TextLine(text.getString(i));
            } else if (value.getValueType() == JsonValue.ValueType.OBJECT) {
                final JsonObject object = text.getJsonObject(i);
                if (object.containsKey("table")) {
                    result[i] = jsonToTableLine(object);
                } else if (object.containsKey("list")) {
                    result[i] = jsonToListLine(object);
                } else {
                    LOG.warning("Found unsupported object [" + object + "]");
                    throw new IllegalArgumentException("Unsupported object: " + object);
                }
            }
        }
        return result;
    }

    /**
     * Before calling this method, don't forget to call setters such as <code>setFirstPage()</code>,
     * <code>setLastPage()</code>, <code>setDetail()</code>, etc.  The parse result from this method
     * can also be obtained later by calling <code>getResult()</code>.
     *
     * @return result of parsing in <code>Pages</code>.
     */
    public Report parse() {
        result = new Report(pageFormat, jsonToTextLine(header), jsonToTextLine(footer), jsonToTextLine(lastPageFooter));
        if (firstPage != null) {
            LOG.fine("Parsing firstPage section");
            result.appendSinglePage(jsonToLine(firstPage), true);
            result.lineBreak();
        }
        if (detail != null) {
            LOG.fine("Parsing detail section");
            for (final Line line: jsonToLine(detail)) {
                result.append(line, false);
            }
        }
        if (lastPage != null) {
            LOG.fine("Parsing lastPage section");
            result.lineBreak();
            result.appendSinglePage(jsonToLine(lastPage), true);
        }
        return getResult();
    }

    /**
     * Get the result of previous <code>parse()</code> invocation.  If <code>parse()</code> hasn't been invoked
     * before, this method will invoke it and return the result.
     *
     * @return result of parsing in <code>Pages</code>.
     */
    public Report getResult() {
        if (result == null) {
            parse();
        }
        return result;
    }

}

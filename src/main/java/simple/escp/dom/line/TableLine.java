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
package simple.escp.dom.line;

import simple.escp.dom.Line;
import simple.escp.dom.TableColumn;
import simple.escp.placeholder.BasicPlaceholder;
import simple.escp.util.EscpUtil;
import simple.escp.util.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * DOM class to represent table.  A table consists of one or more {@link simple.escp.dom.TableColumn}.
 */
public class TableLine extends Line implements Iterable<TableColumn> {

    private List<TableColumn> columns = new ArrayList<>();
    private String source;
    private boolean drawBorder;
    private boolean drawLineSeparator;
    private boolean drawUnderlineSeparator;
    private TextLine[] header;
    private TextLine[] footer;

    /**
     * Create a new <code>TableLine</code>.
     *
     * @param source a placeholder text to retrieve data source for this table.  It should be evaluated to a
     *               <code>Collection</code> during filling.
     */
    public TableLine(String source) {
        this.source = source;
    }

    /**
     * Get the placeholder text that represents data source for this table.
     *
     * @return data source for this table.
     */
    public String getSource() {
        return source;
    }

    /**
     * Add a new <code>TableColumn</code> to this table.
     *
     * @param column a new <code>TableColumn</code> that will be appended at the right-most position.
     * @return the new <code>TableColumn</code>.
     */
    public TableColumn addColumn(TableColumn column) {
        columns.add(column);
        return column;
    }

    /**
     * Add a new <code>TableColumn</code> to this table.
     *
     * @param text the text, may contains placeholder, for this column.
     * @param width width of this column in number of characters.
     * @return the new <code>TableColumn</code>.
     */
    public TableColumn addColumn(String text, int width) {
        TableColumn column = new TableColumn(text, width);
        return addColumn(column);
    }

    /**
     * Get number of columns in this table.
     *
     * @return number of columns in this table.
     */
    public int getNumberOfColumns() {
        return columns.size();
    }

    /**
     * Get a column based on index.
     *
     * @param index the position of column starting.  The left-most column has index <code>1</code>, the next column
     *              is <code>2</code>, and so on.
     * @return the <code>TableColumn</code> at <code>index</code> position.
     */
    public TableColumn getColumnAt(int index) {
        if ((index < 1) || (index > getNumberOfColumns())) {
            throw new IllegalArgumentException("Index [" + index + "] is not valid.");
        }
        return columns.get(index - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDynamic() {
        return true;
    }

    /**
     * Determine if this table line should have border drawn around it.  Drawing border will reduce the
     * width of every columns by one character.  It also will increase number of lines required by the table.
     *
     * @return <code>true</code> if this <code>TableLine</code> has border.
     */
    public boolean isDrawBorder() {
        return drawBorder;
    }

    /**
     * Set wether to enable drawing solid border for this table line or not.
     *
     * @param drawBorder <code>true</code> to enable border for this table.
     */
    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
    }

    /**
     * Determine if an extra line that contains solid line as line separator should be drawn for this table.
     *
     * @return <code>true</code> if line separator should be drawn.
     */
    public boolean isDrawLineSeparator() {
        return drawLineSeparator;
    }

    /**
     * Set wether to enable line separator for this table line or not.
     *
     * @param drawLineSeparator <code>true</code> to enable line separator for this table.
     */
    public void setDrawLineSeparator(boolean drawLineSeparator) {
        this.drawLineSeparator = drawLineSeparator;
    }

    /**
     * Determine if the last line of a row in table should be underlined.  This is more compact than creating
     * a new line consists of box characters.
     *
     * @return <code>true</code> if underline should be applied to last line of a row.
     */
    public boolean isDrawUnderlineSeparator() {
        return drawUnderlineSeparator;
    }

    /**
     * Set wether to underline the last line of a row in this table.
     *
     * @param drawUnderlineSeparator <code>true</code> to underline last line of each row.
     */
    public void setDrawUnderlineSeparator(boolean drawUnderlineSeparator) {
        this.drawUnderlineSeparator = drawUnderlineSeparator;
    }

    /**
     * Get width of lines in this table in number of characters.
     *
     * @return width in number of characters.
     */
    public int getWidth() {
        int result = 0;
        for (TableColumn column : columns) {
            result += column.getWidth();
        }
        return result;
    }

    /**
     * Retrieve the header for this table.  If border for this table is disabled, header will only be a row that
     * contains column's caption.  If border is enabled, the header is three lines with CP347 pseudo-graphic
     * characters to simulate a border.
     *
     * @return the definition of header.
     */
    public TextLine[] getHeader() {
        if (header == null) {
            List<TextLine> tmp = new ArrayList<>();
            StringBuilder line = new StringBuilder();

            // draw header upper border if necessary
            if (isDrawBorder()) {
                line.append(EscpUtil.CP347_LIGHT_DOWN_RIGHT);
                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                    TableColumn column = columns.get(columnIndex);
                    for (int i = 0; i < column.getWidth() - 1; i++) {
                        line.append(EscpUtil.CP347_LIGHT_HORIZONTAL);
                    }
                    if (columnIndex == (columns.size() - 1)) {
                        line.append(EscpUtil.CP347_LIGHT_DOWN_LEFT);
                    } else {
                        line.append(EscpUtil.CP347_LIGHT_DOWN_HORIZONTAL);
                    }
                }
                tmp.add(new TextLine(line.toString()));
            }

            // draw column name
            line = new StringBuilder();
            if (isDrawBorder()) {
                line.append(EscpUtil.CP347_LIGHT_VERTICAL);
            }
            for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                TableColumn column = columns.get(columnIndex);
                int width = column.getWidth() - (isDrawBorder() ? 1 : 0);
                StringUtil.ALIGNMENT alignment = (new BasicPlaceholder(column.getText())).getAlignment();
                if (alignment == null) {
                    alignment = StringUtil.ALIGNMENT.LEFT;
                }
                line.append(StringUtil.align(column.getCaption(), width, alignment));
                if (isDrawBorder()) {
                    line.append(EscpUtil.CP347_LIGHT_VERTICAL);
                }
            }
            tmp.add(new TextLine(line.toString()));

            // draw lower border if necessary
            line = new StringBuilder();
            if (isDrawBorder()) {
                line.append(EscpUtil.CP347_LIGHT_VERTICAL_RIGHT);
                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                    TableColumn column = columns.get(columnIndex);
                    for (int i = 0; i < column.getWidth() - 1; i++) {
                        line.append(EscpUtil.CP347_LIGHT_HORIZONTAL);
                    }
                    if (columnIndex == (columns.size() - 1)) {
                        line.append(EscpUtil.CP347_LIGHT_VERTICAL_LEFT);
                    } else {
                        line.append(EscpUtil.CP347_LIGHT_VERTICAL_HORIZONTAL);
                    }
                }
                tmp.add(new TextLine(line.toString()));
            }
            header = tmp.toArray(new TextLine[0]);
        }
        return Arrays.copyOf(header, header.length);
    }

    /**
     * Retrieve the footer for this table.
     *
     * @return the definition of footer.
     */
    public TextLine[] getFooter() {
        if (footer == null) {
            List<TextLine> tmp = new ArrayList<>();
            StringBuilder line = new StringBuilder();

            // draw lower border if necessary
            if (isDrawBorder()) {
                line.append(EscpUtil.CP347_LIGHT_UP_RIGHT);
                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                    TableColumn column = columns.get(columnIndex);
                    for (int i = 0; i < column.getWidth() - 1; i++) {
                        line.append(EscpUtil.CP347_LIGHT_HORIZONTAL);
                    }
                    if (columnIndex == (columns.size() - 1)) {
                        line.append(EscpUtil.CP347_LIGHT_UP_LEFT);
                    } else {
                        line.append(EscpUtil.CP347_LIGHT_UP_HORIZONTAL);
                    }
                }
                tmp.add(new TextLine(line.toString()));
            }
            footer = tmp.toArray(new TextLine[0]);
        }
        return Arrays.copyOf(footer, footer.length);
    }

    @Override
    public Iterator<TableColumn> iterator() {
        return columns.iterator();
    }

}

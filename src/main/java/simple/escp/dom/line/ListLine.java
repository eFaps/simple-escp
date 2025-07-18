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
import java.util.Arrays;

/**
 * DOM class to represent list.  A list is something like <code>{@link simple.escp.dom.line.TableLine}</code> but
 * doesn't have columnar layout.
 */
public class ListLine extends Line {

    private String source;
    private String lineSource;
    private TextLine[] header;
    private TextLine[] footer;

    /**
     * Create a new <code>ListLine</code>.
     *
     * @param source a placeholder text to retrieve data source for this list.  It should be evaluated to a
     *               <code>Collection</code> during filling.
     * @param lineSource a placeholder text for every line in this list.  It will be used to translate every
     *                   elements in <code>Collection</code> source into a text.
     * @param header header for this list.  Set to <code>null</code> if this list doesn't have header.
     * @param footer footer for this list.  Set to <code>null</code> if this list doesn't have footer.
     */
    public ListLine(String source, String lineSource, TextLine[] header, TextLine[] footer) {
        this.source = source;
        this.lineSource = lineSource;
        this.header = (header == null) ? new TextLine[0] : header;
        this.footer = (footer == null) ? new TextLine[0] : footer;
    }

    /**
     * Get the placeholder text that represents data source for this list.
     *
     * @return data source for this list.
     */
    public String getSource() {
        return source;
    }

    /**
     * Get the placeholder text that will be used to evaluate the text of every lines in this list.
     *
     * @return placeholder text for line.
     */
    public String getLineSource() {
        return lineSource;
    }

    /**
     * Get the header for this list.
     *
     * @return header for this list.
     */

    public TextLine[] getHeader() {
        return Arrays.copyOf(header, header.length);
    }

    /**
     * Get the footer for this list.
     *
     * @return footer for this list.
     */
    public TextLine[] getFooter() {
        return Arrays.copyOf(footer, footer.length);
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

}

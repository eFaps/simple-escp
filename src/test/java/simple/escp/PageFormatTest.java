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

import org.junit.jupiter.api.Test;

import simple.escp.dom.PageFormat;

public class PageFormatTest {

    @Test
    public void pageFormatLineSpacing() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setLineSpacing("1/8");
        final String result = pageFormat.build();
        assertEquals(4, result.length());
        assertEquals((char) 27, result.charAt(0));
        assertEquals('@', result.charAt(1));
        assertEquals((char) 27, result.charAt(2));
        assertEquals((char) 48, result.charAt(3));
    }

    @Test
    public void pageFormatCharacterPitch() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setCharacterPitch("17");
        final String result = pageFormat.build();
        assertEquals(5, result.length());
        assertEquals((char) 27, result.charAt(0));
        assertEquals('@', result.charAt(1));
        assertEquals((char) 27, result.charAt(2));
        assertEquals((char) 33, result.charAt(3));
        assertEquals((char)  4, result.charAt(4));
    }

    @Test
    public void pageFormatPageLength() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(10);
        pageFormat.setUsePrinterPageLength(false);
        final String result = pageFormat.build();
        assertEquals(5, result.length());
        assertEquals((char) 27, result.charAt(0));
        assertEquals('@', result.charAt(1));
        assertEquals((char) 27, result.charAt(2));
        assertEquals((char) 67, result.charAt(3));
        assertEquals((char) 10, result.charAt(4));
    }

    @Test
    public void usePrinterPageLength() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageLength(10);
        pageFormat.setUsePrinterPageLength(true);
        final String result = pageFormat.build();
        assertEquals(2, result.length());
        assertEquals((char) 27, result.charAt(0));
        assertEquals('@', result.charAt(1));
    }

    @Test
    public void pageFormatPageWidth() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageWidth(40);
        final String result = pageFormat.build();
        assertEquals(5, result.length());
        assertEquals((char) 27, result.charAt(0));
        assertEquals('@', result.charAt(1));
        assertEquals((char) 27, result.charAt(2));
        assertEquals((char) 81, result.charAt(3));
        assertEquals((char) 40, result.charAt(4));
    }

    @Test
    public void pageFormatLeftAndRightMargin() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPageWidth(80);
        pageFormat.setLeftMargin(5);
        pageFormat.setRightMargin(10);
        final String result = pageFormat.build();
        assertEquals(8, result.length());
        assertEquals((char) 27, result.charAt(0));
        assertEquals('@', result.charAt(1));
        assertEquals((char) 27, result.charAt(2));
        assertEquals((char) 108, result.charAt(3));
        assertEquals((char) 5, result.charAt(4));
        assertEquals((char) 27, result.charAt(5));
        assertEquals((char) 81, result.charAt(6));
        assertEquals((char) 70, result.charAt(7));
    }

    @Test
    public void pageFormatButtonMargin() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setBottomMargin(80);
        final String result = pageFormat.build();
        assertEquals(5, result.length());
        assertEquals((char) 27, result.charAt(0));
        assertEquals('@', result.charAt(1));
        assertEquals((char) 27, result.charAt(2));
        assertEquals((char) 78, result.charAt(3));
        assertEquals((char) 80, result.charAt(4));
    }

    @Test
    public void pageFormatTypefaceRoman() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setTypeface("roman");
        final String result = pageFormat.build();
        assertEquals(5, result.length());
        assertEquals((char) 27, result.charAt(0));
        assertEquals('@', result.charAt(1));
        assertEquals((char) 27, result.charAt(2));
        assertEquals((char) 107, result.charAt(3));
        assertEquals((char) 0, result.charAt(4));
    }

    @Test
    public void pageFormatTypefaceSansSerif() {
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setTypeface("sans-serif");
        final String result = pageFormat.build();
        assertEquals(5, result.length());
        assertEquals((char) 27, result.charAt(0));
        assertEquals('@', result.charAt(1));
        assertEquals((char) 27, result.charAt(2));
        assertEquals((char) 107, result.charAt(3));
        assertEquals((char) 1, result.charAt(4));
    }

}

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
package simple.escp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
public class EscpUtilTest {

    @Test
    public void esc() {
        final String result = EscpUtil.esc(55);
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 55, result.charAt(1));
    }

    @Test
    public void escValue() {
        String result = EscpUtil.esc(73, 0);
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 73, result.charAt(1));
        assertEquals((char)  0, result.charAt(2));

        result = EscpUtil.esc(40, 71, 1, 0, 20);
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 40, result.charAt(1));
        assertEquals((char) 71, result.charAt(2));
        assertEquals((char)  1, result.charAt(3));
        assertEquals((char)  0, result.charAt(4));
        assertEquals((char) 20, result.charAt(5));
    }

    @Test
    public void escInitialize() {
        final String result = EscpUtil.escInitalize();
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 64, result.charAt(1));
    }

    @Test
    public void escOnePerSixInchLineSpacing() {
        final String result = EscpUtil.escOnePerSixInchLineSpacing();
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 50, result.charAt(1));
    }

    @Test
    public void escOnePerEightInchLineSpacing() {
        final String result = EscpUtil.escOnePerEightInchLineSpacing();
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 48, result.charAt(1));
    }

    @Test
    public void escPageLength() {
        final String result = EscpUtil.escPageLength(10);
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 67, result.charAt(1));
        assertEquals((char) 10, result.charAt(2));
    }

    @Test
    public void escLeftMargin() {
        final String result = EscpUtil.escLeftMargin(5);
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 108, result.charAt(1));
        assertEquals((char) 5, result.charAt(2));
    }

    @Test
    public void escRightMargin() {
        final String result = EscpUtil.escRightMargin(50);
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 81, result.charAt(1));
        assertEquals((char) 50, result.charAt(2));
    }

    @Test
    public void escBottomMargin() {
        final String result = EscpUtil.escBottomMargin(70);
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 78, result.charAt(1));
        assertEquals((char) 70, result.charAt(2));
    }

    @Test
    public void escSelectTypeFace() {
        String result = EscpUtil.escSelectTypeface(EscpUtil.TYPEFACE.ROMAN);
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 107, result.charAt(1));
        assertEquals((char) 0, result.charAt(2));

        result = EscpUtil.escSelectTypeface(EscpUtil.TYPEFACE.SANS_SERIF);
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 107, result.charAt(1));
        assertEquals((char) 1, result.charAt(2));
    }

    @Test
    public void escBold() {
        String result = EscpUtil.escSelectBoldFont();
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 69, result.charAt(1));

        result = EscpUtil.escCancelBoldFont();
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 70, result.charAt(1));
    }

    @Test
    public void escItalic() {
        String result = EscpUtil.escSelectItalicFont();
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 52, result.charAt(1));

        result = EscpUtil.escCancelItalicFont();
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 53, result.charAt(1));
    }

    @Test
    public void escDoubleStrike() {
        String result = EscpUtil.escSelectDoubleStrikeFont();
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 71, result.charAt(1));

        result = EscpUtil.escCancelDoubleStrikeFont();
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 72, result.charAt(1));
    }

    @Test
    public void escUnderline() {
        String result = EscpUtil.escSelectUnderline();
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 45, result.charAt(1));
        assertEquals((char)  1, result.charAt(2));

        result = EscpUtil.escCancelUnderline();
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 45, result.charAt(1));
        assertEquals((char)  0, result.charAt(2));
    }

    @Test
    public void escSubscript() {
        String result = EscpUtil.escSelectSubscript();
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 83, result.charAt(1));
        assertEquals((char)  1, result.charAt(2));

        result = EscpUtil.escCancelSuperscriptOrSubscript();
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 84, result.charAt(1));
    }

    @Test
    public void escSuperscript() {
        String result = EscpUtil.escSelectSuperscript();
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 83, result.charAt(1));
        assertEquals((char)  0, result.charAt(2));

        result = EscpUtil.escCancelSuperscriptOrSubscript();
        assertEquals((char) 27, result.charAt(0));
        assertEquals((char) 84, result.charAt(1));
    }

    @Test
    public void crlf() {
        String result = EscpUtil.CR;
        assertEquals(1, result.length());
        assertEquals((char) 13, result.charAt(0));

        result = EscpUtil.CRLF;
        assertEquals(2, result.length());
        assertEquals((char) 13, result.charAt(0));
        assertEquals((char) 10, result.charAt(1));
    }

}

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

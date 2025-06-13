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
package simple.escp.fill.function;

import simple.escp.dom.Line;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import simple.escp.util.EscpUtil;
import java.util.regex.Matcher;

/**
 * A built-in function that will generate ESC/P to toggle superscript printing.
 * If the current status of superscript printing is off, <code>%{SUPER}</code> will generate ESC/P to switch on
 * superscript printing.  If superscript printing is already turned on (as a result of previous invocation),
 * <code>%{SUPER}</code> will turn off superscript printing.  It will also turn off subscript printing.
 */
public class SuperscriptFunction extends Function {

    private boolean superscript;

    /**
     * Create new instance of this function.
     */
    public SuperscriptFunction() {
        super("%\\{\\s*(SUPER)\\s*\\}");
        superscript = false;
    }

    @Override
    public String process(Matcher matcher, Report report, Page page, Line line) {
        String result = superscript ? EscpUtil.escCancelSuperscriptOrSubscript() : EscpUtil.escSelectSuperscript();
        superscript = !superscript;
        return result;
    }

    @Override
    public void reset() {
        superscript = false;
    }

}

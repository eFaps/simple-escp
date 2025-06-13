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
 * A built-in function that will generate ESC/P to toggle subscript printing.
 * If the current status of subscript printing is off, <code>%{SUB}</code> will generate ESC/P to switch on
 * subscript printing.  If subscript printing is already turned on (as a result of previous invocation),
 * <code>%{SUB}</code> will turn off subscript printing.  It will also turn off superscript printing.
 */
public class SubscriptFunction extends Function {

    private boolean subscript;

    /**
     * Create new instance of this function.
     */
    public SubscriptFunction() {
        super("%\\{\\s*(SUB)\\s*\\}");
        subscript = false;
    }

    @Override
    public String process(Matcher matcher, Report report, Page page, Line line) {
        String result = subscript ? EscpUtil.escCancelSuperscriptOrSubscript() : EscpUtil.escSelectSubscript();
        subscript = !subscript;
        return result;
    }

    @Override
    public void reset() {
        subscript = false;
    }

}

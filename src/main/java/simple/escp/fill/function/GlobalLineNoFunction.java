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
import java.util.regex.Matcher;

/**
 * A built-in function that will return the global line number.  To use this function, use the following
 * expression: <code>%{GLOBAL_LINE_NO}</code>.  Unlike <code>%{LINE_NO}</code> that will reset line number when
 * page changes, this function will continue increase line number until the end of report.
 */
public class GlobalLineNoFunction extends Function {

    /**
     * Create a new instance of this function.
     */
    public GlobalLineNoFunction() {
        super("%\\{\\s*(GLOBAL_LINE_NO)\\s*\\}");
    }

    @Override
    public String process(Matcher matcher, Report report, Page page, Line line) {
        return line.getGlobalLineNumber().toString();
    }

    @Override
    public void reset() {
        // Do nothing
    }
}

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
 *  A built-in function that will return current page number.  To use this function, use the following
 *  expression: <code>%{PAGE_NO}</code>.
 */
public class PageNoFunction extends Function {

    /**
     * Create new instance of this function.
     */
    public PageNoFunction() {
        super("%\\{\\s*(PAGE_NO)\\s*\\}");
    }

    @Override
    public String process(Matcher matcher, Report report, Page page, Line line) {
        return String.valueOf(page.getPageNumber());
    }

    @Override
    public void reset() {
        // Do nothing
    }
}

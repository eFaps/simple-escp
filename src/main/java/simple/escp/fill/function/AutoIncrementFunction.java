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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * A built-in function that will define a new global number variable.  The initial value global variable created
 * by this built-in function is <code>1</code>.  The next subsequent call of this function (with the same
 * variable name) will increase the previous number and print it.
 *
 * <p>Examples:
 *
 * <p><code>%{INC A}</code> will return <code>1</code>.
 *
 * <p>The next <code>%{INC A}</code> occurence will return <code>2</code>.
 *
 * <p><code>%{INC B}</code> will return <code>1</code>.
 *
 * <p>The next occurence of <code>%{INC B}</code> will return <code>2</code>.
 */
public class AutoIncrementFunction extends Function {

    private Map<String, Integer> variables;
    /**
     * Create a new instance of this function.
     */
    public AutoIncrementFunction() {
        super("%\\{\\s*INC\\s*(\\w+)\\s*\\}");
        variables = new HashMap<>();
    }

    @Override
    public String process(Matcher matcher, Report report, Page page, Line line) {
        String variable = matcher.group(1).toLowerCase();
        if (variables.containsKey(variable)) {
            variables.put(variable, variables.get(variable) + 1);
        } else {
            variables.put(variable, 1);
        }
        return variables.get(variable).toString();
    }

    @Override
    public void reset() {
        variables.clear();
    }
}

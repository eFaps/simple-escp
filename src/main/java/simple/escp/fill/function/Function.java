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
import simple.escp.dom.line.TextLine;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a function that can be called in JSON template by using expression in <code>%{...}</code>
 * format.
 */
public abstract class Function {

    private Pattern pattern;

    /**
     * Create a new function with the specified regex pattern.
     * @param pattern a regex pattern to identify this function.
     */
    public Function(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    /**
     * Retrieve the regex pattern that identify this function.
     *
     * @return a regex pattern.
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Set a new regex pattern that identify this function.
     *
     * @param pattern a regex pattern.
     */
    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * The actual implementation of this function.  Given the input parameters, this method should return
     * <code>String</code> as a result of this function.
     *
     * @param matcher the <code>Matcher</code> that matches this function.  If <code>pattern</code> for this
     *                function contains one or more regex group, use <code>matcher</code> to retrieve the group value,
     *                for example: <code>matcher.group(1)</code>.
     * @param report current report that is being evaluated.
     * @param page current page that is being evaluated.
     * @param line current line that is being evaluated.
     * @return result of this function as <code>String</code>.
     */
    public abstract String process(Matcher matcher, Report report, Page page, Line line);

    /**
     * This method will called when starting a new fill job.  It should clears or reset states of this function that
     * should not be persistent across different fill jobs.
     */
    public abstract void reset();

    /**
     * Find this function in <code>report</code> and translates them into actual value.  This method will
     * process all lines in all pages of <code>report</code>.
     *
     * @param report process this report.
     */
    public void process(Report report) {
        for (Page page : report) {
            for (int i = 1; i <= page.getNumberOfLines(); i++) {
                Line line = page.getLine(i);
                if (line instanceof TextLine) {
                    String text = ((TextLine) line).getText();
                    StringBuffer result = new StringBuffer();
                    Matcher matcher = pattern.matcher(text);
                    while (matcher.find()) {
                        matcher.appendReplacement(result, process(matcher, report, page, line));
                    }
                    matcher.appendTail(result);
                    page.setLine(i, new TextLine(result.toString()));
                }
            }
        }
    }

}

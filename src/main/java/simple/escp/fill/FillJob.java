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
package simple.escp.fill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import simple.escp.data.DataSource;
import simple.escp.dom.Page;
import simple.escp.dom.Report;
import simple.escp.fill.function.AsciiFunction;
import simple.escp.fill.function.AutoIncrementFunction;
import simple.escp.fill.function.BoldFunction;
import simple.escp.fill.function.DoubleStrikeFunction;
import simple.escp.fill.function.Function;
import simple.escp.fill.function.GlobalLineNoFunction;
import simple.escp.fill.function.ItalicFunction;
import simple.escp.fill.function.LineNoFunction;
import simple.escp.fill.function.PageNoFunction;
import simple.escp.fill.function.SubscriptFunction;
import simple.escp.fill.function.SuperscriptFunction;
import simple.escp.fill.function.UnderlineFunction;
import simple.escp.placeholder.BasicPlaceholder;
import simple.escp.placeholder.Placeholder;
import simple.escp.placeholder.ScriptPlaceholder;
import simple.escp.util.EscpUtil;

/**
 * <code>FillJob</code> represent the process of filling a <code>Report</code> with one or more
 * <code>DataSource</code>.  The result of this process is a <code>String</code> that may contains ESC/P commands
 * for printing.
 *
 * <p>A new instance of <code>FillJob</code> should be created for every process of <code>Report</code>'s filling.
 * The new instance can reuse existing <code>Report</code> or <code>DataSource</code>.
 */
public class FillJob {

    private static final Logger LOG = Logger.getLogger("simple.escp");

    public static final Pattern BASIC_PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");
    public static final Pattern SCRIPT_PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{(.+?)\\}\\}");
    public static final List<Function> FUNCTIONS;

    static {
        FUNCTIONS = new ArrayList<>();
        FUNCTIONS.add(new BoldFunction());
        FUNCTIONS.add(new ItalicFunction());
        FUNCTIONS.add(new UnderlineFunction());
        FUNCTIONS.add(new DoubleStrikeFunction());
        FUNCTIONS.add(new SuperscriptFunction());
        FUNCTIONS.add(new SubscriptFunction());
        FUNCTIONS.add(new PageNoFunction());
        FUNCTIONS.add(new AsciiFunction());
        FUNCTIONS.add(new AutoIncrementFunction());
        FUNCTIONS.add(new GlobalLineNoFunction());
        FUNCTIONS.add(new LineNoFunction());
    }

    protected Report report;
    protected DataSource[] dataSources;
    protected Map<String, Placeholder> placeholders = new HashMap<>();
    protected ScriptEngine scriptEngine;

    /**
     * Create a new <code>FillJob</code> with empty data source.
     *
     * @param report the <code>Report</code> that will be filled.
     */
    public FillJob(Report report) {
        this(report, new DataSource[0]);
    }

    /**
     * Create a new <code>FillJob</code> with single <code>DataSource</code>.
     *
     * @param report the <code>Report</code> that will be filled.
     * @param dataSource the <code>DataSource</code> that contains values for filling.
     */
    public FillJob(Report report, DataSource dataSource) {
        this(report, new DataSource[] {dataSource});
    }

    /**
     * Create a new <code>FillJob</code> with multipe <code>DataSource</code>.
     *
     * @param report the <code>Report</code> that will be filled.
     * @param dataSources array that contains <code>DataSource</code> as the source values for filling.
     */
    public FillJob(Report report, DataSource[] dataSources) {
        this.report = report;
        this.dataSources = Arrays.copyOf(dataSources, dataSources.length);

        // Create script engine for ScriptPlaceholder
        final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        scriptEngineManager.setBindings(new DataSourceBinding(this.dataSources));
        this.scriptEngine = scriptEngineManager.getEngineByName("groovy");
        if (this.scriptEngine == null) {
            LOG.fine("Can't find Groovy script engine, will use graal.js script engine.");
            this.scriptEngine = scriptEngineManager.getEngineByName("graal.js");
        }
        if (this.scriptEngine == null) {
            LOG.fine("Can't find Groovy script engine, will use JavaScript script engine.");
            this.scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
        }

        // Reset functions
        for (final Function function : FUNCTIONS) {
            function.reset();
        }
    }

    /**
     * Register a new global function.  This function will have a lower priority compared to built-in function.
     *
     * @param function a new function that will be available for current and subsequent executions.
     */
    public static void addFunction(Function function) {
        if (!FUNCTIONS.contains(function)) {
            FUNCTIONS.add(function);
        }
    }

    /**
     * Remove a registered global function.
     *
     * @param function an existing function that will be removed from list available of functions.
     */
    public static void removeFunction(Function function) {
        FUNCTIONS.remove(function);
    }

    /**
     * Add a new variable to current script engine that can be used by script placeholders later.
     *
     * @param variableName the new variable's name.
     * @param value the value of this new variable.
     */
    public void addScriptVariable(String variableName, Object value) {
        scriptEngine.put(variableName, value);
    }

    /**
     * Remove a variable from current script engine.  This method will remove variable that was in engine scope.
     * It can't be used to remove built-in variables in global scope.
     *
     * @param variableName the name of variable that will be removed.
     */
    public void removeScriptVariable(String variableName) {
        scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).remove(variableName);
    }

    /**
     * Retrieve the report that will be filled by this <code>FillJob</code>.
     *
     * @return an instance of <code>Report</code>.
     */
    public Report getReport() {
        return report;
    }

    /**
     * Retrieve all data source for this <code>FillJob</code>.
     *
     * @return an array of <code>DataSource</code>.
     */
    public DataSource[] getDataSources() {
        return Arrays.copyOf(dataSources, dataSources.length);
    }

    /**
     * Get available <code>Placeholder</code> in this report.
     *
     * @return a <code>Map</code> that contains all <code>Placeholder</code> in this report.
     */
    public Map<String, Placeholder> getPlaceholders() {
        return placeholders;
    }

    /**
     * Find and return a <code>Placeholder</code> by its text.
     * @param text the placeholder's text.  A placeholder text appears as is in template.  For example,
     *             text for <code>${\@name}</code> is <code>"@name"</code>.
     * @return a <code>Placeholder</code> if it is found, or <code>null</code> if no placeholder with the specified
     *         text is exists in this report.
     */
    public Placeholder getPlaceholder(String text) {
        return placeholders.get(text);
    }

    /**
     * This method will fill placeholders with value from both supplied <code>Map</code> and Java Bean object.
     *
     * @param text the source text that has placeholders.
     * @return source with placeholders replaced by actual value.
     */
    protected String fillBasicPlaceholder(String text) {
        final StringBuffer result = new StringBuffer();
        final Matcher matcher = BASIC_PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            final String placeholderText = matcher.group(1);
            LOG.fine("Found basic placeholder text [" + placeholderText + "]");
            Placeholder placeholder = placeholders.get(placeholderText);
            if (placeholder == null) {
                placeholder = new BasicPlaceholder(placeholderText);
                placeholders.put(placeholderText, placeholder);
            }
            matcher.appendReplacement(result, placeholder.getValueAsString(dataSources));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * This method will fill placeholders by executing the script inside that placeholder.
     *
     * @param text the source text that has placeholders.
     * @return source with placeholders replaced by actual value.
     */
    protected String fillScriptPlaceholder(String text) {
        final StringBuffer result = new StringBuffer();
        final Matcher matcher = SCRIPT_PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            final String placeholderText = matcher.group(1);
            LOG.fine("Found script placeholder text [" + placeholderText + "]");
            Placeholder placeholder = placeholders.get(placeholderText);
            if (placeholder == null) {
                placeholder = new ScriptPlaceholder(placeholderText, scriptEngine);
                placeholders.put(placeholderText, placeholder);
            }
            matcher.appendReplacement(result, placeholder.getValueAsString(dataSources));
        }
        matcher.appendTail(result);
        return result.toString();

    }

    /**
     * Execute this <code>FillJob</code> action.  This will perform the action of filling <code>Report</code> with
     * one or more <code>DataSource</code>.  This method will not modify the original <code>Report</code>.
     *
     * @return a <code>String</code> that may contains ESC/P commands and can be printed.
     */
    public String fill() {
        final Report parsedReport = new Report(report);

        // Second phase: fill dynamic line, change last page footer, etc.
        if (parsedReport.hasDynamicLine()) {
            LOG.fine("This report has dynamic line.");
            final TableFillJob tableFillJob = new TableFillJob(parsedReport, dataSources);
            final ListFillJob listFillJob = new ListFillJob(parsedReport, dataSources);
            tableFillJob.fill();
            listFillJob.fill();
        }
        final int lastPageFooterLength = parsedReport.getLastPageFooter().length;
        if (lastPageFooterLength > 0) {
            final Page lastPage = parsedReport.getPage(parsedReport.getLastPageNumber());
            lastPage.setFooter(parsedReport.getLastPageFooter());
            if (lastPage.isOverflow()) {
                lastPage.setFooter(parsedReport.getFooter());
                parsedReport.newPage(false).setFooter(parsedReport.getLastPageFooter());
            }
        }

        final StringBuilder result = new StringBuilder();
        final boolean isAutoLineFeed = parsedReport.getPageFormat().isAutoLineFeed();
        final boolean isAutoFormFeed = parsedReport.getPageFormat().isAutoFormFeed();
        result.append(parsedReport.getPageFormat().build());

        // process functions
        for (final Function function : FUNCTIONS) {
            LOG.fine("Executing function [" + function + "]");
            function.process(parsedReport);
        }

        // process placeholders
        for (final Page page : parsedReport) {
            String pageText = page.convertToString(isAutoLineFeed, isAutoFormFeed);
            pageText = fillBasicPlaceholder(pageText);
            pageText = fillScriptPlaceholder(pageText);
            result.append(pageText);
        }

        if (isAutoFormFeed && !result.toString().endsWith(EscpUtil.CRFF)) {
            result.append(EscpUtil.CRFF);
        }
        result.append(EscpUtil.escInitalize());
        return result.toString();
    }

}

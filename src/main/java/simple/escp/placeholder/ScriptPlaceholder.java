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
package simple.escp.placeholder;

import simple.escp.data.DataSource;
import simple.escp.exception.InvalidPlaceholder;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represent a <code>Placeholder</code> that contains scripts that will be executed by using
 * JSR 223 Scripting for the Java Platform API.
 */
public class ScriptPlaceholder extends Placeholder {

    private static final Logger LOG = Logger.getLogger("simple.escp");
    public static final String SEPARATOR = "::";

    private String script;
    private ScriptEngine scriptEngine;

    /**
     * Create a new instance of script placeholder.
     *
     * @param text a string that defines this placeholder.
     * @param scriptEngine a script engine to execute script in this placeholder.
     */
    public ScriptPlaceholder(String text, ScriptEngine scriptEngine) {
        super(text);
        this.scriptEngine = scriptEngine;
        parseText(getText());
    }

    /**
     * Parse placeholder text.
     *
     * @param text full text that represent this placeholder.
     */
    private void parseText(String text) {
        LOG.fine("Parsing [" + text + "]");
        if (text.contains(SEPARATOR)) {
            String[] parts = text.split(SEPARATOR, 2);
            this.script = parts[0].trim();
            parseText(parts[1].split(SEPARATOR));
        } else {
            this.script = text;
        }
    }

    /**
     * Retrieve the script of this placeholder.
     *
     * @return a script that will be executed for this placeholder.
     */
    public String getScript() {
        return script;
    }

    /**
     * Set the script of this placeholder.
     *
     * @param script script that will be executed for this placeholder.
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(DataSource[] dataSources) {
        try {
            return scriptEngine.eval(script);
        } catch (ScriptException e) {
            LOG.log(Level.WARNING, "Error durring executing script.", e);
            return "";
        }
    }

}

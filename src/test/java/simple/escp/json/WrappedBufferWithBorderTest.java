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
package simple.escp.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static simple.escp.util.EscpUtil.CP347_LIGHT_VERTICAL;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import simple.escp.data.DataSource;
import simple.escp.data.DataSources;
import simple.escp.dom.Report;
import simple.escp.dom.line.TableLine;
import simple.escp.dom.line.TextLine;
import simple.escp.fill.DataSourceBinding;
import simple.escp.fill.TableFillHelper;
public class WrappedBufferWithBorderTest {

    private TableFillHelper.WrappedBuffer wrappedBuffer;
    private Report generatedReport;

    @BeforeEach
    public void setup() throws URISyntaxException, IOException {
        final JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/single_table_wrap2.json").toURI());
        final List<JsonTemplateFillTest.Person> persons = new ArrayList<>();
        persons.add(new JsonTemplateFillTest.Person("None12345678901234567890", "David12345678901234567890", "None12345678901234567890"));
        persons.add(new JsonTemplateFillTest.Person("David12345678901234567890", "Solid", "Snake12345678901234567890"));
        persons.add(new JsonTemplateFillTest.Person("Snake12345678901234567890", "Jocki", "Hendry12345678901234567890"));
        final Map<String, Object> source = new HashMap<>();
        source.put("persons", persons);
        final DataSource ds = DataSources.from(source);
        final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        scriptEngineManager.setBindings(new DataSourceBinding(new DataSource[]{ds}));
        final ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("groovy");
        final Report report = jsonTemplate.parse();
        generatedReport = new Report(3, null, null);
        final TableLine tableLine = report.getFirstPageWithTableLines().getTableLines().get(0);
        final TableFillHelper tableFillHelper = new TableFillHelper(generatedReport, scriptEngine, tableLine, persons);
        wrappedBuffer = tableFillHelper.getWrappedBuffer();
    }

    @Test
    public void width() {
        assertEquals(9, wrappedBuffer.getWidth(0));
        assertEquals(19, wrappedBuffer.getWidth(1));
        assertEquals(9, wrappedBuffer.getWidth(2));
    }

    @Test
    public void flush() {
        wrappedBuffer.add(0, "1234567890ABCDEFGHIJ12345");
        wrappedBuffer.add(2, "123456789012345");
        wrappedBuffer.flush();
        assertEquals(1, generatedReport.getNumberOfPages());
        assertEquals(CP347_LIGHT_VERTICAL + "0ABCDEFGH" + CP347_LIGHT_VERTICAL + "                   " + CP347_LIGHT_VERTICAL + "012345   " + CP347_LIGHT_VERTICAL, ((TextLine)generatedReport.getPage(1).getLine(1)).getText());
        assertEquals(CP347_LIGHT_VERTICAL + "IJ12345  " + CP347_LIGHT_VERTICAL + "                   " + CP347_LIGHT_VERTICAL + "         " + CP347_LIGHT_VERTICAL, ((TextLine)generatedReport.getPage(1).getLine(2)).getText());
        assertTrue(wrappedBuffer.isEmpty());
    }

}

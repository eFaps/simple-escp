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
package simple.escp.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static simple.escp.util.EscpUtil.CRFF;
import static simple.escp.util.EscpUtil.CRLF;

import java.util.List;

import javax.json.JsonObject;

import org.junit.jupiter.api.Test;

import simple.escp.fill.FillJob;
import simple.escp.json.JsonTemplate;
import simple.escp.util.EscpUtil;

//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomDataSourceTest {

    private final String INIT = EscpUtil.escInitalize();

    @Test
    public void customDataSource_01() {
        final String jsonTemplate = "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 90" +
            "}," +
            "\"template\": [" +
                "\"Value: ${line1}\"," +
                "{" +
                    "\"table\": \"tables\"," +
                    "\"columns\": [" +
                        "{\"source\": \"data\", \"width\": 13}" +
                    "]" +
                "}" +
            "]" +
        "}";

        final String jsonData = "{" +
            "\"line1\": \"This is line1\"," +
            "\"tables\": [" +
                "{\"data\": \"This is data1\"}," +
                "{\"data\": \"This is data2\"}," +
                "{\"data\": \"This is data3\"}" +
            "]" +
        "}";

        DataSources.register(String.class, CustomJsonDataSource.class);
        DataSources.register(JsonObject.class, CustomJsonDataSource.class);

        final String result = new FillJob(new JsonTemplate(jsonTemplate).parse(), DataSources.from(jsonData)).fill();
        assertEquals( INIT +
            "Value: This is line1" + CRLF +
            "data         " + CRLF +
            "This is data1" + CRLF +
            "This is data2" + CRLF +
            "This is data3" + CRLF +
            CRFF + INIT,
            result
        );
    }

    @Test
    public void customDataSource_02() {
        DataSources.unregister(CustomJsonDataSource.class);
        final List<DataSources.DataSourceEntry> dataSources = DataSources.DATA_SOURCES;
        assertEquals(4, dataSources.size());
    }

}

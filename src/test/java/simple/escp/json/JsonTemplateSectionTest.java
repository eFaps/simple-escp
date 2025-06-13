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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static simple.escp.util.EscpUtil.CRFF;
import static simple.escp.util.EscpUtil.CRLF;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import simple.escp.data.MapDataSource;
import simple.escp.fill.FillJob;
import simple.escp.util.EscpUtil;

public class JsonTemplateSectionTest {

    private final String INIT = EscpUtil.escInitalize();

    @Test
    public void parseNoPageLength() {
        assertThrows(IllegalArgumentException.class, () -> {
            final String jsonString =
            "{" +
                "\"pageFormat\": {" +
                    "\"autoFormFeed\": false" +
                "}," +
                "\"template\": {" +
                    "\"detail\": [\"Your id is ${id}\",  \"Mr. ${nickname}.\"]" +
                "}" +
            "}";
            final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
            jsonTemplate.parse();
        });
    }

    @Test
    public void parseDetail() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 10" +
            "}," +
            "\"template\": {" +
                "\"detail\": [\"Your id is ${id}\",  \"Mr. ${nickname}.\"]" +
            "}" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + "Your id is 007" + CRLF + "Mr. Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void parseFirstPage() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"firstPage\": [\"This should appear in first page only\"]," +
                "\"detail\": [" +
                    "\"Line1\"," +
                    "\"Line2\"," +
                    "\"Line3\"," +
                    "\"Your id is ${id}\"," +
                    "\"Mr. ${nickname}.\"]" +
            "}" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + "This should appear in first page only" + CRLF + CRFF +
            "Line1" + CRLF +
            "Line2" + CRLF +
            "Line3" + CRLF +  CRFF +
            "Your id is 007" + CRLF +
            "Mr. Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void parseLastPage() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"lastPage\": [\"This should appear in last page only\"]," +
                "\"detail\": [" +
                    "\"Line1\"," +
                    "\"Line2\"," +
                    "\"Line3\"," +
                    "\"Your id is ${id}\"," +
                    "\"Mr. ${nickname}.\"]" +
            "}" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + "Line1" + CRLF + "Line2" + CRLF+ "Line3" + CRLF + CRFF +
            "Your id is 007" + CRLF + "Mr. Snake." + CRLF + CRFF +
            "This should appear in last page only" + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source))  .fill()
        );
    }

    @Test
    public void parseHeader() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"header\": [\"This is header.\"]," +
                "\"detail\": [" +
                    "\"Line1\"," +
                    "\"Line2\"," +
                    "\"Line3\"," +
                    "\"Your id is ${id}\"," +
                    "\"Mr. ${nickname}.\"]" +
            "}" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT +
            "This is header." + CRLF + "Line1" + CRLF + "Line2" + CRLF + CRFF +
            "This is header." + CRLF + "Line3" + CRLF +  "Your id is 007" + CRLF + CRFF +
            "This is header." + CRLF + "Mr. Snake." + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void parseFirstPageAndHeader() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"firstPage\": [\"This is first page only.\"]," +
                "\"header\": [\"This is header.\"]," +
                "\"detail\": [" +
                    "\"Line1\"," +
                    "\"Line2\"," +
                    "\"Line3\"," +
                    "\"Your id is ${id}\"," +
                    "\"Mr. ${nickname}.\"]" +
            "}" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + "This is first page only." + CRLF + CRFF +
            "This is header." + CRLF + "Line1" + CRLF + "Line2" + CRLF + CRFF +
            "This is header." + CRLF + "Line3" + CRLF+ "Your id is 007" + CRLF + CRFF +
            "This is header." + CRLF + "Mr. Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void parseFooter() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"footer\": [\"This is footer.\"]," +
                "\"detail\": [" +
                    "\"Line1\"," +
                    "\"Line2\"," +
                    "\"Line3\"," +
                    "\"Your id is ${id}\"," +
                    "\"Mr. ${nickname}.\"]" +
            "}" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT +
            "Line1" + CRLF + "Line2" + CRLF + "This is footer."  + CRLF + CRFF +
            "Line3" + CRLF +  "Your id is 007" + CRLF + "This is footer." + CRLF + CRFF +
            "Mr. Snake." + CRLF + "This is footer." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void parseLastPageAndFooter() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"lastPage\": [\"This is last page only.\"]," +
                "\"footer\": [\"This is footer.\"]," +
                "\"detail\": [" +
                    "\"Line1\"," +
                    "\"Line2\"," +
                    "\"Line3\"," +
                    "\"Your id is ${id}\"," +
                    "\"Mr. ${nickname}.\"]" +
            "}" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + "Line1" + CRLF + "Line2" + CRLF + "This is footer." + CRLF + CRFF +
            "Line3" + CRLF + "Your id is 007" + CRLF + "This is footer." + CRLF + CRFF +
            "Mr. Snake." + CRLF + "This is footer." + CRLF + CRFF +
            "This is last page only." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void parseFooterAndLastPageFooter() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"footer\": [\"This is footer.\"]," +
                "\"lastPageFooter\": [\"This is last page footer.\"]," +
                "\"detail\": [" +
                    "\"Line1\"," +
                    "\"Line2\"," +
                    "\"Line3\"," +
                    "\"Your id is ${id}\"," +
                    "\"Mr. ${nickname}.\"]" +
            "}" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + "Line1" + CRLF + "Line2" + CRLF + "This is footer." + CRLF + CRFF +
            "Line3" + CRLF + "Your id is 007" + CRLF + "This is footer." + CRLF + CRFF +
            "Mr. Snake." + CRLF + "This is last page footer." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void parseLastPageFooterOnly() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"lastPageFooter\": [\"This is last page footer.\"]," +
                "\"detail\": [" +
                    "\"Line1\"," +
                    "\"Line2\"," +
                    "\"Line3\"," +
                    "\"Your id is ${id}\"," +
                    "\"Mr. ${nickname}.\"]" +
            "}" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + "Line1" + CRLF + "Line2" + CRLF + "Line3" + CRLF + CRFF +
            "Your id is 007" + CRLF + "Mr. Snake." + CRLF + "This is last page footer." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void parseLastPageFooterWithDifferentLength() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": {" +
                "\"lastPageFooter\": [" +
                    "\"This is last page footer1.\"," +
                    "\"This is last page footer2.\"" +
                "]," +
                "\"footer\": [\"This is footer.\"]," +
                "\"detail\": [" +
                    "\"Line1\"," +
                    "\"Line2\"," +
                    "\"Line3\"," +
                    "\"Line4\"]" +
            "}" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + "Line1" + CRLF + "Line2" + CRLF + "This is footer." + CRLF + CRFF +
            "Line3" + CRLF + "Line4" + CRLF + "This is footer." + CRLF + CRFF +
            "This is last page footer1." + CRLF + "This is last page footer2." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

}

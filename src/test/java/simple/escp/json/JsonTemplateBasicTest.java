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
import static simple.escp.util.EscpUtil.CR;
import static simple.escp.util.EscpUtil.CRFF;
import static simple.escp.util.EscpUtil.CRLF;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import simple.escp.data.MapDataSource;
import simple.escp.fill.FillJob;
import simple.escp.util.EscpUtil;

public class JsonTemplateBasicTest {

    private final String INIT = EscpUtil.escInitalize();

    @Test
    public void parseString() {
        final String jsonString = "{\"template\": [" +
            "\"This is the first line\"," +
            "\"This is the second line\"" +
            "]}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        jsonTemplate.parse();
        assertEquals(jsonString, jsonTemplate.getOriginalText());
        assertEquals(INIT + "This is the first line" + CRLF + "This is the second line" + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse()).fill());
    }

    @Test
    public void pageFormatLineSpacing() {
        final String jsonString =
            "{" +
                "\"pageFormat\": {" +
                    "\"lineSpacing\": \"1/8\"" +
                "}," +
                "\"template\": [" +
                    "\"Your id is ${id}, Mr. ${nickname}.\"" +
                "]" +
            "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + EscpUtil.escOnePerEightInchLineSpacing() + "Your id is 007, Mr. Snake." + CRLF  + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void pageFormatCharacterPitch() {
        final String jsonString =
            "{" +
                "\"pageFormat\": {" +
                    "\"characterPitch\": \"10\"" +
                "}," +
                "\"template\": [" +
                    "\"Your id is ${id}, Mr. ${nickname}.\"" +
                "]" +
            "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + EscpUtil.escMasterSelect(EscpUtil.CHARACTER_PITCH.CPI_10) + "Your id is 007, Mr. Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void pageFormatPageLengthInString() {
        final String jsonString =
            "{" +
                "\"pageFormat\": {" +
                    "\"pageLength\": \"10\"," +
                    "\"usePageLengthFromPrinter\": false" +
                    "}," +
                "\"template\": [" +
                    "\"Your id is ${id}, Mr. ${nickname}.\"" +
                "]" +
            "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + EscpUtil.escPageLength(10) + "Your id is 007, Mr. Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void pageFormatPageLengthInNumber() {
        final String jsonString =
            "{" +
                "\"pageFormat\": {" +
                    "\"pageLength\": 10," +
                    "\"usePageLengthFromPrinter\": false" +
                "}," +
                "\"template\": [" +
                    "\"Your id is ${id}, Mr. ${nickname}.\"" +
                "]" +
            "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + EscpUtil.escPageLength(10) + "Your id is 007, Mr. Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void pageFormatPageWidth() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageWidth\": \"25\"" +
            "}," +
            "\"template\": [" +
                "\"Your id is ${id}, Mr. ${nickname}.\"" +
            "]" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + EscpUtil.escRightMargin(25) + "Your id is 007, Mr. Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void pageFormatLeftAndRightAndBottomMargin() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageWidth\": \"30\"," +
                "\"leftMargin\": \"5\"," +
                "\"rightMargin\": \"3\"," +
                "\"bottomMargin\": \"70\"" +
            "}," +
            "\"template\": [" +
                "\"Your id is ${id}, Mr. ${nickname}.\"" +
            "]" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + EscpUtil.escLeftMargin(5) + EscpUtil.escRightMargin(27) + EscpUtil.escBottomMargin(70) +
            "Your id is 007, Mr. Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void pageFormatTypeface() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"typeface\": \"sans-serif\"" +
            "}," +
            "\"template\": [" +
                "\"Your id is ${id}, Mr. ${nickname}.\"" +
            "]" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + EscpUtil.escSelectTypeface(EscpUtil.TYPEFACE.SANS_SERIF) +
                "Your id is 007, Mr. Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void pageFormatAutoLineFeed() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"autoLineFeed\": true" +
            "}," +
            "\"template\": [" +
                "\"Your id is ${id}, Mr. ${nickname}.\"" +
            "]" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + "Your id is 007, Mr. Snake." + CR + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void pageFormatAutoFormFeed() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"autoFormFeed\": false" +
            "}," +
            "\"template\": [" +
                "\"Your id is ${id}, Mr. ${nickname}.\"" +
            "]" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("id", "007");
        source.put("nickname", "Snake");
        assertEquals(
            INIT + "Your id is 007, Mr. Snake." + CRLF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void fromFile() throws IOException {
        final File file = Paths.get("src/test/resources/user.json").toFile();
        final JsonTemplate jsonTemplate = new JsonTemplate(file);
        final String LS = System.getProperty("line.separator");
        assertEquals("{" + LS +
            "    \"pageFormat\": {" + LS +
            "        \"pageLength\": 30," + LS +
            "        \"pageWith\": 20" + LS +
            "    }," + LS +
            "    \"template\": [" + LS +
            "        \"User Report\"," + LS +
            "        \"===========\"," + LS +
            "        \"ID    : ${id}\"," + LS +
            "        \"Name  : ${nickname}\"" + LS +
            "    ]" + LS +
            "}", jsonTemplate.getOriginalText());

        final Map<String, String> data = new HashMap<>();
        data.put("id", "007");
        data.put("nickname", "The Solid Snake");
        final String result = new FillJob(jsonTemplate.parse(), new MapDataSource(data)).fill();
        assertEquals(
            EscpUtil.escInitalize() +
            "User Report" + CRLF +
            "===========" + CRLF +
            "ID    : 007" + CRLF +
            "Name  : The Solid Snake" + CRLF +
            CRFF + EscpUtil.escInitalize(),
            result
        );
    }

    @Test
    public void fromURI() throws IOException, URISyntaxException {
        final JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/user.json").toURI());
        final String LS = System.getProperty("line.separator");
        assertEquals("{" + LS +
                "    \"pageFormat\": {" + LS +
                "        \"pageLength\": 30," + LS +
                "        \"pageWith\": 20" + LS +
                "    }," + LS +
                "    \"template\": [" + LS +
                "        \"User Report\"," + LS +
                "        \"===========\"," + LS +
                "        \"ID    : ${id}\"," + LS +
                "        \"Name  : ${nickname}\"" + LS +
                "    ]" + LS +
                "}", jsonTemplate.getOriginalText());

        final Map<String, String> data = new HashMap<>();
        data.put("id", "007");
        data.put("nickname", "The Solid Snake");
        final String result = new FillJob(jsonTemplate.parse(), new MapDataSource(data)).fill();
        assertEquals(
                EscpUtil.escInitalize() +
                        "User Report" + CRLF +
                        "===========" + CRLF +
                        "ID    : 007" + CRLF +
                        "Name  : The Solid Snake" + CRLF +
                        CRFF + EscpUtil.escInitalize(),
                result
        );
    }

}

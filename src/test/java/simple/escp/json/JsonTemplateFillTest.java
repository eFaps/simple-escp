/*
 * Copyright 2014 Jocki Hendry
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
import static simple.escp.util.EscpUtil.CP347_LIGHT_DOWN_HORIZONTAL;
import static simple.escp.util.EscpUtil.CP347_LIGHT_DOWN_LEFT;
import static simple.escp.util.EscpUtil.CP347_LIGHT_DOWN_RIGHT;
import static simple.escp.util.EscpUtil.CP347_LIGHT_HORIZONTAL;
import static simple.escp.util.EscpUtil.CP347_LIGHT_UP_HORIZONTAL;
import static simple.escp.util.EscpUtil.CP347_LIGHT_UP_LEFT;
import static simple.escp.util.EscpUtil.CP347_LIGHT_UP_RIGHT;
import static simple.escp.util.EscpUtil.CP347_LIGHT_VERTICAL;
import static simple.escp.util.EscpUtil.CP347_LIGHT_VERTICAL_HORIZONTAL;
import static simple.escp.util.EscpUtil.CP347_LIGHT_VERTICAL_LEFT;
import static simple.escp.util.EscpUtil.CP347_LIGHT_VERTICAL_RIGHT;
import static simple.escp.util.EscpUtil.CRFF;
import static simple.escp.util.EscpUtil.CRLF;
import static simple.escp.util.EscpUtil.escPageLength;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import simple.escp.data.BeanDataSource;
import simple.escp.data.DataSources;
import simple.escp.data.MapDataSource;
import simple.escp.dom.Report;
import simple.escp.dom.line.TableLine;
import simple.escp.fill.FillJob;
import simple.escp.util.EscpUtil;

public class JsonTemplateFillTest {

    private String jsonStringBasic, jsonStringScriptMap, jsonStringScriptBean;
    private final String INIT = EscpUtil.escInitalize();
    private final String SELECT_UNDERLINE = EscpUtil.escSelectUnderline();
    private final String CANCEL_UNDERLINE = EscpUtil.escCancelUnderline();

    @BeforeEach
    public void setup() {
        jsonStringBasic = "{" +
            "\"template\": [" +
                "\"Your id is ${id}, Mr. ${nickname}.\"" +
            "]" +
        "}";
        jsonStringScriptMap = "{" +
            "\"template\": [" +
                "\"Your id is {{ id }}, Mr. {{ nickname }}.\"" +
            "]" +
        "}";
        jsonStringScriptBean = "{" +
            "\"template\": [" +
                "\"Your id is {{ bean.id }}, Mr. {{ bean.nickname }}.\"" +
            "]" +
        "}";
    }

    @Test
    public void fillMap() {
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonStringBasic);
        final Map<String, String> dataSource = new HashMap<>();
        dataSource.put("id", "007");
        dataSource.put("nickname", "Solid Snake");
        assertEquals(INIT + "Your id is 007, Mr. Solid Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(dataSource)).fill());

        final JsonTemplate jsonTemplateScriptMap = new JsonTemplate(jsonStringScriptMap);
        assertEquals(INIT + "Your id is 007, Mr. Solid Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplateScriptMap.parse(), new MapDataSource(dataSource)).fill());
    }

    @Test
    public void fillObject() {
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonStringBasic);
        final Person person = new Person("Solid Snake", null, null);
        person.setId("007");
        assertEquals(INIT + "Your id is 007, Mr. Solid Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new BeanDataSource(person)).fill());

        final JsonTemplate jsonTemplateScriptBean = new JsonTemplate(jsonStringScriptBean);
        assertEquals(INIT + "Your id is 007, Mr. Solid Snake." + CRLF + CRFF + INIT,
            new FillJob(jsonTemplateScriptBean.parse(), new BeanDataSource(person)).fill());
    }

    @Test
    public void fillObjectWithMethod() {
        final String jsonString =
        "{" +
            "\"template\": [" +
                "\"Your first name is ${ firstName } and your last name is ${lastName}.\"," +
                "\"I know you, ${@name}!\"" +
            "]" +
        "}";
        JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Person person = new Person("Snake", "David", "None");
        assertEquals(INIT + "Your first name is David and your last name is None." + CRLF +
            "I know you, David None alias Snake!" + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new BeanDataSource(person)).fill());

        final String jsonStringScript =
        "{" +
            "\"template\": [" +
                "\"Your first name is {{ firstName }} and your last name is {{ lastName }}.\"," +
                "\"I know you, {{ bean.name() }}!\"" +
            "]" +
        "}";
        jsonTemplate = new JsonTemplate(jsonStringScript);
        assertEquals(INIT + "Your first name is David and your last name is None." + CRLF +
            "I know you, David None alias Snake!" + CRLF + CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new BeanDataSource(person)).fill());
    }

    @Test
    public void fillOneTable() throws URISyntaxException, IOException {
        final JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/single_table.json").toURI());
        final List<Person> persons = new ArrayList<>();
        persons.add(new Person("None", "David", "None"));
        persons.add(new Person("David", "Solid", "Snake"));
        persons.add(new Person("Snake", "Jocki", "Hendry"));
        final Map<String, Object> source = new HashMap<>();
        source.put("persons", persons);
        final String result = new FillJob(jsonTemplate.parse(), DataSources.from(source)).fill();
        assertEquals(
            INIT + escPageLength(3) +
            "This is detail 1." + CRLF +
            "firstName lastName            nickname  " + CRLF +
            "David     None                None      " + CRLF + CRFF +
            "firstName lastName            nickname  " + CRLF +
            "Solid     Snake               David     " + CRLF +
            "Jocki     Hendry              Snake     " + CRLF + CRFF +
            "This is detail 2." + CRLF +
            CRFF + INIT,
            result
        );
    }

    @Test
    public void fillOneTableWithOverflowedString() throws URISyntaxException, IOException {
        final JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/single_table.json").toURI());
        final List<Person> persons = new ArrayList<>();
        persons.add(new Person("None12345678901234567890", "David12345678901234567890", "None12345678901234567890"));
        persons.add(new Person("David12345678901234567890", "Solid12345678901234567890", "Snake12345678901234567890"));
        persons.add(new Person("Snake12345678901234567890", "Jocki12345678901234567890", "Hendry12345678901234567890"));
        final Map<String, Object> source = new HashMap<>();
        source.put("persons", persons);
        final String result = new FillJob(jsonTemplate.parse(), DataSources.from(source)).fill();
        assertEquals(
                INIT + escPageLength(3) +
                        "This is detail 1." + CRLF +
                        "firstName lastName            nickname  " + CRLF +
                        "David12345None1234567890123456None123456" + CRLF + CRFF +
                        "firstName lastName            nickname  " + CRLF +
                        "Solid12345Snake123456789012345David12345" + CRLF +
                        "Jocki12345Hendry12345678901234Snake12345" + CRLF + CRFF +
                        "This is detail 2." + CRLF +
                        CRFF + INIT,
                result
        );
    }

    @Test
     public void fillOneTableWithWrappedString() throws URISyntaxException, IOException {
        final JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/single_table_wrap.json").toURI());
        final List<Person> persons = new ArrayList<>();
        persons.add(new Person("None12345678901234567890", "David12345678901234567890", "None12345678901234567890"));
        persons.add(new Person("David12345678901234567890", "Solid", "Snake12345678901234567890"));
        persons.add(new Person("Snake12345678901234567890", "Jocki", "Hendry12345678901234567890"));
        final Map<String, Object> source = new HashMap<>();
        source.put("persons", persons);
        final String result = new FillJob(jsonTemplate.parse(), DataSources.from(source)).fill();
        assertEquals(
            INIT + escPageLength(3) +
            "This is detail 1." + CRLF +
            "firstName lastName            nickname  " + CRLF +
            "David12345None1234567890123456None123456" + CRLF + CRFF +
            "firstName lastName            nickname  " + CRLF +
            "67890123457890                          " + CRLF +
            "67890                                   " + CRLF + CRFF +
            "firstName lastName            nickname  " + CRLF +
            "Solid     Snake123456789012345David12345" + CRLF +
            "          67890                         " + CRLF + CRFF +
            "firstName lastName            nickname  " + CRLF +
            "Jocki     Hendry12345678901234Snake12345" + CRLF +
            "          567890                        " + CRLF + CRFF +
            "This is detail 2." + CRLF + CRFF +
            INIT,
            result
        );
    }

    @Test
     public void fillOneTableWithAutonumber() throws URISyntaxException, IOException {
        final JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/single_table_autonumber.json").toURI());
        final List<Person> persons = new ArrayList<>();
        persons.add(new Person("None12345678901234567890", "David12345678901234567890", "None12345678901234567890"));
        persons.add(new Person("David12345678901234567890", "Solid", "Snake12345678901234567890"));
        persons.add(new Person("Snake12345678901234567890", "Jocki", "Hendry12345678901234567890"));
        final Map<String, Object> source = new HashMap<>();
        source.put("persons", persons);
        final String result = new FillJob(jsonTemplate.parse(), DataSources.from(source)).fill();
        assertEquals(
            INIT + escPageLength(3) +
            "This is detail 1." + CRLF +
            "row firstName lastName            nickname  col " + CRLF +
            "1   David12345None1234567890123456None1234565   " + CRLF + CRFF +
            "row firstName lastName            nickname  col " + CRLF +
            "    67890123457890                              " + CRLF +
            "    67890                                       " + CRLF + CRFF +
            "row firstName lastName            nickname  col " + CRLF +
            "2   Solid     Snake123456789012345David123455   " + CRLF +
            "              67890                             " + CRLF + CRFF +
            "row firstName lastName            nickname  col " + CRLF +
            "3   Jocki     Hendry12345678901234Snake123455   " + CRLF +
            "              567890                            " + CRLF + CRFF +
            "This is detail 2." + CRLF + CRFF +
            INIT,
            result
        );
    }

    @Test
    public void fillOneTableWithFormatting() throws URISyntaxException, IOException {
        final JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/single_table_with_format.json").toURI());
        final PersonAggregate personAggregate = new PersonAggregate();
        personAggregate.add(new Person("None", "David", "None"));
        personAggregate.add(new Person("David", "Solid", "Snake"));
        personAggregate.add(new Person("Snake", "Jocki", "Hendry"));
        final String result = new FillJob(jsonTemplate.parse(), DataSources.from(personAggregate)).fill();
        assertEquals(
            INIT + escPageLength(3) +
            "This is detail 1." + CRLF +
            " firstNamelastName             nickname " + CRLF +
            "     DavidNone                   None   " + CRLF + CRFF +
            " firstNamelastName             nickname " + CRLF +
            "     SolidSnake                 David   " + CRLF +
            "     JockiHendry                Snake   " + CRLF + CRFF +
            " firstNamelastName             nickname " + CRLF +
            "newFirstNanewLastName          newNick  " + CRLF +
            "This is detail 2." + CRLF +
            CRFF + INIT,
            result
        );
    }

    @Test
    public void fillOneTableWithNullValue() throws URISyntaxException, IOException {
        final JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/single_table.json").toURI());
        final List<Person> persons = new ArrayList<>();
        persons.add(new Person(null, "David", null));
        persons.add(new Person("David", "Solid", "Snake"));
        persons.add(new Person("Snake", "Jocki", "Hendry"));
        final Map<String, Object> source = new HashMap<>();
        source.put("persons", persons);
        final String result = new FillJob(jsonTemplate.parse(), DataSources.from(source)).fill();
        assertEquals(
                INIT + escPageLength(3) +
                        "This is detail 1." + CRLF +
                        "firstName lastName            nickname  " + CRLF +
                        "David                                   " + CRLF + CRFF +
                        "firstName lastName            nickname  " + CRLF +
                        "Solid     Snake               David     " + CRLF +
                        "Jocki     Hendry              Snake     " + CRLF + CRFF +
                        "This is detail 2." + CRLF +
                        CRFF + INIT,
                result
        );
    }

    private String times(final char c, final int times) {
        final StringBuilder result = new StringBuilder();
        for (int i=0; i<times; i++) {
            result.append(c);
        }
        return result.toString();
    }

    @Test
    public void fillTableWithBorder() throws URISyntaxException, IOException {
        final JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/single_table.json").toURI());
        final List<Person> persons = new ArrayList<>();
        persons.add(new Person("None", "David", "None"));
        persons.add(new Person("David", "Solid", "Snake"));
        persons.add(new Person("Snake", "Jocki", "Hendry"));
        final Map<String, Object> source = new HashMap<>();
        source.put("persons", persons);
        final Report report = jsonTemplate.parse();
        report.getPageFormat().setPageLength(6);
        ((TableLine) report.getPage(1).getLine(2)).setDrawBorder(true);
        final String result = new FillJob(report, DataSources.from(source)).fill();
        assertEquals(
            INIT + escPageLength(6) +
            "This is detail 1." + CRLF +
            CP347_LIGHT_DOWN_RIGHT + times(CP347_LIGHT_HORIZONTAL, 9) +  CP347_LIGHT_DOWN_HORIZONTAL +  times( CP347_LIGHT_HORIZONTAL, 19) +  CP347_LIGHT_DOWN_HORIZONTAL +  times( CP347_LIGHT_HORIZONTAL, 9) +  CP347_LIGHT_DOWN_LEFT + CRLF +
            CP347_LIGHT_VERTICAL + "firstName" + CP347_LIGHT_VERTICAL + "lastName           " +  CP347_LIGHT_VERTICAL + "nickname " +  CP347_LIGHT_VERTICAL + CRLF +
            CP347_LIGHT_VERTICAL_RIGHT + times(CP347_LIGHT_HORIZONTAL, 9) + CP347_LIGHT_VERTICAL_HORIZONTAL + times( CP347_LIGHT_HORIZONTAL, 19) +  CP347_LIGHT_VERTICAL_HORIZONTAL + times( CP347_LIGHT_HORIZONTAL, 9) +  CP347_LIGHT_VERTICAL_LEFT + CRLF +
            CP347_LIGHT_VERTICAL + "David    " + CP347_LIGHT_VERTICAL + "None               " +  CP347_LIGHT_VERTICAL + "None     " +  CP347_LIGHT_VERTICAL + CRLF +
            CP347_LIGHT_UP_RIGHT + times(CP347_LIGHT_HORIZONTAL, 9) + CP347_LIGHT_UP_HORIZONTAL + times( CP347_LIGHT_HORIZONTAL, 19) +  CP347_LIGHT_UP_HORIZONTAL + times( CP347_LIGHT_HORIZONTAL, 9) +  CP347_LIGHT_UP_LEFT + CRLF + CRFF +
            CP347_LIGHT_DOWN_RIGHT + times(CP347_LIGHT_HORIZONTAL, 9) +  CP347_LIGHT_DOWN_HORIZONTAL +  times( CP347_LIGHT_HORIZONTAL, 19) +  CP347_LIGHT_DOWN_HORIZONTAL +  times( CP347_LIGHT_HORIZONTAL, 9) +  CP347_LIGHT_DOWN_LEFT + CRLF +
            CP347_LIGHT_VERTICAL + "firstName" + CP347_LIGHT_VERTICAL + "lastName           " +  CP347_LIGHT_VERTICAL + "nickname " +  CP347_LIGHT_VERTICAL + CRLF +
            CP347_LIGHT_VERTICAL_RIGHT + times(CP347_LIGHT_HORIZONTAL, 9) + CP347_LIGHT_VERTICAL_HORIZONTAL + times( CP347_LIGHT_HORIZONTAL, 19) +  CP347_LIGHT_VERTICAL_HORIZONTAL + times( CP347_LIGHT_HORIZONTAL, 9) +  CP347_LIGHT_VERTICAL_LEFT + CRLF +
            CP347_LIGHT_VERTICAL + "Solid    " + CP347_LIGHT_VERTICAL + "Snake              " +  CP347_LIGHT_VERTICAL + "David    " +  CP347_LIGHT_VERTICAL + CRLF +
            CP347_LIGHT_VERTICAL + "Jocki    " + CP347_LIGHT_VERTICAL + "Hendry             " +  CP347_LIGHT_VERTICAL + "Snake    " +  CP347_LIGHT_VERTICAL + CRLF +
            CP347_LIGHT_UP_RIGHT + times(CP347_LIGHT_HORIZONTAL, 9) + CP347_LIGHT_UP_HORIZONTAL + times( CP347_LIGHT_HORIZONTAL, 19) +  CP347_LIGHT_UP_HORIZONTAL + times( CP347_LIGHT_HORIZONTAL, 9) + CP347_LIGHT_UP_LEFT + CRLF + CRFF +
            "This is detail 2." + CRLF +
            CRFF + INIT,
            result
        );
    }

    @Test
    public void fillTableWithBorderAndLineSeparator() throws URISyntaxException, IOException {
        final JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/single_table.json").toURI());
        final List<Person> persons = new ArrayList<>();
        persons.add(new Person("None", "David", "None"));
        persons.add(new Person("David", "Solid", "Snake"));
        persons.add(new Person("Snake", "Jocki", "Hendry"));
        final Map<String, Object> source = new HashMap<>();
        source.put("persons", persons);
        final Report report = jsonTemplate.parse();
        report.getPageFormat().setPageLength(10);
        ((TableLine) report.getPage(1).getLine(2)).setDrawBorder(true);
        ((TableLine) report.getPage(1).getLine(2)).setDrawLineSeparator(true);
        final String result = new FillJob(report, DataSources.from(source)).fill();
        assertEquals(
                INIT + escPageLength(10) +
                        "This is detail 1." + CRLF +
                        CP347_LIGHT_DOWN_RIGHT + times(CP347_LIGHT_HORIZONTAL, 9) +  CP347_LIGHT_DOWN_HORIZONTAL +  times( CP347_LIGHT_HORIZONTAL, 19) +  CP347_LIGHT_DOWN_HORIZONTAL +  times( CP347_LIGHT_HORIZONTAL, 9) +  CP347_LIGHT_DOWN_LEFT + CRLF +
                        CP347_LIGHT_VERTICAL + "firstName" + CP347_LIGHT_VERTICAL + "lastName           " +  CP347_LIGHT_VERTICAL + "nickname " +  CP347_LIGHT_VERTICAL + CRLF +
                        CP347_LIGHT_VERTICAL_RIGHT + times(CP347_LIGHT_HORIZONTAL, 9) + CP347_LIGHT_VERTICAL_HORIZONTAL + times( CP347_LIGHT_HORIZONTAL, 19) +  CP347_LIGHT_VERTICAL_HORIZONTAL + times( CP347_LIGHT_HORIZONTAL, 9) +  CP347_LIGHT_VERTICAL_LEFT + CRLF +
                        CP347_LIGHT_VERTICAL + "David    " + CP347_LIGHT_VERTICAL + "None               " +  CP347_LIGHT_VERTICAL + "None     " +  CP347_LIGHT_VERTICAL + CRLF +
                        CP347_LIGHT_VERTICAL_RIGHT + times(CP347_LIGHT_HORIZONTAL, 9) + CP347_LIGHT_VERTICAL_HORIZONTAL + times(CP347_LIGHT_HORIZONTAL, 19) + CP347_LIGHT_VERTICAL_HORIZONTAL + times(CP347_LIGHT_HORIZONTAL, 9) + CP347_LIGHT_VERTICAL_LEFT + CRLF +
                        CP347_LIGHT_VERTICAL + "Solid    " + CP347_LIGHT_VERTICAL + "Snake              " +  CP347_LIGHT_VERTICAL + "David    " +  CP347_LIGHT_VERTICAL + CRLF +
                        CP347_LIGHT_VERTICAL_RIGHT + times(CP347_LIGHT_HORIZONTAL, 9) + CP347_LIGHT_VERTICAL_HORIZONTAL + times(CP347_LIGHT_HORIZONTAL, 19) + CP347_LIGHT_VERTICAL_HORIZONTAL + times(CP347_LIGHT_HORIZONTAL, 9) + CP347_LIGHT_VERTICAL_LEFT + CRLF +
                        CP347_LIGHT_VERTICAL + "Jocki    " + CP347_LIGHT_VERTICAL + "Hendry             " +  CP347_LIGHT_VERTICAL + "Snake    " +  CP347_LIGHT_VERTICAL + CRLF +
                        CP347_LIGHT_UP_RIGHT + times(CP347_LIGHT_HORIZONTAL, 9) + CP347_LIGHT_UP_HORIZONTAL + times( CP347_LIGHT_HORIZONTAL, 19) +  CP347_LIGHT_UP_HORIZONTAL + times( CP347_LIGHT_HORIZONTAL, 9) +  CP347_LIGHT_UP_LEFT + CRLF +
                        CRFF +
                        "This is detail 2." + CRLF +
                        CRFF + INIT,
                result
        );
    }

    @Test
    public void fillTableWithLineSeparator() throws URISyntaxException, IOException {
        final JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/single_table.json").toURI());
        final List<Person> persons = new ArrayList<>();
        persons.add(new Person("None", "David", "None"));
        persons.add(new Person("David", "Solid", "Snake"));
        persons.add(new Person("Snake", "Jocki", "Hendry"));
        final Map<String, Object> source = new HashMap<>();
        source.put("persons", persons);
        final Report report = jsonTemplate.parse();
        report.getPageFormat().setPageLength(10);
        ((TableLine) report.getPage(1).getLine(2)).setDrawLineSeparator(true);
        final String result = new FillJob(report, DataSources.from(source)).fill();
        assertEquals(
            INIT + escPageLength(10) +
            "This is detail 1." + CRLF +
            "firstName lastName            nickname  " + CRLF +
            "David     None                None      " + CRLF +
            times(CP347_LIGHT_HORIZONTAL, 10) + times(CP347_LIGHT_HORIZONTAL, 20) + times(CP347_LIGHT_HORIZONTAL, 10) + CRLF +
            "Solid     Snake               David     " + CRLF +
            times(CP347_LIGHT_HORIZONTAL, 10) + times(CP347_LIGHT_HORIZONTAL, 20) + times(CP347_LIGHT_HORIZONTAL, 10) + CRLF +
            "Jocki     Hendry              Snake     " + CRLF +
            "This is detail 2." + CRLF +
            CRFF + INIT,
            result
        );
    }

    @Test
    public void fillTableWithUnderlineSeparator() throws URISyntaxException, IOException {
        final JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/single_table.json").toURI());
        final List<Person> persons = new ArrayList<>();
        persons.add(new Person("None", "David", "None"));
        persons.add(new Person("David", "Solid", "Snake"));
        persons.add(new Person("Snake", "Jocki", "Hendry"));
        final Map<String, Object> source = new HashMap<>();
        source.put("persons", persons);
        final Report report = jsonTemplate.parse();
        report.getPageFormat().setPageLength(10);
        ((TableLine) report.getPage(1).getLine(2)).setDrawUnderlineSeparator(true);
        final String result = new FillJob(report, DataSources.from(source)).fill();
        assertEquals(
            INIT + escPageLength(10) +
            "This is detail 1." + CRLF +
            "firstName lastName            nickname  " + CRLF +
            SELECT_UNDERLINE + "David     " + CANCEL_UNDERLINE + SELECT_UNDERLINE + "None                " + CANCEL_UNDERLINE + SELECT_UNDERLINE + "None      " + CANCEL_UNDERLINE + CRLF +
            SELECT_UNDERLINE + "Solid     " + CANCEL_UNDERLINE + SELECT_UNDERLINE + "Snake               " + CANCEL_UNDERLINE + SELECT_UNDERLINE + "David     " + CANCEL_UNDERLINE + CRLF +
            SELECT_UNDERLINE + "Jocki     " + CANCEL_UNDERLINE + SELECT_UNDERLINE + "Hendry              " + CANCEL_UNDERLINE + SELECT_UNDERLINE + "Snake     " + CANCEL_UNDERLINE + CRLF +
            "This is detail 2." + CRLF +
            CRFF + INIT,
            result
        );
    }

    @Test
    public void fillTwoTable() throws URISyntaxException, IOException {
        final JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/multiple_table.json").toURI());
        final List<Person> persons1 = new ArrayList<>();
        persons1.add(new Person("None", "David", "None"));
        persons1.add(new Person("David", "Solid", "Snake"));
        persons1.add(new Person("Snake", "Jocki", "Hendry"));
        final List<Person> persons2 = new ArrayList<>();
        persons2.add(new Person("FooBar", "Foo", "Bar"));
        persons2.add(new Person("BarFoo", "Bar", "Foo"));
        final Map<String, Object> source = new HashMap<>();
        source.put("persons1", persons1);
        source.put("persons2", persons2);
        final String result = new FillJob(jsonTemplate.parse(), DataSources.from(source)).fill();
        assertEquals(
            INIT + escPageLength(3) +
            "This is detail 1." + CRLF +
            "firstName lastName            nickname  " + CRLF +
            "David     None                None      " + CRLF + CRFF +
            "firstName lastName            nickname  " + CRLF +
            "Solid     Snake               David     " + CRLF +
            "Jocki     Hendry              Snake     " + CRLF + CRFF +
            "This is detail 2." + CRLF +
            "firstName lastName            nickname  " + CRLF +
            "Foo       Bar                 FooBar    " + CRLF + CRFF +
            "firstName lastName            nickname  " + CRLF +
            "Bar       Foo                 BarFoo    " + CRLF +
            "This is detail 3." + CRLF +
            CRFF + INIT,
            result
        );
    }

    @Test
    public void fillOneList() throws URISyntaxException, IOException {
        final JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/single_list.json").toURI());
        final List<Person> persons = new ArrayList<>();
        persons.add(new Person("None", "David", "None"));
        persons.add(new Person("David", "Solid", "Snake"));
        persons.add(new Person("Snake", "Jocki", "Hendry"));
        final Map<String, Object> source = new HashMap<>();
        source.put("persons", persons);
        final String result = new FillJob(jsonTemplate.parse(), DataSources.from(source)).fill();
        assertEquals(
            INIT + escPageLength(5) +
            "This is detail 1." + CRLF +
            "This is header of list." + CRLF +
            "Page 1: David None as None" + CRLF +
            "Page 1: Solid Snake as David" + CRLF +
            "This is footer of list." + CRLF + CRFF +
            "This is header of list." + CRLF +
            "Page 2: Jocki Hendry as Snake" + CRLF +
            "This is footer of list." + CRLF +
            "This is detail 2." + CRLF +
            CRFF + INIT,
            result
        );
    }

    @Test
    public void fillOneListWithFormatting() throws URISyntaxException, IOException {
        final JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/single_list_with_format.json").toURI());
        final PersonAggregate personAggregate = new PersonAggregate();
        personAggregate.add(new Person("None", "David", "None"));
        personAggregate.add(new Person("David", "Solid", "Snake"));
        personAggregate.add(new Person("Snake", "Jocki", "Hendry"));
        final String result = new FillJob(jsonTemplate.parse(), DataSources.from(personAggregate)).fill();
        assertEquals(
            INIT + escPageLength(5) +
            "This is detail 1." + CRLF +
            "This is header of list." + CRLF +
            "Page 1: Dav None       as David None" + CRLF +
            "Page 1: Sol Snake      as Solid Snak" + CRLF +
            "This is footer of list." + CRLF + CRFF +
            "This is header of list." + CRLF +
            "Page 2: Joc Hendry     as Jocki Hend" + CRLF +
            "Page 2: new newLastNam as newFirstNa" + CRLF +
            "This is footer of list." + CRLF +
            "This is detail 2." + CRLF +
            CRFF + INIT,
            result
        );
    }

    @Test
    public void fillOneListWithNullValue() throws URISyntaxException, IOException {
        final JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/single_list.json").toURI());
        final List<Person> persons = new ArrayList<>();
        persons.add(new Person(null, "David", null));
        persons.add(new Person("David", "Solid", "Snake"));
        persons.add(new Person("Snake", "Jocki", "Hendry"));
        final Map<String, Object> source = new HashMap<>();
        source.put("persons", persons);
        final String result = new FillJob(jsonTemplate.parse(), DataSources.from(source)).fill();
        assertEquals(
                INIT + escPageLength(5) +
                        "This is detail 1." + CRLF +
                        "This is header of list." + CRLF +
                        "Page 1: David  as " + CRLF +
                        "Page 1: Solid Snake as David" + CRLF +
                        "This is footer of list." + CRLF + CRFF +
                        "This is header of list." + CRLF +
                        "Page 2: Jocki Hendry as Snake" + CRLF +
                        "This is footer of list." + CRLF +
                        "This is detail 2." + CRLF +
                        CRFF + INIT,
                result
        );
    }

    @Test
    public void fillTwoList() throws URISyntaxException, IOException {
        final JsonTemplate jsonTemplate = new JsonTemplate(getClass().getResource("/multiple_list.json").toURI());
        final List<Person> persons1 = new ArrayList<>();
        persons1.add(new Person("None", "David", "None"));
        persons1.add(new Person("David", "Solid", "Snake"));
        persons1.add(new Person("Snake", "Jocki", "Hendry"));
        final List<Person> persons2 = new ArrayList<>();
        persons2.add(new Person("FooBar", "Foo", "Bar"));
        persons2.add(new Person("BarFoo", "Bar", "Foo"));
        final Map<String, Object> source = new HashMap<>();
        source.put("persons1", persons1);
        source.put("persons2", persons2);
        final String result = new FillJob(jsonTemplate.parse(), DataSources.from(source)).fill();
        assertEquals(
            INIT + escPageLength(5) +
            "This is detail 1." + CRLF +
            "Page 1: David None as None" + CRLF +
            "Page 1: Solid Snake as David" + CRLF +
            "Page 1: Jocki Hendry as Snake" + CRLF +
            "This is detail 2." + CRLF + CRFF +
            "This is header of second detail." + CRLF +
            "Page 2: Foo Bar as FooBar" + CRLF +
            "Page 2: Bar Foo as BarFoo" + CRLF +
            "This is footer of second detail." + CRLF +
            "This is detail 3." + CRLF +
            CRFF + INIT,
            result
        );
    }

    @Test
    public void placeholderFormatting() {
         final String jsonString =
        "{" +
            "\"template\": [" +
                "\"Your first name is ${firstName:10} and your last name is ${lastName:5}.\"" +
            "]" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, Object> source = new HashMap<>();
        source.put("firstName", "Jocki");
        source.put("lastName", "Hendry");
        final String result = new FillJob(jsonTemplate.parse(), DataSources.from(source)).fill();
        assertEquals(
            INIT +
            "Your first name is Jocki      and your last name is Hendr." + CRLF +
            CRFF + INIT,
            result
        );
    }

    @Test
    public void placeholderFormattingForScript() {
        final String jsonString =
        "{" +
            "\"template\": [" +
                "\"Your first name is {{firstName::10}} and your last name is {{lastName::5}}.\"" +
            "]" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, Object> source = new HashMap<>();
        source.put("firstName", "Jocki");
        source.put("lastName", "Hendry");
        final String result = new FillJob(jsonTemplate.parse(), DataSources.from(source)).fill();
        assertEquals(
            INIT +
            "Your first name is Jocki      and your last name is Hendr." + CRLF +
            CRFF + INIT,
            result
        );
    }

    @Test
    public void customVariable() {
        final String jsonString =
        "{" +
            "\"template\": [" +
                "\"Your first name is {{firstName}} {{custom}}.\"" +
            "]" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, Object> source = new HashMap<>();
        source.put("firstName", "Jocki");
        source.put("custom", "Hendry");
        final FillJob fillJob = new FillJob(jsonTemplate.parse(), DataSources.from(source));
        fillJob.addScriptVariable("custom", "ABCDEF");
        fillJob.removeScriptVariable("firstName");
        final String result = fillJob.fill();
        assertEquals(
            INIT +
            "Your first name is Jocki ABCDEF." + CRLF +
            CRFF + INIT,
            result
        );
    }

    @Test
    public void fillEmptyTable() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": [" +
                "\"First Line\"," +
                "{" +
                    "\"table\": \"tables\"," +
                    "\"columns\": [ {\"source\": \"test\", \"width\": 10} ]" +
                "}," +
                "\"Second Line\"" +
            "]" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("tables", null);
        assertEquals( INIT +
            "First Line" + CRLF +
            "Second Line" + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

   // @Test(expected = InvalidPlaceholder.class)
    public void fillEmptyTable2() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": [" +
                "\"First Line\"," +
                "{" +
                    "\"table\": \"tables\"," +
                    "\"columns\": [ {\"source\": \"test\", \"width\": 10} ]" +
                "}," +
                "\"Second Line\"" +
            "]" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        assertEquals( INIT +
            "First Line" + CRLF +
            "Second Line" + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void fillEmptyList() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": [" +
                "\"First Line\"," +
                "{" +
                    "\"list\": \"lists\"," +
                    "\"line\": \"This is ${line}\"" +
                "}," +
                "\"Second Line\"" +
            "]" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("lists", null);
        assertEquals( INIT +
            "First Line" + CRLF +
            "Second Line" + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    @Test
    public void fillEmptyListWithHeaderAndFooter() {
        final String jsonString =
        "{" +
            "\"pageFormat\": {" +
                "\"pageLength\": 3" +
            "}," +
            "\"template\": [" +
                "\"First Line\"," +
                "{" +
                    "\"list\": \"lists\"," +
                    "\"line\": \"This is ${line}\"," +
                    "\"header\": [ \"This is header\" ]," +
                    "\"footer\": [ \"This is footer\" ]" +
                "}," +
                "\"Second Line\"" +
            "]" +
        "}";
        final JsonTemplate jsonTemplate = new JsonTemplate(jsonString);
        final Map<String, String> source = new HashMap<>();
        source.put("lists", null);
        assertEquals( INIT +
            "First Line" + CRLF +
            "Second Line" + CRLF +
            CRFF + INIT,
            new FillJob(jsonTemplate.parse(), new MapDataSource(source)).fill()
        );
    }

    public static class PersonAggregate {
        private List<Person> persons = new ArrayList<>();

        public List<Person> getPersons() {
            return persons;
        }

        public void setPersons(final List<Person> persons) {
            this.persons = persons;
        }

        public List<Person> add(final Person person) {
            persons.add(person);
            return persons;
        }

    }

    public static class Person {
        private String id;
        private String nickname;
        private String firstName;
        private String lastName;

        public Person(final String nickname, final String firstName, final String lastName) {
            this.nickname = nickname;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(final String nickname) {
            this.nickname = nickname;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(final String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(final String lastName) {
            this.lastName = lastName;
        }

        public String name() {
            return firstName + " " + lastName + " alias " + nickname;
        }
    }

}

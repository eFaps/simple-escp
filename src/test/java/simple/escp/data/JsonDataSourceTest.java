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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.json.JsonObject;

public class JsonDataSourceTest {

    private String jsonString;

    @BeforeEach
    public void setup() {
        jsonString = """
            {\
            "name": "Steven",\
            "age": 28,\
            "registered": true,\
            "address": {\
            "line1": "address line 1",\
            "line2": "address line 2"\
            },\
            "history": [\
            { "date": 1, "value": 10 },\
            { "date": 2, "value": 20 },\
            { "date": 3, "value": 30 }\
            ]\
            }""";
    }

    @Test
    public void hasMember() {
        final JsonDataSource ds  = new JsonDataSource(jsonString);
        assertTrue(ds.has("name"));
        assertTrue(ds.has("age"));
        assertTrue(ds.has("registered"));
        assertTrue(ds.has("address"));
        assertTrue(ds.has("history"));
        assertFalse(ds.has("unknown"));
    }

    @Test
    public void getMember() {
        final JsonDataSource ds  = new JsonDataSource(jsonString);
        assertEquals("Steven", ds.get("name"));
        assertEquals(new BigDecimal("28"), ds.get("age"));
        assertEquals(true, ds.get("registered"));
        assertEquals("address line 1", ((JsonObject)ds.get("address")).getString("line1"));
        assertEquals("address line 2", ((JsonObject)ds.get("address")).getString("line2"));
        assertEquals(3, ((List)ds.get("history")).size());
    }

    @Test
    public void getMembers() {
        final JsonDataSource ds = new JsonDataSource(jsonString);
        final List<String> result = Arrays.asList(ds.getMembers());
        assertTrue(result.contains("name"));
        assertTrue(result.contains("age"));
        assertTrue(result.contains("registered"));
        assertTrue(result.contains("address"));
        assertTrue(result.contains("history"));
    }

}

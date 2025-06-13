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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import simple.escp.exception.InvalidPlaceholder;

public class MapDataSourceTest
{

    @Test
    public void hasMember()
    {
        final Map<String, String> source = new HashMap<>();
        source.put("name", "Solid Snake");
        final MapDataSource ds = new MapDataSource(source);
        assertTrue(ds.has("name"));
        assertFalse(ds.has("nickname"));
    }

    @Test
    public void getMember()
    {
        final Map<String, String> source = new HashMap<>();
        source.put("name", "Solid Snake");
        final MapDataSource ds = new MapDataSource(source);
        assertEquals("Solid Snake", ds.get("name"));
    }

    @Test
    public void getMembers()
    {
        final Map<String, String> source = new HashMap<>();
        source.put("name", "Solid Snake");
        source.put("firstName", "Solid");
        source.put("lastName", "Snake");
        final MapDataSource ds = new MapDataSource(source);
        assertEquals(3, ds.getMembers().length);
        final List<String> result = Arrays.asList(ds.getMembers());
        assertTrue(result.contains("name"));
        assertTrue(result.contains("firstName"));
        assertTrue(result.contains("lastName"));
    }

    @Test
    public void getInvalidMember()
    {
        assertThrows(InvalidPlaceholder.class, () -> {
            final Map<String, String> source = new HashMap<>();
            source.put("name", "Solid Snake");
            final MapDataSource ds = new MapDataSource(source);
            assertEquals("Solid Snake", ds.get("nickname"));
        });
    }

    @Test
    public void getSource()
    {
        final Map<String, String> source = new HashMap<>();
        source.put("name", "Solid Snake");
        final MapDataSource ds = new MapDataSource(source);
        assertEquals(source, ds.getSource());
    }

}

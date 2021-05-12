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

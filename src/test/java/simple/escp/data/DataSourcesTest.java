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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class DataSourcesTest {

    @Test
    public void fromObject() {
        final Map<String, Object> map = new HashMap<>();
        map.put("firstName", "solid");
        map.put("lastName", "snake");
        DataSource ds = DataSources.from(map);
        assertEquals(MapDataSource.class, ds.getClass());
        assertEquals(map, ds.getSource());

        final BeanDataSourceTest.Employee emp = new BeanDataSourceTest.Employee("test", 10.0, 20.0);
        ds = DataSources.from(emp);
        assertEquals(BeanDataSource.class, ds.getClass());
        assertEquals(emp, ds.getSource());
    }

    @Test
    public void fromObjectArray() {
        final Map<String, Object> map = new HashMap<>();
        map.put("firstName", "solid");
        map.put("lastName", "snake");
        final BeanDataSourceTest.Employee emp = new BeanDataSourceTest.Employee("test", 10.0, 20.0);
        final DataSource[] ds = DataSources.from(new Object[]{map, emp});

        assertEquals(MapDataSource.class, ds[0].getClass());
        assertEquals(map, ds[0].getSource());
        assertEquals(BeanDataSource.class, ds[1].getClass());
        assertEquals(emp, ds[1].getSource());
    }

    @Test
    public void fromMapAndBean() {
        final Map<String, Object> map = new HashMap<>();
        map.put("firstName", "solid");
        map.put("lastName", "snake");
        final BeanDataSourceTest.Employee emp = new BeanDataSourceTest.Employee("test", 10.0, 20.0);
        final DataSource[] ds = DataSources.from(map, emp);

        assertEquals(MapDataSource.class, ds[0].getClass());
        assertEquals(map, ds[0].getSource());
        assertEquals(BeanDataSource.class, ds[1].getClass());
        assertEquals(emp, ds[1].getSource());
    }

    @Test
    public void fromNull() {
        final Object object = null;
        final DataSource dataSource = DataSources.from(object);

        assertEquals(EmptyDataSource.class, dataSource.getClass());
        assertEquals("", dataSource.get("anything"));
    }

}

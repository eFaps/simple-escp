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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jakarta.json.JsonObject;

/**
 * A factory class to create <code>DataSource</code>.
 */
@SuppressWarnings("unchecked")
public abstract class DataSources {

    private static final Logger LOG = Logger.getLogger("simple.escp");
    private static EmptyDataSource emptyDataSource = new EmptyDataSource();
    public static final List<DataSourceEntry> DATA_SOURCES;

    static {
        final List<DataSourceEntry> newDataSource = new ArrayList<>();
        newDataSource.add(new DataSourceEntry(Object.class, BeanDataSource.class));
        newDataSource.add(new DataSourceEntry(Map.class, MapDataSource.class));
        newDataSource.add(new DataSourceEntry(String.class, JsonDataSource.class));
        newDataSource.add(new DataSourceEntry(JsonObject.class, JsonDataSource.class));
        DATA_SOURCES = newDataSource;
    }

    /**
     * Register a custom data source so that <code>DataSources</code> can build the custom data source from
     * provided value.
     *
     * @param supportedType class of object that can be handled by this data source.
     * @param dataSourceType an implementation of <code>DataSource</code> that will be created by this entry.
     */
    public static void register(Class supportedType, Class dataSourceType) {
        DATA_SOURCES.add(new DataSourceEntry(supportedType, dataSourceType));
    }

    /**
     * Unregister a custom data source.
     *
     * @param dataSourceType an implementation of <code>DataSource</code> that will be created by this entry.
     */
    public static void unregister(Class dataSourceType) {
        for (final DataSourceEntry entry : DATA_SOURCES.toArray(new DataSourceEntry[0])) {
            if (entry.getDataSourceType().equals(dataSourceType)) {
                DATA_SOURCES.remove(entry);
            }
        }
    }

    /**
     * Create a new <code>DataSource</code> from an <code>Object</code>.  This method will select the
     * appropriate <code>DataSouce</code> based on the class type of <code>Object</code>.
     *
     * @param object the data source value. Passing <code>null</code> value will always return
     *               an instance of <code>EmptyDataSource</code>.
     * @return an implementation of <code>DataSource</code>.
     */
    public static DataSource from(Object object) {
        if (object == null) {
            return emptyDataSource;
        }
        for (int i = DATA_SOURCES.size() - 1; i >= 0; i--) {
            final DataSourceEntry dataSourceEntry = DATA_SOURCES.get(i);
            if (dataSourceEntry.support(object)) {
                return dataSourceEntry.create(object);
            }
        }
        LOG.severe("Can't create data source for [" + object + "] class [" + object.getClass() + "]");
        throw new UnsupportedOperationException("No data source available for [" + object + "] class [" +
            object.getClass() + "]");
    }

    /**
     * Create an array of <code>DataSource</code> based on an array of <code>Object</code>.  This method will
     * select the appropriate <code>DataSource</code> based on the class type of <code>Object</code>.
     *
     * @param objects an array of data source values.
     * @return an array of implementation of <code>DataSource</code> in order of object's array.
     */
    public static DataSource[] from(Object[] objects) {
        final List<DataSource> sources = new ArrayList<>();
        for (final Object o : objects) {
            sources.add(from(o));
        }
        return sources.toArray(new DataSource[0]);
    }

    /**
     * Create an array of <code>DataSource</code> from a <code>Map</code> and JavaBean object.
     *
     * @param map the <code>Map</code> that contains value.  This will be the data source with the first priority.
     * @param object the object that contains value.
     * @return an array of implementation of <code>DataSource</code>.
     */
    public static DataSource[] from(Map map, Object object) {
        return from(new Object[] {map, object});
    }

    /**
     * Internal class used by <code>DataSource</code>.
     */
    public static class DataSourceEntry {

        private final Class supportedType;
        private final Class dataSourceType;

        /**
         * Create new instance of <code>DataSourceEntry</code>.
         *
         * @param supportedType class of object that can be handled by this data source.
         * @param dataSourceType an implementation of <code>DataSource</code> that will be created by this entry.
         */
        public DataSourceEntry(Class supportedType, Class dataSourceType) {
            this.supportedType = supportedType;
            this.dataSourceType = dataSourceType;
        }

        /**
         * Get the supported value type (in <code>Class</code>) that is supported by this <code>DataSource</code>.
         *
         * @return a <code>Class</code> that represent the type of value that can be handled by this
         *         <code>DataSource</code>.
         */
        public Class getSupportedType() {
            return supportedType;
        }

        /**
         * Get the <code>DataSource</code> that will be created if a matching value was found.
         *
         * @return a <code>Class</code> that should be an implementation of <code>DataSource</code>.
         */
        public Class getDataSourceType() {
            return dataSourceType;
        }

        /**
         * Determine if the <code>DataSource</code> can be used for this <code>object</code>.  An <code>object</code>
         * is considered as supported if it is an instance of <code>supportedType</code> or an instance of
         * subclass of <code>supportedType</code>.
         *
         * @param object the object that contains value.
         * @return <code>true</code> if this object can be used as value source for <code>DataSource</code>.
         */
        public boolean support(Object object) {
            return supportedType.isAssignableFrom(object.getClass());
        }

        /**
         * Create a new instance of <code>DataSource</code> from an <code>object</code>.
         *
         * @param object the object that contains value.
         * @return an instance of <code>dataSourceType</code> constructed from <code>object</code>.
         */
        public DataSource create(Object object) {
            if (!support(object)) {
                throw new UnsupportedOperationException("[" + object + "] type [" + object.getClass() +
                    "] is not supported by [" + dataSourceType + "]");
            }
            try {
                final Constructor constructor = dataSourceType.getConstructor(supportedType);
                return (DataSource) constructor.newInstance(object);
            } catch (final NoSuchMethodException e) {
                LOG.severe("Can't find constructor that accept [" + object.getClass().getName() + "] for [" +
                    dataSourceType.getClass().getName());
                throw new RuntimeException(e);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                LOG.severe("Can't create data source for [" + object + "] class [" + object.getClass() + "]");
                throw new RuntimeException(e);
            }
        }

    }

}

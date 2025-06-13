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

import simple.escp.exception.InvalidPlaceholder;

import java.util.Map;

/**
 * A <code>MapDataSource</code> is a <code>DataSource</code> that obtains its value from a <code>Map</code>.
 */
public class MapDataSource implements DataSource {

    private Map<String, ?> source;

    /**
     * Create a new <code>MapDataSource</code>.
     *
     * @param source the <code>Map</code> that contains the value for this <code>DataSource</code>.
     */
    public MapDataSource(Map<String, ? extends Object> source) {
        this.source = source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean has(String member) {
        return source.containsKey(member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(String member) throws InvalidPlaceholder {
        if (!has(member)) {
            throw new InvalidPlaceholder("Can't find [" + member + "] in data source.");
        }
        return source.get(member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getSource() {
        return source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getMembers() {
        return source.keySet().toArray(new String[0]);
    }
}

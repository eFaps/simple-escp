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

/**
 * An empty data source that does nothing excepts returning empty string.
 */
public class EmptyDataSource implements DataSource {

    @Override
    public boolean has(String member) {
        return false;
    }

    @Override
    public Object get(String member) throws InvalidPlaceholder {
        return "";
    }

    @Override
    public Object getSource() {
        return null;
    }

    @Override
    public String[] getMembers() {
        return new String[0];
    }

}

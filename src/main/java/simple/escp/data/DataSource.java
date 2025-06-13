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
 * A <code>DataSource</code> is used to supply value when filling a <code>Report</code>.
 */
public interface DataSource {

    /**
     * Check if this <code>DataSource</code> contains certain member name.
     *
     * @param member the member that will be verified.
     * @return <code>true</code> if this <code>DataSource</code> contains member.
     */
    public boolean has(String member);

    /**
     * Retrieve value for a member from this <code>DataSource</code>.
     *
     * @param member the member that will be retrieved.  This <code>DataSource</code> must have the specified
     *               member.
     * @return an object that represent the member value.
     * @throws simple.escp.exception.InvalidPlaceholder if member doesn't exists or can't retrieve the member value.
     */
    public Object get(String member) throws InvalidPlaceholder;

    /**
     * Retrieve the source value of this <code>DataSource</code>.
     *
     * @return source value of this <code>DataSource</code>.
     */
    public Object getSource();

    /**
     * Retrieve all member of this <code>DataSource</code>.
     *
     * @return all member of this <code>DataSource</code> or empty array if no member is available.
     */
    public String[] getMembers();

}

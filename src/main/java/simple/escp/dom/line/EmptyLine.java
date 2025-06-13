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
package simple.escp.dom.line;

import simple.escp.dom.Line;

/**
 * This class represents an empty line that doesn't have any value associated with it.  It usually means that
 * the line should be discarded or not to be printed.
 */
public class EmptyLine extends Line {

    @Override
    public boolean isDynamic() {
        return false;
    }

}

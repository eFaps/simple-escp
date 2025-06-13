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

import java.io.StringReader;

import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import simple.escp.exception.InvalidPlaceholder;

public class CustomJsonDataSource implements DataSource {

    private String jsonString;
    private JsonObject json;

    public CustomJsonDataSource(String jsonString) {
        this.jsonString = jsonString;
        try (JsonReader reader = Json.createReader(new StringReader(jsonString))) {
            json = reader.readObject();
        }
    }

    public CustomJsonDataSource(JsonObject jsonObject) {
        this.json = jsonObject;
    }

    @Override
    public boolean has(String s) {
        return json.containsKey(s);
    }

    @Override
    public Object get(String member) throws InvalidPlaceholder {
        final JsonValue value = json.get(member);
        if (value.getValueType() == JsonValue.ValueType.ARRAY) {
            return value;
        } else if (value.getValueType() == JsonValue.ValueType.NUMBER) {
            return ((JsonNumber) value).bigDecimalValue();
        } else if (value.getValueType() == JsonValue.ValueType.STRING) {
            return ((JsonString) value).getString();
        } else {
            return value.toString();
        }
    }

    @Override
    public Object getSource() {
        return jsonString;
    }

    @Override
    public String[] getMembers() {
        return json.keySet().toArray(new String[0]);
    }
}

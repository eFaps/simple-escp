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
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

/**
 * A <code>BeanDataSource</code> is a <code>DataSource</code> that obtains its value from a Java Bean object.
 */
public class BeanDataSource implements DataSource {

    private static final Logger LOG = Logger.getLogger("simple.escp");

    private Object source;
    private PropertyDescriptor[] propertyDescriptors;
    private MethodDescriptor[] methodDescriptors;

    /**
     * Create a new <code>BeanDataSource</code>.
     *
     * @param source the Java Bean object that contains the value for this <code>DataSource</code>.
     */
    public BeanDataSource(Object source) {
        this.source = source;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(source.getClass());
            propertyDescriptors = beanInfo.getPropertyDescriptors();
            methodDescriptors = beanInfo.getMethodDescriptors();
        } catch (IntrospectionException e) {
            throw new RuntimeException("Can't read information from object.", e);
        }
    }

    /**
     * Find and return a method in this data source.
     *
     * @param methodName search for this method's name.
     * @return a <code>MethodDescriptor</code> or <code>null</code> if this data source doesn't have
     *         the specified method name.
     */
    public MethodDescriptor getMethod(String methodName) {
        for (MethodDescriptor methodDescriptor : methodDescriptors) {
            if (methodDescriptor.getName().equals(methodName)) {
                return methodDescriptor;
            }
        }
        LOG.fine("Can't find method [" + methodName + "] in this bean.");
        return null;
    }

    /**
     * Find and return a property in this data source.
     *
     * @param propertyName search for this property's name.
     * @return a <code>PropertyDescriptor</code> or <code>null</code> if this data source doesn't have
     *         the specified property name.
     */
    public PropertyDescriptor getProperty(String propertyName) {
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (propertyDescriptor.getName().equals(propertyName)) {
                return propertyDescriptor;
            }
        }
        LOG.fine("Can't find property [" + propertyName + "] in this bean.");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean has(String member) {
        if (member.contains(".")) {
            String[] memberParts = member.split("\\.", 2);
            String curentPart = memberParts[0];
            String compositePart = memberParts[1];
            if (!has(curentPart)) {
                return false;
            } else {
                return (new BeanDataSource(get(curentPart))).has(compositePart);
            }
        } else {
            if (member.startsWith("@")) {
                return getMethod(member.substring(1)) != null ? true : false;
            } else {
                return getProperty(member) != null ? true : false;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(String member) throws InvalidPlaceholder {
        if (!has(member)) {
            throw new InvalidPlaceholder("Can't find [" + member + "] in this data source.");
        }
        if (member.contains(".")) {
            String[] memberParts = member.split("\\.", 2);
            String curentPart = memberParts[0];
            String compositePart = memberParts[1];
            return (new BeanDataSource(get(curentPart))).get(compositePart);
        } else {
            if (member.startsWith("@")) {
                try {
                    return getMethod(member.substring(1)).getMethod().invoke(source);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new InvalidPlaceholder("Can't execute [" + member + "].", e);
                }
            } else {
                try {
                    return getProperty(member).getReadMethod().invoke(source);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new InvalidPlaceholder("Can't read [" + member + "].", e);
                }
            }
        }
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
        String[] result = new String[propertyDescriptors.length];
        int i = 0;
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            result[i++] = propertyDescriptor.getName();
        }
        return result;
    }

}

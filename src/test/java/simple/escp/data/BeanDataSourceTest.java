package simple.escp.data;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class BeanDataSourceTest {

    @Test
    public void getMethod() {
        final Employee emp = new Employee("test", 10.0, 24.0);
        final BeanDataSource ds = new BeanDataSource(emp);
        final MethodDescriptor md = ds.getMethod("grossIncome");
        assertEquals("grossIncome", md.getName());
    }

    @Test
    public void getProperty() {
        final Employee emp = new Employee("test", 10.0, 24.0);
        final BeanDataSource ds = new BeanDataSource(emp);
        PropertyDescriptor pd = ds.getProperty("name");
        assertEquals("name", pd.getName());
        pd = ds.getProperty("hourRate");
        assertEquals("hourRate", pd.getName());
        pd = ds.getProperty("workHours");
        assertEquals("workHours", pd.getName());
    }

    @Test
    public void hasMember() {
        final Employee emp = new Employee("test", 10.0, 24.0);
        final BeanDataSource ds = new BeanDataSource(emp);
        assertTrue(ds.has("name"));
        assertTrue(ds.has("hourRate"));
        assertTrue(ds.has("workHours"));
        assertTrue(ds.has("@grossIncome"));
        assertFalse(ds.has("nickname"));
        assertFalse(ds.has("@tax"));
    }

    @Test
    public void getMember() {
        final Employee emp = new Employee("test", 10.0, 24.0);
        final BeanDataSource ds = new BeanDataSource(emp);
        assertEquals("test", ds.get("name"));
        assertEquals(10.0, ds.get("hourRate"));
        assertEquals(24.0, ds.get("workHours"));
        assertEquals(240.0, ds.get("@grossIncome"));
    }

    public void getMembers() {
        final Employee emp = new Employee("test", 10.0, 24.0);
        final BeanDataSource ds = new BeanDataSource(emp);
        assertEquals(4, ds.getMembers().length);
        final List<String> result = Arrays.asList(ds.getMembers());
        assertTrue(result.contains("name"));
        assertTrue(result.contains("hourRate"));
        assertTrue(result.contains("workHours"));
        assertTrue(result.contains("address"));
    }

    //@Test(expected = InvalidPlaceholder.class)
    public void getInvalidMember() {
        final Employee emp = new Employee("test", 10.0, 24.0);
        final BeanDataSource ds = new BeanDataSource(emp);
        ds.get("invalid");
    }

    @Test
    public void getSource() {
        final Employee emp = new Employee("test", 10.0, 24.0);
        final BeanDataSource ds = new BeanDataSource(emp);
        assertEquals(emp, ds.getSource());
    }

    @Test
    public void nestedProperty() {
        final Employee emp = new Employee("test", 10.0, 24.0);
        final City city = new City("CA", "City");
        final Address address = new Address("Line1", "Line2", city);
        emp.setAddress(address);
        final BeanDataSource ds = new BeanDataSource(emp);
        assertTrue(ds.has("address.line1"));
        assertFalse(ds.has("address.line3"));
        assertEquals("Line1", ds.get("address.line1"));
        assertEquals("Line2", ds.get("address.line2"));
        assertEquals("CA", ds.get("address.city.code"));
        assertEquals("City", ds.get("address.city.name"));
    }

    @Test
    public void nestedMethod() {
        final Employee emp = new Employee("test", 10.0, 24.0);
        final City city = new City("CA", "City");
        final Address address = new Address("Line1", "Line2", city);
        emp.setAddress(address);
        final BeanDataSource ds = new BeanDataSource(emp);
        assertTrue(ds.has("address.@shortAddress"));
        assertFalse(ds.has("address.@longAddress"));
        assertEquals("Line1 City", ds.get("address.@shortAddress"));
    }

    public static class Employee {
        private String name;
        private double hourRate;
        private double workHours;
        private Address address;

        public Employee(final String name, final double hourRate, final double workHours) {
            this.name = name;
            this.hourRate = hourRate;
            this.workHours = workHours;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public double getHourRate() {
            return hourRate;
        }

        public void setHourRate(final double hourRate) {
            this.hourRate = hourRate;
        }

        public double getWorkHours() {
            return workHours;
        }

        public void setWorkHours(final double workHours) {
            this.workHours = workHours;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(final Address address) {
            this.address = address;
        }

        public double grossIncome() {
            return hourRate * workHours;
        }
    }

    public static class Address {

        private String line1;
        private String line2;
        private City city;

        public Address(final String line1, final String line2, final City city) {
            this.line1 = line1;
            this.line2 = line2;
            this.city = city;
        }

        public String getLine1() {
            return line1;
        }

        public void setLine1(final String line1) {
            this.line1 = line1;
        }

        public String getLine2() {
            return line2;
        }

        public void setLine2(final String line2) {
            this.line2 = line2;
        }

        public City getCity() {
            return city;
        }

        public void setCity(final City city) {
            this.city = city;
        }

        public String shortAddress() {
            return line1 + " " + city.getName();
        }
    }

    public static class City {

        private String code;
        private String name;

        public City(final String code, final String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(final String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }
    }

}

package simple.escp.placeholder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.jupiter.api.Test;

import simple.escp.data.DataSources;
import simple.escp.fill.DataSourceBinding;

public class ScriptPlaceholderTest {

    @Test
    public void getText() {
        final String text = "rate * 0.5 :: number :: 10";
        final ScriptPlaceholder placeholder = new ScriptPlaceholder(text, null);
        assertEquals("rate * 0.5 :: number :: 10", placeholder.getText());
    }

    @Test
    public void getName() {
        assertEquals("rate * 0.5", new ScriptPlaceholder("rate * 0.5::currency", null).getScript());
        assertEquals("rate * 0.5", new ScriptPlaceholder("rate * 0.5::10", null).getScript());
        assertEquals("rate * 0.5", new ScriptPlaceholder("rate * 0.5::10         ", null).getScript());
        assertEquals("rate * 0.5", new ScriptPlaceholder("rate * 0.5 ::10", null).getScript());
        assertEquals("rate * 0.5", new ScriptPlaceholder(" rate * 0.5 :: 10", null).getScript());
        assertEquals("rate * 0.5", new ScriptPlaceholder("rate * 0.5::currency::20", null).getScript());
    }

    @Test
    public void getWidth() {
        assertEquals(0, new ScriptPlaceholder("rate * 0.5::currency", null).getWidth());
        assertEquals(10, new ScriptPlaceholder("rate * 0.5::10", null).getWidth());
        assertEquals(10, new ScriptPlaceholder("rate * 0.5::10         ", null).getWidth());
        assertEquals(10, new ScriptPlaceholder("rate * 0.5 ::10", null).getWidth());
        assertEquals(10, new ScriptPlaceholder(" rate * 0.5 :: 10", null).getWidth());
        assertEquals(20, new ScriptPlaceholder("rate * 0.5::currency::20", null).getWidth());
    }

    @Test
    public void getFormat() {
        assertEquals(DecimalFormat.class, new ScriptPlaceholder("rate * 0.5::number::10", null).getFormat().getClass());
        assertEquals(DecimalFormat.class, new ScriptPlaceholder("rate * 0.5::number ::10     ", null).getFormat().getClass());
        assertEquals(DecimalFormat.class, new ScriptPlaceholder("rate * 0.5::  number   ::  10", null).getFormat().getClass());
        assertEquals(DecimalFormat.class, new ScriptPlaceholder("rate * 0.5::integer", null).getFormat().getClass());
        assertEquals(DecimalFormat.class, new ScriptPlaceholder("rate * 0.5::currency", null).getFormat().getClass());
        assertEquals(SimpleDateFormat.class, new ScriptPlaceholder("rate * 0.5::date_full::20", null).getFormat().getClass());
        assertEquals(SimpleDateFormat.class, new ScriptPlaceholder("rate * 0.5::date_long", null).getFormat().getClass());
        assertEquals(SimpleDateFormat.class, new ScriptPlaceholder("rate * 0.5::date_medium", null).getFormat().getClass());
        assertEquals(SimpleDateFormat.class, new ScriptPlaceholder("rate * 0.5::date_short", null).getFormat().getClass());
    }

    @Test
    public void getFormattedNullValue() {
        assertEquals("", new ScriptPlaceholder("name", null).getFormatted(null));
        assertEquals("          ", new ScriptPlaceholder("name::10", null).getFormatted(null));
    }

    @Test
    public void formattedValueSum() {
        final List<Integer> data = new ArrayList<>();
        data.add(10);
        data.add(20);
        data.add(30);
        assertEquals(new BigDecimal("60.0"), new BasicPlaceholder("total::sum").getFormatted(data));

        final List<BigDecimal> data2 = new ArrayList<>();
        data2.add(new BigDecimal("10.25"));
        data2.add(new BigDecimal("20.75"));
        assertEquals(NumberFormat.getCurrencyInstance().format(31), new BasicPlaceholder("total::sum::currency").getFormatted(data2));
    }

    @Test
    public void formattedValueCount() {
        final List<Integer> data = new ArrayList<>();
        data.add(10);
        data.add(20);
        data.add(30);
        assertEquals(3, new BasicPlaceholder("total::count").getFormatted(data));

        final List<BigDecimal> data2 = new ArrayList<>();
        data2.add(new BigDecimal("10.25"));
        data2.add(new BigDecimal("20.75"));
        assertEquals(NumberFormat.getCurrencyInstance().format(2), new BasicPlaceholder("total::count::currency").getFormatted(data2));
    }

    @Test
    public void getNullValue() {
        final Student student = new Student("student");
        final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        scriptEngineManager.setBindings(new DataSourceBinding(DataSources.from(new Object[]{student})));
        final ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");

        ScriptPlaceholder placeholder = new ScriptPlaceholder("bean.name", scriptEngine);
        assertEquals("student", placeholder.getValueAsString(null));

        placeholder = new ScriptPlaceholder("name::10", scriptEngine);
        assertEquals("student   ", placeholder.getValueAsString(null));

        placeholder = new ScriptPlaceholder("bean.name::10", scriptEngine);
        assertEquals("student   ", placeholder.getValueAsString(null));

        placeholder = new ScriptPlaceholder("unknown", scriptEngine);
        assertEquals("", placeholder.getValueAsString(null));

        placeholder = new ScriptPlaceholder("unknown::10", scriptEngine);
        assertEquals("          ", placeholder.getValueAsString(null));
    }

    public static class Student {

        private String name;

        public Student(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

    }
}

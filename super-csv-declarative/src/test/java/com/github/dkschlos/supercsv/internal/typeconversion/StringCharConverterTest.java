package com.github.dkschlos.supercsv.internal.typeconversion;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class StringCharConverterTest {

    private final StringCharConverter instance = new StringCharConverter();

    @Test
    public void testConvertNull() {
        final String input = null;
        final Character expResult = null;
        final Character result = instance.convert(input);
        assertEquals(result, expResult);
    }

    @Test
    public void testConvertValue() {
        final String input = "abc";
        final Character expResult = 'a';
        final Character result = instance.convert(input);
        assertEquals(result, expResult);
    }

}

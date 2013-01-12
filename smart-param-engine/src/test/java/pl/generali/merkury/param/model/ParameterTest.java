package pl.generali.merkury.param.model;

import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * @author Przemek Hertel
 */
public class ParameterTest {

    Parameter par;

    @Before
    public void init() {
        par = new Parameter();
    }

    @Test
    public void testId() {
        // konfiguracja testu
        int expectedValue = 1234567;

        // test
        par.setId(expectedValue);
        int result = par.getId();

        // sprawdzenie wynikow testu
        assertEquals(expectedValue, result);
    }

    @Test
    public void testName() {

        // konfiguracja testu
        String expectedValue = "par.name";

        // test
        par.setName(expectedValue);
        String result = par.getName();

        // sprawdzenie wynikow testu
        assertEquals(expectedValue, result);
    }

    @Test
    public void testLabel() {

        // konfiguracja testu
        String label = "label";

        // test
        par.setLabel(label);
        String result = par.getLabel();

        // sprawdzenie wynikow testu
        assertEquals(label, result);
    }

    @Test
    public void testDescription() {

        // konfiguracja testu
        String desc = "desc";

        // test
        par.setDescription(desc);
        String result = par.getDescription();

        // sprawdzenie wynikow testu
        assertEquals(desc, result);
    }

    @Test
    public void testArchive() {

        assertFalse(par.isArchive());

        par.setArchive(true);
        assertTrue(par.isArchive());
    }

    @Test
    public void testMultivalue() {

        assertFalse(par.isMultivalue());

        par.setMultivalue(true);
        assertTrue(par.isMultivalue());
    }

    @Test
    public void testNullable() {

        assertFalse(par.isNullable());

        par.setNullable(true);
        assertTrue(par.isNullable());
    }

    @Test
    public void testArray() {

        assertFalse(par.isArray());

        par.setArray(true);
        assertTrue(par.isArray());
    }

    @Test
    public void testArraySeparator() {

        assertEquals(Parameter.DEFAULT_ARRAY_SEPARATOR, par.getArraySeparator());

        par.setArraySeparator(',');
        assertEquals(',', par.getArraySeparator());
    }

    @Test
    public void testType() {

        // test
        par.setType("number");
        String result = par.getType();

        // sprawdzenie wynikow testu
        assertEquals("number", result);
    }

    @Test
    public void testType__enum() {

        // test
        par.setType(Codes.INTEGER);
        String result = par.getType();

        // sprawdzenie wynikow testu
        assertEquals("integer", result);
    }

    @Test
    public void testSetLevels() {

        // konfiguracja testu
        List<Level> list = new ArrayList<Level>();
        list.add(l("L1", 0));
        list.add(l("L2", 3));
        list.add(l("L3", 0));

        // test
        par.setLevels(list);

        // sprawdzenie wynikow testu
        assertSame(list, par.getLevels());
        verifyLevel(list.get(0), 0, "L1");
        verifyLevel(list.get(1), 1, "L2");
        verifyLevel(list.get(2), 2, "L3");
    }

    @Test
    public void testSetLevels__null() {

        // test
        par.setLevels(null);

        // weryfikacja
        assertNull(par.getLevels());
    }

    @Test
    public void testAddLevel() {

        // konfiguracja zaleznosci
        Level l1 = l("L1", 0);
        Level l2 = l("L2", 0);
        Level l3 = l("L3", 0);

        // test
        par.addLevel(l1);
        par.addLevel(l2, l3);

        // sprawdzenie wynikow testu
        verifyLevel(par.getLevels().get(0), 0, "L1");
        verifyLevel(par.getLevels().get(1), 1, "L2");
        verifyLevel(par.getLevels().get(2), 2, "L3");
    }

    @Test
    public void testGetLevelCount() {

        // konfiguracja zaleznosci
        Level l1 = l("L1", 0);
        Level l2 = l("L2", 0);

        // test 1
        assertEquals(0, par.getLevelCount());

        // test 2
        par.addLevel();
        par.addLevel(l1, l2);
        assertEquals(2, par.getLevelCount());
    }

    @Test
    public void testSetEntries() {

        // konfiguracja testu
        Set<ParameterEntry> entries = new HashSet<ParameterEntry>();

        // test
        par.setEntries(entries);

        // weryfikacja
        assertEquals(entries, par.getEntries());
    }

    @Test
    public void testAddEntries() {

        // konfiguracja zaleznosci
        ParameterEntry pe1 = new ParameterEntry("A;1", "value1");
        ParameterEntry pe2 = new ParameterEntry("A;2", "value2");
        ParameterEntry pe3 = new ParameterEntry("A;3", "value3");

        // test
        par.addEntries(Arrays.asList(pe1, pe2, pe3));

        // weryfikacja
        assertTrue(par.getEntries().contains(pe1));
        assertTrue(par.getEntries().contains(pe2));
        assertTrue(par.getEntries().contains(pe3));
    }

    @Test
    public void testToString() {

        // konfiguracja testu
        Parameter[] tests = {
            par(11, "par.a", "string", 4, 0, false, false, false, false, true),
            par(22, "par.b", "number", 4, 2, true, false, true, false, false),
            par(33, "par.c", "number", 4, 3, true, true, true, true, false)
        };

        // oczekiwane wyniki
        String[] expected = {
            "Parameter#11[par.a, type=string, levels=4, inputLevels=0, notnull]",
            "Parameter#22[par.b, type=number, levels=4, inputLevels=2, nullable, array, nocache]",
            "Parameter#33[par.c, type=number, levels=4, inputLevels=3, nullable, multivalue, array, archive, nocache]"
        };

        // test
        for (int i = 0; i < tests.length; i++) {
            Parameter par = tests[i];
            String expectedResult = expected[i];

            String result = par.toString();
            assertEquals(expectedResult, result);
        }
    }

    private Parameter par(int id, String name, String type, int levels, int inputLevels,
            boolean nullable, boolean multivalue, boolean array, boolean archive, boolean cacheable) {

        Parameter p = new Parameter();
        p.setId(id);
        p.setName(name);
        p.setType(type);
        for (int i = 0; i < levels; ++i) {
            p.addLevel(new Level());
        }
        p.setInputLevels(inputLevels);

        p.setNullable(nullable);
        p.setMultivalue(multivalue);
        p.setArray(array);
        p.setArchive(archive);
        p.setCacheable(cacheable);

        return p;
    }

    private void verifyLevel(Level l, int expectedOrderNo, String expectedLabel) {
        assertEquals(expectedOrderNo, l.getOrderNo());
        assertEquals(expectedLabel, l.getLabel());
    }

    private Level l(String label, int orderNo) {
        Level l = new Level();
        l.setLabel(label);
        l.setOrderNo(orderNo);
        return l;
    }

    private enum Codes {

        STRING, INTEGER;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}

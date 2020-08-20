/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.editors.types;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.editors.types.RangeValue;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DMNParseServiceImplTest {

    private DMNParseServiceImpl service;

    @Before
    public void setup() {
        service = new DMNParseServiceImpl();
    }

    @Test
    public void testParseFEELListWithARegularNumberList() {

        final List<String> actualList = service.parseFEELList("1, 2, 3");
        final List<String> expectedList = asList("1", "2", "3");

        assertEquals(expectedList, actualList);
    }

    @Test
    public void testParseFEELListWithARegularStringList() {

        final List<String> actualList = service.parseFEELList("\"Sao Paulo, SP\", \"Campinas, SP\", \"Rio de Janeiro, RJ\"");
        final List<String> expectedList = asList("\"Sao Paulo, SP\"", "\"Campinas, SP\"", "\"Rio de Janeiro, RJ\"");

        assertEquals(expectedList, actualList);
    }

    @Test
    public void testParseFEELListWithAnArbitraryValue() {

        final List<String> actualList = service.parseFEELList("aaabbbcccdddeee");
        final List<String> expectedList = singletonList("aaabbbcccdddeee");

        assertEquals(expectedList, actualList);
    }

    @Test
    public void testParseFEELListWithASemicolon() {

        final List<String> actualList = service.parseFEELList("abcdef, \"Sao Paulo, SP\"; 123");
        final List<String> expectedList = asList("abcdef", "\"Sao Paulo, SP\"");

        assertEquals(expectedList, actualList);
    }

    @Test
    public void testParseFEELListWithASpecialCharacter() {

        final List<String> actualList = service.parseFEELList("%20C");
        final List<String> expectedList = emptyList();

        assertEquals(expectedList, actualList);
    }

    @Test
    public void testParseRange() {

        final RangeValue rangeValue = service.parseRangeValue("[1..2]");
        assertTrue(rangeValue.getIncludeStartValue());
        assertTrue(rangeValue.getIncludeEndValue());
        assertEquals("1", rangeValue.getStartValue());
        assertEquals("2", rangeValue.getEndValue());
    }

    @Test
    public void testParseString() {

        final RangeValue rangeValue = service.parseRangeValue("[abc..def]");
        assertTrue(rangeValue.getIncludeStartValue());
        assertTrue(rangeValue.getIncludeEndValue());
        assertEquals("abc", rangeValue.getStartValue());
        assertEquals("def", rangeValue.getEndValue());
    }

    @Test
    public void testParseStringWithDots() {

        final RangeValue rangeValue = service.parseRangeValue("[\"abc..\"..\"d..ef\"]");
        assertTrue(rangeValue.getIncludeStartValue());
        assertTrue(rangeValue.getIncludeEndValue());
        assertEquals("\"abc..\"", rangeValue.getStartValue());
        assertEquals("\"d..ef\"", rangeValue.getEndValue());
    }

    @Test
    public void testParseFunctionInvocation() {

        final RangeValue rangeValue = service.parseRangeValue("[ date ( \"2018-01-01\" ) .. date( \"2018-02-02\" ) ]");
        assertEquals("date(\"2018-01-01\")", rangeValue.getStartValue());
        assertEquals("date(\"2018-02-02\")", rangeValue.getEndValue());
    }

    @Test
    public void testParseFunctionInvocation_MissingEnd() {

        final RangeValue rangeValue = service.parseRangeValue("[date(\"2018-01-01\")..]");
        assertNotNull(rangeValue);
        assertEquals("", rangeValue.getStartValue());
        assertEquals("", rangeValue.getEndValue());
    }

    @Test
    public void testParseWithInteger() {

        final RangeValue rangeValue = service.parseRangeValue("[0..5]");
        assertTrue(rangeValue.getIncludeStartValue());
        assertTrue(rangeValue.getIncludeEndValue());
        assertEquals("0", rangeValue.getStartValue());
        assertEquals("5", rangeValue.getEndValue());
    }

    @Test
    public void testParseExcludeStart() {

        final RangeValue rangeValue = service.parseRangeValue("(0..5]");
        assertFalse(rangeValue.getIncludeStartValue());
    }

    @Test
    public void testParseExcludeEnd() {

        final RangeValue rangeValue = service.parseRangeValue("[0..5)");
        assertFalse(rangeValue.getIncludeEndValue());
    }

    @Test
    public void testParseFEELListMultipleCommasInside() {

        final List<String> actualList = service.parseFEELList("\"Sao Paulo, SP, South America\",\"New York, NY, North America\"");
        final List<String> expectedList = asList("\"Sao Paulo, SP, South America\"", "\"New York, NY, North America\"");

        assertEquals(expectedList, actualList);
    }
}

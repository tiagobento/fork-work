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

package org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.AssociationDeclaration.Direction.Input;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.AssociationDeclaration.Direction.Output;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.AssociationDeclaration.Type.SourceTarget;

public class AssociationDeclarationTest {

    private static final String INPUT_ASSIGNMENTS_VALUE = "[din]var1->input1";
    private static final String INPUT_ASSIGNMENTS_VALUE_MISSING = "[din]var1->";
    private static final String OUTPUT_ASSIGNMENTS_VALUE = "[dout]output1->var1";
    private static final String OUTPUT_ASSIGNMENTS_VALUE_MISSING = "[dout]output1->";

    @Test(expected = IllegalArgumentException.class)
    public void testFromStringNull() {
        AssociationDeclaration.fromString(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromStringEmpty() {
        AssociationDeclaration.fromString("");
    }

    @Test
    public void testFromStringInput() {
        AssociationDeclaration associationDeclaration = AssociationDeclaration.fromString(INPUT_ASSIGNMENTS_VALUE);
        assertEquals(associationDeclaration.getSource(), "var1");
        assertEquals(associationDeclaration.getTarget(), "input1");
        assertEquals(associationDeclaration.getType(), SourceTarget);
        assertEquals(associationDeclaration.getDirection(), Input);
    }

    @Test
    public void testFromStringOutput() {
        AssociationDeclaration associationDeclaration = AssociationDeclaration.fromString(OUTPUT_ASSIGNMENTS_VALUE);
        assertEquals(associationDeclaration.getSource(), "output1");
        assertEquals(associationDeclaration.getTarget(), "var1");
        assertEquals(associationDeclaration.getType(), SourceTarget);
        assertEquals(associationDeclaration.getDirection(), Output);
    }

    @Test
    public void testFromStringInputMissing() {
        AssociationDeclaration associationDeclaration = AssociationDeclaration.fromString(INPUT_ASSIGNMENTS_VALUE_MISSING);
        assertEquals(associationDeclaration.getSource(), "var1");
        assertEquals(associationDeclaration.getTarget(), "");
        assertEquals(associationDeclaration.getType(), SourceTarget);
        assertEquals(associationDeclaration.getDirection(), Input);
    }

    @Test
    public void testFromStringOutputMissing() {
        AssociationDeclaration associationDeclaration = AssociationDeclaration.fromString(OUTPUT_ASSIGNMENTS_VALUE_MISSING);
        assertEquals(associationDeclaration.getSource(), "output1");
        assertEquals(associationDeclaration.getTarget(), "");
        assertEquals(associationDeclaration.getType(), SourceTarget);
        assertEquals(associationDeclaration.getDirection(), Output);
    }
}
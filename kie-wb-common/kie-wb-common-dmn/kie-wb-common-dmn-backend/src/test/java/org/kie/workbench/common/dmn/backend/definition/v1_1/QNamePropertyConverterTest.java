/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.Optional;

import javax.xml.XMLConstants;

import org.junit.Test;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.v1_1.TDefinitions;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase.Namespace;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QNamePropertyConverterTest {

    static final Decision parent11 = new org.kie.dmn.model.v1_1.TDecision();

    static {
        parent11.getNsContext().put(Namespace.FEEL.getPrefix(), parent11.getURIFEEL());
    }

    static final Decision parent12 = new org.kie.dmn.model.v1_2.TDecision();

    static {
        parent12.getNsContext().put(Namespace.FEEL.getPrefix(), parent12.getURIFEEL());
    }

    @Test
    public void testWBfromDMNnull() {
        final QName wb = QNamePropertyConverter.wbFromDMN(null, parent11);

        assertThat(wb).isNotNull();
        assertThat(wb).isEqualTo(BuiltInType.UNDEFINED.asQName());
    }

    @Test
    public void testDMNfromWBnull() {
        Optional<javax.xml.namespace.QName> dmn = QNamePropertyConverter.dmnFromWB(new QName(XMLConstants.NULL_NS_URI,
                                                                                             BuiltInType.UNDEFINED.getName(),
                                                                                             Namespace.FEEL.getPrefix()));
        assertThat(dmn).isEmpty();
    }

    @Test
    public void testGetDefaultNamespace() {

        final String defaultNamespace = "http://www.kiegroup.org/";
        final TDefinitions definition = mock(TDefinitions.class);
        when(definition.getNamespace()).thenReturn(defaultNamespace);

        final String actual = QNamePropertyConverter.getDefaultNamespace(definition);

        assertEquals(defaultNamespace, actual);
    }

    @Test
    public void testGetDefaultNamespaceFromParent() {

        final String defaultNamespace = "http://www.kiegroup.org/";
        final TDefinitions definition = mock(TDefinitions.class);
        final DMNModelInstrumentedBase model = mock(DMNModelInstrumentedBase.class);

        when(model.getParent()).thenReturn(definition);
        when(definition.getNamespace()).thenReturn(defaultNamespace);

        final String actual = QNamePropertyConverter.getDefaultNamespace(model);

        assertEquals(defaultNamespace, actual);
    }

    @Test
    public void testWbFromDMNForBuiltInDataType11WithSameUriAsDefaultNamespace(){

        final String defaultNamespace = "http://www.kiegroup.org/";
        final TDefinitions definition = mock(TDefinitions.class);

        when(definition.getURIFEEL()).thenReturn("");
        when(definition.getNamespace()).thenReturn(defaultNamespace);
        when(definition.getNamespaceURI(Namespace.KIE.getPrefix())).thenReturn(defaultNamespace);

        final javax.xml.namespace.QName dmn = new javax.xml.namespace.QName(defaultNamespace,
                                                                            BuiltInType.STRING.getName(),
                                                                            Namespace.KIE.getPrefix());
        final QName wb = QNamePropertyConverter.wbFromDMN(dmn, definition);

        assertEquals(BuiltInType.STRING.getName(), wb.getLocalPart());
        assertEquals(XMLConstants.NULL_NS_URI, wb.getPrefix());
    }

    @Test
    public void testWBfromDMNForBuiltInDataType11() {
        final javax.xml.namespace.QName dmn = new javax.xml.namespace.QName(XMLConstants.NULL_NS_URI,
                                                                            BuiltInType.STRING.getName(),
                                                                            Namespace.FEEL.getPrefix());
        final QName wb = QNamePropertyConverter.wbFromDMN(dmn, parent11);

        assertThat(wb).isNotNull();
        assertThat(wb.getNamespaceURI()).isEqualTo(QName.NULL_NS_URI);
        assertThat(wb.getLocalPart()).isEqualTo(BuiltInType.STRING.getName());
        assertThat(wb.getPrefix()).isEqualTo(QName.DEFAULT_NS_PREFIX);
    }

    @Test
    public void testWBfromDMNForBuiltInDataType11variant() {
        final Decision parent11_variant = new org.kie.dmn.model.v1_1.TDecision();
        final String nonTrivialFEELPrefix = "friendlyenough";
        parent11_variant.getNsContext().put(nonTrivialFEELPrefix, parent11_variant.getURIFEEL());

        final javax.xml.namespace.QName dmn = new javax.xml.namespace.QName(XMLConstants.NULL_NS_URI,
                                                                            BuiltInType.STRING.getName(),
                                                                            nonTrivialFEELPrefix);
        final QName wb = QNamePropertyConverter.wbFromDMN(dmn, parent11_variant);

        assertThat(wb).isNotNull();
        assertThat(wb.getNamespaceURI()).isEqualTo(QName.NULL_NS_URI);
        assertThat(wb.getLocalPart()).isEqualTo(BuiltInType.STRING.getName());
        assertThat(wb.getPrefix()).isEqualTo(QName.DEFAULT_NS_PREFIX);
    }

    @Test
    public void testWBfromDMNForBuiltInDataType12() {
        final javax.xml.namespace.QName dmn = new javax.xml.namespace.QName(XMLConstants.NULL_NS_URI,
                                                                            BuiltInType.STRING.getName());
        final QName wb = QNamePropertyConverter.wbFromDMN(dmn, parent12);

        assertThat(wb).isNotNull();
        assertThat(wb.getNamespaceURI()).isEqualTo(QName.NULL_NS_URI);
        assertThat(wb.getLocalPart()).isEqualTo(BuiltInType.STRING.getName());
        assertThat(wb.getPrefix()).isEqualTo(QName.DEFAULT_NS_PREFIX);
    }

    @Test
    public void testWBfromDMNCustomDataType() {
        final javax.xml.namespace.QName dmn = new javax.xml.namespace.QName(Namespace.KIE.getUri(),
                                                                            "tCustom",
                                                                            Namespace.KIE.getPrefix());
        final QName wb = QNamePropertyConverter.wbFromDMN(dmn, parent11);

        assertThat(wb).isNotNull();
        assertThat(wb.getNamespaceURI()).isEqualTo(Namespace.KIE.getUri());
        assertThat(wb.getLocalPart()).isEqualTo("tCustom");
        assertThat(wb.getPrefix()).isEqualTo(Namespace.KIE.getPrefix());
    }
}

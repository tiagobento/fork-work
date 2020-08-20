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

package org.kie.workbench.common.stunner.core;

import java.util.Optional;

import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class TestingSimpleDomainObject implements DomainObject {

    public static final String PROPERTY_SET = "propertySet";
    public static final String NAME = "name";
    public static final String NAME_FIELD = PROPERTY_SET + "." + NAME;

    public static class SomePropertyBean {

    }

    public static class SomePropertySetBean {

    }

    private final SomePropertySetBean propertySet;
    private final SomePropertyBean nameProperty;

    @SuppressWarnings("unchecked")
    public TestingSimpleDomainObject(final TestingGraphMockHandler graphMockHandler) {
        propertySet = new SomePropertySetBean();
        nameProperty = new SomePropertyBean();
        when(graphMockHandler.getDefinitionAdapter().accepts(eq(TestingSimpleDomainObject.class))).thenReturn(true);
        when(graphMockHandler.getDefinitionAdapter().accepts(eq(SomePropertySetBean.class))).thenReturn(false);
        when(graphMockHandler.getDefinitionAdapter().accepts(eq(SomePropertyBean.class))).thenReturn(false);
        when(graphMockHandler.getPropertyAdapter().accepts(eq(SomePropertyBean.class))).thenReturn(true);
        when(graphMockHandler.getPropertyAdapter().accepts(eq(SomePropertySetBean.class))).thenReturn(false);
        when(graphMockHandler.getPropertyAdapter().accepts(eq(TestingSimpleDomainObject.class))).thenReturn(false);
        when(graphMockHandler.getPropertySetAdapter().accepts(eq(SomePropertySetBean.class))).thenReturn(true);
        when(graphMockHandler.getPropertySetAdapter().accepts(eq(SomePropertyBean.class))).thenReturn(false);
        when(graphMockHandler.getPropertySetAdapter().accepts(eq(TestingSimpleDomainObject.class))).thenReturn(false);
        when(graphMockHandler.getPropertySetAdapter().getId(eq(propertySet))).thenReturn(PROPERTY_SET);
        when(graphMockHandler.getPropertyAdapter().getId(eq(nameProperty))).thenReturn(NAME);
        when(graphMockHandler.getDefinitionAdapter().getProperty(eq(this), eq(PROPERTY_SET))).thenReturn(Optional.of(propertySet));
        when(graphMockHandler.getPropertySetAdapter().getProperty(eq(propertySet), eq(NAME))).thenReturn(Optional.of(nameProperty));
        when(graphMockHandler.getDefinitionAdapter().getNameField(eq(this))).thenReturn(Optional.of(NAME_FIELD));
        when(graphMockHandler.getDefinitionAdapter().getMetaProperty(eq(PropertyMetaTypes.NAME), eq(this))).thenReturn(nameProperty);
    }

    public SomePropertySetBean getPropertySet() {
        return propertySet;
    }

    public SomePropertyBean getNameProperty() {
        return nameProperty;
    }

    @Override
    public String getDomainObjectUUID() {
        return getClass().getName();
    }

    @Override
    public String getDomainObjectNameTranslationKey() {
        return "";
    }
}

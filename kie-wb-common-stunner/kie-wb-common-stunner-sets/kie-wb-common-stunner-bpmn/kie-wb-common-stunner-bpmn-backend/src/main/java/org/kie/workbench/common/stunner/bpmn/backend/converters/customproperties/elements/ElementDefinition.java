/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.elements;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;

public abstract class ElementDefinition<T> {

    private final T defaultValue;
    private final String name;

    public ElementDefinition(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String name() {
        return name;
    }

    public final T getDefaultValue() {
        return defaultValue;
    }

    public abstract T getValue(BaseElement element);

    public abstract void setValue(BaseElement element, T value);

    public static FeatureMap getExtensionElements(BaseElement element) {
        if (element.getExtensionValues() == null || element.getExtensionValues().isEmpty()) {
            ExtensionAttributeValue eav = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
            element.getExtensionValues().add(eav);
            return eav.getValue();
        } else {
            return element.getExtensionValues().get(0).getValue();
        }
    }

    public CustomElement<T> of(BaseElement element) {
        return new CustomElement<>(this, element);
    }
}
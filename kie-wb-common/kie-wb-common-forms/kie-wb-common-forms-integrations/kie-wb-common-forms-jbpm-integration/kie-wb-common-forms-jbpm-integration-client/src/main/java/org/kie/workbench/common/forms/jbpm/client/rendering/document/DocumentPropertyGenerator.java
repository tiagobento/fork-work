/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.jbpm.client.rendering.document;

import javax.enterprise.context.Dependent;

import org.jboss.errai.databinding.client.PropertyType;
import org.kie.workbench.common.forms.dynamic.client.helper.PropertyGenerator;
import org.kie.workbench.common.forms.jbpm.model.authoring.document.definition.DocumentFieldDefinition;
import org.kie.workbench.common.forms.jbpm.model.document.DocumentData;

@Dependent
public class DocumentPropertyGenerator implements PropertyGenerator<DocumentFieldDefinition> {

    @Override
    public Class<DocumentFieldDefinition> getType() {
        return DocumentFieldDefinition.class;
    }

    @Override
    public PropertyType generatePropertyType(DocumentFieldDefinition field) {
        return new PropertyType(DocumentData.class);
    }
}

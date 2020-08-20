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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.lists;

import java.util.List;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.FieldValueMarshaller;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.StringMultipleInputFieldDefinition;

@Dependent
public class StringMultipleInputValueMarshaller extends AbstractStringMultipleFieldValueMarshaller<StringMultipleInputFieldDefinition> {

    @Override
    public Class<StringMultipleInputFieldDefinition> getSupportedField() {
        return StringMultipleInputFieldDefinition.class;
    }

    @Override
    public Supplier<FieldValueMarshaller<List, List<String>, StringMultipleInputFieldDefinition>> newInstanceSupplier() {
        return StringMultipleInputValueMarshaller::new;
    }
}

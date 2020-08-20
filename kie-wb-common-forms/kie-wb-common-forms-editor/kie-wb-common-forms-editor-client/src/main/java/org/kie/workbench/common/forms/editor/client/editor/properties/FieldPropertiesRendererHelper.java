/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.client.editor.properties;

import java.util.List;
import java.util.Set;

import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.uberfire.backend.vfs.Path;

public interface FieldPropertiesRendererHelper {

    FormRenderingContext getCurrentRenderingContext();

    FieldDefinition getCurrentField();

    Set<String> getAvailableModelFields(FieldDefinition fieldDefinition);

    List<String> getCompatibleFieldTypes(FieldDefinition fieldDefinition);

    void onClose();

    void onPressOk(FieldDefinition fieldCopy);

    FieldDefinition onFieldTypeChange(FieldDefinition field,
                                      String newType);

    FieldDefinition onFieldBindingChange(FieldDefinition field,
                                         String newBinding);

    Path getPath();
}

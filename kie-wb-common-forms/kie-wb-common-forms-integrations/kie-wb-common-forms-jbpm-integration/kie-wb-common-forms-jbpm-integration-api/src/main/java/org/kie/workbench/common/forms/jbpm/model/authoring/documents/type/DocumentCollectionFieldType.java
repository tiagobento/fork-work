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

package org.kie.workbench.common.forms.jbpm.model.authoring.documents.type;

import org.kie.workbench.common.forms.model.FieldType;

public class DocumentCollectionFieldType implements FieldType {


    public static final String DOCUMENT_COLLECTION_TYPE = "org.jbpm.document.DocumentCollection";
    public static final String DOCUMENT_COLLECTION_IMPL_TYPE = "org.jbpm.document.service.impl.DocumentCollectionImpl";
    public static final String DOCUMENTS_TYPE = "org.jbpm.document.Documents";

    public static final String NAME = "DocumentCollection";

    @Override
    public String getTypeName() {
        return NAME;
    }
}

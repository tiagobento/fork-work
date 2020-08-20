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
package org.kie.workbench.common.forms.jbpm.server.service;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.forms.jbpm.model.authoring.document.type.DocumentFieldType;
import org.kie.workbench.common.forms.jbpm.model.authoring.documents.type.DocumentCollectionFieldType;
import org.kie.workbench.common.forms.model.util.formModel.FormModelPropertiesUtil;
import org.uberfire.commons.services.cdi.Startup;

@ApplicationScoped
@Startup
public class FormsJBPMIntegrationBackendEntryPoint {

    @PostConstruct
    public void init() {
        // registering Document Types to ModelPropertiesUtil
        FormModelPropertiesUtil.registerBaseType(DocumentFieldType.DOCUMENT_TYPE);
        FormModelPropertiesUtil.registerBaseType(DocumentFieldType.DOCUMENT_IMPL_TYPE);
        
        FormModelPropertiesUtil.registerBaseType(DocumentCollectionFieldType.DOCUMENTS_TYPE);
        FormModelPropertiesUtil.registerBaseType(DocumentCollectionFieldType.DOCUMENT_COLLECTION_TYPE);
        FormModelPropertiesUtil.registerBaseType(DocumentCollectionFieldType.DOCUMENT_COLLECTION_IMPL_TYPE);
    }
}

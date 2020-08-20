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
package org.kie.workbench.common.stunner.cm.backend.indexing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.project.backend.indexing.AbstractBpmnFileIndexer;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.cm.resource.CaseManagementDefinitionSetResourceType;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
@CaseManagementEditor
public class CaseManagementFileIndexer extends AbstractBpmnFileIndexer {

    @Inject
    protected CaseManagementDefinitionSetResourceType cmTypeDefinition;

    @Override
    protected String getProcessDescriptorName() {
        return CaseManagementDataEventListener.NAME;
    }

    @Override
    public boolean supportsPath(Path path) {
        return cmTypeDefinition.accept(Paths.convert(path));
    }
}
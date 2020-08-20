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

package org.kie.workbench.common.dmn.api.editors.types;

import java.util.List;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.bus.server.annotations.Remote;

/**
 * This service handlers calls related to Data Objects.
 */
@Remote
public interface DataObjectsService {

    /**
     * Loads the Data Objects (Java classes) from a given project.
     * @param workspaceProject The project from which the Data Objects will be loaded.
     * @return All {@link DataObject}s from the given project.
     */
    List<DataObject> loadDataObjects(final WorkspaceProject workspaceProject);
}

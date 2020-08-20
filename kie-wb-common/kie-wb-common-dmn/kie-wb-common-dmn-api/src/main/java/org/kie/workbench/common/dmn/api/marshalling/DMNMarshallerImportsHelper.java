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

package org.kie.workbench.common.dmn.api.marshalling;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.backend.vfs.Path;

/**
 * This helper provides methods to handle imports into the DMNMarshaller.
 */
public interface DMNMarshallerImportsHelper<IMPORT, DEFINITIONS, DRGELEMENT, ITEMDEFINITION> {

    /**
     * This method loads all imported DMN definitions from a list of imports.
     * @param metadata represents the metadata from the main DMN model.
     * @param imports represent the list of imported files.
     * @return a map {@link DEFINITIONS} indexed by {@link IMPORT}s.
     */
    Map<IMPORT, DEFINITIONS> getImportDefinitions(final Metadata metadata,
                                                  final List<IMPORT> imports);

    /**
     * This method loads all imported PMML documents from a list of imports.
     * @param metadata represents the metadata from the main DMN model.
     * @param imports represent the list of imported files.
     * @return a map {@link PMMLDocumentMetadata} indexed by {@link IMPORT}s.
     */
    Map<IMPORT, PMMLDocumentMetadata> getPMMLDocuments(final Metadata metadata,
                                                       final List<IMPORT> imports);

    /**
     * This method loads {@link String} of all imported XML files from a list of imports.
     * @param metadata represents the metadata from the main DMN model.
     * @param imports represent the list of imported files.
     * @return a map {@link String} indexed by {@link IMPORT}s.
     */
    Map<IMPORT, String> getImportXML(final Metadata metadata,
                                     final List<IMPORT> imports);

    /**
     * This method extract a list of {@link DRGELEMENT}s from the <code>importDefinitions</code> map.
     * @param importDefinitions is a map of {@link DEFINITIONS} indexed by {@link IMPORT}.
     * @return a list of imported {@link DRGELEMENT}s.
     */
    List<DRGELEMENT> getImportedDRGElements(final Map<IMPORT, DEFINITIONS> importDefinitions);

    /**
     * This method extract a list of {@link ITEMDEFINITION} from the <code>importDefinitions</code> map.
     * @param importDefinitions is a map of {@link DEFINITIONS} indexed by {@link IMPORT}.
     * @return a list of imported {@link ITEMDEFINITION}s.
     */
    List<ITEMDEFINITION> getImportedItemDefinitions(final Map<IMPORT, DEFINITIONS> importDefinitions);

    /**
     * This method finds the list of {@link ITEMDEFINITION}s for a given <code>namespace</code>.
     * @param workspaceProject represents the project that will be scanned.
     * @param modelName is the value used as the prefix for imported {@link ITEMDEFINITION}s.
     * @param namespace is the namespace of the model that provides the list of {@link ITEMDEFINITION}s.
     * @return a list of imported {@link ITEMDEFINITION}s.
     */
    List<ITEMDEFINITION> getImportedItemDefinitionsByNamespace(final WorkspaceProject workspaceProject,
                                                               final String modelName,
                                                               final String namespace);

    /**
     * This method finds the {@link Path} of DMN model.
     * @param metadata represents the metadata from the a DMN model from the scanned project.
     * @param modelNamespace represents the namespace of the desired DMN model.
     * @param modelName represents the mode name of the desired DMN model.
     * @return the {@link Path} of the desired DMN model.
     */
    Path getDMNModelPath(final Metadata metadata,
                         final String modelNamespace,
                         final String modelName);

    /**
     * This method loads the {@link InputStream} from a given {@link org.uberfire.backend.vfs.Path}.
     * @param path to be loaded.
     * @return the {@link InputStream} when the path is valid, otherwise is returns empty.
     */
    Optional<InputStream> loadPath(final Path path);
}

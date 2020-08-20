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

package org.kie.workbench.common.stunner.project.service;

import java.util.Optional;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.file.SupportsUpdate;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.stunner.core.service.BaseDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;

@Remote
public interface ProjectDiagramService extends BaseDiagramService<ProjectMetadata, ProjectDiagram>,
                                               SupportsUpdate<ProjectDiagram>,
                                               SupportsDelete {

    Path create(final Path path,
                final String name,
                final String defSetId,
                final String projectName,
                final Package projectPkg,
                final Optional<String> projectType);

    Path saveAsXml(final Path path,
                   final String xml,
                   final Metadata metadata,
                   final String comment);
}

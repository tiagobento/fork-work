/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.projecteditor.service;

import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorContent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;

@Remote
public interface PomEditorService extends SupportsSaveAndRename<String, Metadata> {

    DefaultEditorContent loadContent(final Path path);

    Path save(final Path path,
              final String content,
              final Metadata metadata,
              final String comment,
              final DeploymentMode mode);
}

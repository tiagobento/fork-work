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
package org.kie.workbench.common.stunner.project.diagram;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.backend.vfs.Path;

public interface ProjectMetadata extends Metadata {

    enum SVGGenerator {
        JBPM_DESIGNER,
        STUNNER
    }

    String getModuleName();

    Package getProjectPackage();

    Overview getOverview();

    String getProjectType();

    SVGGenerator getDiagramSVGGenerator();

    void setDiagramSVGGenerator(SVGGenerator svgGenerator);

    Path getDiagramSVGPath();

    void setDiagramSVGPath(Path diagramSVGPath);
}

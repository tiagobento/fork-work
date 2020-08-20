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
 *
 */

package org.kie.workbench.common.screens.examples.validation;

import java.util.Optional;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleProjectError;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

public abstract class ImportProjectValidator {

    public static final String POM_XML = "pom.xml";
    private Logger logger = LoggerFactory.getLogger(ImportProjectValidator.class);

    public Optional<ExampleProjectError> validate(OrganizationalUnit organizationalUnit, ImportProject importProject) {
        if (logger.isDebugEnabled()) {
            logger.debug("Validation project [{}]",
                         importProject.getName());
        }

        Optional<ExampleProjectError> error = this.getError(organizationalUnit, importProject);

        if (logger.isDebugEnabled() && error.isPresent()) {
            logger.debug("Error found [{} - {} - {}]",
                         importProject.getName(),
                         error.get().getId(),
                         error.get().getDescription());
        }

        return error;
    }

    protected abstract Optional<ExampleProjectError> getError(OrganizationalUnit organizationalUnit, ImportProject importProject);

    protected POM getPom(POMService pomService,
                         Path rootPath) {

        String rootPathUri = rootPath.toURI();
        String pomUri = POM_XML;

        if (!rootPathUri.endsWith("/")) {
            pomUri = "/" + pomUri;
        }

        Path pomPath = PathFactory.newPath(POM_XML,
                                           rootPathUri + pomUri);
        return pomService.load(pomPath);
    }
}

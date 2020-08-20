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

package org.kie.workbench.common.stunner.project.backend.service;

import java.util.Map;
import java.util.Optional;

import javax.enterprise.inject.Instance;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.services.backend.service.KieServiceOverviewLoader;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.backend.service.AbstractVFSDiagramService;
import org.kie.workbench.common.stunner.core.definition.service.DefinitionSetService;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.registry.BackendRegistryFactory;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectMetadataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.StandardDeleteOption;

class ProjectDiagramServiceController extends AbstractVFSDiagramService<ProjectMetadata, ProjectDiagram> {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectDiagramServiceController.class.getName());

    private final IOService ioService;
    private final KieModuleService moduleService;
    private final KieServiceOverviewLoader overviewLoader;

    ProjectDiagramServiceController(final DefinitionManager definitionManager,
                                    final FactoryManager factoryManager,
                                    final Instance<DefinitionSetService> definitionSetServiceInstances,
                                    final IOService ioService,
                                    final BackendRegistryFactory registryFactory,
                                    final KieModuleService moduleService,
                                    final KieServiceOverviewLoader overviewLoader,
                                    final User identity) {
        super(definitionManager,
              factoryManager,
              definitionSetServiceInstances,
              registryFactory,
              identity);
        this.ioService = ioService;
        this.moduleService = moduleService;
        this.overviewLoader = overviewLoader;
    }

    @Override
    protected void initialize() {
        // Initialize caches.
        super.initialize();
    }

    @Override
    protected Class<? extends Metadata> getMetadataType() {
        return ProjectMetadata.class;
    }

    @Override
    public Path create(final Path path,
                       final String name,
                       final String defSetId) {
        return create(path,
                      name,
                      defSetId,
                      getCurrentModule(path).getModuleName(),
                      null,
                      Optional.empty());
    }

    public Path create(final Path path,
                       final String name,
                       final String defSetId,
                       final String moduleName,
                       final Package projectPkg,
                       final Optional<String> projectType) {
        final ProjectMetadata metadata = buildProjectMetadataInstance(path,
                                                                      name,
                                                                      defSetId,
                                                                      moduleName,
                                                                      projectPkg,
                                                                      overviewLoader.loadOverview(path),
                                                                      projectType);
        return this.create(path,
                           name,
                           defSetId,
                           metadata);
    }

    @Override
    public ProjectDiagram getDiagramByPath(Path file) {
        final ProjectDiagram projectDiagram = super.getDiagramByPath(file);
        final org.uberfire.java.nio.file.Path svgPath = getDiagramSvgFilePath(projectDiagram);
        if (getIoService().exists(svgPath)) {
            projectDiagram.getMetadata().setDiagramSVGPath(Paths.convert(svgPath));
            final String svgContent = getIoService().readAllString(svgPath);
            //jBPM svg files generated with oryx has the xmlns:oryx="http://oryx-editor.org\" configured
            if (svgContent != null && svgContent.contains("xmlns:oryx=\"http://oryx-editor.org\"")) {
                projectDiagram.getMetadata().setDiagramSVGGenerator(ProjectMetadata.SVGGenerator.JBPM_DESIGNER);
            } else {
                projectDiagram.getMetadata().setDiagramSVGGenerator(ProjectMetadata.SVGGenerator.STUNNER);
            }
        }
        return projectDiagram;
    }

    @Override
    protected Metadata buildMetadataInstance(final Path path,
                                             final String defSetId,
                                             final String title) {
        final Package modulePackage = moduleService.resolvePackage(path);
        final KieModule kieModule = getCurrentModule(path);
        return buildProjectMetadataInstance(path,
                                            title,
                                            defSetId,
                                            kieModule.getModuleName(),
                                            modulePackage,
                                            overviewLoader.loadOverview(path),
                                            Optional.empty());
    }

    private KieModule getCurrentModule(final Path path) {
        return moduleService.resolveModule(path);
    }

    private ProjectMetadata buildProjectMetadataInstance(final Path path,
                                                         final String name,
                                                         final String defSetId,
                                                         final String moduleName,
                                                         final Package projPkg,
                                                         final Overview overview,
                                                         final Optional<String> projectType) {
        return new ProjectMetadataImpl.ProjectMetadataBuilder()
                .forDefinitionSetId(defSetId)
                .forModuleName(moduleName)
                .forProjectPackage(projPkg)
                .forOverview(overview)
                .forTitle(name)
                .forPath(path)
                .forProjectType(projectType.orElse(null))
                .build();
    }

    public Path save(final Path path,
                     final ProjectDiagram diagram,
                     final Map<String, ?> attributes,
                     final OpenOption... comment) {
        try {
            String[] raw = serialize(diagram);
            getIoService().write(Paths.convert(path),
                                 raw[0],
                                 attributes,
                                 comment);
        } catch (Exception e) {
            LOG.error("Error while saving diagram with UUID [" + diagram.getName() + "].",
                      e);
            throw new RuntimeException(e);
        }
        return path;
    }

    public Path saveAsXml(final Path path,
                          final String xml,
                          final Map<String, ?> attributes,
                          final OpenOption... comment) {
        try {
            getIoService().write(Paths.convert(path),
                                 xml,
                                 attributes,
                                 comment);
        } catch (Exception e) {
            LOG.error("Error while saving diagram.",
                      e);
            throw new RuntimeException(e);
        }
        return path;
    }

    @Override
    protected ProjectMetadata doSave(final ProjectDiagram diagram,
                                     final String raw,
                                     final String metadata) {
        try {
            Path _path = diagram.getMetadata().getPath();
            org.uberfire.java.nio.file.Path path = Paths.convert(_path);
            if (!getIoService().exists(path)) {
                final org.uberfire.java.nio.file.Path parent = path.getParent();
                final String fileName = path.getFileName().toString();
                path = parent.resolve(fileName);
            }
            getIoService().write(path,
                                 raw);
        } catch (Exception e) {
            LOG.error("Error while saving diagram with UUID [" + diagram.getName() + "].",
                      e);
            throw new RuntimeException(e);
        }
        return diagram.getMetadata();
    }

    // TODO: Use commit message.
    public boolean delete(final Path _path,
                          final String message) {
        final org.uberfire.java.nio.file.Path path = Paths.convert(_path);
        return getIoService().deleteIfExists(path,
                                             StandardDeleteOption.NON_EMPTY_DIRECTORIES);
    }

    @Override
    protected boolean doDelete(final Path path) {
        return delete(path,
                      "");
    }

    @Override
    protected IOService getIoService() {
        return ioService;
    }
}

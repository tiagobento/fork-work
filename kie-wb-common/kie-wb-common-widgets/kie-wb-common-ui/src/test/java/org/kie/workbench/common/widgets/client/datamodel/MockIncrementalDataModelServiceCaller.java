/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.widgets.client.datamodel;

import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.IncrementalDataModelServiceImpl;
import org.kie.workbench.common.services.datamodel.backend.server.cache.LRUDataModelOracleCache;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleIncrementalPayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Mockito.*;

public class MockIncrementalDataModelServiceCaller implements Caller<IncrementalDataModelService> {

    private IncrementalDataModelServiceImplWrapper service;

    public MockIncrementalDataModelServiceCaller() {
        this(mock(PackageDataModelOracle.class));
    }

    public MockIncrementalDataModelServiceCaller(final PackageDataModelOracle packageLoader) {
        final KieModule module = mock(KieModule.class);
        final Package pkg = new Package(mock(Path.class),
                                        mock(Path.class),
                                        mock(Path.class),
                                        mock(Path.class),
                                        mock(Path.class),
                                        packageLoader.getPackageName(),
                                        packageLoader.getPackageName(),
                                        packageLoader.getPackageName());
        final LRUDataModelOracleCache cachePackages = mock(LRUDataModelOracleCache.class);
        when(cachePackages.assertPackageDataModelOracle(module,
                                                        pkg)).thenReturn(packageLoader);

        final KieModuleService moduleService = mock(KieModuleService.class);
        when(moduleService.resolveModule(any(Path.class))).thenReturn(module);
        when(moduleService.resolvePackage(any(Path.class))).thenReturn(pkg);

        this.service = new IncrementalDataModelServiceImplWrapper(cachePackages,
                                                                  moduleService);
    }

    @Override
    public IncrementalDataModelService call() {
        return service;
    }

    @Override
    public IncrementalDataModelService call(final RemoteCallback<?> remoteCallback) {
        service.setCallback(remoteCallback);
        return service;
    }

    @Override
    public IncrementalDataModelService call(final RemoteCallback<?> remoteCallback,
                                            final ErrorCallback<?> errorCallback) {
        service.setCallback(remoteCallback);
        return service;
    }

    private static class IncrementalDataModelServiceImplWrapper extends IncrementalDataModelServiceImpl {

        private RemoteCallback<?> remoteCallback;

        public IncrementalDataModelServiceImplWrapper(final LRUDataModelOracleCache cachePackages,
                                                      final KieModuleService moduleService) {
            super(cachePackages,
                  moduleService);
        }

        public void setCallback(final RemoteCallback<?> remoteCallback) {
            this.remoteCallback = remoteCallback;
        }

        @Override
        public PackageDataModelOracleIncrementalPayload getUpdates(final Path resourcePath,
                                                                   final Imports imports,
                                                                   final String factType) {
            final PackageDataModelOracleIncrementalPayload payload = super.getUpdates(resourcePath,
                                                                                      imports,
                                                                                      factType);
            final RemoteCallback r = remoteCallback;
            r.callback(payload);
            return payload;
        }
    }
}

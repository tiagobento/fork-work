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

package org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.redeploy;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.jboss.errai.common.client.api.Caller;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.workbench.common.screens.projecteditor.build.exec.SnapshotDeploymentSettings;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionContext;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.dialog.BuildDialog;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.deploy.SnapshotBuildAndDeployExecutor;
import org.kie.workbench.common.screens.projecteditor.client.editor.DeploymentPopup;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.uberfire.workbench.events.NotificationEvent;

public class SnapshotRedeployExecutor extends SnapshotBuildAndDeployExecutor {

    public SnapshotRedeployExecutor(final Caller<BuildService> buildServiceCaller,
                                    final Event<BuildResults> buildResultsEvent,
                                    final Event<NotificationEvent> notificationEvent,
                                    final BuildDialog buildDialog,
                                    final DeploymentPopup deploymentPopup,
                                    final Caller<SpecManagementService> specManagementService,
                                    final SnapshotDeploymentSettings settings) {
        super(buildServiceCaller, buildResultsEvent, notificationEvent, buildDialog, deploymentPopup, specManagementService, settings);
    }

    @Override
    protected void updateContainerSpec(final BuildExecutionContext context, final ContainerSpec containerSpec) {
        containerSpec.setStatus(context.getServerTemplate().getContainerSpec(containerSpec.getId()).getStatus());
        specManagementService.call(ignore -> {
            notifyUpdateSuccess();
        }, (o, throwable) -> {
            notifyUpdateError();
            return false;
        }).updateContainerSpec(context.getServerTemplate().getId(), context.getContainerId(), containerSpec, true);
    }
}

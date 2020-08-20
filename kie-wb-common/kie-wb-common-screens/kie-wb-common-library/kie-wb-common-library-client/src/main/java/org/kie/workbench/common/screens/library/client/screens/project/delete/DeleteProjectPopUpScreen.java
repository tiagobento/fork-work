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

package org.kie.workbench.common.screens.library.client.screens.project.delete;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public class DeleteProjectPopUpScreen {

    public interface View extends UberElemental<DeleteProjectPopUpScreen>,
                                  HasBusyIndicator {

        String getConfirmedName();

        void show(String name);

        void showError(final String errorMessage);

        void hide();

        String getWrongConfirmedNameValidationMessage();

        String getDeletingMessage();

    }

    private WorkspaceProject project;

    private DeleteProjectPopUpScreen.View view;

    private LibraryPlaces libraryPlaces;

    @Inject
    public DeleteProjectPopUpScreen(final DeleteProjectPopUpScreen.View view,
                                    final LibraryPlaces libraryPlaces) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public void show(final WorkspaceProject project) {
        this.project = project;
        view.show(project.getName());
    }

    public void delete() {
        final String confirmedName = view.getConfirmedName();
        if (!project.getName().equals(confirmedName)) {
            view.showError(view.getWrongConfirmedNameValidationMessage());
            return;
        }

        libraryPlaces.closeAllPlacesOrNothing(() -> {
            view.showBusyIndicator(view.getDeletingMessage());
            libraryPlaces.deleteProject(project, view);
        });
    }

    public void cancel() {
        view.hide();
    }
}

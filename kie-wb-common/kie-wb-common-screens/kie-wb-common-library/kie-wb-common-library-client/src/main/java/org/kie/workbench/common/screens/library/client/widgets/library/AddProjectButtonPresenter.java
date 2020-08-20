/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.widgets.library;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.project.AddProjectPopUpPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.util.ResourceUtils;
import org.kie.workbench.common.screens.library.client.widgets.common.MenuResourceHandlerWidget;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewWorkspaceProjectHandler;
import org.uberfire.client.mvp.UberElement;

public class AddProjectButtonPresenter {

    public interface View extends UberElement<AddProjectButtonPresenter> {

        void addOtherProject(MenuResourceHandlerWidget menuResourceHandlerWidget);

        void hideOtherProjects();

        void addNewMenuItem(final MenuResourceHandlerWidget menuResourceHandlerWidget);
    }

    private View view;

    private ManagedInstance<AddProjectPopUpPresenter> addProjectPopUpPresenters;

    private ManagedInstance<MenuResourceHandlerWidget> menuResourceHandlerWidgets;

    private ManagedInstance<NewWorkspaceProjectHandler> newProjectHandlers;

    private NewResourcePresenter newResourcePresenter;

    private ProjectController projectController;

    private LibraryPlaces libraryPlaces;

    TranslationService translationService;

    @Inject
    public AddProjectButtonPresenter(final View view,
                                     final ManagedInstance<AddProjectPopUpPresenter> addProjectPopUpPresenters,
                                     final ManagedInstance<MenuResourceHandlerWidget> menuResourceHandlerWidgets,
                                     final ManagedInstance<NewWorkspaceProjectHandler> newProjectHandlers,
                                     final NewResourcePresenter newResourcePresenter,
                                     final ProjectController projectController,
                                     final TranslationService translationService,
                                     final LibraryPlaces libraryPlaces) {
        this.view = view;
        this.addProjectPopUpPresenters = addProjectPopUpPresenters;
        this.menuResourceHandlerWidgets = menuResourceHandlerWidgets;
        this.newProjectHandlers = newProjectHandlers;
        this.newResourcePresenter = newResourcePresenter;
        this.projectController = projectController;
        this.translationService = translationService;
        this.libraryPlaces = libraryPlaces;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        setupOtherProjects();
    }

    private void setupOtherProjects() {
        if (userCanCreateProjects()) {
            boolean hasOtherProjects = false;
            for (NewWorkspaceProjectHandler newWorkspaceProjectHandler : getNewProjectHandlers()) {
                if (!ResourceUtils.isDefaultProjectHandler(newWorkspaceProjectHandler) && newWorkspaceProjectHandler.canCreate()) {
                    addNewProjectHandler(newWorkspaceProjectHandler);
                    hasOtherProjects = true;
                }
            }

            if (!hasOtherProjects) {
                view.hideOtherProjects();
            }
        }
    }

    void addNewProjectHandler(final NewWorkspaceProjectHandler newWorkspaceProjectHandler) {
        newWorkspaceProjectHandler.setOpenEditorOnCreation(false);
        newWorkspaceProjectHandler.setCreationSuccessCallback(project -> {
            if (project != null) {
                libraryPlaces.goToProject(project);
            }
        });

        final MenuResourceHandlerWidget menuResourceHandlerWidget = menuResourceHandlerWidgets.get();
        menuResourceHandlerWidget.init(newWorkspaceProjectHandler.getDescription(),
                                       () -> libraryPlaces.closeAllPlacesOrNothing(() -> {
                                                                                       libraryPlaces.goToLibrary();
                                                                                       newWorkspaceProjectHandler.getCommand(newResourcePresenter).execute();
                                                                                   }));
        view.addOtherProject(menuResourceHandlerWidget);
    }

    public void addProject() {
        if (userCanCreateProjects()) {
            final AddProjectPopUpPresenter addProjectPopUpPresenter = addProjectPopUpPresenters.get();
            addProjectPopUpPresenter.show();
        }
    }

    public void enableTrySamples(){
        final MenuResourceHandlerWidget menuResourceHandlerWidget = menuResourceHandlerWidgets.get();
        menuResourceHandlerWidget.init(this.translationService.getTranslation(LibraryConstants.TrySamples),
                                       () -> libraryPlaces.closeAllPlacesOrNothing(() -> {
                                               libraryPlaces.goToLibrary();
                                               libraryPlaces.goToTrySamples();
                                           }));
        view.addNewMenuItem(menuResourceHandlerWidget);
    }
    public void enableImportProject() {
        final MenuResourceHandlerWidget menuResourceHandlerWidget = menuResourceHandlerWidgets.get();
        menuResourceHandlerWidget.init(this.translationService.getTranslation(LibraryConstants.ImportProject),
                                       () -> libraryPlaces.goToImportRepositoryPopUp());
        view.addNewMenuItem(menuResourceHandlerWidget);
    }

    public boolean userCanCreateProjects() {
        return projectController.canCreateProjects(libraryPlaces.getActiveSpace());
    }

    public View getView() {
        return view;
    }

    Iterable<NewWorkspaceProjectHandler> getNewProjectHandlers() {
        return newProjectHandlers;
    }
}

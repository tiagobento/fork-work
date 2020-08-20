/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.contributors.SpaceContributorsUpdatedEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectCountUpdate;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab.ContributorsListPresenter;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab.SpaceContributorsListServiceImpl;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.delete.DeleteOrganizationalUnitPopUpPresenter;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.settings.SettingsScreenPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.promise.Promises;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.spaces.Space;
import org.uberfire.workbench.events.NotificationEvent;

@WorkbenchScreen(identifier = LibraryPlaces.LIBRARY_SCREEN,
        owningPerspective = LibraryPerspective.class)
public class LibraryScreen {

    private View view;
    private ManagedInstance<DeleteOrganizationalUnitPopUpPresenter> deleteOrganizationalUnitPopUpPresenters;
    private ProjectController projectController;
    private OrganizationalUnitController organizationalUnitController;

    private WorkspaceProjectContext projectContext;
    private EmptyLibraryScreen emptyLibraryScreen;
    private PopulatedLibraryScreen populatedLibraryScreen;
    private OrgUnitsMetricsScreen orgUnitsMetricsScreen;
    private SettingsScreenPresenter settingsScreenPresenter;
    private ContributorsListPresenter contributorsListPresenter;
    private Caller<LibraryService> libraryService;
    private Caller<OrganizationalUnitService> organizationalUnitService;
    private LibraryPlaces libraryPlaces;
    private SpaceContributorsListServiceImpl spaceContributorsListService;
    private Event<NotificationEvent> notificationEvent;
    private TranslationService translationService;
    private Promises promises;

    @Inject
    public LibraryScreen(final View view,
                         final ManagedInstance<DeleteOrganizationalUnitPopUpPresenter> deleteOrganizationalUnitPopUpPresenters,
                         final WorkspaceProjectContext projectContext,
                         final ProjectController projectController,
                         final OrganizationalUnitController organizationalUnitController,
                         final EmptyLibraryScreen emptyLibraryScreen,
                         final PopulatedLibraryScreen populatedLibraryScreen,
                         final OrgUnitsMetricsScreen orgUnitsMetricsScreen,
                         final SettingsScreenPresenter settingsScreenPresenter,
                         final ContributorsListPresenter contributorsListPresenter,
                         final Caller<LibraryService> libraryService,
                         final Caller<OrganizationalUnitService> organizationalUnitService,
                         final LibraryPlaces libraryPlaces,
                         final SpaceContributorsListServiceImpl spaceContributorsListService,
                         final Event<NotificationEvent> notificationEvent,
                         final TranslationService translationService,
                         final Promises promises) {
        this.view = view;
        this.deleteOrganizationalUnitPopUpPresenters = deleteOrganizationalUnitPopUpPresenters;
        this.projectContext = projectContext;
        this.projectController = projectController;
        this.organizationalUnitController = organizationalUnitController;
        this.emptyLibraryScreen = emptyLibraryScreen;
        this.populatedLibraryScreen = populatedLibraryScreen;
        this.orgUnitsMetricsScreen = orgUnitsMetricsScreen;
        this.settingsScreenPresenter = settingsScreenPresenter;
        this.contributorsListPresenter = contributorsListPresenter;
        this.libraryService = libraryService;
        this.organizationalUnitService = organizationalUnitService;
        this.libraryPlaces = libraryPlaces;
        this.spaceContributorsListService = spaceContributorsListService;
        this.notificationEvent = notificationEvent;
        this.translationService = translationService;
        this.promises = promises;
    }

    @PostConstruct
    public void init() {

        final OrganizationalUnit activeOU = projectContext.getActiveOrganizationalUnit()
                .orElseThrow(() -> new IllegalStateException("Cannot initialize library screen without an active organizational unit."));

        view.init(this);
        view.setTitle(activeOU.getName());
        view.setDescription(activeOU.getDescription());
        view.showSettingsTab(organizationalUnitController.canUpdateOrgUnit(activeOU));

        showProjects();

        contributorsListPresenter.setup(spaceContributorsListService,
                                        contributorsCount -> view.setContributorsCount(contributorsCount));
    }

    public void trySamples() {
        if (userCanCreateProjects()) {
            libraryPlaces.closeAllPlacesOrNothing(() -> {
                libraryPlaces.goToLibrary();
                libraryPlaces.goToTrySamples();
            });
        }
    }

    public void importProject() {
        if (userCanCreateProjects()) {
            libraryPlaces.goToImportRepositoryPopUp();
        }
    }

    public void delete() {
        if (userCanDeleteOrganizationalUnit()) {
            final DeleteOrganizationalUnitPopUpPresenter deleteOrganizationalUnitPopUpPresenter = deleteOrganizationalUnitPopUpPresenters.get();
            deleteOrganizationalUnitPopUpPresenter.show(projectContext.getActiveOrganizationalUnit()
                                                                .orElseThrow(() -> new IllegalStateException("Cannot delete organizational unit if none is active.")));
        }
    }

    public void showProjects() {
        final OrganizationalUnit activeOU = projectContext.getActiveOrganizationalUnit()
                .orElseThrow(() -> new IllegalStateException("Cannot try to query library projects without an active organizational unit."));

        this.showProjects(activeOU);
    }

    protected void showProjects(OrganizationalUnit organizationalUnit) {
        if (organizationalUnitController.canReadOrgUnit(organizationalUnit)) {
            libraryService.call((RemoteCallback<Boolean>) this::setLibraryProjectScreen).hasProjects(organizationalUnit);
        } else {
            notificationEvent.fire(new NotificationEvent(translationService.format(LibraryConstants.SpacePermissionsChanged, organizationalUnit.getName()), NotificationEvent.NotificationType.WARNING));
            libraryPlaces.closeAllPlacesOrNothing(() -> libraryPlaces.goToOrganizationalUnits());
        }
    }

    protected void setLibraryProjectScreen(boolean hasProjects) {
        if (hasProjects) {
            showPopulatedLibraryScreen();
        } else {
            showEmptyLibraryScreen();
        }
    }

    protected void changeDescription(String description) {
        projectContext.getActiveOrganizationalUnit().ifPresent(p -> {
            organizationalUnitService.call((RemoteCallback<OrganizationalUnit>) this::setDescriptionChanged).updateOrganizationalUnit(p.getName(), p.getDefaultGroupId(), null, description);
        });
    }

    private void setDescriptionChanged(OrganizationalUnit organizationalUnit) {
        view.setDescription(organizationalUnit.getDescription());
        notificationEvent.fire(new NotificationEvent(translationService.format(LibraryConstants.SpaceDescriptionChanged, organizationalUnit.getName()), NotificationEvent.NotificationType.SUCCESS));
    }

    private void showEmptyLibraryScreen() {
        view.setProjectsCount(0);
        if (view.isProjectsTabActive()) {
            view.updateContent(emptyLibraryScreen.getView().getElement());
        }
    }

    private void showPopulatedLibraryScreen() {
        view.setProjectsCount(populatedLibraryScreen.getProjectsCount());
        if (view.isProjectsTabActive()) {
            view.updateContent(populatedLibraryScreen.getView().getElement());
        }
    }

    public void showContributors() {
        if (view.isContributorsTabActive()) {
            view.updateContent(contributorsListPresenter.getView().getElement());
        }
    }

    public void showMetrics() {
        orgUnitsMetricsScreen.refresh();
        if (view.isMetricsTabActive()) {
            view.updateContent(orgUnitsMetricsScreen.getView().getElement());
        }
    }

    public void showSettings() {
        if (view.isSettingsTabActive()) {
            settingsScreenPresenter.setupUsingCurrentSection().then(v -> {
                view.updateContent(settingsScreenPresenter.getView().getElement());
                return promises.resolve();
            });
        }
    }

    public boolean userCanCreateProjects() {
        return projectController.canCreateProjects(libraryPlaces.getActiveSpace());
    }

    public boolean userCanUpdateOrganizationalUnit() {
        return organizationalUnitController.canUpdateOrgUnit(projectContext.getActiveOrganizationalUnit().orElseThrow(() -> new IllegalStateException("Cannot try to update an organizational unit when none is active.")));
    }

    public boolean userCanDeleteOrganizationalUnit() {
        return organizationalUnitController.canDeleteOrgUnit(projectContext.getActiveOrganizationalUnit().orElseThrow(() -> new IllegalStateException("Cannot try to delete an organizational unit when none is active.")));
    }

    public void onNewProject(@Observes NewProjectEvent e) {
        projectContext.getActiveOrganizationalUnit().ifPresent(p -> {
            if (eventOnCurrentSpace(p, e.getWorkspaceProject().getSpace())) {
                showProjects();
            }
        });
    }

    public void onRepositoryRemovedEvent(@Observes RepositoryRemovedEvent e) {
        projectContext.getActiveOrganizationalUnit().ifPresent(p -> {
            if (eventOnCurrentSpace(p, e.getRepository().getSpace())) {
                showProjects();
            }
        });
    }

    boolean eventOnCurrentSpace(final OrganizationalUnit p,
                                final Space space) {
        return p.getSpace().getName().equalsIgnoreCase(space.getName());
    }

    public void onProjectCountUpdate(@Observes final ProjectCountUpdate projectCountUpdate) {
        projectContext.getActiveOrganizationalUnit().ifPresent(p -> {
            if (eventOnCurrentSpace(p, projectCountUpdate.getSpace())) {
                view.setProjectsCount(projectCountUpdate.getCount());
            }
        });
    }

    public void onSpaceContributorsUpdated(@Observes final SpaceContributorsUpdatedEvent spaceContributorsUpdatedEvent) {
        projectContext.getActiveOrganizationalUnit().ifPresent(p -> {
            if (eventOnCurrentSpace(p, spaceContributorsUpdatedEvent.getOrganizationalUnit().getSpace())) {
                view.setContributorsCount(spaceContributorsUpdatedEvent.getOrganizationalUnit().getContributors().size());
                this.showProjects(spaceContributorsUpdatedEvent.getOrganizationalUnit());
            }
        });
    }

    @OnClose
    public void onClose() {
        orgUnitsMetricsScreen.onClose();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Library Screen";
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }

    public interface View extends UberElement<LibraryScreen> {

        void setTitle(String title);

        void setDescription(String description);

        void setProjectsCount(int count);

        void setContributorsCount(int count);

        void updateContent(HTMLElement content);

        boolean isProjectsTabActive();

        boolean isContributorsTabActive();

        boolean isMetricsTabActive();

        boolean isSettingsTabActive();

        void showSettingsTab(boolean isVisible);
    }
}

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

package org.kie.workbench.common.workbench.client.admin;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.guvnor.common.services.shared.preferences.GuvnorPreferenceScopes;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.soup.commons.util.Sets;
import org.kie.workbench.common.profile.api.preferences.ProfileService;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.LanguageConfigurationHandler;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationPresenter;
import  org.kie.workbench.common.services.shared.resources.PerspectiveIds;
import org.kie.workbench.common.workbench.client.admin.resources.i18n.PreferencesConstants;
import org.kie.workbench.common.workbench.client.authz.WorkbenchFeatures;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.ext.preferences.client.admin.page.AdminPage;
import org.uberfire.ext.preferences.client.admin.page.AdminPageOptions;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.impl.SearchRequestImpl;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;

import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.ADMIN;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.GUVNOR_M2REPO;
import static org.kie.workbench.common.services.shared.resources.PerspectiveIds.SECURITY_MANAGEMENT;

public class DefaultAdminPageHelper {

    DefaultWorkbenchConstants constants = DefaultWorkbenchConstants.INSTANCE;

    @Inject
    private AdminPage adminPage;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ClientUserSystemManager userSystemManager;

    @Inject
    private TranslationService translationService;

    @Inject
    private AuthorizationManager authorizationManager;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private PreferenceScopeFactory scopeFactory;

    @Inject
    private UberfireBreadcrumbs breadcrumbs;

    @Inject
    private WorkbenchConfigurationPresenter workbenchConfigurationPresenter;

    @Inject
    private LanguageConfigurationHandler languageConfigurationHandler;

    @Inject
    private ClientExperimentalFeaturesRegistryService experimentalFeaturesService;

    @Inject
    Caller<ProfileService> profileService;

    private static final String ADVANCED_CATEGORY = "advanced";

    public void setup() {
        setup(true,
              true,
              true);
    }

    public void setup(boolean libraryPreferencesEnabled,
                      boolean artifactRepositoryPreferencesEnabled,
                      boolean stunnerEnabled) {
        adminPage.addScreen("root",
                            constants.Settings());
        adminPage.setDefaultScreen("root");

        addSecurityPerspective();
        addArtifactsPerspective();
        addDataSourcePerspective();
        addDataSetPerspective();
        addGlobalPreferences(libraryPreferencesEnabled,
                             artifactRepositoryPreferencesEnabled);
        addGeneralPreferences();
        addStunnerPreferences(stunnerEnabled);
        addExperimentalPreferences();
        addSSHKeys();
        addProfilePreferences();
        addDataTransferPerspective();

        if (libraryPreferencesEnabled) {
            addArchetypesPerspective();
        }
    }

    private void addDataTransferPerspective() {
        boolean authorized = authorizationManager.authorize(
            WorkbenchFeatures.ACCESS_DATA_TRANSFER,
            sessionInfo.getIdentity());

        if (!authorized) {
            return;
        }

        adminPage.addTool(
            "root",
            constants.DataTransfer(),
            new Sets.Builder().add("pficon").add("pficon-import").build(),
            "services",
            () -> {
                Command cmd = () -> placeManager.goTo(PerspectiveIds.DATA_TRANSFER);
                cmd.execute();
                addAdminBreadcrumbs(PerspectiveIds.DATA_TRANSFER, constants.DataTransfer(), cmd);
            });
    }

    private void addProfilePreferences() {
        final boolean canEditProfilePreferences = authorizationManager.authorize(WorkbenchFeatures.EDIT_PROFILE_PREFERENCES,
                                                                                 sessionInfo.getIdentity());
        profileService.call((Boolean force) -> {
            if (canEditProfilePreferences && !force) {
                adminPage.addPreference("root",
                                        "ProfilePreferences",
                                        translationService.format(PreferencesConstants.ProfilePreferences_Title),
                                        new Sets.Builder().add("fa").add("fa-list").build(),
                                        ADVANCED_CATEGORY,
                                        scopeFactory.createScope(GuvnorPreferenceScopes.GLOBAL),
                                        AdminPageOptions.WITH_BREADCRUMBS);
            }
        }).isForce();
    }

    private void addGeneralPreferences() {
        adminPage.addTool("root",
                          constants.Languages(),
                          new Sets.Builder().add("fa").add("fa-language").build(),
                          "preferences",
                          () -> workbenchConfigurationPresenter.show(languageConfigurationHandler));
    }

    private void addStunnerPreferences(boolean stunnerEnabled) {
        if (stunnerEnabled) {
            adminPage.addPreference("root",
                                    "StunnerPreferences",
                                    constants.StunnerDesignerPreferences(),
                                    new Sets.Builder().add("fa").add("fa-object-group").build(),
                                    "general",
                                    scopeFactory.createScope(GuvnorPreferenceScopes.GLOBAL),
                                    AdminPageOptions.WITH_BREADCRUMBS);
        }
    }

    private void addExperimentalPreferences() {
        if (hasAccessToPerspective(PerspectiveIds.EXPERIMENTAL_FEATURES) && experimentalFeaturesService.isExperimentalEnabled() && !experimentalFeaturesService.getFeaturesRegistry().getAllFeatures().isEmpty()) {
            adminPage.addTool("root",
                              constants.ExperimentalSettings(),
                              new Sets.Builder().add("fa").add("fa-flask").build(),
                              ADVANCED_CATEGORY,
                              () -> {
                                  final Command accessExperimentals = () -> placeManager.goTo(PerspectiveIds.EXPERIMENTAL_FEATURES);
                                  accessExperimentals.execute();
                                  addAdminBreadcrumbs(PerspectiveIds.EXPERIMENTAL_FEATURES,
                                                      constants.ExperimentalSettings(),
                                                      accessExperimentals);
                              });
        }
    }

    private void addSSHKeys() {
        adminPage.addTool("root",
                          constants.SSHKeys(),
                          new Sets.Builder().add("fa").add("fa-key").build(),
                          "general",
                          () -> {
                              final Command accessSSHKeysEditor = () -> placeManager.goTo(PerspectiveIds.SSH_KEYS_EDITOR);
                              accessSSHKeysEditor.execute();
                              addAdminBreadcrumbs(PerspectiveIds.SSH_KEYS_EDITOR, constants.SSHKeys(), accessSSHKeysEditor);
                          });
    }

    private void addSecurityPerspective() {
        if (hasAccessToPerspective(PerspectiveIds.SECURITY_MANAGEMENT)) {
            adminPage.addTool("root",
                              constants.Roles(),
                              new Sets.Builder().add("fa").add("fa-unlock-alt").build(),
                              "security",
                              () -> {
                                  final Command accessRoles = () -> {
                                      Map<String, String> params = new HashMap<>();
                                      params.put("activeTab",
                                                 "RolesTab");
                                      placeManager.goTo(new DefaultPlaceRequest(SECURITY_MANAGEMENT,
                                                                                params));
                                  };

                                  accessRoles.execute();
                                  addAdminBreadcrumbs(SECURITY_MANAGEMENT,
                                                      constants.SecurityManagement(),
                                                      accessRoles);
                              },
                              command -> userSystemManager.roles((AbstractEntityManager.SearchResponse<Role> response) -> {
                                                                     if (response != null) {
                                                                         command.execute(response.getTotal());
                                                                     }
                                                                 },
                                                                 (o, throwable) -> false).search(new SearchRequestImpl("",
                                                                                                                       1,
                                                                                                                       1,
                                                                                                                       null)));

            adminPage.addTool("root",
                              constants.Groups(),
                              new Sets.Builder().add("fa").add("fa-users").build(),
                              "security",
                              () -> {
                                  final Command accessGroups = () -> {
                                      Map<String, String> params = new HashMap<>();
                                      params.put("activeTab",
                                                 "GroupsTab");
                                      placeManager.goTo(new DefaultPlaceRequest(SECURITY_MANAGEMENT,
                                                                                params));
                                  };

                                  accessGroups.execute();
                                  addAdminBreadcrumbs(SECURITY_MANAGEMENT,
                                                      constants.SecurityManagement(),
                                                      accessGroups);
                              },
                              command -> userSystemManager.groups((AbstractEntityManager.SearchResponse<Group> response) -> {
                                                                      if (response != null) {
                                                                          command.execute(response.getTotal());
                                                                      }
                                                                  },
                                                                  (o, throwable) -> false).search(new SearchRequestImpl("",
                                                                                                                        1,
                                                                                                                        1,
                                                                                                                        null)));

            adminPage.addTool("root",
                              constants.Users(),
                              new Sets.Builder().add("fa").add("fa-user").build(),
                              "security",
                              () -> {
                                  final Command accessUsers = () -> {
                                      Map<String, String> params = new HashMap<>();
                                      params.put("activeTab",
                                                 "UsersTab");
                                      placeManager.goTo(new DefaultPlaceRequest(SECURITY_MANAGEMENT,
                                                                                params));
                                  };

                                  accessUsers.execute();
                                  addAdminBreadcrumbs(SECURITY_MANAGEMENT,
                                                      constants.SecurityManagement(),
                                                      accessUsers);
                              },
                              command -> userSystemManager.users((AbstractEntityManager.SearchResponse<User> response) -> {
                                                                     if (response != null) {
                                                                         command.execute(response.getTotal());
                                                                     }
                                                                 },
                                                                 (o, throwable) -> false).search(new SearchRequestImpl("",
                                                                                                                       1,
                                                                                                                       1,
                                                                                                                       null)));
        }
    }

    private void addArtifactsPerspective() {
        if (hasAccessToPerspective(PerspectiveIds.GUVNOR_M2REPO)) {
            adminPage.addTool("root",
                              constants.Artifacts(),
                              new Sets.Builder().add("fa").add("fa-download").build(),
                              "perspectives",
                              () -> {
                                  final Command accessArtifacts = () -> placeManager.goTo(GUVNOR_M2REPO);
                                  accessArtifacts.execute();
                                  addAdminBreadcrumbs(GUVNOR_M2REPO,
                                                      constants.Artifacts(),
                                                      accessArtifacts);
                              });
        }
    }

    private void addDataSourcePerspective() {
        if (hasAccessToPerspective(PerspectiveIds.DATASOURCE_MANAGEMENT)) {
            adminPage.addTool("root",
                              constants.DataSources(),
                              new Sets.Builder().add("fa").add("fa-database").build(),
                              "perspectives",
                              () -> {
                                  final Command accessDataSources = () -> placeManager.goTo(PerspectiveIds.DATASOURCE_MANAGEMENT);
                                  accessDataSources.execute();
                                  addAdminBreadcrumbs(PerspectiveIds.DATASOURCE_MANAGEMENT,
                                                      constants.DataSources(),
                                                      accessDataSources);
                              });
        }
    }

    private void addDataSetPerspective() {
        if (hasAccessToPerspective(PerspectiveIds.DATASET_AUTHORING)) {
            adminPage.addTool("root",
                              constants.DataSets(),
                              new Sets.Builder().add("fa").add("fa-folder-open").build(),
                              "perspectives",
                              () -> {
                                  final Command accessDataSets = () -> placeManager.goTo(PerspectiveIds.DATASET_AUTHORING);
                                  accessDataSets.execute();
                                  addAdminBreadcrumbs(PerspectiveIds.DATASET_AUTHORING,
                                                      constants.DataSets(),
                                                      accessDataSets);
                              });
        }
    }

    private void addArchetypesPerspective() {
        if (hasAccessToPerspective(PerspectiveIds.ARCHETYPE_MANAGEMENT)) {
            adminPage.addTool("root",
                              constants.Archetypes(),
                              new Sets.Builder().add("fa").add("fa-file-code-o").build(),
                              ADVANCED_CATEGORY,
                              () -> {
                                  final Command accessArchetypeMgmt = () -> placeManager.goTo(PerspectiveIds.ARCHETYPE_MANAGEMENT);
                                  accessArchetypeMgmt.execute();
                                  addAdminBreadcrumbs(PerspectiveIds.ARCHETYPE_MANAGEMENT,
                                                      constants.Archetypes(),
                                                      accessArchetypeMgmt);
                              });
        }
    }

    private void addGlobalPreferences(boolean libraryPreferencesEnabled,
                                      boolean artifactRepositoryPreferencesEnabled) {
        final boolean canEditGlobalPreferences = authorizationManager.authorize(WorkbenchFeatures.EDIT_GLOBAL_PREFERENCES,
                                                                                sessionInfo.getIdentity());

        if (canEditGlobalPreferences == false) {
            return;
        }

        if (libraryPreferencesEnabled) {
            adminPage.addPreference("root",
                                    "LibraryPreferences",
                                    translationService.format(PreferencesConstants.LibraryPreferences_Title),
                                    new Sets.Builder().add("fa").add("fa-cubes").build(),
                                    "preferences",
                                    scopeFactory.createScope(GuvnorPreferenceScopes.GLOBAL),
                                    AdminPageOptions.WITH_BREADCRUMBS);
        }

        if (artifactRepositoryPreferencesEnabled) {
            adminPage.addPreference("root",
                                    "ArtifactRepositoryPreference",
                                    translationService.format(PreferencesConstants.ArtifactRepositoryPreferences_Title),
                                    new Sets.Builder().add("fa").add("fa-archive").build(),
                                    "preferences",
                                    scopeFactory.createScope(GuvnorPreferenceScopes.GLOBAL),
                                    AdminPageOptions.WITH_BREADCRUMBS);
        }

        adminPage.addPreference("root",
                                "ManagePreferences",
                                constants.ProcessAdministration(),
                                new Sets.Builder().add("pficon").add("pficon-storage-domain").build(),
                                "general",
                                scopeFactory.createScope(GuvnorPreferenceScopes.GLOBAL),
                                AdminPageOptions.WITH_BREADCRUMBS);

        adminPage.addTool("root",
                                constants.CustomTasksAdministration(),
                                new Sets.Builder().add("fa").add("fa-cogs").build(),
                                "services",
                                () -> {
                                    final Command accessServiceTasks = () -> placeManager.goTo(PerspectiveIds.SERVICE_TASK_ADMIN);
                                    accessServiceTasks.execute();
                                    addAdminBreadcrumbs(PerspectiveIds.SERVICE_TASK_ADMIN,
                                                        constants.CustomTasksAdministration(),
                                                        accessServiceTasks);
                                });
    }

    private void addAdminBreadcrumbs(final String perspective,
                                     final String label,
                                     final Command accessCommand) {
        breadcrumbs.clearBreadcrumbs(perspective);
        breadcrumbs.addBreadCrumb(perspective,
                                  constants.Admin(),
                                  new DefaultPlaceRequest(ADMIN),
                                  () -> placeManager.goTo(PerspectiveIds.ADMIN));
        breadcrumbs.addBreadCrumb(perspective,
                                  label,
                                  accessCommand);
    }

    boolean hasAccessToPerspective(final String perspectiveId) {
        ResourceRef resourceRef = new ResourceRef(perspectiveId,
                                                  ActivityResourceType.PERSPECTIVE);
        return authorizationManager.authorize(resourceRef,
                                              sessionInfo.getIdentity());
    }
}

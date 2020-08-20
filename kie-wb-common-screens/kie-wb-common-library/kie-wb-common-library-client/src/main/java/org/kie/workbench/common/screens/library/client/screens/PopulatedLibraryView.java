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
package org.kie.workbench.common.screens.library.client.screens;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.KeyUpEvent;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.widgets.common.TileWidget;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

@Templated
public class PopulatedLibraryView implements PopulatedLibraryScreen.View,
                                             IsElement {

    private PopulatedLibraryScreen presenter;

    @Inject
    private TranslationService ts;

    @Inject
    @DataField("actions")
    Div actions;

    @Inject
    @DataField("project-list")
    Div projectList;

    @Inject
    @DataField("filter-text")
    Input filterText;

    @Override
    public void init(final PopulatedLibraryScreen presenter) {
        this.presenter = presenter;
        filterText.setAttribute("placeholder",
                                ts.getTranslation(LibraryConstants.Search));
    }

    @Override
    public void clearProjects() {
        projectList.setTextContent("");
    }

    @Override
    public void addProject(final TileWidget<WorkspaceProject> project) {
        projectList.appendChild(project.getView().getElement());
    }

    @Override
    public void addProject(TileWidget<WorkspaceProject> tileToAdd, TileWidget<WorkspaceProject> tileAfter) {
        projectList.insertBefore(tileToAdd.getView().getElement(), tileAfter.getView().getElement());
    }

    @Override
    public void removeProject(TileWidget<WorkspaceProject> tile) {
        projectList.removeChild(tile.getView().getElement());
    }

    @Override
    public void addAction(final HTMLElement action) {
        actions.appendChild(action);
    }

    @Override
    public void clearFilterText() {
        this.filterText.setValue("");
    }

    @EventHandler("filter-text")
    public void filterTextChange(final KeyUpEvent event) {
        presenter.filterProjects(filterText.getValue());
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}
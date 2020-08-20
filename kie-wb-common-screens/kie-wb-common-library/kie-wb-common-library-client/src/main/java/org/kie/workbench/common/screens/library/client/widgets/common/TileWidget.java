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

package org.kie.workbench.common.screens.library.client.widgets.common;

import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

public class TileWidget<CONTENT> {

    public interface View extends UberElement<TileWidget> {

        void setup(String label,
                   String description,
                   Command selectCommand);

        boolean isSelected();

        void setSelected(boolean selected);

        void setNumberOfAssets(Integer numberOfAssets);
    }

    private CONTENT content;

    private View view;

    private String label;

    @Inject
    public TileWidget(final View view) {
        this.view = view;
    }

    public void init(final String label,
                     final String description,
                     final Command selectCommand) {

        this.label = label;

        view.init(this);

        view.setup(label,
                   description,
                   selectCommand);

        view.setNumberOfAssets(0);
    }

    public CONTENT getContent() {
        return content;
    }

    public void setContent(CONTENT content) {
        this.content = content;
    }

    public boolean isSelected() {
        return view.isSelected();
    }

    public void setSelected(final boolean selected) {
        view.setSelected(selected);
    }

    public String getLabel() {
        return label;
    }

    public void setNumberOfAssets(Integer numberOfAssets) {
        view.setNumberOfAssets(numberOfAssets);
    }

    public View getView() {
        return view;
    }
}

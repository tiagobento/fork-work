/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions.workitemhandler;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated("#root")
public class WorkItemHandlerListItemView implements WorkItemHandlerListItemPresenter.View {

    @Inject
    @DataField("type")
    private HTMLInputElement type;

    @Inject
    @DataField("name")
    private HTMLInputElement name;

    @Inject
    @Named("span")
    @DataField("remove-button")
    private HTMLElement removeButton;

    private WorkItemHandlerListItemPresenter presenter;

    @Override
    public void init(final WorkItemHandlerListItemPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("remove-button")
    public void onRemoveButtonClicked(final ClickEvent ignore) {
        presenter.remove();
    }

    @EventHandler("type")
    public void onTypeChanged(final ChangeEvent ignore) {
        presenter.setType(type.value);
    }

    @EventHandler("name")
    public void onNameChanged(final ChangeEvent ignore) {
        presenter.setName(name.value);
    }

    @Override
    public void setType(final String type) {
        this.type.value = type;
    }

    @Override
    public void setName(final String name) {
        this.name.value = name;
    }
}

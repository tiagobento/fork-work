/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.sections.persistence.persistabledataobjects;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Event;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated("#root")
public class PersistableDataObjectsItemView implements PersistableDataObjectsItemPresenter.View {

    @Inject
    @DataField("remove-button")
    private HTMLAnchorElement removeButton;

    @Inject
    @DataField("class-name")
    private HTMLInputElement className;

    private PersistableDataObjectsItemPresenter presenter;

    @Override
    public void init(final PersistableDataObjectsItemPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("remove-button")
    public void onRemove(final ClickEvent ignore) {
        presenter.remove();
    }

    @Override
    public void setClassName(final String className) {
        this.className.value = className;
    }

    @EventHandler("class-name")
    public void onClassNameChange(final @ForEvent("change") Event event) {
        presenter.onClassNameChange(className.value);
    }
}

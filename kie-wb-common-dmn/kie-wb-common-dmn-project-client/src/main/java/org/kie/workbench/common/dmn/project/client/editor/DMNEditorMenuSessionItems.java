/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.project.client.editor;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.guvnor.messageconsole.client.console.MessageConsoleScreen;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNPerformAutomaticLayoutCommand;
import org.kie.workbench.common.dmn.project.client.session.DMNEditorSessionCommands;
import org.kie.workbench.common.stunner.client.widgets.menu.MenuUtils;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditorMenuSessionItems;
import org.kie.workbench.common.stunner.kogito.client.editor.ValidationAction;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.model.menu.MenuItem;

@Dependent
@Typed(DMNEditorMenuSessionItems.class)
public class DMNEditorMenuSessionItems extends AbstractDiagramEditorMenuSessionItems<DMNEditorMenuItemsBuilder> {

    private final PlaceManager placeManager;

    @Inject
    public DMNEditorMenuSessionItems(final DMNEditorMenuItemsBuilder itemsBuilder,
                                     final @DMNEditor DMNEditorSessionCommands sessionCommands,
                                     final PlaceManager placeManager) {
        super(itemsBuilder,
              sessionCommands);
        this.placeManager = placeManager;
    }

    @Override
    public void populateMenu(final FileMenuBuilder menu) {
        superPopulateMenu(menu);
        addPerformAutomaticLayout(menu);
    }

    void superPopulateMenu(final FileMenuBuilder menu) {
        super.populateMenu(menu);
    }

    void addPerformAutomaticLayout(final FileMenuBuilder menu) {
        final MenuItem performAutomaticLayoutMenuItem = newPerformAutomaticLayout();
        addMenuItem(DMNPerformAutomaticLayoutCommand.class, performAutomaticLayoutMenuItem);
        menu.addNewTopLevelMenu(performAutomaticLayoutMenuItem);
    }

    MenuItem newPerformAutomaticLayout() {
        final MenuUtils.HasEnabledIsWidget buttonWrapper = MenuUtils.buildHasEnabledWidget(new Button() {{
            setSize(ButtonSize.SMALL);
            setTitle(getTranslationService().getValue(CoreTranslationMessages.PERFORM_AUTOMATIC_LAYOUT));
            setIcon(IconType.SITEMAP);
            addClickHandler(clickEvent -> ((DMNEditorSessionCommands) getCommands()).getPerformAutomaticLayoutCommand().execute());
        }});
        return MenuUtils.buildItem(buttonWrapper);
    }

    @Override
    public void setEnabled(final boolean enabled) {
        superSetEnabled(enabled);
        setItemEnabled(DMNPerformAutomaticLayoutCommand.class, enabled);
    }

    void superSetEnabled(final boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    protected ValidationAction makeValidation() {
        final ValidationAction validationAction = super.makeValidation();

        validationAction.setAfterValidation(() -> placeManager.goTo(MessageConsoleScreen.ALERTS));
        return validationAction;
    }
}

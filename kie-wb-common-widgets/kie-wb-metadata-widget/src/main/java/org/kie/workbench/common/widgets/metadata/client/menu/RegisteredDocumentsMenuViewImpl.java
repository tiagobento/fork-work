/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.metadata.client.menu;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.NodeList;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class RegisteredDocumentsMenuViewImpl implements RegisteredDocumentsMenuView {

    @Inject
    @DataField("view")
    Div view;

    @Inject
    @DataField("registeredDocumentsMenuDropdown")
    Button registeredDocumentsMenuDropdown;

    @Inject
    @DataField("registeredDocumentsViewContainer")
    Div registeredDocumentsViewContainer;

    @Inject
    @DataField("newDocumentMenuButton")
    Button newDocumentMenuButton;

    @Inject
    @DataField("openDocumentMenuButton")
    Button openDocumentMenuButton;

    protected RegisteredDocumentsMenuBuilder presenter;

    @Override
    public void init( final RegisteredDocumentsMenuBuilder presenter ) {
        this.presenter = presenter;
    }

    @Override
    public HTMLElement getElement() {
        return view;
    }

    @Override
    public boolean isEnabled() {
        return !registeredDocumentsMenuDropdown.getDisabled();
    }

    @Override
    public void setEnabled( final boolean enabled ) {
        registeredDocumentsMenuDropdown.setDisabled( !enabled );
    }

    @Override
    public void clear() {
        final NodeList children = registeredDocumentsViewContainer.getChildNodes();
        for ( int i = 0; i < children.getLength(); i++ ) {
            registeredDocumentsViewContainer.removeChild( children.item( i ) );
        }
    }

    @Override
    public void enableNewDocumentButton( final boolean enabled ) {
        newDocumentMenuButton.setDisabled( !enabled );
    }

    @Override
    public void enableOpenDocumentButton( final boolean enabled ) {
        openDocumentMenuButton.setDisabled( !enabled );
    }

    @Override
    public void addDocument( final DocumentMenuItem document ) {
        registeredDocumentsViewContainer.appendChild( document.getElement() );
    }

    @Override
    public void deleteDocument( final DocumentMenuItem document ) {
        registeredDocumentsViewContainer.removeChild( document.getElement() );
    }

    @Override
    public void setReadOnly( final boolean isReadOnly ) {
        openDocumentMenuButton.setDisabled( isReadOnly );
    }

    @SuppressWarnings("unused")
    @EventHandler("newDocumentMenuButton")
    public void onClickNewDocumentButton( final ClickEvent e ) {
        presenter.onNewDocument();
    }

    @SuppressWarnings("unused")
    @EventHandler("openDocumentMenuButton")
    public void onClickOpenDocumentButton( final ClickEvent e ) {
        presenter.onOpenDocument();
    }

}

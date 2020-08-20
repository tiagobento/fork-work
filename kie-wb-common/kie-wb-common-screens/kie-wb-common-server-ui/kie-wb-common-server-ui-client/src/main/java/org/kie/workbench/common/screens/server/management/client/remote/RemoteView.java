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

package org.kie.workbench.common.screens.server.management.client.remote;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.server.management.client.remote.empty.RemoteEmptyPresenter;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.client.widget.Div;

@Dependent
@Templated
public class RemoteView extends Composite
        implements RemotePresenter.View {

    private RemotePresenter presenter;

    private TranslationService translationService;

    @Inject
    @DataField("remote-content")
    Div remoteContent;

    @Inject
    @DataField
    Button refresh;

    @Inject
    @DataField
    Button remove;

    @DataField("server-name")
    Heading serverName = new Heading( HeadingSize.H3 );

    @Inject
    @DataField
    Anchor url;

    @Inject
    public RemoteView( final TranslationService translationService ) {
        super();
        this.translationService = translationService;
    }

    @Override
    public void init( final RemotePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setServerName( final String serverName ) {
        this.serverName.setText( serverName );
    }

    @Override
    public void setServerURL( final String url ) {
        this.url.setHref( url );
        this.url.setText( url );
    }

    @Override
    public void setEmptyView( final RemoteEmptyPresenter.View view ) {
        clear();
        remoteContent.add( view.asWidget() );
    }

    @Override
    public void setStatusPresenter( final RemoteStatusPresenter.View view ) {
        clear();
        remoteContent.add( view.asWidget() );
    }

    @Override
    public void clear() {
        remoteContent.clear();
    }

    @EventHandler("refresh")
    public void onRefresh( final ClickEvent event ) {
        presenter.refresh();
    }

    @EventHandler("remove")
    public void onRemove( final ClickEvent event ) {
        presenter.remove();
    }

    @Override
    public String getRemoteInstanceRemoveSuccessMessage() {
        return translationService.format( Constants.RemoteView_RemoteInstanceRemoveSuccess );
    }

    @Override
    public String getRemoteInstanceRemoveErrorMessage() {
        return translationService.format( Constants.RemoteView_RemoteInstanceRemoveError );
    }

}

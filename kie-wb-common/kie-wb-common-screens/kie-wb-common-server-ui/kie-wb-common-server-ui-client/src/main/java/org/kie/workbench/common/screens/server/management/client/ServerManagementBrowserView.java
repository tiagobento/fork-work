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

package org.kie.workbench.common.screens.server.management.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.server.management.client.empty.ServerEmptyPresenter;
import org.kie.workbench.common.screens.server.management.client.navigation.ServerNavigationPresenter;
import org.kie.workbench.common.screens.server.management.client.navigation.template.ServerTemplatePresenter;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.client.util.ClientContainerRuntimeOperation;
import org.kie.workbench.common.screens.server.management.client.widget.Div;

@Dependent
@Templated
public class ServerManagementBrowserView extends Composite
        implements ServerManagementBrowserPresenter.View {

    @Inject
    @DataField
    Div container;

    @Inject
    @DataField("first-nav")
    Div firstLevelNavigation;

    @Inject
    @DataField("second-nav")
    Div secondLevalNavigation;

    @Inject
    @DataField
    Div content;

    @Inject
    private TranslationService translationService;

    @PostConstruct
    public void init() {
        container.clear();
    }

    @Override
    public void setNavigation( final ServerNavigationPresenter.View view ) {
        container.add( firstLevelNavigation );
        firstLevelNavigation.clear();
        firstLevelNavigation.add( view.asWidget() );
    }

    @Override
    public void setServerTemplate( final ServerTemplatePresenter.View view ) {
        content.getElement().removeClassName( "col-md-10" );
        content.getElement().removeClassName( "col-sm-9" );
        content.getElement().addClassName( "col-md-8" );
        content.getElement().addClassName( "col-sm-6" );

        if ( secondLevalNavigation.getParent() == null ||
                !secondLevalNavigation.getParent().equals( container ) ) {
            boolean isEmpty = container.getWidgetCount() == 2;
            if ( isEmpty ) {
                container.remove( 1 );
            }
            container.add( secondLevalNavigation );
            if ( isEmpty ) {
                container.add( content );
            }
        }
        secondLevalNavigation.clear();
        secondLevalNavigation.add( view.asWidget() );
    }

    @Override
    public void setEmptyView( final ServerEmptyPresenter.View view ) {
        content.getElement().removeClassName( "col-md-8" );
        content.getElement().removeClassName( "col-sm-6" );
        content.getElement().addClassName( "col-md-10" );
        content.getElement().addClassName( "col-sm-9" );
        container.remove( secondLevalNavigation );
        content.clear();
        content.add( view.asWidget() );

        if ( content.getParent() == null ||
                !content.getParent().equals( container ) ) {
            container.add( content );
        }
    }

    @Override
    public void setContent( final IsWidget view ) {
        content.clear();
        content.add( view );
        if ( content.getParent() == null ||
                !content.getParent().equals( container ) ) {
            container.add( content );
        }
    }

    @Override
    public String getSuccessMessage( final ClientContainerRuntimeOperation containerRuntimeOperation,
                                     final int size ) {
        return translationService.format( Constants.ContainerUpdateEvent_SuccessMessage,
                                          containerRuntimeOperation.getValue( translationService ),
                                          size );
    }

    @Override
    public String getErrorMessage( final ClientContainerRuntimeOperation containerRuntimeOperation,
                                   final int size ) {
        return translationService.format( Constants.ContainerUpdateEvent_ErrorMessage,
                                          containerRuntimeOperation.getValue( translationService ),
                                          size );
    }

    @Override
    public String getWarnMessage( final ClientContainerRuntimeOperation containerRuntimeOperation,
                                  final int size ) {
        return translationService.format( Constants.ContainerUpdateEvent_WarnMessage,
                                          containerRuntimeOperation.getValue( translationService ),
                                          size );
    }
}

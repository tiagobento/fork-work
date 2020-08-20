/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.wizard;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.client.ArtifactIdChangeHandler;
import org.guvnor.common.services.project.client.GAVEditor;
import org.guvnor.common.services.project.client.GAVEditorView;
import org.guvnor.common.services.project.client.GroupIdChangeHandler;
import org.guvnor.common.services.project.client.NameChangeHandler;
import org.guvnor.common.services.project.client.POMEditorPanel;
import org.guvnor.common.services.project.client.POMEditorPanelView;
import org.guvnor.common.services.project.client.VersionChangeHandler;
import org.guvnor.common.services.project.client.preferences.ProjectScopedResolutionStrategySupplier;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.preferences.GAVPreferences;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class POMWizardPageTest {

    @Mock
    private POMEditorPanelView pomEditorView;

    private GAVEditor gavEditor;

    @Mock
    private GAVEditorView gavEditorView;

    @Mock
    private SyncBeanManager syncBeanManager;

    @Mock
    private POMWizardPageView view;

    @Mock
    private ProjectScreenService projectScreenService;

    @Mock
    private ValidationService validationService;

    @Mock
    private GAVPreferences gavPreferences;

    @Mock
    protected ProjectScopedResolutionStrategySupplier projectScopedResolutionStrategySupplier;

    @Mock
    Event event;

    @Captor
    ArgumentCaptor<NameChangeHandler> nameChangeCaptor;

    @Captor
    ArgumentCaptor<VersionChangeHandler> versionChangeCaptor;

    @Captor
    ArgumentCaptor<GroupIdChangeHandler> groupChangeCaptor;

    @Captor
    ArgumentCaptor<ArtifactIdChangeHandler> artifactChangeCaptor;

    private POMWizardPage page;
    private POMEditorPanel pomEditor;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {

        gavEditor = new GAVEditor( gavEditorView );

        pomEditor = spy( new POMEditorPanel( pomEditorView,
                                             syncBeanManager,
                                             gavPreferences,
                                             projectScopedResolutionStrategySupplier ) );

        //POMEditorView implementation updates a nested GAVEditor presenter. Mock the implementation to avoid use of real widgets
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( final InvocationOnMock invocation ) throws Throwable {
                final String artifactId = (String) invocation.getArguments()[ 0 ];
                gavEditor.setArtifactID( artifactId );
                return null;
            }
        } ).when( pomEditorView ).setArtifactID( any( String.class ) );

        //POMEditorView implementation updates a nested GAVEditor presenter. Mock the implementation to avoid use of real widgets
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( final InvocationOnMock invocation ) throws Throwable {
                final GAV gav = (GAV) invocation.getArguments()[ 0 ];
                gavEditor.setGAV( gav );
                return null;
            }
        } ).when( pomEditorView ).setGAV( any( GAV.class ) );

        //POMEditorView implementation updates a nested GAVEditor presenter. Mock the implementation to avoid use of real widgets
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( final InvocationOnMock invocation ) throws Throwable {
                final ArtifactIdChangeHandler handler = (ArtifactIdChangeHandler) invocation.getArguments()[ 0 ];
                gavEditor.addArtifactIdChangeHandler( handler );
                return null;
            }
        } ).when( pomEditorView ).addArtifactIdChangeHandler( any( ArtifactIdChangeHandler.class ) );

        page = spy( new POMWizardPage( pomEditor,
                                       view,
                                       event,
                                       new CallerMock<ProjectScreenService>( projectScreenService ),
                                       new CallerMock<ValidationService>( validationService ) ) );
        page.initialise();

        doAnswer( invocationOnMock -> {
            ( (ParameterizedCommand<GAVPreferences>) invocationOnMock.getArguments()[1] ).execute( gavPreferences );
            return null;
        } ).when( gavPreferences ).load( any( PreferenceScopeResolutionStrategyInfo.class ), any( ParameterizedCommand.class ), any( ParameterizedCommand.class ) );
    }

    @Test
    public void testNameChangeHandlersRegistered() {
        when( pomEditor.getPom() ).thenReturn( new POM( "name", "description","url",  new GAV( "g", "a", "v" ) ) );
        doNothing().when( pomEditor ).setArtifactID( anyString() );

        verify( pomEditor, times( 1 ) ).addNameChangeHandler( nameChangeCaptor.capture() );
        nameChangeCaptor.getValue().onChange( "any new value" );
        verify( event ).fire( any( WizardPageStatusChangeEvent.class ) );
    }

    @Test
    public void testVersionChangeHandlersRegistered() {
        when( pomEditor.getPom() ).thenReturn( new POM( "name", "description", "url", new GAV( "g", "a", "v" ) ) );

        verify( pomEditor, times( 1 ) ).addVersionChangeHandler( versionChangeCaptor.capture() );
        versionChangeCaptor.getValue().onChange( "any new value" );
        verify( event ).fire( any( WizardPageStatusChangeEvent.class ) );
    }

    @Test
    public void testGroupChangeHandlersRegistered() {
        when( pomEditor.getPom() ).thenReturn( new POM( "name", "description","url", new GAV( "g", "a", "v" ) ) );

        verify( pomEditor, times( 1 ) ).addGroupIdChangeHandler( groupChangeCaptor.capture() );
        groupChangeCaptor.getValue().onChange( "any new value" );
        verify( event ).fire( any( WizardPageStatusChangeEvent.class ) );
    }

    @Test
    public void testArtifactChangeHandlersRegistered() {
        when( pomEditor.getPom() ).thenReturn( new POM( "name", "description","url", new GAV( "g", "a", "v" ) ) );

        verify( pomEditor, times( 1 ) ).addArtifactIdChangeHandler( artifactChangeCaptor.capture() );
        artifactChangeCaptor.getValue().onChange( "any new value" );
        verify( event ).fire( any( WizardPageStatusChangeEvent.class ) );
    }

    @Test
    public void testInvalidPOMWithParent() throws Exception {
        mockValidationOfPom( false );
        POM pom = new POM();
        pom.setParent( new GAV() );
        page.setPom( pom );

        verify( page,
                times( 1 ) ).validateName( any( String.class ) );
        verify( page,
                times( 1 ) ).validateGroupId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateArtifactId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateVersion( any( String.class ) );

        verify( pomEditor,
                times( 1 ) ).setValidName( eq( false ) );
        verify( pomEditor,
                times( 1 ) ).setValidGroupID( eq( false ) );
        verify( pomEditor,
                times( 1 ) ).setValidArtifactID( eq( false ) );
        verify( pomEditor,
                times( 1 ) ).setValidVersion( eq( false ) );
    }

    @Test
    public void testPomsWithParentDataDisableFieldsParentNotSet() throws Exception {
        page.setPom( new POM() );

        verify( pomEditor, never() ).disableGroupID( anyString() );
        verify( pomEditor, never() ).disableVersion( anyString() );
    }

    @Test
    public void testInvalidPOMWithoutParent() throws Exception {
        mockValidationOfPom( false );
        page.setPom( new POM() );

        verify( page,
                times( 1 ) ).validateName( any( String.class ) );
        verify( page,
                times( 1 ) ).validateGroupId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateArtifactId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateVersion( any( String.class ) );

        verify( pomEditor,
                times( 1 ) ).setValidName( eq( false ) );
        verify( pomEditor,
                times( 1 ) ).setValidGroupID( eq( false ) );
        verify( pomEditor,
                times( 1 ) ).setValidArtifactID( eq( false ) );
        verify( pomEditor,
                times( 1 ) ).setValidVersion( eq( false ) );
    }

    @Test
    public void testValidPOMWithParent() throws Exception {
        mockValidationOfPom( true );

        POM pom = new POM();
        pom.setParent( new GAV() );
        page.setPom( pom );

        verify( page,
                times( 1 ) ).validateName( any( String.class ) );
        verify( page,
                times( 1 ) ).validateGroupId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateArtifactId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateVersion( any( String.class ) );

        verify( pomEditor,
                times( 1 ) ).setValidName( eq( true ) );
        verify( pomEditor,
                times( 1 ) ).setValidGroupID( eq( true ) );
        verify( pomEditor,
                times( 1 ) ).setValidArtifactID( eq( true ) );
        verify( pomEditor,
                times( 1 ) ).setValidVersion( eq( true ) );
    }

    @Test
    public void testValidPOMWithoutParent() throws Exception {
        mockValidationOfPom( true );
        page.setPom( new POM() );

        verify( page,
                times( 1 ) ).validateName( any( String.class ) );
        verify( page,
                times( 1 ) ).validateGroupId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateArtifactId( any( String.class ) );
        verify( page,
                times( 1 ) ).validateVersion( any( String.class ) );

        verify( pomEditor,
                times( 1 ) ).setValidName( eq( true ) );
        verify( pomEditor,
                times( 1 ) ).setValidGroupID( eq( true ) );
        verify( pomEditor,
                times( 1 ) ).setValidArtifactID( eq( true ) );
        verify( pomEditor,
                times( 1 ) ).setValidVersion( eq( true ) );
    }

    @Test
    public void testSetNameValidArtifactID() {
        mockValidationOfPom( true );

        //POMEditorView implementation updates a nested GAVEditor presenter. Mock the implementation to avoid use of real widgets
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( final InvocationOnMock invocation ) throws Throwable {
                final String artifactId = (String) invocation.getArguments()[ 0 ];
                gavEditor.setArtifactID( artifactId );
                return null;
            }
        } ).when( pomEditorView ).setArtifactID( any( String.class ) );

        //POMEditorView implementation updates a nested GAVEditor presenter. Mock the implementation to avoid use of real widgets
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( final InvocationOnMock invocation ) throws Throwable {
                final GAV gav = (GAV) invocation.getArguments()[ 0 ];
                gavEditor.setGAV( gav );
                return null;
            }
        } ).when( pomEditorView ).setGAV( any( GAV.class ) );

        final POM pom = new POM();
        page.setPom( pom );
        verify( validationService,
                times( 1 ) ).isProjectNameValid( null );
        verify( validationService,
                times( 1 ) ).validateArtifactId( null );

        pomEditor.onNameChange( "project-name" );

        verify( pomEditorView,
                times( 1 ) ).setArtifactID( eq( "project-name" ) );
        verify( validationService,
                times( 1 ) ).isProjectNameValid( eq( "project-name" ) );
        verify( validationService,
                times( 1 ) ).validateArtifactId( eq( "project-name" ) );

        assertEquals( "project-name",
                      pom.getName() );
        assertEquals( "project-name",
                      pom.getGav().getArtifactId() );
    }

    @Test
    public void testSetNameInvalidArtifactID() {
        mockValidationOfPom( true );

        final POM pom = new POM();
        page.setPom( pom );
        verify( validationService,
                times( 1 ) ).isProjectNameValid( null );
        verify( validationService,
                times( 1 ) ).validateArtifactId( null );

        pomEditor.onNameChange( "project-name!" );

        verify( pomEditorView,
                times( 1 ) ).setArtifactID( eq( "project-name" ) );
        verify( validationService,
                times( 1 ) ).isProjectNameValid( eq( "project-name!" ) );
        verify( validationService,
                times( 1 ) ).validateArtifactId( eq( "project-name" ) );

        assertEquals( "project-name!",
                      pom.getName() );
        assertEquals( "project-name",
                      pom.getGav().getArtifactId() );
    }

    @Test
    public void testSetNameValidArtifactIDUserChanged() {
        mockValidationOfPom( true );

        final POM pom = new POM();
        page.setPom( pom );
        verify( validationService,
                times( 1 ) ).isProjectNameValid( null );
        verify( validationService,
                times( 1 ) ).validateArtifactId( null );

        gavEditor.onArtifactIdChange( "artifactId" );
        pomEditor.onNameChange( "project-name" );

        verify( pomEditorView,
                never() ).setArtifactID( eq( "project-name" ) );
        verify( validationService,
                times( 1 ) ).isProjectNameValid( eq( "project-name" ) );
        verify( validationService,
                times( 1 ) ).validateArtifactId( eq( "artifactId" ) );

        assertEquals( "project-name",
                      pom.getName() );
        assertEquals( "artifactId",
                      pom.getGav().getArtifactId() );
    }

    @Test
    public void testSetNameValidArtifactIDUserChangedThenRevert() {
        mockValidationOfPom( true );

        final POM pom = new POM();
        page.setPom( pom );
        verify( validationService,
                times( 1 ) ).isProjectNameValid( null );
        verify( validationService,
                times( 1 ) ).validateArtifactId( null );

        //Simulate the User change the Artifact ID manually
        gavEditor.onArtifactIdChange( "artifactId" );
        pomEditor.onNameChange( "project-name" );

        verify( pomEditorView,
                never() ).setArtifactID( eq( "project-name" ) );
        verify( validationService,
                times( 1 ) ).isProjectNameValid( eq( "project-name" ) );
        verify( validationService,
                times( 1 ) ).validateArtifactId( eq( "artifactId" ) );

        assertEquals( "project-name",
                      pom.getName() );
        assertEquals( "artifactId",
                      pom.getGav().getArtifactId() );

        //Revert change to Artifact ID
        gavEditor.onArtifactIdChange( "" );
        pomEditor.onNameChange( "project-name" );

        verify( pomEditorView,
                times( 1 ) ).setArtifactID( eq( "project-name" ) );
        verify( validationService,
                times( 2 ) ).isProjectNameValid( eq( "project-name" ) );
        verify( validationService,
                times( 1 ) ).validateArtifactId( eq( "artifactId" ) );

        assertEquals( "project-name",
                      pom.getName() );
        assertEquals( "project-name",
                      pom.getGav().getArtifactId() );

    }

    @Test
    public void testIsComplete() {
        when( validationService.validate( any( POM.class ) ) ).thenReturn( true );
        Callback<Boolean> callback = mock( Callback.class );
        page.isComplete( callback );
        verify( callback, times( 1 ) ).callback( true );
    }

    @Test
    public void testIsNotComplete() {
        when( validationService.validate( any( POM.class ) ) ).thenReturn( false );
        Callback<Boolean> callback = mock( Callback.class );
        page.isComplete( callback );
        verify( callback, times( 1 ) ).callback( false );
    }

    private void mockValidationOfPom( boolean isValid ) {
        when( validationService.validateGroupId( any( String.class ) ) ).thenReturn( isValid );
        when( validationService.validateArtifactId( any( String.class ) ) ).thenReturn( isValid );
        when( validationService.validateGAVVersion( any( String.class ) ) ).thenReturn( isValid );
        when( validationService.isProjectNameValid( any( String.class ) ) ).thenReturn( isValid );
    }
}

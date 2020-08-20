/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.SpacesScreenService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SpacesScreenServiceImplTest {

    @Mock
    private OrganizationalUnitService organizationalUnitService;

    private SessionInfo sessionInfo;

    private SpacesScreenServiceImpl spacesScreenService;

    @Before
    public void before() {
        sessionInfo = new SessionInfoMock();
        spacesScreenService = spy(new SpacesScreenServiceImpl(organizationalUnitService,
                                                              sessionInfo));
    }

    @Test
    public void testGetSpaces() {
        final Collection<OrganizationalUnit> spaces = Arrays.asList(new OrganizationalUnitImpl("foo", "org.foo"), new OrganizationalUnitImpl("bar", "org.bar"));
        doReturn(spaces).when(organizationalUnitService).getOrganizationalUnits(true);
        assertEquals(spaces, spacesScreenService.getSpaces());
    }

    @Test
    public void testGetSpace() {
        final OrganizationalUnitImpl space = new OrganizationalUnitImpl("test", "org.test");
        doReturn(space).when(organizationalUnitService).getOrganizationalUnit("test", true);
        assertEquals(space, spacesScreenService.getSpace("test"));
    }

    @Test
    public void testSaveSpace() {

        final SpacesScreenService.NewSpace space = new SpacesScreenService.NewSpace() {{
            name = "test";
            groupId = "com.test";
            description="Test space";
        }};

        final List<Contributor> contributors = new ArrayList<>();
        contributors.add(new Contributor(sessionInfo.getIdentity().getIdentifier(), ContributorType.OWNER));

        assertEquals(201, spacesScreenService.postSpace(space).getStatus());
        verify(organizationalUnitService).createOrganizationalUnit("test", "com.test", new ArrayList<>(), contributors, "Test space");
    }

    @Test
    public void testIsValidGroupId() {
        doReturn(true).when(organizationalUnitService).isValidGroupId("org.foo");
        assertTrue(spacesScreenService.isValidGroupId("org.foo"));
    }

    @Test
    public void testIsValidGroupIdFalse() {
        doReturn(false).when(organizationalUnitService).isValidGroupId("org.foo");
        assertFalse(spacesScreenService.isValidGroupId("org.foo"));
    }
}
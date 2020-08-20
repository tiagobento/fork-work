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

package org.kie.workbench.common.stunner.bpmn.project.backend.forms.conditions;

import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.ConditionEditorService;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

public class ConditionEditorServiceImplTest
        extends ConditionEditorServiceBaseTest {

    @Mock
    private KieModuleService moduleService;

    @Mock
    private ModuleClassLoaderHelper moduleClassLoaderHelper;

    @Mock
    private KieModule module;

    @Override
    public void setUp() {
        super.setUp();
        when(moduleService.resolveModule(path)).thenReturn(module);
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        when(moduleClassLoaderHelper.getModuleClassLoader(module)).thenReturn(classLoader);
    }

    @Override
    protected ConditionEditorService createService() {
        return new ConditionEditorServiceImpl(moduleService, moduleClassLoaderHelper);
    }
}

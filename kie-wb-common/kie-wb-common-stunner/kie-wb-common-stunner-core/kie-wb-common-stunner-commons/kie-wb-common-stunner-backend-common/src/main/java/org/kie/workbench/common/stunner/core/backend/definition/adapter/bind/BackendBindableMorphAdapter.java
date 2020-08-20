/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.backend.definition.adapter.bind;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.BindableMorphAdapter;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManager;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinitionProvider;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@Dependent
public class BackendBindableMorphAdapter<S> extends BindableMorphAdapter<S> {

    Instance<MorphDefinitionProvider> morphDefinitionInstances;

    @Inject
    public BackendBindableMorphAdapter(final DefinitionUtils definitionUtils,
                                       final FactoryManager factoryManager,
                                       final CloneManager cloneManager,
                                       final Instance<MorphDefinitionProvider> morphDefinitionInstances) {
        super(definitionUtils,
              factoryManager,
              cloneManager);
        this.morphDefinitionInstances = morphDefinitionInstances;
    }

    public BackendBindableMorphAdapter(final DefinitionUtils definitionUtils,
                                       final FactoryManager factoryManager,
                                       final CloneManager cloneManager,
                                       final Collection<MorphDefinition> morphDefinitions1) {
        super(definitionUtils,
              factoryManager,
              cloneManager);
        morphDefinitions.addAll(morphDefinitions1);
    }

    @PostConstruct
    public void init() {
        initMorphDefinitions();
    }

    private void initMorphDefinitions() {
        if (null != morphDefinitionInstances) {
            for (MorphDefinitionProvider morphDefinitionProvider : morphDefinitionInstances) {
                morphDefinitions.addAll(morphDefinitionProvider.getMorphDefinitions());
            }
        }
    }

    @Override
    public boolean accepts(final Class<?> type) {
        return true;
    }
}

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

package org.kie.workbench.common.stunner.core.client.definition.adapter.binding;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.AbstractBindableDefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionAdapter;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

class ClientBindableDefinitionAdapter
        extends AbstractBindableDefinitionAdapter<Object>
        implements BindableDefinitionAdapter<Object> {

    private StunnerTranslationService translationService;

    ClientBindableDefinitionAdapter(final DefinitionUtils definitionUtils,
                                    StunnerTranslationService translationService) {
        super(definitionUtils);
        this.translationService = translationService;
    }

    @Override
    public String getCategory(final Object pojo) {
        return getProxiedValue(pojo,
                               getPropertyCategoryFieldNames().get(pojo.getClass()));
    }

    @Override
    public String getTitle(final Object pojo) {
        String title = translationService.getDefinitionTitle(pojo.getClass().getName());
        if (title != null) {
            return title;
        }
        return getProxiedValue(pojo,
                               getPropertyTitleFieldNames().get(pojo.getClass()));
    }

    @Override
    public Optional<String> getNameField(Object pojo) {
        return Optional.ofNullable(propertyNameFields.get(pojo.getClass()));
    }

    @Override
    public String getDescription(final Object pojo) {
        String description = translationService.getDefinitionDescription(pojo.getClass().getName());
        if (description != null) {
            return description;
        }
        return getProxiedValue(pojo,
                               getPropertyDescriptionFieldNames().get(pojo.getClass()));
    }

    @Override
    public Set<String> getLabels(final Object pojo) {
        final String fName = getPropertyLabelsFieldNames().get(pojo.getClass());
        final Set<String> labels = getProxiedValue(pojo,
                                                   fName);
        return null != labels ? labels : Collections.emptySet();
    }

    @Override
    public Set<?> getPropertySets(final Object pojo) {
        return getProxiedSet(pojo,
                             getPropertySetsFieldNames().get(pojo.getClass()));
    }

    @Override
    protected Set<?> getBindProperties(final Object pojo) {
        return getProxiedSet(pojo,
                             getPropertiesFieldNames().get(pojo.getClass()));
    }

    @Override
    public Optional<?> getProperty(Object pojo, String propertyName) {
        return Stream.concat(Optional.ofNullable(getPropertiesFieldNames().get(pojo.getClass()))
                                     .orElse(Collections.emptySet()).stream(),
                             Optional.ofNullable(getPropertySetsFieldNames().get(pojo.getClass()))
                                     .orElse(Collections.emptySet()).stream())
                .filter(name -> Objects.equals(name, propertyName))
                .findFirst()
                .map(prop -> getProxiedValue(pojo, prop));
    }

    @Override
    protected String getStringFieldValue(final Object pojo,
                                         final String fieldName) {
        return getProxiedValue(pojo,
                               fieldName);
    }

    private <T, R> R getProxiedValue(final T pojo,
                                     final String fieldName) {
        return ClientBindingUtils.getProxiedValue(pojo,
                                                  fieldName);
    }

    private <T, R> Set<R> getProxiedSet(final T pojo,
                                        final Collection<String> fieldNames) {
        return ClientBindingUtils.getProxiedSet(pojo,
                                                fieldNames);
    }
}

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

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.AbstractBindableDefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionAdapter;
import org.kie.workbench.common.stunner.core.graph.util.Exceptions;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.workbench.common.stunner.core.backend.definition.adapter.ReflectionAdapterUtils.getFieldValue;
import static org.kie.workbench.common.stunner.core.backend.definition.adapter.ReflectionAdapterUtils.getFieldValues;

// TODO: Handle meta-properties
// TODO: Handle i18n bundle.

class BackendBindableDefinitionAdapter<T>
        extends AbstractBindableDefinitionAdapter<T>
        implements BindableDefinitionAdapter<T> {

    private static final Logger LOG = LoggerFactory.getLogger(BackendBindableDefinitionAdapter.class);

    BackendBindableDefinitionAdapter(final DefinitionUtils definitionUtils) {
        super(definitionUtils);
    }

    @Override
    public String getCategory(final T definition) {
        Class<?> type = definition.getClass();
        try {
            return getFieldValue(definition,
                                 propertyCategoryFieldNames.get(type));
        } catch (IllegalAccessException e) {
            LOG.error("Error obtaining category for Definition with id " + getId(definition));
        }
        return null;
    }

    @Override
    public String getTitle(final T definition) {
        Class<?> type = definition.getClass();
        try {
            return getFieldValue(definition,
                                 propertyTitleFieldNames.get(type));
        } catch (IllegalAccessException e) {
            LOG.error("Error obtaining title for Definition with id " + getId(definition));
        }
        return null;
    }

    @Override
    public Optional<String> getNameField(T definition) {
        return Exceptions
                .swallow(() -> Optional.ofNullable(getFieldValue(definition,
                                                                 propertyNameFields.get(definition.getClass()))),
                         Optional.empty());
    }

    @Override
    public String getDescription(final T definition) {
        Class<?> type = definition.getClass();
        try {
            return getFieldValue(definition,
                                 propertyDescriptionFieldNames.get(type));
        } catch (IllegalAccessException e) {
            LOG.error("Error obtaining description for Definition with id " + getId(definition));
        }
        return null;
    }

    @Override
    public Set<String> getLabels(final T definition) {
        Class<?> type = definition.getClass();
        try {
            return getFieldValue(definition,
                                 propertyLabelsFieldNames.get(type));
        } catch (IllegalAccessException e) {
            LOG.error("Error obtaining labels for Definition with id " + getId(definition));
        }
        return Collections.emptySet();
    }

    @Override
    public Set<?> getPropertySets(final T definition) {
        Class<?> type = definition.getClass();
        Set<String> fields = propertySetsFieldNames.get(type);
        try {
            return getFieldValues(definition,
                                  fields);
        } catch (IllegalAccessException e) {
            LOG.error("Error obtaining property sets for Definition with id " + getId(definition));
        }
        return Collections.emptySet();
    }

    @Override
    protected Set<?> getBindProperties(final T definition) {
        Class<?> type = definition.getClass();
        Set<String> fields = propertiesFieldNames.get(type);
        try {
            if (null != fields) {
                return getFieldValues(definition,
                                      fields);
            }
        } catch (IllegalAccessException e) {
            LOG.error("Error obtaining properties for Definition with id " + getId(definition));
        }
        return Collections.emptySet();
    }

    @Override
    public Optional<?> getProperty(T definition, String propertyName) {
        return Optional.ofNullable(propertiesFieldNames.get(definition.getClass()))
                .orElse(Collections.emptySet())
                .stream()
                .filter(name -> Objects.equals(name, propertyName))
                .findFirst()
                .map(prop -> Exceptions.swallow(() -> getFieldValue(definition, prop), null));
    }

    @Override
    protected String getStringFieldValue(final Object pojo,
                                         final String fieldName) {
        try {
            return getFieldValue(pojo,
                                 fieldName);
        } catch (IllegalAccessException e) {
            LOG.error("Error obtaining the field [" + fieldName + "] value for type [" + pojo.getClass() + "]");
        }
        return null;
    }
}

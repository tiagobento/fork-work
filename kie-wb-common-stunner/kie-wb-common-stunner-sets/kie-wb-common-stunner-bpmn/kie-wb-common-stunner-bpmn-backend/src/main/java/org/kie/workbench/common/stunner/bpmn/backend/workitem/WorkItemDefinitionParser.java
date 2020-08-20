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

package org.kie.workbench.common.stunner.bpmn.backend.workitem;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.guvnor.common.services.project.model.Dependencies;
import org.guvnor.common.services.project.model.Dependency;
import org.jbpm.process.core.ParameterDefinition;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.datatype.impl.type.EnumDataType;
import org.jbpm.process.core.impl.ParameterDefinitionImpl;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.jbpm.util.WidMVELEvaluator;
import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNCategories;
import org.kie.workbench.common.stunner.bpmn.workitem.IconDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.core.backend.util.URLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.workbench.common.stunner.core.util.StringUtils.nonEmpty;

public class WorkItemDefinitionParser {

    private static final Logger LOG = LoggerFactory.getLogger(WorkItemDefinitionParser.class.getName());

    public static final Map<Class<?>, Function<Object, String>> DATA_TYPE_FORMATTERS =
            new Maps.Builder<Class<?>, Function<Object, String>>()
                    .put(String.class, value -> value.toString().trim().length() > 0 ? value.toString() : null)
                    .put(EnumDataType.class, Object::toString).build();

    public static final String ENCODING = StandardCharsets.UTF_8.name();
    private static final Pattern UNICODE_WORDS_PATTERN = Pattern.compile("\\p{L}+",
                                                                         Pattern.UNICODE_CHARACTER_CLASS);

    public static Collection<WorkItemDefinition> parse(final String content,
                                                       final Function<WorkDefinitionImpl, String> uriProvider,
                                                       final Function<String, String> dataUriProvider) throws Exception {
        final Map<String, WorkDefinitionImpl> definitionMap = parseJBPMWorkItemDefinitions(content,
                                                                                           dataUriProvider);
        return definitionMap.values().stream()
                .map(wid -> parse(wid,
                                  uriProvider,
                                  dataUriProvider))
                .collect(Collectors.toList());
    }

    public static WorkItemDefinition parse(final WorkDefinitionImpl workDefinition,
                                           final Function<WorkDefinitionImpl, String> uriProvider,
                                           final Function<String, String> dataUriProvider) {
        final WorkItemDefinition workItem = new WorkItemDefinition();
        // Attributes..
        workItem.setUri(uriProvider.apply(workDefinition));
        workItem.setName(workDefinition.getName());
        workItem.setCategory(workDefinition.getCategory());
        workItem.setDocumentation(workDefinition.getDocumentation());
        workItem.setDescription(workDefinition.getDescription());
        workItem.setDefaultHandler(workDefinition.getDefaultHandler());
        workItem.setDisplayName(workDefinition.getDisplayName());
        // Icon.
        final IconDefinition iconDefinition = new IconDefinition();
        final String iconEncoded = workDefinition.getIconEncoded();
        final String icon = workDefinition.getIcon();
        String iconData = null;
        if (null != iconEncoded && iconEncoded.trim().length() > 0) {
            iconData = iconEncoded;
        } else if (null != icon && icon.trim().length() > 0) {
            final String iconUrl = workDefinition.getPath() + "/" + icon;
            iconData = dataUriProvider.apply(iconUrl);
        }
        iconDefinition.setUri(icon);
        iconDefinition.setIconData(iconData);
        workItem.setIconDefinition(iconDefinition);
        // Parameters.
        workItem.setParameters(parseParameters(workDefinition.getParameters()));
        // Results.
        workItem.setResults(parseParameters(workDefinition.getResults()));
        // Dependencies.
        final String[] dependencies = workDefinition.getMavenDependencies();
        final List<Dependency> dependencyList = null == dependencies ?
                Collections.emptyList() :
                Stream.of(dependencies)
                        .map(WorkItemDefinitionParser::parseDependency)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
        workItem.setDependencies(new Dependencies(dependencyList));
        return workItem;
    }

    private static Dependency parseDependency(final String raw) {
        final String[] gav = raw.split("\\s*:\\s*");
        Dependency result = null;
        if (gav.length >= 3) {
            result = new Dependency();
            result.setGroupId(gav[0]);
            result.setArtifactId(gav[1]);
            result.setVersion(gav[2]);
        }
        if (gav.length >= 4) {
            result.setScope(gav[3]);
        }
        return result;
    }

    private static String parseParameters(final Collection<ParameterDefinition> parameters) {
        return "|" + parameters.stream()
                .map(param -> param.getName() + ":" + param.getType().getStringType())
                .sorted(String::compareTo)
                .collect(Collectors.joining(",")) + "|";
    }

    @SuppressWarnings("unchecked")
    private static Map<String, WorkDefinitionImpl> parseJBPMWorkItemDefinitions(final String content,
                                                                                final Function<String, String> dataUriProvider) {
        final List<Map<String, Object>> workDefinitionsMaps = (List<Map<String, Object>>) WidMVELEvaluator.eval(content);
        final Map<String, WorkDefinitionImpl> result = new HashMap<>(workDefinitionsMaps.size());
        for (Map<String, Object> workDefinitionMap : workDefinitionsMaps) {
            if (workDefinitionMap != null) {
                String origWidName = ((String) workDefinitionMap.get("name")).replaceAll("\\s", "");
                Matcher widNameMatcher = UNICODE_WORDS_PATTERN.matcher(origWidName);
                if (widNameMatcher.matches()) {
                    final WorkDefinitionImpl workDefinition = parseMVELWorkItemDefinition(workDefinitionMap,
                                                                                          dataUriProvider,
                                                                                          widNameMatcher.group());
                    result.put(workDefinition.getName(),
                               workDefinition);
                } else {
                    LOG.error("The work item has an invalid name [ " +
                                      workDefinitionMap.get("name") + "]." +
                                      "It must contain words only, skipping it.");
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static WorkDefinitionImpl parseMVELWorkItemDefinition(final Map<String, Object> workDefinitionMap,
                                                                  final Function<String, String> dataUriProvider,
                                                                  final String name) {
        final WorkDefinitionImpl workDefinition = new WorkDefinitionImpl();

        // Name.
        workDefinition.setName(name);

        // Display name.
        set(workDefinitionMap,
            "displayName",
            workDefinition::setDisplayName);

        // Category.
        set(workDefinitionMap,
            "category",
            BPMNCategories.CUSTOM_TASKS,
            workDefinition::setCategory);

        // Icon.
        set(workDefinitionMap,
            "icon",
            workDefinition::setIcon);

        // Icon data-uri.
        final String icon = workDefinition.getIcon();
        if (nonEmpty(icon)) {
            final String iconData = dataUriProvider.apply(icon);
            workDefinition.setIcon(icon);
            workDefinition.setIconEncoded(iconData);
        }

        // Custom editor.
        set(workDefinitionMap,
            "customEditor",
            workDefinition::setCustomEditor);

        // Parameters.
        setParameters(workDefinitionMap,
                      "parameters",
                      workDefinition::setParameters);

        // Results.
        setParameters(workDefinitionMap,
                      "results",
                      workDefinition::setResults);

        // Parameter values.
        final Map<String, Object> values = (Map<String, Object>) workDefinitionMap.get("parameterValues");
        if (null != values) {
            final Map<String, Object> parameterValues = new HashMap<>(values.size());
            values.entrySet().forEach(entry -> {
                final Object value = entry.getValue();
                final Function<Object, String> dataTypeFormatter = DATA_TYPE_FORMATTERS.get(value.getClass());
                if (null != dataTypeFormatter) {
                    parameterValues.put(entry.getKey(),
                                        dataTypeFormatter.apply(value));
                } else {
                    LOG.error("The work item's parameter type [" +
                                      value.getClass() +
                                      "] is not supported. " +
                                      "Skipping this parameter.");
                }
            });
            workDefinition.setParameterValues(parameterValues);
        }

        // Default Handler.
        set(workDefinitionMap,
            "defaultHandler",
            "",
            workDefinition::setDefaultHandler);

        // Dependencies.
        setArray(workDefinitionMap,
                 "dependencies",
                 workDefinition::setDependencies);

        // Documentation.
        set(workDefinitionMap,
            "documentation",
            "",
            workDefinition::setDocumentation);

        // Version.
        set(workDefinitionMap,
            "version",
            "",
            workDefinition::setVersion);

        // Description.
        set(workDefinitionMap,
            "description",
            "",
            workDefinition::setDescription);

        // Maven dependencies.
        setArray(workDefinitionMap,
                 "mavenDependencies",
                 workDefinition::setMavenDependencies);

        return workDefinition;
    }

    private static void set(final Map<String, Object> map,
                            final String key,
                            final Consumer<String> consumer) {
        set(map,
            key,
            null,
            consumer);
    }

    private static void set(final Map<String, Object> map,
                            final String key,
                            final String defaultValue,
                            final Consumer<String> consumer) {
        final String value = (String) map.get(key);
        if (nonEmpty(value)) {
            consumer.accept(value);
        } else if (null != defaultValue) {
            consumer.accept(defaultValue);
        }
    }

    @SuppressWarnings("unchecked")
    private static void setParameters(final Map<String, Object> map,
                                      final String key,
                                      final Consumer<Set<ParameterDefinition>> consumer) {
        final Map<String, DataType> parameterMap = (Map<String, DataType>) map.get(key);
        if (null != parameterMap) {
            consumer.accept(parameterMap.entrySet().stream()
                                    .map(entry -> new ParameterDefinitionImpl(entry.getKey(),
                                                                              entry.getValue()))
                                    .collect(Collectors.toSet()));
        }
    }

    @SuppressWarnings("unchecked")
    private static void setArray(final Map<String, Object> map,
                                 final String key,
                                 final Consumer<String[]> consumer) {
        final List<String> values = (List<String>) map.get(key);
        if (null != values) {
            consumer.accept(values.toArray(new String[0]));
        } else {
            consumer.accept(new String[0]);
        }
    }

    public static String buildDataURIFromURL(final String url) {
        try {
            return URLUtils.buildDataURIFromURL(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

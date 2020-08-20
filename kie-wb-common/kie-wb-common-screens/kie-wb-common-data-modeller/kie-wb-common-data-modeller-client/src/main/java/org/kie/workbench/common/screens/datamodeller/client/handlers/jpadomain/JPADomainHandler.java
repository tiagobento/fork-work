/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.workbench.common.screens.datamodeller.client.command.AddPropertyCommand;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommand;
import org.kie.workbench.common.screens.datamodeller.client.command.FieldTypeChangeCommand;
import org.kie.workbench.common.screens.datamodeller.client.handlers.DomainHandler;
import org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.command.AdjustFieldDefaultRelationsCommand;
import org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.command.JPACommandBuilder;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.ResourceOptions;
import org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.options.JPANewResourceOptions;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.JPADomainAnnotations;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

@ApplicationScoped
public class JPADomainHandler implements DomainHandler {

    public static final String ENABLE_DATA_OBJECT_AUDIT = "enable-data-object-audit";

    @Inject
    private Instance<JPANewResourceOptions> newResourceOptions;

    @Inject
    private JPACommandBuilder commandBuilder;

    public JPADomainHandler() {
    }

    @Override
    public String getName() {
        return "JPA";
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public ResourceOptions getResourceOptions(boolean newInstance) {
        //currently same instance is always returned, since file handlers are all ApplicationScoped
        return newResourceOptions.get();
    }

    @Override
    public void postCommandProcessing(DataModelCommand command) {
        if (command instanceof FieldTypeChangeCommand &&
                (isPersistable(((FieldTypeChangeCommand) command).getDataObject()) ||
                        isRelationConfigured(((FieldTypeChangeCommand) command).getField()))) {

            AdjustFieldDefaultRelationsCommand postCommand = commandBuilder.buildAdjustFieldDefaultRelationsCommand(
                    (FieldTypeChangeCommand) command,
                    getName(),
                    ((FieldTypeChangeCommand) command).getField());
            postCommand.execute();
        } else if (command instanceof AddPropertyCommand &&
                isPersistable(((AddPropertyCommand) command).getDataObject())) {
            AdjustFieldDefaultRelationsCommand postCommand = commandBuilder.buildAdjustFieldDefaultRelationsCommand(
                    (AddPropertyCommand) command,
                    getName(),
                    ((AddPropertyCommand) command).getProperty());
            postCommand.execute();
        }
    }

    public boolean isOptionEnabled(String option) {
        return ApplicationPreferences.getBooleanPref("data-modeler-options." + option);
    }

    public boolean isDataObjectAuditEnabled() {
        return isOptionEnabled(JPADomainHandler.ENABLE_DATA_OBJECT_AUDIT);
    }

    private boolean isPersistable(DataObject dataObject) {
        return dataObject.getAnnotation(JPADomainAnnotations.JAVAX_PERSISTENCE_ENTITY_ANNOTATION) != null;
    }

    private boolean isRelationConfigured(ObjectProperty objectProperty) {
        return objectProperty.getAnnotation(JPADomainAnnotations.JAVAX_PERSISTENCE_MANY_TO_ONE) != null ||
                objectProperty.getAnnotation(JPADomainAnnotations.JAVAX_PERSISTENCE_MANY_TO_MANY) != null ||
                objectProperty.getAnnotation(JPADomainAnnotations.JAVAX_PERSISTENCE_ONE_TO_ONE) != null ||
                objectProperty.getAnnotation(JPADomainAnnotations.JAVAX_PERSISTENCE_ONE_TO_MANY) != null ||
                objectProperty.getAnnotation(JPADomainAnnotations.JAVAX_PERSISTENCE_ELEMENT_COLLECTION) != null;
    }
}

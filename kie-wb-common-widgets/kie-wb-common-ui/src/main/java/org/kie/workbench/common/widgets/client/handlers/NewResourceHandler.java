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
package org.kie.workbench.common.widgets.client.handlers;

import java.util.Arrays;
import java.util.List;

import org.guvnor.common.services.project.model.Package;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.type.ResourceTypeDefinition;
import org.kie.workbench.common.profile.api.preferences.Profile;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Definition of Handler to support creation of new resources
 */
public interface NewResourceHandler {

    /**
     * A description of the new resource type
     * @return
     */
    String getDescription();

    /**
     * An icon representing the new resource type
     * @return
     */
    IsWidget getIcon();

    /**
     * Get the ResourceType represented by the Handler
     * @return resource type
     */
    ResourceTypeDefinition getResourceType();

    /**
     * Asks if the resource handler can be created or not.
     * For example a lack of a certain role can return false.
     * @return If true this resource handler can be added.
     */
    boolean canCreate();

    /**
     * An entry-point for the creation of the new resource
     * @param pkg the Package context where new resource should be created
     * @param baseFileName the base name of the new resource
     * @param presenter underlying presenter
     */
    void create(final Package pkg,
                final String baseFileName,
                final NewResourcePresenter presenter);

    /**
     * Return a List of Widgets that the NewResourceHandler can use to gather additional parameters for the
     * new resource. The List is of Pairs, where each Pair consists of a String caption and IsWidget editor.
     * @return null if no extension is provided
     */
    List<Pair<String, ? extends IsWidget>> getExtensions();

    /**
     * Provide NewResourceHandlers with the ability to validate additional parameters before the creation of the new resource
     * @param baseFileName The base file name for the new item (excluding extension)
     * @param callback Callback depending on validation result
     */
    void validate(final String baseFileName,
                  final ValidatorWithReasonCallback callback);

    /**
     * Indicates if the NewResourceHandler can create the assets on the default package
     * @return
     */
    default boolean supportsDefaultPackage() {
        return true;
    }

    /**
     * Indicates if the NewResourceHandler can create a resource to this path
     * @return
     */
    void acceptContext(final Callback<Boolean, Void> callback);

    /**
     * A command to execute instead of defaulting to the NewResourceView.
     * If this returns null the NewResourceView is shown by default.
     * @param newResourcePresenter
     * @return
     */
    Command getCommand(final NewResourcePresenter newResourcePresenter);

    /**
     * Defines the handler order for UI purposes.
     * @return The handler order. The smallest number comes first.
     */
    default int order() {
        return 0;
    }

    /**
     * Defines whether this resource is a project asset and should appear on the Add Asset screen
     * @return true if it is a project asset; false otherwise.
     */
    default boolean isProjectAsset() {
        return true;
    }
    
    /**
     * Defines the list of profiles where this resource handler should be used. <br>
     * By default it supports all profiles.
     * 
     * @return the list of supported Profiles
     */
    default List<Profile> getProfiles() {
        return Arrays.asList(Profile.values());
    }
}

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
package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.validation;

import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;

/**
 * Component able to read the constraints on form models
 */
public interface ContextModelConstraintsExtractor {

    /**
     * Checks for bean validation constraints on the form models and initializes the context validations
     * @param clientRenderingContext
     * @param classLoader
     */
    void readModelConstraints(MapModelRenderingContext clientRenderingContext,
                              ClassLoader classLoader);
}

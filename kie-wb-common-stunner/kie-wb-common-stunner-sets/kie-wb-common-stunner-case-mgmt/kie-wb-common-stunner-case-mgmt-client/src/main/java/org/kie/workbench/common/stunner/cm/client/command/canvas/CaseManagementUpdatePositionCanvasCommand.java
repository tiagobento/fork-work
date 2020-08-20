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

package org.kie.workbench.common.stunner.cm.client.command.canvas;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;

public class CaseManagementUpdatePositionCanvasCommand extends AbstractCanvasCommand {

    @Override
    @SuppressWarnings("unused")
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        // The current implementation does not update View positions from the Graph; instead the
        // vice-versa occurs. ILayoutHandler drives the View layout and the Graph is updated
        // following View changes.
        return buildResult();
    }

    @Override
    @SuppressWarnings("unused")
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        // The current implementation does not update View positions from the Graph; instead the
        // vice-versa occurs. ILayoutHandler drives the View layout and the Graph is updated
        // following View changes.
        return buildResult();
    }
}

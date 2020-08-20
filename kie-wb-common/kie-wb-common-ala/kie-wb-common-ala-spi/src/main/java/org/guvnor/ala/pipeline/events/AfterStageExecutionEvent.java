/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.pipeline.events;

import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.Stage;

/*
 * Event emitted by the PipelineExecutor after each pipeline's stage execution
*/
public class AfterStageExecutionEvent
        extends StageExecutionPipelineEvent {

    public AfterStageExecutionEvent(final String executionId,
                                    final Pipeline pipeline,
                                    final Stage stage) {
        super(executionId,
              pipeline,
              stage);
    }
}
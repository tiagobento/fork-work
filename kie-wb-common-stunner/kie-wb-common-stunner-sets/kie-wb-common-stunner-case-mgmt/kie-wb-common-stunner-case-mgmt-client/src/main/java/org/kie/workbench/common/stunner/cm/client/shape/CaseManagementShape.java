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

package org.kie.workbench.common.stunner.cm.client.shape;

import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.impl.SVGShapeImpl;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;

public class CaseManagementShape extends SVGShapeImpl {

    private CaseManagementSvgShapeDef shapeDef;

    public CaseManagementShape(SVGShapeViewImpl view, CaseManagementSvgShapeDef shapeDef) {
        super(view);

        this.shapeDef = shapeDef;
    }

    public CaseManagementSvgShapeDef getShapeDef() {
        return shapeDef;
    }
}
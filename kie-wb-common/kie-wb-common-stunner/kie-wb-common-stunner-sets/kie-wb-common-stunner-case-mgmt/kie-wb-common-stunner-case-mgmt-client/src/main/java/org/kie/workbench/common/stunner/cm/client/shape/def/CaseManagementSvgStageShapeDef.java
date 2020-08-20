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
package org.kie.workbench.common.stunner.cm.client.shape.def;

import java.util.Optional;

import org.kie.workbench.common.stunner.bpmn.client.shape.def.BaseDimensionedShapeDef;
import org.kie.workbench.common.stunner.cm.client.resources.CaseManagementSVGGlyphFactory;
import org.kie.workbench.common.stunner.cm.client.resources.CaseManagementSVGViewFactory;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class CaseManagementSvgStageShapeDef extends BaseDimensionedShapeDef
        implements CaseManagementSvgShapeDef<AdHocSubprocess> {

    @Override
    public SizeHandler<AdHocSubprocess, SVGShapeView> newSizeHandler() {
        return newSizeHandlerBuilder()
                .width(e -> e.getDimensionsSet().getWidth().getValue())
                .height(e -> e.getDimensionsSet().getHeight().getValue())
                .minWidth(e -> 50d)
                .minHeight(e -> 50d)
                .build();
    }

    @Override
    public SVGShapeView<?> newViewInstance(final CaseManagementSVGViewFactory factory, final AdHocSubprocess bean) {
        // stage should not be resizable in case modeler
        return newViewInstance(Optional.empty(), Optional.empty(), factory.stage());
    }

    @Override
    public Glyph getGlyph(final Class<? extends AdHocSubprocess> type,
                          final String defId) {
        return CaseManagementSVGGlyphFactory.STAGE_GLYPH;
    }
}

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
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.cm.client.resources.CaseManagementSVGGlyphFactory;
import org.kie.workbench.common.stunner.cm.client.resources.CaseManagementSVGViewFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class CaseManagementSvgNullShapeDef extends BaseDimensionedShapeDef
        implements CaseManagementSvgShapeDef<BPMNViewDefinition> {

    @Override
    public FontHandler<BPMNViewDefinition, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder().build();
    }

    @Override
    public SizeHandler<BPMNViewDefinition, SVGShapeView> newSizeHandler() {
        return newSizeHandlerBuilder().build();
    }

    @Override
    public SVGShapeView<?> newViewInstance(final CaseManagementSVGViewFactory factory,
                                           final BPMNViewDefinition bean) {
        return newViewInstance(Optional.empty(), Optional.empty(), factory.task());
    }

    @Override
    public Glyph getGlyph(final Class<? extends BPMNViewDefinition> type,
                          final String defId) {
        return CaseManagementSVGGlyphFactory.TASK_GLYPH;
    }
}

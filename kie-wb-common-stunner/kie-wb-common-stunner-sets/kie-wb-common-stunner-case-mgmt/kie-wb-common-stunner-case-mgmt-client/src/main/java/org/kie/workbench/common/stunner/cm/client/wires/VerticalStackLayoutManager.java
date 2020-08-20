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
package org.kie.workbench.common.stunner.cm.client.wires;

import java.util.List;

import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementShapeView;

public class VerticalStackLayoutManager extends AbstractNestedLayoutHandler {

    static final double PADDING_X = 0.0;
    static final double PADDING_Y = 15.0;

    @Override
    protected void orderChildren(final WiresShape wiresShape,
                                 final WiresContainer container,
                                 final Point2D mouseRelativeLoc) {
        if (container == null) {
            return;
        }

        CaseManagementShapeView shape = (CaseManagementShapeView) wiresShape;

        final NFastArrayList<WiresShape> nChildren = container.getChildShapes().copy();
        final List<WiresShape> children = nChildren.remove(shape).toList();

        int targetIndex = children.size();

        final double my = mouseRelativeLoc.getY();

        for (int idx = 0; idx < children.size(); idx++) {
            final WiresShape child = children.get(idx);
            if (my < child.getY()) {
                targetIndex = idx;
                break;
            }
        }

        if (container instanceof CaseManagementShapeView) {
            final int currentIndex = ((CaseManagementShapeView) container).getIndex(shape);
            if (currentIndex != targetIndex) {
                ((CaseManagementShapeView) container).addShape(shape,
                                                               targetIndex);
            }
        }
    }

    @Override
    public void layout(final WiresContainer container) {
        double y = PADDING_Y;

        if (container instanceof WiresShape) {
            y = y + ((WiresShape) container).getPath().getBoundingBox().getHeight();
        }

        for (WiresShape ws : container.getChildShapes()) {
            ws.setLocation(new Point2D(PADDING_X,
                                       y));
            y = y + getTotalHeight(ws) + PADDING_Y;
        }
    }

    private double getTotalHeight(WiresShape ws) {
        double height = ws.getPath().getBoundingBox().getHeight();

        for (WiresShape child : ws.getChildShapes()) {
            height += (getTotalHeight(child) + PADDING_Y);
        }

        return height;
    }
}

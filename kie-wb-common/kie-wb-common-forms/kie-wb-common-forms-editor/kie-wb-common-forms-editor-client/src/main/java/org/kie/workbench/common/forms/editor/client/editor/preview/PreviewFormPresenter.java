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

package org.kie.workbench.common.forms.editor.client.editor.preview;

import java.util.HashMap;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;

@Dependent
public class PreviewFormPresenter implements IsWidget {

    public interface PreviewFormPresenterView extends IsWidget {

        public void preview(FormRenderingContext context);
    }

    private PreviewFormPresenterView view;

    @Inject
    public PreviewFormPresenter(PreviewFormPresenterView view) {
        this.view = view;
    }

    public void preview(FormRenderingContext context) {

        MapModelRenderingContext mapContext = new MapModelRenderingContext("edit");
        mapContext.getAvailableForms().putAll(context.getAvailableForms());
        mapContext.setRootForm(context.getRootForm());
        mapContext.setModel(new HashMap<>());

        view.preview(mapContext);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}

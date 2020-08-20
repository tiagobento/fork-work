/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.widgets.common;

import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Templated
public class MenuResourceHandlerWidget implements IsElement {

    @Inject
    @DataField
    Anchor link;

    public void init(final String text,
                     final String tooltip,
                     final Command onClick) {
        if (tooltip != null) {
            link.setTitle(tooltip);
        }

        this.init(text,
                  onClick);
    }

    public void init(final String text,
                     final Command onClick) {
        link.setTextContent(text);

        if (onClick != null) {
            link.setOnclick(e -> onClick.execute());
        }
    }
}

/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.client.promise.Promises;

@Dependent
public class DependencySelectorPopup implements DependencySelectorPresenter {

    @Inject
    private DependencySelectorPopupView view;

    @Inject
    private Caller<M2RepoService> m2RepoService;

    @Inject
    private Promises promises;

    private List<GAVSelectionHandler> selectionHandlers = new ArrayList<>();

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void show() {
        view.show();
    }

    @Override
    public void onPathSelection(final String pathToDependency) {

        promises.promisify(m2RepoService, s -> {
            return s.loadGAVFromJar(pathToDependency);
        }).then(gav -> {
            selectionHandlers.forEach(h -> h.onSelection(gav));
            return promises.resolve();
        });

        view.hide();
    }

    public void addSelectionHandler(final GAVSelectionHandler selectionHandler) {
        selectionHandlers.add(selectionHandler);
    }
}

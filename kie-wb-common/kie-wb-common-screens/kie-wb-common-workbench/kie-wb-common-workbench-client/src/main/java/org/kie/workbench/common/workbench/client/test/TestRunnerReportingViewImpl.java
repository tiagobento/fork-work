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

package org.kie.workbench.common.workbench.client.test;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.workbench.client.resources.i18n.WorkbenchConstants;

@Templated
public class TestRunnerReportingViewImpl
        implements TestRunnerReportingView {

    private Presenter presenter;

    @DataField
    private HTMLDivElement resultPanel;

    @DataField
    private HTMLDivElement testResultIcon;

    @DataField
    private HTMLDivElement testResultText;

    @DataField
    private HTMLDivElement scenariosRun;

    @DataField
    private HTMLDivElement completedAt;

    @DataField
    private HTMLDivElement duration;

    @DataField
    private HTMLAnchorElement viewAlerts;

    @DataField
    private HTMLDivElement donutDivContainer;

    @DataField
    private HTMLDivElement donutDiv;

    private TestResultDonutPresenter testResultDonutPresenter;
    private TranslationService translationService;

    @Inject
    public TestRunnerReportingViewImpl(final HTMLDivElement resultPanel,
                                       final HTMLDivElement testResultIcon,
                                       final HTMLDivElement testResultText,
                                       final HTMLDivElement scenariosRun,
                                       final HTMLDivElement completedAt,
                                       final HTMLDivElement duration,
                                       final HTMLAnchorElement viewAlerts,
                                       final HTMLDivElement donutDivContainer,
                                       final HTMLDivElement donutDiv,
                                       final TestResultDonutPresenter testResultDonutPresenter,
                                       final TranslationService translationService) {
        this.resultPanel = resultPanel;
        this.testResultIcon = testResultIcon;
        this.testResultText = testResultText;
        this.scenariosRun = scenariosRun;
        this.completedAt = completedAt;
        this.duration = duration;
        this.viewAlerts = viewAlerts;
        this.donutDivContainer = donutDivContainer;
        this.donutDiv = donutDiv;
        this.testResultDonutPresenter = testResultDonutPresenter;
        this.translationService = translationService;
        testResultDonutPresenter.init(donutDiv);
    }

    @EventHandler("viewAlerts")
    public void onClickEvent(ClickEvent event) {
        presenter.onViewAlerts();
    }

    @Override
    public Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void reset() {
        testResultIcon.className = "";
        testResultText.textContent = "";
        this.duration.textContent = "";
        this.completedAt.textContent = "";
        this.scenariosRun.textContent = "";
        resetDonut();
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showSuccess() {
        testResultIcon.className = "pficon pficon-ok";
        testResultText.textContent = translationService.format(WorkbenchConstants.PASSED);
    }

    @Override
    public void showFailure() {
        testResultIcon.className = "pficon pficon-error-circle-o";
        testResultText.textContent = translationService.format(WorkbenchConstants.FAILED);
    }

    @Override
    public void setRunStatus(String completedAt,
                             String scenariosRun,
                             String duration) {
        this.completedAt.textContent = completedAt;
        this.scenariosRun.textContent = scenariosRun;
        this.duration.textContent = duration;
    }

    @Override
    public void resetDonut() {
        donutDivContainer.hidden = true;
    }

    @Override
    public void showSuccessFailureDiagram(final int passed,
                                          final int failed) {
        donutDivContainer.hidden = false;
        testResultDonutPresenter.showSuccessFailureDiagram(passed,
                                                           failed);
    }

    @Override
    public Widget asWidget() {
        return ElementWrapperWidget.getWidget(resultPanel);
    }
}

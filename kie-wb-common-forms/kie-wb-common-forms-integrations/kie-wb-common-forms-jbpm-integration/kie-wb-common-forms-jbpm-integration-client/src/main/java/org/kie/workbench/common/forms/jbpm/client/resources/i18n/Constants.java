/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.jbpm.client.resources.i18n;

import org.jboss.errai.ui.shared.api.annotations.TranslationKey;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;

public interface Constants {

    @TranslationKey(defaultValue = "Business Process")
    String Process = BusinessProcessFormModel.class.getName();

    @TranslationKey(defaultValue = "")
    String Task = TaskFormModel.class.getName();

    @TranslationKey(defaultValue = "Start Process Form")
    String JBPMFormModelCreationViewImplStartProcessForm = "JBPMFormModelCreationViewImpl.StartProcessForm";

    @TranslationKey(defaultValue = "Form for Task {0} ({1})")
    String JBPMFormModelCreationViewImplTaskName = "JBPMFormModelCreationViewImpl.TaskName";

    @TranslationKey(defaultValue = "There's no process or task selected")
    String InvalidFormModel = "JBPMFormModelCreationPresenter.InvalidFormModel";

    @TranslationKey(defaultValue = "")
    String DocumentUploadViewImplAbort = "DocumentUploadViewImpl.abort";

    @TranslationKey(defaultValue = "")
    String DocumentUploadViewImplRemove = "DocumentUploadViewImpl.remove";

    @TranslationKey(defaultValue = "")
    String DocumentUploadViewImplRetry = "DocumentUploadViewImpl.retry";

    @TranslationKey(defaultValue = "")
    String DocumentUploadViewImplMaxDocuments = "DocumentUploadViewImpl.maxDocuments";

    @TranslationKey(defaultValue = "")
    String DocumentListFieldRendererMaxContentLengthWarning = "DocumentListFieldRenderer.maxContentLengthWarning";

    @TranslationKey(defaultValue = "")
    String DocumentListFieldRendererMaxDocumentsReached = "DocumentListFieldRenderer.maxDocumentsReached";
}

/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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
package org.kie.workbench.common.services.refactoring.backend.server.query.response;

import java.util.List;

import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.paging.PageResponse;

public interface ResponseBuilder {

    PageResponse<RefactoringPageRow> buildResponse( final int pageSize,
                                                    final int startRow,
                                                    final List<KObject> kObjects );

    List<RefactoringPageRow> buildResponse( final List<KObject> kObjects );

}

/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.refactoring.model.index.terms.valueterms;

import org.kie.workbench.common.services.refactoring.model.index.terms.IndexTerm;

public interface ValueIndexTerm extends IndexTerm {

    String getValue();

    /**
     * The type of extended (term) functionality used in the term
     */
    public enum TermSearchType {
        NORMAL,
        PREFIX,  // lucene's PrefixQuery
        WILDCARD, // lucene's PrefixQuery
        REGEXP; // lucene's RegExpQuery
    }

    default TermSearchType getSearchType() {
        return TermSearchType.NORMAL;
    }

}

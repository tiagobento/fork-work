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

package org.kie.workbench.common.services.backend.rulename;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Module;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindRulesByModuleQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueModuleRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringRuleNamePageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class RuleNameServiceImpl
        implements RuleNamesService {

    private RefactoringQueryService queryService;

    @Inject
    private KieModuleService moduleService;

    public RuleNameServiceImpl() {
        //CDI proxy
    }

    @Inject
    public RuleNameServiceImpl(final RefactoringQueryService queryService,
                               final KieModuleService moduleService) {
        this.queryService = queryService;
        this.moduleService = moduleService;
    }

    @Override
    public Collection<String> getRuleNames(final Path path,
                                           final String packageName) {
        final Module module = moduleService.resolveModule(path);

        if (module == null) {
            return Collections.emptyList();
        } else {
            return queryRuleNames(
                    packageName,
                    module.getRootPath().toURI());
        }
    }

    private List<String> queryRuleNames(final String packageName,
                                        final String modulePath) {

        //Query for Rule Names
        final List<RefactoringPageRow> results = queryService.query(FindRulesByModuleQuery.NAME,
                                                                    new HashSet<ValueIndexTerm>() {{
                                                                        add(new ValueModuleRootPathIndexTerm(modulePath));
                                                                        add(new ValuePackageNameIndexTerm(packageName));
                                                                    }});

        return convertToRuleNames(results);
    }

    private List<String> convertToRuleNames(List<RefactoringPageRow> results) {
        final List<String> ruleNames = new ArrayList<String>();
        for (RefactoringPageRow row : results) {
            ruleNames.add(((RefactoringRuleNamePageRow.RuleName) row.getValue()).getSimpleRuleName());
        }
        Collections.sort(ruleNames);
        return ruleNames;
    }
}

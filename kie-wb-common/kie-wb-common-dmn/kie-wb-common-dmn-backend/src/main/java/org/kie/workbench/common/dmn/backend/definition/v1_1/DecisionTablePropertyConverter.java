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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.model.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.model.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClause;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

public class DecisionTablePropertyConverter {

    public static DecisionTable wbFromDMN(final org.kie.dmn.model.api.DecisionTable dmn) {
        final Id id = new Id(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef(), dmn);

        final DecisionTable result = new DecisionTable();
        result.setId(id);
        result.setDescription(description);
        result.setTypeRef(typeRef);

        if (!(dmn instanceof org.kie.dmn.model.v1_1.TDecisionTable)) {
            if (dmn.getAnnotation().isEmpty()) {
                final RuleAnnotationClause ruleAnnotationClause = new RuleAnnotationClause();
                ruleAnnotationClause.setParent(result);
                result.getAnnotations().add(ruleAnnotationClause);
            } else {
                for (final org.kie.dmn.model.api.RuleAnnotationClause ruleAnnotationClause : dmn.getAnnotation()) {
                    final RuleAnnotationClause converted = RuleAnnotationClauseConverter.wbFromDMN(ruleAnnotationClause);
                    if (converted != null) {
                        converted.setParent(result);
                    }
                    result.getAnnotations().add(converted);
                }
            }
        }
        for (org.kie.dmn.model.api.InputClause input : dmn.getInput()) {
            final InputClause inputClauseConverted = InputClausePropertyConverter.wbFromDMN(input);
            if (inputClauseConverted != null) {
                inputClauseConverted.setParent(result);
            }
            result.getInput().add(inputClauseConverted);
        }
        for (org.kie.dmn.model.api.OutputClause output : dmn.getOutput()) {
            final OutputClause outputClauseConverted = OutputClausePropertyConverter.wbFromDMN(output);
            if (outputClauseConverted != null) {
                outputClauseConverted.setParent(result);
            }
            result.getOutput().add(outputClauseConverted);
        }
        if (result.getOutput().size() == 1) {
            final OutputClause outputClause = result.getOutput().get(0);
            outputClause.setName(null); // DROOLS-3281
            outputClause.setTypeRef(null); // DROOLS-5178
        }
        for (org.kie.dmn.model.api.DecisionRule dr : dmn.getRule()) {
            final DecisionRule decisionRuleConverted = DecisionRulePropertyConverter.wbFromDMN(dr);
            if (decisionRuleConverted != null) {
                decisionRuleConverted.setParent(result);
            }
            result.getRule().add(decisionRuleConverted);
        }
        if (dmn.getHitPolicy() != null) {
            result.setHitPolicy(HitPolicy.fromValue(dmn.getHitPolicy().value()));
        }
        if (dmn.getAggregation() != null) {
            result.setAggregation(BuiltinAggregator.fromValue(dmn.getAggregation().value()));
        }
        if (dmn.getPreferredOrientation() != null) {
            result.setPreferredOrientation(DecisionTableOrientation.fromValue(dmn.getPreferredOrientation().value()));
        }

        result.setOutputLabel(dmn.getOutputLabel());

        return result;
    }

    public static org.kie.dmn.model.api.DecisionTable dmnFromWB(final DecisionTable wb) {
        final org.kie.dmn.model.api.DecisionTable result = new org.kie.dmn.model.v1_2.TDecisionTable();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(), result::setTypeRef);

        for (final RuleAnnotationClause annotation : wb.getAnnotations()) {
            final org.kie.dmn.model.api.RuleAnnotationClause converted = RuleAnnotationClauseConverter.dmnFromWB(annotation);
            if (converted != null) {
                converted.setParent(result);
            }
            result.getAnnotation().add(converted);
        }
        for (InputClause input : wb.getInput()) {
            final org.kie.dmn.model.api.InputClause c = InputClausePropertyConverter.dmnFromWB(input);
            if (c != null) {
                c.setParent(result);
            }
            result.getInput().add(c);
        }
        for (OutputClause input : wb.getOutput()) {
            final org.kie.dmn.model.api.OutputClause c = OutputClausePropertyConverter.dmnFromWB(input);
            if (c != null) {
                c.setParent(result);
            }
            result.getOutput().add(c);
        }
        if (result.getOutput().size() == 1) {
            final org.kie.dmn.model.api.OutputClause outputClause = result.getOutput().get(0);
            outputClause.setName(null); // DROOLS-3281
            outputClause.setTypeRef(null); // DROOLS-5178
        }
        for (DecisionRule dr : wb.getRule()) {
            final org.kie.dmn.model.api.DecisionRule c = DecisionRulePropertyConverter.dmnFromWB(dr);
            if (c != null) {
                c.setParent(result);
            }
            result.getRule().add(c);
        }
        if (wb.getHitPolicy() != null) {
            result.setHitPolicy(org.kie.dmn.model.api.HitPolicy.fromValue(wb.getHitPolicy().value()));
        }
        if (wb.getAggregation() != null) {
            result.setAggregation(org.kie.dmn.model.api.BuiltinAggregator.fromValue(wb.getAggregation().value()));
        }
        if (wb.getPreferredOrientation() != null) {
            result.setPreferredOrientation(org.kie.dmn.model.api.DecisionTableOrientation.fromValue(wb.getPreferredOrientation().value()));
        }

        result.setOutputLabel(wb.getOutputLabel());

        return result;
    }
}
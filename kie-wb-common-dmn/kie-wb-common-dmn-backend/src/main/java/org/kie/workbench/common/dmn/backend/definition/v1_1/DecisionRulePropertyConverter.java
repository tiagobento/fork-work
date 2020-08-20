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

import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClauseText;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;

public class DecisionRulePropertyConverter {

    public static DecisionRule wbFromDMN(final org.kie.dmn.model.api.DecisionRule dmn) {
        final Id id = new Id(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());

        final DecisionRule result = new DecisionRule();
        result.setId(id);

        if (!(dmn instanceof org.kie.dmn.model.v1_1.TDecisionRule)) {
            for (final org.kie.dmn.model.api.RuleAnnotation ruleAnnotation : dmn.getAnnotationEntry()) {
                final RuleAnnotationClauseText annotationEntryConverted = RuleAnnotationClauseTextConverter.wbFromDMN(ruleAnnotation);
                if (annotationEntryConverted != null) {
                    annotationEntryConverted.setParent(result);
                }
                result.getAnnotationEntry().add(annotationEntryConverted);
            }
        }

        if (result.getAnnotationEntry().isEmpty()) {
            // If it's empty, then there is none RuleAnnotation and the description was not converted yet to RuleAnnotation.
            final RuleAnnotationClauseText annotationEntryText = new RuleAnnotationClauseText();
            annotationEntryText.getText().setValue(description.getValue());
            annotationEntryText.setParent(result);
            result.getAnnotationEntry().add(annotationEntryText);
        }

        for (final org.kie.dmn.model.api.UnaryTests ie : dmn.getInputEntry()) {
            final UnaryTests inputEntryConverted = UnaryTestsPropertyConverter.wbFromDMN(ie);
            if (inputEntryConverted != null) {
                inputEntryConverted.setParent(result);
            }
            result.getInputEntry().add(inputEntryConverted);
        }
        for (final org.kie.dmn.model.api.LiteralExpression oe : dmn.getOutputEntry()) {
            final LiteralExpression outputEntryConverted = LiteralExpressionPropertyConverter.wbFromDMN(oe);
            if (outputEntryConverted != null) {
                outputEntryConverted.setParent(result);
            }
            result.getOutputEntry().add(outputEntryConverted);
        }

        return result;
    }

    public static org.kie.dmn.model.api.DecisionRule dmnFromWB(final DecisionRule wb) {
        final org.kie.dmn.model.api.DecisionRule result = new org.kie.dmn.model.v1_2.TDecisionRule();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));

        for (final RuleAnnotationClauseText ruleAnnotationClauseText : wb.getAnnotationEntry()) {
            final org.kie.dmn.model.api.RuleAnnotation ruleAnnotation = RuleAnnotationClauseTextConverter.dmnFromWB(ruleAnnotationClauseText);
            if (ruleAnnotation != null) {
                ruleAnnotation.setParent(result);
            }
            result.getAnnotationEntry().add(ruleAnnotation);
        }
        for (final UnaryTests ie : wb.getInputEntry()) {
            final org.kie.dmn.model.api.UnaryTests inputEntryConverted = UnaryTestsPropertyConverter.dmnFromWB(ie);
            if (inputEntryConverted != null) {
                inputEntryConverted.setParent(result);
            }
            result.getInputEntry().add(inputEntryConverted);
        }
        for (final LiteralExpression oe : wb.getOutputEntry()) {
            final org.kie.dmn.model.api.LiteralExpression outputEntryConverted = LiteralExpressionPropertyConverter.dmnFromWB(oe);
            if (outputEntryConverted != null) {
                outputEntryConverted.setParent(result);
            }
            result.getOutputEntry().add(outputEntryConverted);
        }

        return result;
    }
}
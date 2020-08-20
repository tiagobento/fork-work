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

import org.kie.dmn.model.api.UnaryTests;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.InputClauseUnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class InputClausePropertyConverter {

    public static InputClause wbFromDMN(final org.kie.dmn.model.api.InputClause dmn) {
        final Id id = new Id(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final InputClauseLiteralExpression inputExpression = InputClauseLiteralExpressionPropertyConverter.wbFromDMN(dmn.getInputExpression());
        final InputClauseUnaryTests inputValues = InputClauseUnaryTestsPropertyConverter.wbFromDMN(dmn.getInputValues());

        final InputClause result = new InputClause(id,
                                                   description,
                                                   inputExpression,
                                                   inputValues);

        if (inputExpression != null) {
            inputExpression.setParent(result);
        }
        if (inputValues != null) {
            inputValues.setParent(result);
        }

        return result;
    }

    public static org.kie.dmn.model.api.InputClause dmnFromWB(final InputClause wb) {
        final org.kie.dmn.model.api.InputClause result = new org.kie.dmn.model.v1_2.TInputClause();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        final org.kie.dmn.model.api.LiteralExpression expression = LiteralExpressionPropertyConverter.dmnFromWB(wb.getInputExpression());
        final UnaryTests inputValues = UnaryTestsPropertyConverter.dmnFromWB(wb.getInputValues());

        if (expression != null) {
            expression.setParent(result);
        }
        result.setInputExpression(expression);

        if (inputValues != null && StringUtils.nonEmpty(inputValues.getText())) {
            inputValues.setParent(result);
            result.setInputValues(inputValues);
        }

        return result;
    }
}
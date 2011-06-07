/*
 * Copyright 2011 JBoss, a divison Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.ioc.rebind.ioc.codegen;

import org.jboss.errai.ioc.rebind.ioc.codegen.meta.MetaClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class CallParameters extends AbstractStatement {
    private List<Statement> parameters;

    public CallParameters(List<Statement> parameters) {
        this.parameters = parameters;
    }

    public static CallParameters fromStatements(Statement... statements) {
        return new CallParameters(Arrays.asList(statements));
    }

    public static CallParameters fromStatements(String... statements) {
        List<Statement> parameters = new ArrayList<Statement>(statements.length);
        for (String s : statements) {
            parameters.add(new StringStatement(s));
        }
        return new CallParameters(parameters);
    }

    public static CallParameters none() {
        return new CallParameters(Collections.<Statement>emptyList());
    }

    public MetaClass[] getParameterTypes() {
        MetaClass[] parameterTypes = new MetaClass[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            parameterTypes[i] = parameters.get(i).getType();
        }
        return parameterTypes;
    }

    public String generate(Context context) {
        StringBuilder buf = new StringBuilder("(");
        for (int i = 0; i < parameters.size(); i++) {
            buf.append(parameters.get(i).generate(context));

            if (i + 1 < parameters.size()) {
                buf.append(", ");
            }
        }
        return buf.append(")").toString();
    }
}
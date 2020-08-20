/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodeller.parser;

import org.junit.Test;
import org.kie.workbench.common.services.datamodeller.parser.descr.MethodDescr;
import org.kie.workbench.common.services.datamodeller.parser.util.ParserUtil;

public class ParseMethodMode1Test {

    @Test
    public void testMethodMode( ) {

        String sentence = "public int method1() { return -1}";

        try {

            JavaParser parser = JavaParserFactory.newParser( sentence, JavaParserBase.ParserMode.PARSE_METHOD );
            parser.methodDeclaration( );
            MethodDescr methodDescr = parser.getMethodDescr( );

            int i = 0;
        } catch ( Exception e ) {
            e.printStackTrace( );
        }

    }
}

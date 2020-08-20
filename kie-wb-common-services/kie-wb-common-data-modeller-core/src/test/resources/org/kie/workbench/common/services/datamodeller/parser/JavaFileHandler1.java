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

import org.kie.workbench.common.services.datamodeller.parser.test.TestAnnotation;

/**
 * Changes on this file can break JavaFileHandler1Test and JavaFileHandler1.java.delete#N.txt files
 * Important! IntelliJ is not the best editor to manipulate the expected result files JavaFileHandler1.deleteN.txt
 * because it deletes some blank spaces for example for empty lines that starts with blank spaces.
 * It's recommended to use vi to edit the expected result files.
 */
public class JavaFileHandler1 {

    @TestAnnotation
    private String field1;

    /*@TestAnnotation*/
    private String field2 = "field2";

    private int field3 = 1234, field4  ;

    private java.lang.Boolean  field5 ,    field6 = new Boolean(true) ;

    private String[] field7 = new String[]{"one", "two"}    ;

    private String field8  , field9  ,field10 = "three";

    private java.lang.Boolean field11 = (10 > (15 -4))    , field12=false,  field13;

    public String getField1() {
        return field1;
    }

    /**
     * javadoc comment
     * @param field1
     */
    public void setField1(String field1) {
        this.field1 = field1;  /****** asfsd */
    }//****hello
    public String getField2() {
        return field2;
    }
    public void setField2(String field2) {
        this.field2 = field2;
    }

    //some comments at the end of file 1
}

//some comments at the end of file 2

//end
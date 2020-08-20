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

package org.kie.workbench.common.services.datamodeller.parser.descr;

import java.util.ArrayList;
import java.util.List;

public class ElementValuePairListDescr extends ElementDescriptor {

    public ElementValuePairListDescr( ) {
        super( ElementType.ELEMENT_VALUE_PAIR_LIST );
    }

    public ElementValuePairListDescr( String text, int start, int line, int position ) {
        this( text, start, -1, line, position );
    }

    public ElementValuePairListDescr( String text, int start, int stop ) {
        this( text, start, stop, -1, -1 );
    }

    public ElementValuePairListDescr( String text, int start, int stop, int line, int position ) {
        super( ElementType.ELEMENT_VALUE_PAIR_LIST, text, start, stop, line, position );
    }

    public ElementValuePairListDescr addValuePair( ElementValuePairDescr valuePair ) {
        getElements( ).add( valuePair );
        return this;
    }

    public List<ElementValuePairDescr> getValuePairs( ) {
        List<ElementValuePairDescr> valuePairs = new ArrayList<ElementValuePairDescr>( );
        for ( ElementDescriptor element : getElements( ).getElementsByType( ElementType.ELEMENT_VALUE_PAIR ) ) {
            valuePairs.add( ( ElementValuePairDescr ) element );
        }
        return valuePairs;
    }

    public int size() {
        List<ElementValuePairDescr> values = getValuePairs();
        return values != null ? values.size() : 0;

    }

}

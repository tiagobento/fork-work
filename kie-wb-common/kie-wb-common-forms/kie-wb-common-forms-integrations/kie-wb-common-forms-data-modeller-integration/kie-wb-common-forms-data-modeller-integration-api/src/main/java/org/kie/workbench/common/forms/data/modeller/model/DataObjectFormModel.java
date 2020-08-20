/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.data.modeller.model;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.forms.model.JavaFormModel;
import org.kie.workbench.common.forms.model.impl.AbstractFormModel;

@Portable
public class DataObjectFormModel extends AbstractFormModel implements
                                                           JavaFormModel {

    private Source source = Source.INTERNAL;
    private String className;

    private DataObjectFormModel() {
        // Only for serialization purposes
        source = Source.INTERNAL;
    }

    public DataObjectFormModel(@MapsTo("name") String name,
                               @MapsTo("className") String className) {
        this.name = name;
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    @Override
    public String getType() {
        return getClassName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DataObjectFormModel that = (DataObjectFormModel) o;

        if (source != that.source) {
            return false;
        }
        return className != null ? className.equals(that.className) : that.className == null;
    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = ~~result;
        return result;
    }
}

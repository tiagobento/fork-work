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

package org.kie.workbench.common.screens.datasource.management.model;

import org.uberfire.backend.vfs.Path;

public abstract class DefInfo {

    protected String uuid;

    protected String name;

    protected Path path;

    public DefInfo() {
    }

    public DefInfo( String uuid, String name, Path path ) {
        this.uuid = uuid;
        this.name = name;
        this.path = path;
    }

    public DefInfo( String uuid, String name ) {
        this.uuid = uuid;
        this.name = name;
    }

    public boolean isManaged() {
        return path != null;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }

    abstract boolean isDeployed();

    @Override
    public String toString() {
        return "DefInfo{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", path=" + path +
                '}';
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        DefInfo defInfo = ( DefInfo ) o;

        if ( uuid != null ? !uuid.equals( defInfo.uuid ) : defInfo.uuid != null ) {
            return false;
        }
        if ( name != null ? !name.equals( defInfo.name ) : defInfo.name != null ) {
            return false;
        }
        return path != null ? path.equals( defInfo.path ) : defInfo.path == null;

    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( path != null ? path.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}

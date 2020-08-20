/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenConfig;
import org.kie.workbench.common.services.backend.compiler.impl.external339.AFCliRequest;

/***
 * Implementation of CompilationRequest, holds the information for the AFMavenCli
 */
public class DefaultCompilationRequest implements CompilationRequest {

    private AFCliRequest req;
    private WorkspaceCompilationInfo info;
    private String requestUUID;
    private String[] originalArgs;
    private String mavenRepoPath;
    private Boolean skipPrjDependenciesCreationList;
    private Boolean restoreOverride;
    private Properties bannedEnvVars;

    /***
     * @param mavenRepoPath a string representation of the Path
     * @param info
     * @param args param for maven, can be used {@link org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs}
     */
    public DefaultCompilationRequest(String mavenRepoPath,
                                     WorkspaceCompilationInfo info,
                                     String[] args) {
        this(mavenRepoPath, info, args, UUID.randomUUID().toString());
    }

    /***
     * @param uuid a unique uuid identifier
     * @param mavenRepoPath a string representation of the Path
     * @param info
     * @param args param for maven, can be used {@link org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs}
     */
    public DefaultCompilationRequest(String mavenRepoPath,
                                     WorkspaceCompilationInfo info,
                                     String[] args, String uuid) {

        this(mavenRepoPath, info, args, Boolean.TRUE, Boolean.FALSE, uuid);
    }

    /***
     * @param mavenRepoPath a string representation of the Path
     * @param info
     * @param args param for maven, can be used {@link org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs}
     * @param skipPrjDependenciesCreationList if false a List with all dependencies of the project will be available in the response
     */
    public DefaultCompilationRequest(String mavenRepoPath,
                                     WorkspaceCompilationInfo info,
                                     String[] args,
                                     Boolean skipPrjDependenciesCreationList, String uuid) {
        this(mavenRepoPath, info, args, skipPrjDependenciesCreationList, true, uuid);
    }

    /***
     * @param mavenRepoPath a string representation of the Path
     * @param info
     * @param args param for maven, can be used {@link org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs}
     * @param skipPrjDependenciesCreationList if false a List with all dependencies of the project will be available in the response
     */
    public DefaultCompilationRequest(String mavenRepoPath,
                                     WorkspaceCompilationInfo info,
                                     String[] args,
                                     Boolean skipPrjDependenciesCreationList) {
        this(mavenRepoPath, info, args, skipPrjDependenciesCreationList, true);
    }

    public DefaultCompilationRequest(String mavenRepoPath,
                                     WorkspaceCompilationInfo info,
                                     String[] args,
                                     Boolean skipPrjDependenciesCreationList,
                                     boolean restoreOverride, String uuid) {
        this.mavenRepoPath = mavenRepoPath;
        this.info = info;
        this.skipPrjDependenciesCreationList = skipPrjDependenciesCreationList;
        this.requestUUID = uuid.trim().isEmpty() ? UUID.randomUUID().toString() : uuid;
        this.restoreOverride = restoreOverride;
        this.originalArgs = args;
        this.bannedEnvVars = info.getBennedEnvVars();
        Map internalMap = new HashMap();
        internalMap.put(MavenConfig.COMPILATION_ID, this.requestUUID);
        this.req = new AFCliRequest(this.info.getPrjPath().toAbsolutePath().toString(),
                                    args,
                                    internalMap,
                                    this.requestUUID,
                                    this.bannedEnvVars);
    }

    public DefaultCompilationRequest(String mavenRepoPath,
                                     WorkspaceCompilationInfo info,
                                     String[] args,
                                     Boolean skipPrjDependenciesCreationList,
                                     boolean restoreOverride) {
        this(mavenRepoPath, info, args, skipPrjDependenciesCreationList, restoreOverride, UUID.randomUUID().toString());
    }

    @Override
    public String getRequestUUID() {
        return requestUUID;
    }

    @Override
    public Boolean skipAutoSourceUpdate() {
        return true;
    }

    @Override
    public WorkspaceCompilationInfo getInfo() {
        return info;
    }

    public AFCliRequest getReq() {
        return req;
    }

    @Override
    public AFCliRequest getKieCliRequest() {
        return req;
    }

    @Override
    public String getMavenRepo() {
        return mavenRepoPath;
    }

    @Override
    public String[] getOriginalArgs() {
        return originalArgs;
    }

    @Override
    public Map<String, Object> getMap() {
        return req.getMap();
    }

    @Override
    public Boolean skipProjectDependenciesCreationList() {
        return skipPrjDependenciesCreationList;
    }

    @Override
    public Boolean getRestoreOverride() {
        return restoreOverride;
    }

    @Override
    public Properties getBannedEnvVars() {
        return bannedEnvVars;
    }

}
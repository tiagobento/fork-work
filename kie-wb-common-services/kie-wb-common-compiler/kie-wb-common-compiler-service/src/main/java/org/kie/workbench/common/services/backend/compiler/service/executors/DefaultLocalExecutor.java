/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.service.executors;

import java.io.InputStream;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.guvnor.common.services.backend.cache.LRUCache;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.uberfire.java.nio.file.Path;

/**
 * Implementation for a local build requested by a local execution (Contains NIO Objects)
 */
public class DefaultLocalExecutor implements CompilerExecutor {

    private ExecutorService executor;
    private LRUCache<Path, CompilerAggregateEntryCache> compilerCacheForLocalInvocation;

    public DefaultLocalExecutor(ExecutorService executorService) {
        executor = executorService;
        compilerCacheForLocalInvocation = new LRUCache<Path, CompilerAggregateEntryCache>() {
        };
    }

    private AFCompiler getCompiler(Path projectPath) {
        CompilerAggregateEntryCache info = compilerCacheForLocalInvocation.getEntry(projectPath);
        if (info != null && info.getCompiler() != null) {
            return info.getCompiler();
        } else {
            return getNewCachedAFCompiler(projectPath);
        }
    }

    private CompilationRequest getDefaultRequest(String mavenRepoPath,
                                                 WorkspaceCompilationInfo info,
                                                 boolean skipProjectDepCreation,
                                                 String[] goals) {
        return new DefaultCompilationRequest(mavenRepoPath,
                                             info,
                                             goals,
                                             skipProjectDepCreation);
    }

    private AFCompiler getNewCachedAFCompiler(Path projectPath) {
        CompilerAggregateEntryCache info = setupCompileInfo(projectPath);
        compilerCacheForLocalInvocation.setEntry(projectPath,
                                                 info);
        return info.getCompiler();
    }

    private CompilerAggregateEntryCache setupCompileInfo(Path workingDir) {
        final AFCompiler compiler = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.ENABLE_LOGGING,
                                                                                   KieDecorator.UPDATE_JGIT_BEFORE_BUILD,
                                                                                   KieDecorator.STORE_KIE_OBJECTS,
                                                                                   KieDecorator.STORE_BUILD_CLASSPATH,
                                                                                   KieDecorator.ENABLE_INCREMENTAL_BUILD));
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(workingDir);
        return new CompilerAggregateEntryCache(compiler,
                                               info);
    }

    private CompletableFuture<KieCompilationResponse> internalBuild(Path projectPath,
                                                                    String mavenRepoPath,
                                                                    String settingXML,
                                                                    boolean skipProjectDepCreation,
                                                                    String goal) {
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(projectPath);
        AFCompiler compiler = getCompiler(projectPath);
        CompilationRequest req;
        if (settingXML != null) {
            req = getDefaultRequest(mavenRepoPath,
                                    info,
                                    skipProjectDepCreation,
                                    new String[]{MavenCLIArgs.ALTERNATE_USER_SETTINGS + settingXML, goal});
        } else {
            req = getDefaultRequest(mavenRepoPath,
                                    info,
                                    skipProjectDepCreation,
                                    new String[]{goal});
        }
        return CompletableFuture.supplyAsync(() -> ((KieCompilationResponse) compiler.compile(req)),
                                             executor);
    }

    private CompletableFuture<KieCompilationResponse> internalBuild(Path projectPath,
                                                                    String mavenRepoPath,
                                                                    boolean skipProjectDepCreation,
                                                                    String[] args) {
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(projectPath);
        AFCompiler compiler = getCompiler(projectPath);
        CompilationRequest req = getDefaultRequest(mavenRepoPath,
                                                   info,
                                                   skipProjectDepCreation,
                                                   args);
        return CompletableFuture.supplyAsync(() -> ((KieCompilationResponse) compiler.compile(req)),
                                             executor);
    }

    private CompletableFuture<KieCompilationResponse> internalBuild(Path projectPath,
                                                                    String mavenRepoPath,
                                                                    String settingXML,
                                                                    boolean skipProjectDepCreation,
                                                                    String goal,
                                                                    Map<Path, InputStream> override) {
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(projectPath);
        AFCompiler compiler = getCompiler(projectPath);
        CompilationRequest req;
        if (settingXML != null) {
            req = getDefaultRequest(mavenRepoPath,
                                    info,
                                    skipProjectDepCreation,
                                    new String[]{MavenCLIArgs.ALTERNATE_USER_SETTINGS + settingXML, goal});
        } else {
            req = getDefaultRequest(mavenRepoPath,
                                    info,
                                    skipProjectDepCreation,
                                    new String[]{goal});
        }
        return CompletableFuture.supplyAsync(() -> ((KieCompilationResponse) compiler.compile(req,
                                                                                              override)),
                                             executor);
    }

    /************************************ Suitable for the Local Builds ***********************************************/
    @Override
    public CompletableFuture<KieCompilationResponse> build(Path projectPath,
                                                           String mavenRepoPath,
                                                           String settingXML) {
        return internalBuild(projectPath,
                             mavenRepoPath,
                             settingXML,
                             Boolean.FALSE,
                             MavenCLIArgs.COMPILE);
    }

    @Override
    public CompletableFuture<KieCompilationResponse> build(Path projectPath,
                                                           String mavenRepoPath,
                                                           String settingXML,
                                                           Map<Path, InputStream> override) {
        return internalBuild(projectPath,
                             mavenRepoPath,
                             settingXML,
                             Boolean.FALSE,
                             MavenCLIArgs.COMPILE,
                             override);
    }

    @Override
    public CompletableFuture<KieCompilationResponse> build(Path projectPath,
                                                           String mavenRepoPath,
                                                           String settingXML,
                                                           Boolean skipPrjDependenciesCreationList) {
        return internalBuild(projectPath,
                             mavenRepoPath,
                             settingXML,
                             skipPrjDependenciesCreationList,
                             MavenCLIArgs.COMPILE);
    }

    @Override
    public CompletableFuture<KieCompilationResponse> buildAndInstall(Path projectPath,
                                                                     String mavenRepoPath,
                                                                     String settingXML) {
        return internalBuild(projectPath,
                             mavenRepoPath,
                             settingXML,
                             Boolean.FALSE,
                             MavenCLIArgs.INSTALL);
    }

    @Override
    public CompletableFuture<KieCompilationResponse> buildAndInstall(Path projectPath,
                                                                     String mavenRepoPath,
                                                                     String settingXML,
                                                                     Boolean skipPrjDependenciesCreationList) {
        return internalBuild(projectPath,
                             mavenRepoPath,
                             settingXML,
                             skipPrjDependenciesCreationList,
                             MavenCLIArgs.INSTALL);
    }

    @Override
    public CompletableFuture<KieCompilationResponse> buildSpecialized(Path projectPath,
                                                                      String mavenRepoPath,
                                                                      String[] args) {
        return internalBuild(projectPath,
                             mavenRepoPath,
                             Boolean.FALSE,
                             args);
    }

    @Override
    public CompletableFuture<KieCompilationResponse> buildSpecialized(Path projectPath,
                                                                      String mavenRepoPath,
                                                                      String[] args,
                                                                      Boolean skipPrjDependenciesCreationList) {
        return internalBuild(projectPath,
                             mavenRepoPath,
                             skipPrjDependenciesCreationList,
                             args);
    }
}

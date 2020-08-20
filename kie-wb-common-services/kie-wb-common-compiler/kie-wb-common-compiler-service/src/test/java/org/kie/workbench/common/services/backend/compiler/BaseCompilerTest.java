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
package org.kie.workbench.common.services.backend.compiler;

import java.io.File;
import java.io.Serializable;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class BaseCompilerTest implements Serializable {

    protected static Path tmpRoot;
    protected String mavenRepoPath;
    protected static Logger logger = LoggerFactory.getLogger(BaseCompilerTest.class);
    protected String alternateSettingsAbsPath;
    protected WorkspaceCompilationInfo info;
    protected AFCompiler compiler;

    @BeforeClass
    public static void setup() {
        System.setProperty("org.uberfire.nio.git.daemon.enabled", "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled", "false");
    }

    public BaseCompilerTest(String prjName) {
        try {
            mavenRepoPath = TestUtilMaven.getMavenRepo();
            tmpRoot = Files.createTempDirectory("repo");
            alternateSettingsAbsPath = new File("src/test/settings.xml").getAbsolutePath();
            Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(), "dummy"));
            TestUtil.copyTree(Paths.get(prjName), tmp);
            info = new WorkspaceCompilationInfo(Paths.get(tmp.toUri()));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @AfterClass
    public static void tearDown() {
        System.clearProperty("org.uberfire.nio.git.daemon.enabled");
        System.clearProperty("org.uberfire.nio.git.ssh.enabled");
        if (tmpRoot != null) {
            TestUtil.rm(tmpRoot.toFile());
        }
    }
}

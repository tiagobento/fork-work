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

package org.uberfire.java.nio.fs.jgit.manager;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.java.nio.fs.jgit.AbstractTestInfra;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemImpl;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemLock;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProxy;
import org.uberfire.java.nio.fs.jgit.util.Git;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JGitFileSystemsCacheTest extends AbstractTestInfra {

    JGitFileSystemsCache cache;
    private JGitFileSystemProviderConfiguration config;

    @Before
    public void setup() {
        config = mock(JGitFileSystemProviderConfiguration.class);
    }

    @Test
    public void addAndGetTest() {
        when(config.getJgitFileSystemsInstancesCache()).thenReturn(2);
        cache = new JGitFileSystemsCache(config);

        assertTrue(cache.fileSystemsSuppliers.isEmpty());
        assertTrue(cache.memoizedSuppliers.isEmpty());

        assertEquals(null,
                     cache.get("fs1"));

        JGitFileSystem fs1 = mock(JGitFileSystem.class);
        Supplier<JGitFileSystem> fs1Supplier = () -> fs1;
        cache.addSupplier("fs1",
                          fs1Supplier);

        assertFalse(cache.fileSystemsSuppliers.isEmpty());
        assertFalse(cache.memoizedSuppliers.isEmpty());

        JGitFileSystemProxy fs1Proxy = (JGitFileSystemProxy) cache.get("fs1");

        assertEquals(fs1,
                     fs1Proxy.getRealJGitFileSystem());

        assertTrue(cache.containsKey("fs1"));

        cache.clear();

        assertTrue(cache.fileSystemsSuppliers.isEmpty());
        assertTrue(cache.memoizedSuppliers.isEmpty());
    }

    @Test
    public void addAndReplaceTest() {
        when(config.getJgitFileSystemsInstancesCache()).thenReturn(2);
        cache = new JGitFileSystemsCache(config);

        JGitFileSystem fs1 = mock(JGitFileSystem.class);
        Supplier<JGitFileSystem> fs1Supplier = () -> fs1;
        cache.addSupplier("fs1",
                          fs1Supplier);

        assertFalse(cache.fileSystemsSuppliers.isEmpty());
        assertFalse(cache.memoizedSuppliers.isEmpty());

        JGitFileSystemProxy fs1Proxy = (JGitFileSystemProxy) cache.get("fs1");
        assertEquals(fs1,
                     fs1Proxy.getRealJGitFileSystem());

        JGitFileSystem fs2 = mock(JGitFileSystem.class);
        Supplier<JGitFileSystem> fs2Supplier = () -> fs2;
        cache.replaceSupplier("fs1",
                              fs2Supplier);

        JGitFileSystemProxy fs2Proxy = (JGitFileSystemProxy) cache.get("fs1");

        assertEquals(fs2,
                     fs2Proxy.getRealJGitFileSystem());

        assertTrue(cache.containsKey("fs1"));

        cache.clear();

        assertTrue(cache.fileSystemsSuppliers.isEmpty());
        assertTrue(cache.memoizedSuppliers.isEmpty());
    }

    @Test
    public void addMoreFSThanCacheSupports() {
        when(config.getJgitFileSystemsInstancesCache()).thenReturn(2);
        cache = new JGitFileSystemsCache(config);

        JGitFileSystem fs1 = mock(JGitFileSystem.class);
        Supplier<JGitFileSystem> fs1Supplier = getSupplierSpy(fs1);
        cache.addSupplier("fs1",
                          fs1Supplier);

        assertEquals(1,
                     cache.fileSystemsSuppliers.size());
        assertEquals(1,
                     cache.memoizedSuppliers.size());

        ((JGitFileSystemProxy) cache.get("fs1")).getRealJGitFileSystem();

        JGitFileSystem fs2 = mock(JGitFileSystem.class);
        Supplier<JGitFileSystem> fs2Supplier = getSupplierSpy(fs2);
        cache.addSupplier("fs2",
                          fs2Supplier);
        ((JGitFileSystemProxy) cache.get("fs2")).getRealJGitFileSystem();

        assertEquals(2,
                     cache.fileSystemsSuppliers.size());
        assertEquals(2,
                     cache.memoizedSuppliers.size());

        JGitFileSystem fs3 = mock(JGitFileSystem.class);
        Supplier<JGitFileSystem> fs3Supplier = getSupplierSpy(fs3);
        cache.addSupplier("fs3",
                          fs3Supplier);

        ((JGitFileSystemProxy) cache.get("fs3")).getRealJGitFileSystem();

        assertEquals(3,
                     cache.fileSystemsSuppliers.size());
        assertEquals(2,
                     cache.memoizedSuppliers.size());

        ((JGitFileSystemProxy) cache.get("fs2")).getRealJGitFileSystem();

        //just one call because is on memoized cache
        verify(fs2Supplier,
               times(1)).get();

        ((JGitFileSystemProxy) cache.get("fs3")).getRealJGitFileSystem();

        //just one call because is on memoized cache
        verify(fs3Supplier,
               times(1)).get();

        ((JGitFileSystemProxy) cache.get("fs1")).getRealJGitFileSystem();

        // two calls because is on no longer on memoized cache (oldest instance) needs to regenerate
        // from fs supplier
        verify(fs1Supplier,
               times(2)).get();
    }

    @Test
    public void cacheIsOrderedByTheAccessOrder() {

        JGitFileSystemProviderConfiguration config = setupConfigMock();

        setupCacheToTestOrder(config, "fs1", "fs2", "fs3");
        assertFalse(cache.memoizedSuppliers.containsKey("fs1"));

        setupCacheToTestOrder(config, "fs1", "fs2", "fs3");
        cache.get("fs1");
        assertFalse(cache.memoizedSuppliers.containsKey("fs2"));

        setupCacheToTestOrder(config, "fs1", "fs2", "fs3");
        cache.get("fs1");
        cache.get("fs2");
        assertFalse(cache.memoizedSuppliers.containsKey("fs3"));

        cache.get("fs1");
        cache.get("fs2");
        cache.get("fs3");
        setupCacheToTestOrder(config, "fs1", "fs2", "fs3");
        assertFalse(cache.memoizedSuppliers.containsKey("fs1"));
    }

    @Test
    public void removeEldestEntryTest() {

        JGitFileSystemProviderConfiguration config = setupConfigMock();

        //no fs is on use
        setupCacheToTestOrder(config, "fs1", "fs2", "fs3");
        assertFalse(cache.memoizedSuppliers.containsKey("fs1"));

        //fs1 is on use
        cache = new JGitFileSystemsCache(config);

        JGitFileSystem fs1 = mock(JGitFileSystem.class);
        when(fs1.hasBeenInUse()).thenReturn(true);
        Supplier<JGitFileSystem> fsSupplier1 = getSupplierSpy(fs1);
        cache.addSupplier("fs1",
                          fsSupplier1);

        JGitFileSystem fs2 = mock(JGitFileSystem.class);
        Supplier<JGitFileSystem> fsSupplier2 = getSupplierSpy(fs2);
        when(fs2.hasBeenInUse()).thenReturn(false);
        cache.addSupplier("fs2",
                          fsSupplier2);

        JGitFileSystem fs3 = mock(JGitFileSystem.class);
        Supplier<JGitFileSystem> fsSupplier = getSupplierSpy(fs3);
        when(fs3.hasBeenInUse()).thenReturn(false);
        cache.addSupplier("fs3",
                          fsSupplier);

        assertTrue(cache.memoizedSuppliers.containsKey("fs1"));
        assertTrue(cache.memoizedSuppliers.containsKey("fs3"));
        assertFalse(cache.memoizedSuppliers.containsKey("fs2"));
    }

    @Test
    public void removeEldestEntryTestAllOpen() {

        JGitFileSystemProviderConfiguration config = setupConfigMock();

        cache = new JGitFileSystemsCache(config);

        JGitFileSystem fs1 = mock(JGitFileSystem.class);
        when(fs1.hasBeenInUse()).thenReturn(true);
        Supplier<JGitFileSystem> fsSupplier1 = getSupplierSpy(fs1);
        cache.addSupplier("fs1",
                          fsSupplier1);

        JGitFileSystem fs2 = mock(JGitFileSystem.class);
        Supplier<JGitFileSystem> fs2Supplier = getSupplierSpy(fs2);
        when(fs2.hasBeenInUse()).thenReturn(true);
        cache.addSupplier("fs2",
                          fs2Supplier);

        JGitFileSystem fs3 = mock(JGitFileSystem.class);
        Supplier<JGitFileSystem> fs3Supplier = getSupplierSpy(fs3);
        when(fs3.hasBeenInUse()).thenReturn(true);
        cache.addSupplier("fs3",
                          fs3Supplier);

        JGitFileSystem fs4 = mock(JGitFileSystem.class);
        Supplier<JGitFileSystem> fs4Supplier = getSupplierSpy(fs4);
        when(fs4.hasBeenInUse()).thenReturn(true);
        cache.addSupplier("fs4",
                          fs3Supplier);

        //> cache because all fs are in use
        assertTrue(cache.memoizedSuppliers.containsKey("fs1"));
        assertTrue(cache.memoizedSuppliers.containsKey("fs3"));
        assertTrue(cache.memoizedSuppliers.containsKey("fs2"));
        assertTrue(cache.memoizedSuppliers.containsKey("fs4"));

        when(fs1.hasBeenInUse()).thenReturn(false);
        when(fs2.hasBeenInUse()).thenReturn(false);
        when(fs4.hasBeenInUse()).thenReturn(false);

        JGitFileSystem fs5 = mock(JGitFileSystem.class);
        Supplier<JGitFileSystem> fs5Supplier = getSupplierSpy(fs5);
        when(fs5.hasBeenInUse()).thenReturn(true);
        cache.addSupplier("fs5",
                          fs5Supplier);

        //fs5 is in use and also fs4
        assertTrue(cache.memoizedSuppliers.containsKey("fs3"));
        assertTrue(cache.memoizedSuppliers.containsKey("fs5"));
        assertFalse(cache.memoizedSuppliers.containsKey("fs1"));
        assertFalse(cache.memoizedSuppliers.containsKey("fs2"));
    }

    @Test
    public void fsInUseAreAlwaysOnTheCache() throws IOException, GitAPIException {

        JGitFileSystemProviderConfiguration config = new JGitFileSystemProviderConfiguration() {
            @Override
            public int getJgitFileSystemsInstancesCache() {
                return 2;
            }
        };

        cache = new JGitFileSystemsCache(config);

        final JGitFileSystemProvider fsProvider = mock(JGitFileSystemProvider.class);

        final Git git = setupGit();

        final JGitFileSystemImpl fs1 = new JGitFileSystemImpl(fsProvider,
                                                              null,
                                                              git,
                                                              new JGitFileSystemLock(git,
                                                                                     TimeUnit.MILLISECONDS,
                                                                                     config.getJgitCacheEvictThresholdDuration()),
                                                              "fs1",
                                                              CredentialsProvider.getDefault(),
                                                              null,
                                                              null);

        Supplier<JGitFileSystem> fs1Supplier = getSupplierSpy(fs1);
        cache.addSupplier("fs1",
                          fs1Supplier);

        fs1.lock();
        fs1.lock();
        fs1.unlock();
        assertTrue(cache.memoizedSuppliers.containsKey("fs1"));

        JGitFileSystem fs2 = mock(JGitFileSystem.class);
        Supplier<JGitFileSystem> fs2Supplier = getSupplierSpy(fs2);
        when(fs2.hasBeenInUse()).thenReturn(true);
        cache.addSupplier("fs2",
                          fs2Supplier);

        JGitFileSystem fs3 = mock(JGitFileSystem.class);
        Supplier<JGitFileSystem> fs3Supplier = getSupplierSpy(fs3);
        when(fs3.hasBeenInUse()).thenReturn(true);
        cache.addSupplier("fs5",
                          fs3Supplier);

        assertTrue(cache.memoizedSuppliers.containsKey("fs1"));
    }

    private void setupCacheToTestOrder(JGitFileSystemProviderConfiguration config, String... fsNames) {
        cache = new JGitFileSystemsCache(config);

        Arrays.stream(fsNames).forEach(fsName -> {
            JGitFileSystem fs = mock(JGitFileSystem.class);
            Supplier<JGitFileSystem> fsSupplier = getSupplierSpy(fs);
            cache.addSupplier(fsName,
                              fsSupplier);
        });
    }

    private Supplier<JGitFileSystem> getSupplierSpy(final JGitFileSystem fs1) {
        return spy(new Supplier<JGitFileSystem>() {
            @Override
            public JGitFileSystem get() {
                return fs1;
            }
        });
    }

    private JGitFileSystemProviderConfiguration setupConfigMock() {
        return new JGitFileSystemProviderConfiguration() {
            @Override
            public int getJgitFileSystemsInstancesCache() {
                return 2;
            }

            @Override
            public int getJgitRemoveEldestEntryIterations() {
                return 10;
            }

            @Override
            public int getJgitCacheOverflowCleanupSize() {
                return 10;
            }
        };
    }
}
/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.jpa.client.local.backend;

import org.jboss.errai.jpa.client.local.ErraiEntityManager;

/**
 * Creates instances of StorageBackend, tying each new instance to a particular
 * ErraiEntityManager. This factory class exists to break the reference cycle
 * between WebStorageBackend instances (which want a final reference to an
 * ErraiEntityManager) and ErraiEntityManager (which wants a final reference to
 * its StorageBackend).
 *
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
public interface StorageBackendFactory {

  /**
   * Creates a new instance of some implementation of StorageBackend which is
   * permanently bound to the given ErraiEntityManager.
   *
   * @param em
   *          The EntityManager the StorageBackend serves.
   * @return a new instance of some implementation of StorageBackend.
   */
  StorageBackend createInstanceFor(ErraiEntityManager em);

}

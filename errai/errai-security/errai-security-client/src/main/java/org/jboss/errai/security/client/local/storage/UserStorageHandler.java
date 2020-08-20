/*
 * Copyright (C) 2014 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.security.client.local.storage;

import org.jboss.errai.security.shared.api.identity.User;

/**
 * Contract for things that can remember the current user (identifier, roles,
 * properties) in some way that can persist between browser sessions.
 */
public interface UserStorageHandler {

  /**
   * Returns the locally persisted user if one can be found.
   * 
   * @return the persisted user, or null if none can be found.
   */
  User getUser();

  /**
   * Persists the given user locally, using whatever means this implementation
   * supports.
   * 
   * @param user
   *          the user instance to remember for later, or null to forget the
   *          current user (clears any existing information about a user out
   *          of the persistent store).
   */
  void setUser(final User user);
}

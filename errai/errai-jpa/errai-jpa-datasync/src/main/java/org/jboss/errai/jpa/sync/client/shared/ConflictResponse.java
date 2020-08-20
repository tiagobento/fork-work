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

package org.jboss.errai.jpa.sync.client.shared;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * A SyncResponse that represents conflicting state due to a change that happened on the responding end.
 *
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 * @param <X> The entity type that was used in the corresponding SyncRequest
 */
@Portable
public class ConflictResponse<X> extends SyncResponse<X> {

  private final X expected;
  private final X actualNew;
  private final X requestedNew;

  public ConflictResponse(
          @MapsTo("expected") X expected,
          @MapsTo("actualNew") X actualNew,
          @MapsTo("requestedNew") X requestedNew) {
    this.expected = expected;
    this.actualNew = actualNew;
    this.requestedNew = requestedNew;
  }

  public X getExpected() {
    return expected;
  }

  public X getActualNew() {
    return actualNew;
  }

  public X getRequestedNew() {
    return requestedNew;
  }

  @Override
  public String toString() {
    return "Conflict: expected=" + expected + " actual=" + actualNew + " requested=" + requestedNew;
  }
}

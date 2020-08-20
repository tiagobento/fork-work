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

package org.jboss.errai.bus.client.tests.support;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * An immutable portable entity class that contains a references to an enum type.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@Portable
public class ImmutableEnumContainer {

  private final TestEnumA test;

  public ImmutableEnumContainer(@MapsTo("test") TestEnumA test) {
    this.test = test;
  }

  public TestEnumA getTest() {
    return test;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((test == null) ? 0 : test.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ImmutableEnumContainer other = (ImmutableEnumContainer) obj;
    if (test != other.test)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ImmutableEnumContainer [test=" + test + "]";
  }
  
}

/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * @author Mike Brock
 */
@Portable
public class EntityWithStringBufferAndStringBuilder {
  private StringBuffer stringBuffer;
  private StringBuilder stringBuilder;

  public StringBuffer getStringBuffer() {
    return stringBuffer;
  }

  public void setStringBuffer(StringBuffer stringBuffer) {
    this.stringBuffer = stringBuffer;
  }

  public StringBuilder getStringBuilder() {
    return stringBuilder;
  }

  public void setStringBuilder(StringBuilder stringBuilder) {
    this.stringBuilder = stringBuilder;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((stringBuffer == null) ? 0 : stringBuffer.hashCode());
    result = prime * result + ((stringBuilder == null) ? 0 : stringBuilder.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EntityWithStringBufferAndStringBuilder)) return false;
    return o.toString().equals(toString());
  }

  @Override
  public String toString() {
    return "EntityWithStringBufferAndStringBuilder{" +
            "stringBuffer=" + stringBuffer +
            ", stringBuilder=" + stringBuilder +
            '}';
  }
}

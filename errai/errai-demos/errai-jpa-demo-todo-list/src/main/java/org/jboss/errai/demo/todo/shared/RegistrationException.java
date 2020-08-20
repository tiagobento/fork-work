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

package org.jboss.errai.demo.todo.shared;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RegistrationException extends Exception {

  // TODO include a list of BeanValidation failure items

  private static final long serialVersionUID = 1L;

  /**
   * Default constructor for the marshaller.
   */
  public RegistrationException() {
  }

  public RegistrationException(String message) {
    super(message);
  }
}

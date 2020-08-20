/*
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.bus.server.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated client-side class implements a remote interface
 * (see {@link Remote}) and is to be used as a service endpoint in the following
 * scenarios:
 * 
 * <ul>
 * <li>Remote communication is turned off
 * <li>Errai's message bus is not in connected state
 * <li>A remote endpoint for the service does not exist
 * </ul>
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Mike Brock
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ShadowService {

  /**
   * The name of the bus service. This parameter has no effect on RPC services.
   */
  String value() default "";
}

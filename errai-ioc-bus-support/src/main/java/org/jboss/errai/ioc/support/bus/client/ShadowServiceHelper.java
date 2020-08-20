/*
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ioc.support.bus.client;

import com.google.gwt.core.client.Scheduler;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class ShadowServiceHelper {

  public static void deferred(final Runnable invocation) {
    Scheduler.get().scheduleDeferred(() -> {
      try {
        invocation.run();
      } catch (final Throwable t) {
        throw new RuntimeException("Error occured during deferred ShadowService invocation.", t);
      }
    });
  }

}

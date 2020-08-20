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

package org.jboss.errai.bus.server.api;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.errai.bus.client.api.QueueSession;
import org.jboss.errai.bus.client.api.messaging.Message;

/**
 * This utility provides access to {@link Message} resources otherwise not visible to RPC endpoints. It can be used to
 * gain access to HTTP session and servlet request objects.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Mike Brock
 */
public class RpcContext {
  private static final ThreadLocal<Message> threadLocalMessage = new ThreadLocal<Message>();

  /**
   * Reads resources from the provided {@link Message} and stores them in {@link ThreadLocal}s.
   *
   * @param message
   */
  public static void set(final Message message) {
    threadLocalMessage.set(message);
  }

  /**
   * Removes the resources associated with the current thread.
   */
  public static void remove() {
    threadLocalMessage.remove();
  }

  public static Message getMessage() {
    return threadLocalMessage.get();
  }

  /**
   * @return the QueueSession associated with this {@link Thread}
   */
  public static QueueSession getQueueSession() {
    final Message m = getMessage();
    if (m == null) {
      return null;
    }
    
    return m.getResource(QueueSession.class, "Session");
  }

  /**
   * @return the HTTP session object associated with this {@link Thread}
   */
  public static HttpSession getHttpSession() {
    final QueueSession qs = getQueueSession();
    if (qs == null) {
      return null;
    }
    
    return qs.getAttribute(HttpSession.class, HttpSession.class.getName());
  }

  /**
   * @return the servlet request instance associated with this {@link Thread}
   */
  public static ServletRequest getServletRequest() {
    return getMessage().getResource(HttpServletRequest.class, HttpServletRequest.class.getName());
  }
}

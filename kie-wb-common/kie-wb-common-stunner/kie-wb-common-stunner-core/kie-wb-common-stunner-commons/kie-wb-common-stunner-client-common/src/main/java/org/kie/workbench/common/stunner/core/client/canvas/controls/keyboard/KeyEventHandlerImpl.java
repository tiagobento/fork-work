/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import com.google.gwt.user.client.Timer;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyDownEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyUpEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * A helper class for component that listen to keyboard events.
 * It provides keyboard shortcuts support by listening for
 * multiple key events.
 */
@Dependent
public class KeyEventHandlerImpl implements KeyEventHandler {

    private final static int KEYS_TIMER_DELAY = 100;

    private final Set<KeyboardEvent.Key> keys = new HashSet<>();
    private final List<KeyboardControl.KeyShortcutCallback> shortcutCallbacks = new ArrayList<>();

    private boolean enabled = true;
    private KeyboardEvent.Key[] _keys;
    private Timer timer;
    private int delay = KEYS_TIMER_DELAY;

    @Override
    public KeyEventHandler addKeyShortcutCallback(final KeyboardControl.KeyShortcutCallback shortcutCallback) {

        final Optional<KeyboardControl.KogitoKeyShortcutCallback> possibleKogitoShortcutCallback = getAssociatedKogitoKeyShortcutCallback(shortcutCallback);
        if (possibleKogitoShortcutCallback.isPresent()) {
            return this;
        }

        this.shortcutCallbacks.add(shortcutCallback);
        return this;
    }

    @Override
    public KeyEventHandler setTimerDelay(final int millis) {
        this.delay = millis;
        return this;
    }

    @Override
    @PreDestroy
    public void clear() {
        if (null != timer && timer.isRunning()) {
            timer.cancel();
        }
        shortcutCallbacks.clear();
        reset();
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public void onKeyUpEvent(final @Observes KeyUpEvent event) {
        checkNotNull("event",
                     event);
        onKeyUp(event.getKey());
    }

    void onKeyDownEvent(final @Observes KeyDownEvent event) {
        checkNotNull("event",
                     event);
        onKeyDown(event.getKey());
    }

    private void onKeyDown(final KeyboardEvent.Key key) {
        if (!enabled) {
            return;
        }
        if (!shortcutCallbacks.isEmpty()) {
            startKeysTimer(key);
        }
    }

    private void onKeyUp(final KeyboardEvent.Key key) {
        if (!enabled) {
            return;
        }
        keys.remove(key);
        shortcutCallbacks.stream().forEach(s -> s.onKeyUp(key));
    }

    private void startKeysTimer(final KeyboardEvent.Key key) {
        keys.add(key);
        this._keys = keys.toArray(new KeyboardEvent.Key[this.keys.size()]);
        if (null == timer) {
            timer = new Timer() {
                @Override
                public void run() {
                    KeyEventHandlerImpl.this.keysTimerTimeIsUp();
                }
            };
        }
        timer.schedule(delay);
    }

    void keysTimerTimeIsUp() {
        if (!shortcutCallbacks.isEmpty() && null != _keys) {
            shortcutCallbacks.stream().forEach(s -> s.onKeyShortcut(_keys));
        }
    }

    void reset() {
        setEnabled(false);
        _keys = null;
        keys.clear();
        timer = null;
    }
}

/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.util;

import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.mvp.Command;

public class ErrorPopupHelper {

    public static void showErrorPopup( final String message ) {
        ErrorPopup.showMessage( message );
    }

    public static void showErrorPopup( final String message, final Command afterShowCommand, final Command afterCloseCommand ) {
        ErrorPopup.showMessage( message, new com.google.gwt.user.client.Command() {
            @Override
            public void execute() {
                if ( afterShowCommand != null ) {
                    afterShowCommand.execute();
                }
            }
        }, new com.google.gwt.user.client.Command() {
            @Override public void execute() {
                if ( afterCloseCommand != null ) {
                    afterCloseCommand.execute();
                }
            }
        } );
    }

}

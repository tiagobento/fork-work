/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.discussion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.shared.metadata.model.DiscussionRecord;
import org.gwtbootstrap3.client.ui.TextArea;

public class DiscussionWidgetViewImpl extends Composite implements DiscussionWidgetView {

    private Presenter presenter;

    interface Binder
            extends
            UiBinder<Widget, DiscussionWidgetViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    VerticalPanel lines;

    @UiField
    TextArea textBox;

    @UiField
    ScrollPanel commentScroll;

    public DiscussionWidgetViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    public void addRow( DiscussionRecord line ) {
        CommentLinePresenter commentLine = new CommentLinePresenter( );
        commentLine.setRecord( line );
        lines.add( commentLine );
    }

    @Override
    public void clear() {
        lines.clear();
    }

    @Override
    public void clearCommentBox() {
        textBox.setText( "" );
    }

    @Override
    public void scrollToBottom() {
        commentScroll.scrollToBottom();
    }

    @UiHandler("textBox")
    public void onCommentBoxEnter( final KeyUpEvent event ) {
        if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
            presenter.onAddComment( textBox.getText() );
        }
    }
}

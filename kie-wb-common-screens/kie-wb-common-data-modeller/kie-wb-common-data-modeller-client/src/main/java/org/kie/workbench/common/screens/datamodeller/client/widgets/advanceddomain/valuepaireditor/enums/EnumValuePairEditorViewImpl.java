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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.enums;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.uberfire.commons.data.Pair;

public class EnumValuePairEditorViewImpl
        extends Composite
        implements EnumValuePairEditorView {

    interface EnumValuePairEditorViewImplUiBinder
            extends
            UiBinder<Widget, EnumValuePairEditorViewImpl> {

    }

    private static EnumValuePairEditorViewImplUiBinder uiBinder = GWT.create( EnumValuePairEditorViewImplUiBinder.class );

    @UiField
    FormLabel valuePairLabel;

    @UiField
    Select listBox;

    @UiField
    HelpBlock helpBlock;

    private EnumValuePairEditorView.Presenter presenter;

    public EnumValuePairEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void initOptions( List<Pair<String, String>> options ) {
        UIUtil.initList( listBox, options, true );
    }

    @Override
    public void setSelectedValue( String value ) {
        UIUtil.setSelectedValue( listBox, value );
    }

    @Override
    public String getSelectedValue() {
        return listBox.getValue();
    }

    @Override
    public void setValuePairLabel( String valuePairLabel ) {
        this.valuePairLabel.setText( valuePairLabel );
    }

    @Override
    public void showValuePairName( boolean show ) {
        this.valuePairLabel.setVisible( show );
    }

    @Override
    public void showValuePairRequiredIndicator( boolean required ) {
        this.valuePairLabel.setShowRequiredIndicator( required );
    }

    @Override
    public void setErrorMessage( String errorMessage ) {
        helpBlock.setText( errorMessage );
    }

    @Override
    public void clearErrorMessage() {
        helpBlock.setText( null );
    }

    @UiHandler("listBox")
    void onValueChange( ValueChangeEvent<String> event ) {
        presenter.onValueChange();
    }
}

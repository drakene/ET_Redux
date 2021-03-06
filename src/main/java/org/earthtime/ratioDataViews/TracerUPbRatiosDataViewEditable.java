/*
 * TracerUPbRatiosDataViewEditable.java
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.earthtime.ratioDataViews;

import java.awt.Dimension;
import org.earthtime.UPb_Redux.valueModelPanelViews.ValueModelsPanelViewEditable;
import org.earthtime.exceptions.ETException;
import org.earthtime.matrices.matrixViews.MatrixGridViewEditable;
import org.earthtime.matrices.matrixViews.MatrixGridViewNotEditable;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.tracers.TracerUPbModel;

/**
 *
 * @author James F. Bowring
 */
public class TracerUPbRatiosDataViewEditable extends TracerUPbRatiosAbstractDataView {

    private final boolean showTableOnly;
    /**
     *
     *
     * @param ratiosDataModel the value of ratiosDataModel
     * @param parentDimension the value of parentDimension
     * @param showTableOnly the value of showTableOnly
     */
    public TracerUPbRatiosDataViewEditable ( //
            AbstractRatiosDataModel ratiosDataModel, Dimension parentDimension, boolean showTableOnly) {

        super( ratiosDataModel, parentDimension );
        this.showTableOnly = showTableOnly;
        setupViews();
        initView(true);
    }

    /**
     *
     */
    @Override
    protected final void setupViews () {
        this.valueModelsPanelView = new ValueModelsPanelViewEditable(//
                dataModel.getData(), this );

        this.covarianceVarUnctMatrixView = new MatrixGridViewNotEditable( dataModel.getDataCovariancesVarUnct(),showTableOnly );
        this.correlationVarUnctMatrixView = new MatrixGridViewEditable( dataModel.getDataCorrelationsVarUnct(), this, showTableOnly, true );
    }

    /**
     *
     *
     * @param checkCovarianceValidity the value of checkCovarianceValidity
     * @throws org.earthtime.exceptions.ETException
     */
    @Override
    protected void saveEdits ( boolean checkCovarianceValidity )
            throws ETException {
        super.saveEdits( checkCovarianceValidity );
        valueModelsPanelView.saveEdits();
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main ( String[] args ) throws Exception {

        AbstractRatiosDataView testView = new TracerUPbRatiosDataViewEditable( TracerUPbModel.getET535ModelInstance(), null, false );

        testView.displayModelInFrame();

    }
}

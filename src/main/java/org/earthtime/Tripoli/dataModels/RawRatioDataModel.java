/*
 * RawRatioDataModel.java
 *
 * Created Jun 29, 2011
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
package org.earthtime.Tripoli.dataModels;

import Jama.Matrix;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.earthtime.Tripoli.dataModels.collectorModels.AbstractCollectorModel;
import org.earthtime.Tripoli.dataModels.collectorModels.IonCounterCollectorModel;
import org.earthtime.Tripoli.fitFunctions.AbstractFunctionOfX;
import org.earthtime.Tripoli.fitFunctions.AbstractOverDispersionLMAlgorithmInterface;
import org.earthtime.Tripoli.fitFunctions.ConstantFitFunctionWithCovS;
import org.earthtime.Tripoli.fitFunctions.LevenbergMarquardGeneralSolverWithCovS;
import org.earthtime.Tripoli.fitFunctions.LevenbergMarquardGeneralSolverWithCovS.AbstractOverDispersionLMAlgorithm;
import org.earthtime.Tripoli.fitFunctions.LevenbergMarquardGeneralSolverWithVecV;
import org.earthtime.Tripoli.fitFunctions.LevenbergMarquardGeneralSolverWithVecV.AbstractOverDispersionLMVecAlgorithm;
import org.earthtime.Tripoli.fitFunctions.MeanFitFunction;
import org.earthtime.UPb_Redux.utilities.comparators.IntuitiveStringComparator;
import org.earthtime.dataDictionaries.FitFunctionTypeEnum;
import org.earthtime.dataDictionaries.IsotopeNames;
import org.earthtime.dataDictionaries.RawRatioNames;
import org.earthtime.statistics.NonParametricStats;

/**
 *
 * @author James F. Bowring
 */
public class RawRatioDataModel //
        implements Serializable, Comparable<RawRatioDataModel>, DataModelInterface, DataModelFitFunctionInterface {

    // Class variables
    private static final long serialVersionUID = 3111511502335804607L;

    /**
     *
     */
    private boolean USING_FULL_PROPAGATION;
    private final RawRatioNames rawRatioName;
    private final DataModelInterface topIsotope;
    private final DataModelInterface botIsotope;
    private double[] ratios;
    // may 2016 to accomodate shrimp prototype
    private double[] uncertaintyOneSigmaAbsRatios;
    private double[] logRatios;
    private double[] alphas;
    private double[] fitFunctionLogValues;
    private double[] correctedRatios;
    private double[] logDifferencesFromWeightedMean;
    private boolean usedForFractionationCorrections;
    // oct 2014
    private boolean usedForCommonLeadCorrections;
    private double standardValue;
    private boolean[] dataActiveMap;
    private long COLLECTOR_DATA_FREQUENCY_MILLISECS;
    // values calculated about the residuals from the fitted down hole fractionation curve
    private double meanOfResidualsFromFittedFractionation;
    private double stdErrOfmeanOfResidualsFromFittedFractionation;
    private double meanOfCorrectedRatios;
    private double stdDevOfCorrectedRatios;
    private double stdErrOfMeanCorrectedRatios;
    // used for individual intercept fractionation
    private Map<String, AbstractFunctionOfX> logRatioFitFunctionsNoOD;
    private Map<String, AbstractFunctionOfX> logRatioFitFunctionsWithOD;
    private AbstractFunctionOfX downHoleFitFunction;

    /**
     *
     */
    protected boolean overDispersionSelected;
    private FitFunctionTypeEnum selectedFitFunctionType;

    private boolean overDispersionSelectedDownHole;

    private boolean belowDetection;
    private Matrix SlogRatioX_Yfull;
    private transient Matrix SlogRatioX_Y;
    private transient Matrix matrixSxyod;
    private boolean calculatedInitialFitFunctions;
    // these three introduced feb 2013 to streamline choice of points in function fitting
    private double[] activeXvalues;
    private double[] activeYvalues;
    private boolean[] activeData;//dataActiveMap with inactive removed, i.e. all true
    // nov 2014
    private Matrix topSopbclr;
    private Matrix botSopbclr;

    /**
     *
     * @param rawRatioName
     * @param topIsotope
     * @param botIsotope
     * @param usedForFractionationCorrections
     * @param usedForCommonLeadCorrection the value of
     * usedForCommonLeadCorrection
     * @param collectorDataFrequencyMillisecs
     */
    public RawRatioDataModel( //
            RawRatioNames rawRatioName, DataModelInterface topIsotope, DataModelInterface botIsotope, boolean usedForFractionationCorrections, boolean usedForCommonLeadCorrection, long collectorDataFrequencyMillisecs) {

        this.rawRatioName = rawRatioName;
        this.topIsotope = topIsotope;
        this.botIsotope = botIsotope;
        this.usedForFractionationCorrections = usedForFractionationCorrections;
        this.usedForCommonLeadCorrections = usedForCommonLeadCorrection;
        this.COLLECTOR_DATA_FREQUENCY_MILLISECS = collectorDataFrequencyMillisecs;

        this.USING_FULL_PROPAGATION = true;
        this.correctedRatios = null;
        this.ratios = null;
        this.logRatios = null;
        this.alphas = null;
        this.fitFunctionLogValues = null;
        this.logDifferencesFromWeightedMean = null;

        this.meanOfResidualsFromFittedFractionation = 0.0;
        this.stdErrOfmeanOfResidualsFromFittedFractionation = 0.0;

        this.meanOfCorrectedRatios = 0.0;
        this.stdDevOfCorrectedRatios = 0.0;
        this.stdErrOfMeanCorrectedRatios = 0.0;

        this.logRatioFitFunctionsNoOD = new TreeMap<>();
        this.logRatioFitFunctionsWithOD = new TreeMap<>();
        this.overDispersionSelected = true;
        this.selectedFitFunctionType = FitFunctionTypeEnum.LINE;

        this.overDispersionSelectedDownHole = true;

        this.belowDetection = false;
        this.SlogRatioX_Yfull = null;
        this.SlogRatioX_Y = null;

        this.calculatedInitialFitFunctions = false;

    }

    /**
     *
     * @param rm
     * @return
     */
    @Override
    public int compareTo(RawRatioDataModel rm) {
        String rmName = rm.getRawRatioModelName().getName();
        String myName = this.getRawRatioModelName().getName();

        Comparator<String> intuitiveString = new IntuitiveStringComparator<>();
        return intuitiveString.compare(myName, rmName);
    }

    // TODO: equals and hashcode
    /**
     *
     * @param index
     * @param included
     */
    @Override
    public void toggleOneDataAquisition(int index, boolean included) {
        dataActiveMap[index] = included;
        topIsotope.toggleOneDataAquisition(index, included);
        botIsotope.toggleOneDataAquisition(index, included);
    }

    /**
     *
     */
    @Override
    public void applyMaskingArray() {

        dataActiveMap = MaskingSingleton.getInstance().applyMask(dataActiveMap.clone());//    .getMaskingArray().clone();
        topIsotope.applyMaskingArray();
        botIsotope.applyMaskingArray();
    }

    /**
     *
     */
    public void calculateRawAndLogRatios() {
        ratios = new double[((RawIntensityDataModel) topIsotope).getOnPeakVirtualCollector().getIntensities().length];
        logRatios = new double[ratios.length];

        if (((RawIntensityDataModel) topIsotope).isBelowDetection() || ((RawIntensityDataModel) botIsotope).isBelowDetection()) {
            belowDetection = true;
        } else {

            double[] topCorrectedIntensities;
            double[] botCorrectedIntensities;
            double[] topLogCorrectedIntensities;
            double[] botLogCorrectedIntensities;

            topCorrectedIntensities = ((RawIntensityDataModel) topIsotope).getOnPeakVirtualCollector().getCorrectedIntensities();
            botCorrectedIntensities = ((RawIntensityDataModel) botIsotope).getOnPeakVirtualCollector().getCorrectedIntensities();

            topLogCorrectedIntensities = ((RawIntensityDataModel) topIsotope).getOnPeakVirtualCollector().getLogCorrectedIntensities();
            botLogCorrectedIntensities = ((RawIntensityDataModel) botIsotope).getOnPeakVirtualCollector().getLogCorrectedIntensities();

            alphas = new double[topCorrectedIntensities.length];
            fitFunctionLogValues = new double[topCorrectedIntensities.length];

            // calculate ratios
            for (int i = 0; i < ratios.length; i++) {

                double top = topCorrectedIntensities[i];
                double bot = botCorrectedIntensities[i];

                // dec 2012 these ratios are going to be for plotting only as we switch to log ratios for unct prop
                ratios[i] = top / bot;
                logRatios[i] = topLogCorrectedIntensities[i] - botLogCorrectedIntensities[i];
            }
            calculateAlphas();
        }
    }

    /**
     *
     * @return
     */
    public String outputLogRatios() {
        String retval
                = //
                this.getDataModelName() + " \tLogRatios:\t";

        for (int i = 0; i < logRatios.length; i++) {
            retval += logRatios[i] + ", ";
        }

        return retval;
    }

    /**
     *
     */
    public void propagateUnctInRatios() {

        // April 2015
        // refactor to improve performance
        // Since SLogRatioX_Y is calculated on the first pass, we can merely
        // toggle rows and columns per dataactive map instead of recalculating everything
        // make the current version transient and save only the full        
        if (SlogRatioX_Yfull == null) {
            // create all true dataActiveMap for initial pass
            boolean[] allTrueDataActiveMap = new boolean[dataActiveMap.length];
            Arrays.fill(allTrueDataActiveMap, Boolean.TRUE);

            boolean[] topDataActiveMap = ((RawIntensityDataModel) topIsotope).getOnPeakVirtualCollector().getDataActiveMap();
            ((RawIntensityDataModel) topIsotope).getOnPeakVirtualCollector().setDataActiveMap(allTrueDataActiveMap);

            ((RawIntensityDataModel) topIsotope).prepareDataForFitFunctions();
            ((RawIntensityDataModel) topIsotope).propagateUnctInBaselineCorrOnPeakIntensities();

            boolean[] botDataActiveMap = ((RawIntensityDataModel) botIsotope).getOnPeakVirtualCollector().getDataActiveMap();
            ((RawIntensityDataModel) botIsotope).getOnPeakVirtualCollector().setDataActiveMap(allTrueDataActiveMap);//dataActiveMap.clone());

            ((RawIntensityDataModel) botIsotope).prepareDataForFitFunctions();
            ((RawIntensityDataModel) botIsotope).propagateUnctInBaselineCorrOnPeakIntensities();

            Matrix numerator = ((RawIntensityDataModel) topIsotope).getSopbclr();
            Matrix denominator = ((RawIntensityDataModel) botIsotope).getSopbclr();

            if ((numerator != null) & (denominator != null)) {

                // only if both numerator and denominator are ion counters do we do matrixSxyod below       
                if ((topIsotope.getCollectorModel() //
                        instanceof IonCounterCollectorModel)//
                        && //
                        (botIsotope.getCollectorModel() //
                        instanceof IonCounterCollectorModel)//
                        &&//
                        // v3 jan 2013 check if the SAME ion counter
                        hasTwoIdenticalIonCounters()) {

                    matrixSxyod
                            = ((RawIntensityDataModel) topIsotope).getColumnVectorOfCorrectedOnPeakIntensities()//
                            .times(((RawIntensityDataModel) botIsotope).getColumnVectorOfCorrectedOnPeakIntensities().transpose());

                    double deadtimeOneSigmaAbsSqr
                            = ((IonCounterCollectorModel) botIsotope//
                            .getCollectorModel()).getDeadTime().getOneSigmaAbs().movePointLeft(0).pow(2).doubleValue();

                    matrixSxyod.timesEquals(deadtimeOneSigmaAbsSqr);

                    SlogRatioX_Yfull = numerator.plus(denominator).minus(matrixSxyod.times(2.0));

                } else {
                    try {
                        SlogRatioX_Yfull = numerator.plus(denominator);
                    } catch (Exception e) {
                        System.out.println("SlogRatioX_Yfull trouble" + e.getMessage());
                    }
                }

                // restore active data maps to normal status
                ((RawIntensityDataModel) topIsotope).getOnPeakVirtualCollector().setDataActiveMap(topDataActiveMap);
                ((RawIntensityDataModel) botIsotope).getOnPeakVirtualCollector().setDataActiveMap(botDataActiveMap);

                topSopbclr = numerator.copy();
                botSopbclr = denominator.copy();
            }

            ((DataModelFitFunctionInterface) topIsotope).cleanupUnctCalcs();
            ((DataModelFitFunctionInterface) botIsotope).cleanupUnctCalcs();

            System.gc();

        }
        calculateSlogRatioX_Y();
    }

    public void calculateSlogRatioX_Y() {
        calculateSlogRatioX_Y(dataActiveMap);
    }

    public void calculateSlogRatioX_Y(boolean[] mapOfActiveData) {
        // choose rows and columns based on active data
        // nov 2014 need to catch special case where */pb204 ratios have different dataactivemaps
        if (SlogRatioX_Yfull != null) {
            ArrayList<Integer> selectedRowsColsList = new ArrayList<>();
            for (int i = 0; i < mapOfActiveData.length; i++) {
                if (mapOfActiveData[i]) {
                    selectedRowsColsList.add(i);
                }
            }

            int[] selectedRowsCols = new int[selectedRowsColsList.size()];
            for (int i = 0; i < selectedRowsCols.length; i++) {
                selectedRowsCols[i] = selectedRowsColsList.get(i);
            }

            SlogRatioX_Y = SlogRatioX_Yfull.getMatrix(selectedRowsCols, selectedRowsCols);
        } else {
            SlogRatioX_Y = null;
        }
    }

    /**
     *
     * @return
     */
    public String outputLogRatioFitFunctionParameters() {
        String retval = getDataModelName() + "\n";
        AbstractFunctionOfX fOfXcurrent = getSelectedFitFunction();

        if (fOfXcurrent == null) {
            retval += "NO FIT FUNCTION";
        } else {
            retval += fOfXcurrent.showParameters() + "\n";
        }

        return retval;
    }

    /**
     *
     * @return
     */
    public String outputLogRatioFitFunctionYInterceptData() {
        String retval = getDataModelName() + "\n";
        AbstractFunctionOfX fOfXcurrent = getSelectedFitFunction();
        if (fOfXcurrent == null) {
            retval += "NO FIT FUNCTION";
        } else {
            retval += fOfXcurrent.showYInterceptData() + "\n";
        }
        return retval;
    }

    /**
     *
     * @return
     */
    public double[] calculateAlphas() {

        for (int i = 0; i < alphas.length; i++) {
            alphas[i] = calculateAlpha(logRatios[i]);
        }

        return alphas;
    }

    /**
     *
     * @param logRatio
     * @return
     */
    public double calculateAlpha(double logRatio) {
        return (standardValue / Math.exp(logRatio)) - 1.0;
    }

    /**
     *
     */
    public void generateFitFunctionsForDownhole() {
        // calculate the logDifferencesFromWeightedMean between logratios and fit function

        if (downHoleFitFunction != null) {
            boolean[] dataCommonActiveMap = MaskingSingleton.getInstance().getMaskingArray();
            int countOfActiveData = 0;
            for (int i = 0; i < dataCommonActiveMap.length; i++) {
                if (dataCommonActiveMap[i]) {
                    countOfActiveData++;
                }
            }

            activeData = new boolean[countOfActiveData];
            logDifferencesFromWeightedMean = new double[countOfActiveData];
            double[] normalizedOnPeakAquireTimes = getNormalizedOnPeakAquireTimes();
            ArrayList<Integer> matrixIndicesToRemove = new ArrayList<>();

            int index = 0;
            for (int i = 0; i < dataCommonActiveMap.length; i++) {
                if (dataCommonActiveMap[i]) {
                    activeData[index] = true;
                    logDifferencesFromWeightedMean[index] = downHoleFitFunction.f(normalizedOnPeakAquireTimes[i]) - logRatios[i];
                    index++;
                }
            }

            Matrix matrixSfCopy = downHoleFitFunction.getMatrixSf().copy();

            calculateSlogRatioX_Y(dataCommonActiveMap);
            Matrix SfPlusSlogRarioX_Y = matrixSfCopy.plus(getSlogRatioX_Y(false));

            AbstractOverDispersionLMAlgorithm algorithmForMEAN = LevenbergMarquardGeneralSolverWithCovS.getInstance()//
                    .getSelectedLMAlgorithm(//
                            FitFunctionTypeEnum.MEAN,//
                            activeData, //
                            null, //this is mean so x does not matter
                            logDifferencesFromWeightedMean,//
                            SfPlusSlogRarioX_Y,//matrixSfCopy.plus(getSlogRatioX_Y(false)),//
                            false);

            // algorithmForMEAN contains both the non OD and OD versions
            AbstractFunctionOfX fOfX_MEAN = algorithmForMEAN.getInitialFofX();
            AbstractFunctionOfX fOfX_MEAN_OD;
            if ((fOfX_MEAN != null) && fOfX_MEAN.verifyPositiveVariances()) {

                fOfX_MEAN_OD = algorithmForMEAN.getFinalFofX();

                if ((fOfX_MEAN_OD != null) && fOfX_MEAN_OD.verifyPositiveVariances()) {
                } else {
                    fOfX_MEAN_OD = fOfX_MEAN;
                }

            } else {
                // to handle really bad data sets, for which LM wont work, do good old fashioned mean
                System.out.println("LM would not fit mean , so using arithmetic mean fit");
                fOfX_MEAN = MeanFitFunction.getInstance()//
                        .getFunctionOfX(//
                                activeData, //
                                activeXvalues, //
                                logDifferencesFromWeightedMean,//
                                SfPlusSlogRarioX_Y,//matrixSfCopy.plus(getSlogRatioX_Y(false)),//
                                false);

                fOfX_MEAN_OD = fOfX_MEAN;
            }

            meanOfResidualsFromFittedFractionation = fOfX_MEAN_OD.getA();
            stdErrOfmeanOfResidualsFromFittedFractionation = fOfX_MEAN_OD.getStdErrOfA();

            logRatioFitFunctionsNoOD.put(FitFunctionTypeEnum.MEAN_DH.getName(), fOfX_MEAN);
            logRatioFitFunctionsWithOD.put(FitFunctionTypeEnum.MEAN_DH.getName(), fOfX_MEAN_OD);
            overDispersionSelectedDownHole = true;
        }
    }

    /**
     *
     */
    @Override
    public void calculateCorrectedRatioStatistics() {
        NonParametricStats nonParametricStats = NonParametricStats.getInstance();

        if (correctedRatios != null) {
            nonParametricStats.calculateStats(dataActiveMap, correctedRatios);

            meanOfCorrectedRatios = nonParametricStats.getSampleMean();
            stdDevOfCorrectedRatios = Math.sqrt(nonParametricStats.getVariance());
            stdErrOfMeanCorrectedRatios = nonParametricStats.getStdErrSampleMean();
        }
    }

    private void generateCONSTANTfitFunction() {
        // CONSTANT *********************************************************

        AbstractFunctionOfX fOfX_CONSTANT;

        if (USING_FULL_PROPAGATION) {
            fOfX_CONSTANT = ConstantFitFunctionWithCovS.getInstance().getFunctionOfX(//
                    activeData, //
                    activeXvalues, //
                    activeYvalues, //,
                    SlogRatioX_Y, false);
        } else {
            fOfX_CONSTANT = ConstantFitFunctionWithCovS.getInstance().getFunctionOfX(//
                    activeData, //
                    activeXvalues, //
                    activeYvalues, //,
                    SlogRatioX_Y, false);
        }

        logRatioFitFunctionsNoOD.put(FitFunctionTypeEnum.CONSTANT.getName(), fOfX_CONSTANT);
        logRatioFitFunctionsWithOD.put(FitFunctionTypeEnum.CONSTANT.getName(), fOfX_CONSTANT);

    }

    private void generateMEANfitFunctionForPbcRatiosWhereNegativeValues() {

        //create mean ratio (no log due to negative values)- we are here because this ratio is */204 and has >10% negative values 
        NonParametricStats nonParametricStats = NonParametricStats.getInstance();
        double[] isotopeOPBC = ((RawIntensityDataModel) topIsotope).getOnPeakVirtualCollector().getCorrectedIntensities();
        nonParametricStats.calculateStats(dataActiveMap, isotopeOPBC);
        ((RawIntensityDataModel) topIsotope).setForcedMeanForCommonLeadRatios(nonParametricStats.getSampleMean());

        isotopeOPBC = ((RawIntensityDataModel) botIsotope).getOnPeakVirtualCollector().getCorrectedIntensities();
        nonParametricStats.calculateStats(dataActiveMap, isotopeOPBC);
        ((RawIntensityDataModel) botIsotope).setForcedMeanForCommonLeadRatios(nonParametricStats.getSampleMean());

        double forcedMean;
        try {
            forcedMean = ((RawIntensityDataModel) topIsotope).getForcedMeanForCommonLeadRatios() / ((RawIntensityDataModel) botIsotope).getForcedMeanForCommonLeadRatios();
        } catch (Exception e) {
            forcedMean = 0.0;
        }

        double[] forcedMeanValues = new double[activeData.length];
        for (int i = 0; i < forcedMeanValues.length; i++) {
            forcedMeanValues[i] = forcedMean;
        }

        AbstractFunctionOfX fOfX_FORCEDMEAN;

        // april 2015 replacing code below
        Matrix matrixTopMeanBotMean = new Matrix(//
                new double[]{//
                    ((RawIntensityDataModel) topIsotope).getForcedMeanForCommonLeadRatios(), //
                    ((RawIntensityDataModel) botIsotope).getForcedMeanForCommonLeadRatios()}, //
                1);
        fOfX_FORCEDMEAN = MeanFitFunction.getInstance()//
                .getFunctionOfX(//
                        activeData, //
                        activeXvalues, //
                        activeYvalues,//
                        matrixTopMeanBotMean,//null, //
                        false);

        fOfX_FORCEDMEAN.setShortName(FitFunctionTypeEnum.MEANRATIO);
        logRatioFitFunctionsNoOD.put(FitFunctionTypeEnum.MEANRATIO.getName(), fOfX_FORCEDMEAN);
        logRatioFitFunctionsWithOD.put(FitFunctionTypeEnum.MEANRATIO.getName(), fOfX_FORCEDMEAN);

        logRatioFitFunctionsNoOD.remove(FitFunctionTypeEnum.MEAN.getName());
        logRatioFitFunctionsWithOD.remove(FitFunctionTypeEnum.MEAN.getName());
    }

    private boolean generateMEANfitFunctionUsingLM() {

        boolean retVal;

        // algorithmForMEAN contains both the non OD and OD versions
        AbstractFunctionOfX fOfX_MEAN;
        AbstractFunctionOfX fOfX_MEAN_OD;

        if (USING_FULL_PROPAGATION) {
            try {
                AbstractOverDispersionLMAlgorithm algorithmForMEAN = LevenbergMarquardGeneralSolverWithCovS.getInstance()//
                        .getSelectedLMAlgorithm(//
                                FitFunctionTypeEnum.MEAN,//
                                activeData, //
                                activeXvalues, //
                                activeYvalues, //,
                                SlogRatioX_Y, false);

                fOfX_MEAN = algorithmForMEAN.getInitialFofX();
                fOfX_MEAN_OD = algorithmForMEAN.getFinalFofX();
            } catch (Exception e) {
                fOfX_MEAN = null;
                fOfX_MEAN_OD = null;
            }
        } else {
            try {
                AbstractOverDispersionLMVecAlgorithm algorithmForMEAN = LevenbergMarquardGeneralSolverWithVecV.getInstance()//
                        .getSelectedLMAlgorithm(//
                                FitFunctionTypeEnum.MEAN,//
                                activeData, //
                                activeXvalues, //
                                activeYvalues, //,
                                SlogRatioX_Y, false);

                fOfX_MEAN = algorithmForMEAN.getInitialFofX();
                fOfX_MEAN_OD = algorithmForMEAN.getFinalFofX();
            } catch (Exception e) {
                fOfX_MEAN = null;
                fOfX_MEAN_OD = null;
            }
        }

        if ((fOfX_MEAN != null) && fOfX_MEAN.verifyPositiveVariances()) {
            if (logRatioFitFunctionsNoOD.containsKey(fOfX_MEAN.getShortNameString())) {
                logRatioFitFunctionsNoOD.remove(fOfX_MEAN.getShortNameString());
                logRatioFitFunctionsNoOD.put(fOfX_MEAN.getShortNameString(), fOfX_MEAN);
            } else {
                logRatioFitFunctionsNoOD.put(fOfX_MEAN.getShortNameString(), fOfX_MEAN);
            }

            if ((fOfX_MEAN_OD != null) && fOfX_MEAN_OD.verifyPositiveVariances()) {
                if (logRatioFitFunctionsWithOD.containsKey(fOfX_MEAN_OD.getShortNameString())) {
                    logRatioFitFunctionsWithOD.remove(fOfX_MEAN_OD.getShortNameString());
                    logRatioFitFunctionsWithOD.put(fOfX_MEAN_OD.getShortNameString(), fOfX_MEAN_OD);
                } else {
                    logRatioFitFunctionsWithOD.put(fOfX_MEAN_OD.getShortNameString(), fOfX_MEAN_OD);
                }
            } else {
                logRatioFitFunctionsWithOD.put(fOfX_MEAN.getShortNameString(), fOfX_MEAN);
            }

            retVal = true;

        } else {
            // to handle really bad data sets, for which LM wont work, do good old fashioned mean
            System.out.println("LM would not fit mean , so using arithmetic mean fit");
            // nov 2014 - for the case of common lead */pb204 we use the matrix slot for a matrix containing the two means top and bottom
            Matrix matrixTopMeanBotMean = new Matrix(//
                    new double[]{//
                        ((RawIntensityDataModel) topIsotope).getForcedMeanForCommonLeadRatios(), //
                        ((RawIntensityDataModel) botIsotope).getForcedMeanForCommonLeadRatios()}, //
                    1);
            fOfX_MEAN = MeanFitFunction.getInstance()//
                    .getFunctionOfX(//
                            activeData, //
                            activeXvalues, //
                            activeYvalues,//
                            matrixTopMeanBotMean,//null, //
                            false);

            logRatioFitFunctionsNoOD.put(FitFunctionTypeEnum.MEAN.getName(), fOfX_MEAN);
            logRatioFitFunctionsWithOD.put(FitFunctionTypeEnum.MEAN.getName(), fOfX_MEAN);

            selectedFitFunctionType = FitFunctionTypeEnum.MEAN;

            retVal = false;
        }

        return retVal;
    }

    private void generateLINEfitFunctionUsingLM() {

        // algorithmForLINE contains both the non OD and OD versions
        AbstractFunctionOfX fOfX_LINE;
        AbstractFunctionOfX fOfX_LINE_OD;

        if (USING_FULL_PROPAGATION) {
            AbstractOverDispersionLMAlgorithm algorithmForLINE = LevenbergMarquardGeneralSolverWithCovS.getInstance()//
                    .getSelectedLMAlgorithm(//
                            FitFunctionTypeEnum.LINE,//
                            activeData, //
                            activeXvalues, //
                            activeYvalues, //,
                            SlogRatioX_Y, false);

            fOfX_LINE = algorithmForLINE.getInitialFofX();
            fOfX_LINE_OD = algorithmForLINE.getFinalFofX();

        } else {
            AbstractOverDispersionLMVecAlgorithm algorithmForLINE = LevenbergMarquardGeneralSolverWithVecV.getInstance()//
                    .getSelectedLMAlgorithm(//
                            FitFunctionTypeEnum.LINE,//
                            activeData, //
                            activeXvalues, //
                            activeYvalues, //,
                            SlogRatioX_Y, false);

            fOfX_LINE = algorithmForLINE.getInitialFofX();
            fOfX_LINE_OD = algorithmForLINE.getFinalFofX();
        }

        if ((fOfX_LINE != null) && fOfX_LINE.verifyPositiveVariances()) {
            if (logRatioFitFunctionsNoOD.containsKey(fOfX_LINE.getShortNameString())) {
                logRatioFitFunctionsNoOD.remove(fOfX_LINE.getShortNameString());
                logRatioFitFunctionsNoOD.put(fOfX_LINE.getShortNameString(), fOfX_LINE);
            } else {
                logRatioFitFunctionsNoOD.put(fOfX_LINE.getShortNameString(), fOfX_LINE);
            }

            if ((fOfX_LINE_OD != null) && fOfX_LINE_OD.verifyPositiveVariances()) {
                if (logRatioFitFunctionsWithOD.containsKey(fOfX_LINE_OD.getShortNameString())) {
                    logRatioFitFunctionsWithOD.remove(fOfX_LINE_OD.getShortNameString());
                    logRatioFitFunctionsWithOD.put(fOfX_LINE_OD.getShortNameString(), fOfX_LINE_OD);

                } else {
                    logRatioFitFunctionsWithOD.put(fOfX_LINE_OD.getShortNameString(), fOfX_LINE_OD);
                }
            } else {
                logRatioFitFunctionsWithOD.put(fOfX_LINE.getShortNameString(), fOfX_LINE);
            }
        } else {
            logRatioFitFunctionsNoOD.remove(FitFunctionTypeEnum.LINE.getName());
            logRatioFitFunctionsWithOD.remove(FitFunctionTypeEnum.LINE.getName());
            selectedFitFunctionType = FitFunctionTypeEnum.MEAN;
        }
    }

    private void generateEXPONENTIALfitFunctionUsingLM() {

        System.out.println("trying expfast");

        AbstractFunctionOfX fOfX_ExpFast = null;

        if (USING_FULL_PROPAGATION) {
            AbstractOverDispersionLMAlgorithm algorithmForEXPFAST = LevenbergMarquardGeneralSolverWithCovS.getInstance().getSelectedLMAlgorithm(//
                    FitFunctionTypeEnum.EXPFAST,//
                    activeData, //
                    activeXvalues, //
                    activeYvalues, //,
                    SlogRatioX_Y, false);

            fOfX_ExpFast = algorithmForEXPFAST.getInitialFofX();

        } else {

            AbstractOverDispersionLMVecAlgorithm algorithmForEXPFAST = LevenbergMarquardGeneralSolverWithVecV.getInstance().getSelectedLMAlgorithm(//
                    FitFunctionTypeEnum.EXPFAST,//
                    activeData, //
                    activeXvalues, //
                    activeYvalues, //,
                    SlogRatioX_Y, false);

            fOfX_ExpFast = algorithmForEXPFAST.getInitialFofX();
        }

        AbstractOverDispersionLMAlgorithmInterface algorithmForEXPMAT;

        if (fOfX_ExpFast != null) //
        {
            System.out.println("now trying expmat with expfast input");

            if (USING_FULL_PROPAGATION) {
                algorithmForEXPMAT = LevenbergMarquardGeneralSolverWithCovS.getInstance().getSelectedLMAlgorithmUsingIntialFofX(//
                        FitFunctionTypeEnum.EXPMAT,//
                        activeData, //
                        activeXvalues, //
                        activeYvalues, //,
                        SlogRatioX_Y, false, //
                        fOfX_ExpFast);
            } else {

                algorithmForEXPMAT = LevenbergMarquardGeneralSolverWithVecV.getInstance().getSelectedLMAlgorithmUsingIntialFofX(//
                        FitFunctionTypeEnum.EXPMAT,//
                        activeData, //d
                        activeXvalues,
                        activeYvalues, //,
                        SlogRatioX_Y, false, //
                        fOfX_ExpFast);
            }

            AbstractFunctionOfX fOfX_EXPMAT = algorithmForEXPMAT.getFinalFofX();

            if ((fOfX_EXPMAT != null) && (fOfX_EXPMAT.verifyPositiveVariances())) {
                if (logRatioFitFunctionsNoOD.containsKey(fOfX_EXPMAT.getShortNameString())) {
                    logRatioFitFunctionsNoOD.remove(fOfX_EXPMAT.getShortNameString());
                    logRatioFitFunctionsNoOD.put(fOfX_EXPMAT.getShortNameString(), fOfX_EXPMAT);

                } else {
                    logRatioFitFunctionsNoOD.put(fOfX_EXPMAT.getShortNameString(), fOfX_EXPMAT);
                }

                if (fOfX_EXPMAT.getMSWD() >= 1.0) {
                    System.out.println("now trying expOD with expfast input");

                    AbstractFunctionOfX fOfX_EXPOD;

                    if (USING_FULL_PROPAGATION) {
                        AbstractOverDispersionLMAlgorithm algorithmForEXPOD = LevenbergMarquardGeneralSolverWithCovS.getInstance().getSelectedLMAlgorithmUsingIntialFofX(//
                                FitFunctionTypeEnum.EXPONENTIAL,//
                                activeData, //
                                activeXvalues, //
                                activeYvalues, //,
                                SlogRatioX_Y, false,//
                                fOfX_ExpFast);

                        fOfX_EXPOD = algorithmForEXPOD.getFinalFofX();
                    } else {
                        AbstractOverDispersionLMVecAlgorithm algorithmForEXPOD = LevenbergMarquardGeneralSolverWithVecV.getInstance().getSelectedLMAlgorithmUsingIntialFofX(//
                                FitFunctionTypeEnum.EXPONENTIAL,//
                                activeData, //
                                activeXvalues, //
                                activeYvalues, //,
                                SlogRatioX_Y, false,//
                                fOfX_ExpFast);//

                        fOfX_EXPOD = algorithmForEXPOD.getFinalFofX();
                    }

                    if ((fOfX_EXPOD != null) && (fOfX_EXPOD.verifyPositiveVariances())) {

                        if (logRatioFitFunctionsWithOD.containsKey(fOfX_EXPOD.getShortNameString())) {
                            logRatioFitFunctionsWithOD.remove(fOfX_EXPOD.getShortNameString());
                            logRatioFitFunctionsWithOD.put(fOfX_EXPOD.getShortNameString(), fOfX_EXPOD);
                        } else {
                            logRatioFitFunctionsWithOD.put(fOfX_EXPOD.getShortNameString(), fOfX_EXPOD);
                        }
                    } else {
                        logRatioFitFunctionsWithOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());
                        selectedFitFunctionType = FitFunctionTypeEnum.LINE;
                    }

                } else {
                    logRatioFitFunctionsWithOD.put(fOfX_EXPMAT.getShortNameString(), fOfX_EXPMAT);
                }
            } else {
                logRatioFitFunctionsNoOD.remove(FitFunctionTypeEnum.EXPMAT.getName());
                logRatioFitFunctionsWithOD.remove(FitFunctionTypeEnum.EXPMAT.getName());
                logRatioFitFunctionsNoOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());
                logRatioFitFunctionsWithOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());
                selectedFitFunctionType = FitFunctionTypeEnum.LINE;
            }
        } else {
            logRatioFitFunctionsNoOD.remove(FitFunctionTypeEnum.EXPMAT.getName());
            logRatioFitFunctionsWithOD.remove(FitFunctionTypeEnum.EXPMAT.getName());
            logRatioFitFunctionsNoOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());
            logRatioFitFunctionsWithOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());
            selectedFitFunctionType = FitFunctionTypeEnum.LINE;
        }

        // march 2014 be sure line is valid
        if (!logRatioFitFunctionsNoOD.containsKey(selectedFitFunctionType.getName())) {
            selectedFitFunctionType = FitFunctionTypeEnum.MEAN;
        }
        if (!logRatioFitFunctionsWithOD.containsKey(selectedFitFunctionType.getName())) {
            selectedFitFunctionType = FitFunctionTypeEnum.MEAN;
        }
    }

    /**
     *
     */
    @Override
    public void cleanupUnctCalcs() {
        // April 2015 saving a copy and using transient setSlogRatioX_Y(null);
        setMatrixSxyod(null);
    }

    /**
     *
     *
     * @param propagateUncertainties the value of propagateUncertainties
     * @param doApplyMaskingArray the value of doApplyMaskingArray
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void generateSetOfFitFunctions(boolean propagateUncertainties, boolean doApplyMaskingArray, boolean inLiveMode) {
        if (!belowDetection && (usedForFractionationCorrections || usedForCommonLeadCorrections)) {

            // april 2014
            if (doApplyMaskingArray) {
                applyMaskingArray();
            }

            // nov 2014 - detect if common lead ratios have entered or left negative territory
            if (botIsotope.getDataModelName().equalsIgnoreCase(IsotopeNames.Pb204.getName())) {
                NonParametricStats nonParametricStats = NonParametricStats.getInstance();
                ((RawIntensityDataModel) botIsotope).setForceMeanForCommonLeadRatios(//
                        nonParametricStats.determineIfTenPercentOrMoreAreNegative(//
                                dataActiveMap, //
                                ((RawIntensityDataModel) botIsotope).getOnPeakVirtualCollector().getCorrectedIntensities()));
            }

            if (propagateUncertainties) {
                propagateUnctInRatios();
            } else {
                //nov 2015
                calculateSlogRatioX_Y();
            }

            // feb 2013 clean up choice of points
            int countOfActiveData = 0;
            for (int i = 0; i < dataActiveMap.length; i++) {
                if (dataActiveMap[i]) {
                    countOfActiveData++;
                }
            }

            activeData = new boolean[countOfActiveData];
            activeXvalues = new double[countOfActiveData];
            activeYvalues = new double[countOfActiveData];

            double[] topCorrectedIntensities = ((RawIntensityDataModel) topIsotope).getOnPeakVirtualCollector().getCorrectedIntensities();
            double[] activeTopCorrectedIntensities = new double[countOfActiveData];
            double[] botCorrectedIntensities = ((RawIntensityDataModel) botIsotope).getOnPeakVirtualCollector().getCorrectedIntensities();
            double[] activeBotCorrectedIntensities = new double[countOfActiveData];

            double[] onPeakTimes = topIsotope.getNormalizedOnPeakAquireTimes();
            int index = 0;
            for (int i = 0; i < dataActiveMap.length; i++) {
                if (dataActiveMap[i]) {
                    activeData[index] = true;
                    activeXvalues[index] = onPeakTimes[i];
                    activeYvalues[index] = logRatios[i];

                    activeTopCorrectedIntensities[index] = topCorrectedIntensities[i];
                    activeBotCorrectedIntensities[index] = botCorrectedIntensities[i];

                    index++;
                }
            }

            // feb 2013 new strategy to do only once
            // also MEAN returns false if it had to use an arithmentic mean and stops further processing
            System.out.println("\nCalculate Fit Functions for Ratio  " + getRawRatioModelName().getDisplayName() //
                    + "  USING " + (USING_FULL_PROPAGATION ? "FULL PROPAGATION" : "FAST PROPAGATION") + "  COUNT = " + countOfActiveData);

            // June 2016 in support of live mode
            if (inLiveMode && rawRatioName.compareTo(RawRatioNames.r206_207w) == 0) {
                selectedFitFunctionType = FitFunctionTypeEnum.MEAN;
            }

            FitFunctionTypeEnum saveSelection = selectedFitFunctionType;
            // nov 2014
            if (isForceMeanForCommonLeadRatios()) {
                generateMEANfitFunctionForPbcRatiosWhereNegativeValues();
                saveSelection = FitFunctionTypeEnum.MEANRATIO;

            } else if (generateMEANfitFunctionUsingLM()) {

                // nov 2014 force mean for Pbc */204 cases where negative values (<10%) have been turned off by not proceeding
                logRatioFitFunctionsNoOD.remove(FitFunctionTypeEnum.MEANRATIO.getName());
                logRatioFitFunctionsWithOD.remove(FitFunctionTypeEnum.MEANRATIO.getName());
                if (!usedForCommonLeadCorrections) {
                    try {
                        generateLINEfitFunctionUsingLM();
                    } catch (Exception e) {
                        System.out.println("Exception generating LINE");
                        logRatioFitFunctionsNoOD.remove(FitFunctionTypeEnum.LINE.getName());
                        logRatioFitFunctionsWithOD.remove(FitFunctionTypeEnum.LINE.getName());
                    }

                    try {
                        generateEXPONENTIALfitFunctionUsingLM();
                    } catch (Exception e) {
                        System.out.println("Exception generating EXPONENTIAL");
                        logRatioFitFunctionsNoOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());
                        logRatioFitFunctionsWithOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());
                    }

                    calculatedInitialFitFunctions = true;

                    if (hasTwoIdenticalIonCounters()) {
                        // section 7.a.2
                        double[] intDiffValues = new double[countOfActiveData];
                        for (int i = 0; i < countOfActiveData; i++) {
                            intDiffValues[i] = activeTopCorrectedIntensities[i] - activeBotCorrectedIntensities[i];
                        }

                        Matrix matrixIntDiff = new Matrix(intDiffValues, countOfActiveData);

                        //for each of the fit functions - two cases = od and no od
                        Iterator<String> sessionFitFuncsNoOdIterator = logRatioFitFunctionsNoOD.keySet().iterator();
                        while (sessionFitFuncsNoOdIterator.hasNext()) {
                            String key = sessionFitFuncsNoOdIterator.next();
                            // skip downhole function
                            if (key.compareToIgnoreCase(FitFunctionTypeEnum.MEAN_DH.getName()) != 0) {
                                AbstractFunctionOfX FofX = logRatioFitFunctionsNoOD.get(key);

                                if (FofX != null) {
                                    Matrix JIntp = FofX.assembleMatrixJIntp(SlogRatioX_Y);

                                    try {
                                        FofX.setdLrInt_dDt(JIntp.times(matrixIntDiff).get(0, 0));
                                    } catch (Exception e) {
                                    }
                                } else {
                                    logRatioFitFunctionsNoOD.remove(key);
                                }
                            }
                        }

                        Iterator<String> sessionFitFuncsWithOdIterator = logRatioFitFunctionsWithOD.keySet().iterator();
                        while (sessionFitFuncsWithOdIterator.hasNext()) {
                            String key = sessionFitFuncsWithOdIterator.next();
                            // skip downhole function
                            if (key.compareToIgnoreCase(FitFunctionTypeEnum.MEAN_DH.getName()) != 0) {
                                AbstractFunctionOfX FofX = logRatioFitFunctionsWithOD.get(key);

                                if (FofX != null) {
                                    double OD = FofX.getOverDispersion();
                                    Matrix ODdiag = Matrix.identity(countOfActiveData, countOfActiveData).times(OD);

                                    Matrix JIntp = FofX.assembleMatrixJIntp(SlogRatioX_Y.plus(ODdiag));

                                    try {
                                        FofX.setdLrInt_dDt(JIntp.times(matrixIntDiff).get(0, 0));
                                    } catch (Exception e) {
                                    }
                                } else {
                                    logRatioFitFunctionsWithOD.remove(key);
                                }
                            }
                        }
                    }
                }
            } else {
                saveSelection = FitFunctionTypeEnum.MEAN;//oct 2014 to catch failure to fit
            }

            if (saveSelection != null) {
                selectedFitFunctionType = saveSelection;
            }

            System.gc();
        }

    }

//    /**
//     *
//     * @param fitFunctionTypeName
//     */
//    @Override
//    public void calculateFittedFunctions(String fitFunctionTypeName) {
//
//    }
    /**
     *
     * @return
     */
    @Override
    public double[] getNormalizedOnPeakAquireTimes() {
        double[] normalizedAquire = new double[ratios.length];
        for (int i = 0; i < normalizedAquire.length; i++) {
            normalizedAquire[i]
                    =//
                    ((RawIntensityDataModel) topIsotope).getOnPeakVirtualCollector().getOnPeakAquireTimes()[i] / COLLECTOR_DATA_FREQUENCY_MILLISECS;
        }

        return normalizedAquire;
    }

    /**
     *
     * @return
     */
    @Override
    public double[] getOnPeakAquireTimesInSeconds() {
        double[] onPeakAquireTimesInSeconds = new double[ratios.length];
        for (int i = 0; i < onPeakAquireTimesInSeconds.length; i++) {
            onPeakAquireTimesInSeconds[i]
                    =//
                    ((RawIntensityDataModel) topIsotope).getOnPeakVirtualCollector().getOnPeakAquireTimes()[i] / 1000.0;
        }

        return onPeakAquireTimesInSeconds;
    }

    /**
     *
     */
    public void printData() {
        String retVal = rawRatioName.getName() + "\n";

        for (int i = 0; i < ratios.length; i++) {

            retVal += Double.toString(ratios[i]) + "\n";
        }

        System.out.println(retVal);
    }

    /**
     * @return the rawRatioName
     */
    @Override
    public RawRatioNames getRawRatioModelName() {
        return rawRatioName;
    }

    /**
     * @return the topIsotope
     */
    public RawIntensityDataModel getTopIsotope() {
        return (RawIntensityDataModel) topIsotope;
    }

    /**
     * @return the botIsotope
     */
    public RawIntensityDataModel getBotIsotope() {
        return (RawIntensityDataModel) botIsotope;
    }

    /**
     * @return the ratios
     */
    public double[] getRatios() {
        return ratios;
    }

    /**
     * @param ratios the ratios to set
     */
    public void setRatios(double[] ratios) {
        this.ratios = ratios;
    }

    /**
     * @return the alphas
     */
    public double[] getAlphas() {
        return alphas;
    }

    /**
     *
     * @param CollectorDataFrequencyMillisecs
     */
    @Override
    public void setCollectorDataFrequencyMillisecs(long CollectorDataFrequencyMillisecs) {
        COLLECTOR_DATA_FREQUENCY_MILLISECS = CollectorDataFrequencyMillisecs;
    }

    /**
     *
     * @return
     */
    @Override
    public long getCollectorDataFrequencyMillisecs() {
        return COLLECTOR_DATA_FREQUENCY_MILLISECS;
    }

    /**
     * @return the dataActiveMap
     */
    @Override
    public boolean[] getDataActiveMap() {
        return dataActiveMap;
    }

    /**
     * @param dataActiveMap the dataActiveMap to set
     */
    public void setDataActiveMap(boolean[] dataActiveMap) {
        this.dataActiveMap = dataActiveMap;
    }

    /**
     *
     * @return
     */
    @Override
    public String getDataModelName() {
        return rawRatioName.getDisplayName();
    }

    /**
     * @return the usedForFractionationCorrections
     */
    public boolean isUsedForFractionationCorrections() {
        return usedForFractionationCorrections;
    }

    /**
     * @param standardValue the standardValue to set
     */
    public void setStandardValue(double standardValue) {
        this.standardValue = standardValue;
    }

    /**
     *
     * @param sessionTechnique
     * @return
     */
    public double getSessionValueBySessionTechnique(String sessionTechnique) {
        double retVal = 0.0;
        if (sessionTechnique.compareToIgnoreCase("DOWNHOLE") == 0) {
            try {
                retVal = getSelectedDownHoleFitFunction().getA();//updated june 2015         getMeanOfResidualsFromFittedFractionation();
            } catch (Exception e) {
                retVal = 0.0;
            }
        } else if (sessionTechnique.compareToIgnoreCase("INTERCEPT") == 0) {
            try {
                retVal = getSelectedFitFunction().getYIntercept();
            } catch (Exception e) {
                retVal = 0.0;
            }
        }

        return retVal;
    }

    /**
     *
     * @param sessionTechnique the value of sessionTechnique
     * @return the double
     */
    public double getSessionErrorBySessionTechnique(String sessionTechnique) {
        double retVal = 0.0;
        if (sessionTechnique.compareToIgnoreCase("DOWNHOLE") == 0) {
            try {
                retVal = getSelectedDownHoleFitFunction().getStdErrOfA();//updated june 2015 getStdErrOfmeanOfResidualsFromFittedFractionation();
            } catch (Exception e) {
                retVal = 0.0;
            }
        } else if (sessionTechnique.compareToIgnoreCase("INTERCEPT") == 0) {
            try {
                retVal = getSelectedFitFunction().getYInterceptStdErr();
            } catch (Exception e) {
                retVal = 0.0;
            }
        }

        return retVal;
    }

    /**
     *
     * @param sessionTechnique
     * @return
     */
    public double getSessionErrorPlusODBySessionTechnique(String sessionTechnique) {
        double retVal = 0.0;
        if (sessionTechnique.compareToIgnoreCase("DOWNHOLE") == 0) {
            try {
                AbstractFunctionOfX FofX = getSelectedDownHoleFitFunction();
                retVal = Math.sqrt(Math.pow(FofX.getStdErrOfA(), 2) + FofX.getOverDispersion());//  updated june 2015   getStdErrOfmeanOfResidualsFromFittedFractionation();
            } catch (Exception e) {
            }
        }

        if (sessionTechnique.compareToIgnoreCase("INTERCEPT") == 0) {
            try {
                AbstractFunctionOfX FofX = getSelectedFitFunction();
                retVal = Math.sqrt(FofX.getYInterceptVariance() + FofX.getOverDispersion());
            } catch (Exception e) {
                retVal = 0.0;
            }
        }

        return retVal;
    }

    /**
     *
     * @param sessionTechnique the value of sessionTechnique
     * @return the double
     */
    public double getSessionVarianceBySessionTechnique(String sessionTechnique) {
        double retVal = 0.0;
        if (sessionTechnique.compareToIgnoreCase("DOWNHOLE") == 0) {
            try {
                retVal = Math.pow(getSelectedDownHoleFitFunction().getStdErrOfA(), 2); // updated june 2015 from getStdErrOfmeanOfResidualsFromFittedFractionation()
            } catch (Exception e) {
                retVal = 0.0;
            }
        } else if (sessionTechnique.compareToIgnoreCase("INTERCEPT") == 0) {
            try {
                retVal = getSelectedFitFunction().getYInterceptVariance();
            } catch (Exception e) {
                retVal = 0.0;
            }
        }

        return retVal;
    }

    /**
     * @return the meanOfResidualsFromFittedFractionation
     */
    public double getMeanOfResidualsFromFittedFractionation() {
        return meanOfResidualsFromFittedFractionation;
    }

    /**
     * @param meanOfResidualsFromFittedFractionation the
     * meanOfResidualsFromFittedFractionation to set
     */
    public void setMeanOfResidualsFromFittedFractionation(double meanOfResidualsFromFittedFractionation) {
        this.meanOfResidualsFromFittedFractionation = meanOfResidualsFromFittedFractionation;
    }

    /**
     * @return the stdErrOfmeanOfResidualsFromFittedFractionation
     */
    public double getStdErrOfmeanOfResidualsFromFittedFractionation() {
        return stdErrOfmeanOfResidualsFromFittedFractionation;
    }

    /**
     * @param stdErrOfmeanOfResidualsFromFittedFractionation the
     * stdErrOfmeanOfResidualsFromFittedFractionation to set
     */
    public void setStdErrOfmeanOfResidualsFromFittedFractionation(double stdErrOfmeanOfResidualsFromFittedFractionation) {
        this.stdErrOfmeanOfResidualsFromFittedFractionation = stdErrOfmeanOfResidualsFromFittedFractionation;
    }

    /**
     * @return the correctedRatios
     */
    public double[] getCorrectedRatios() {
        return correctedRatios;
    }

    /**
     * @param correctedRatios the correctedRatios to set
     */
    public void setCorrectedRatios(double[] correctedRatios) {
        this.correctedRatios = correctedRatios;
    }

    /**
     * @return the meanOfCorrectedRatios
     */
    public double getMeanOfCorrectedRatios() {
        if (!Double.isFinite(meanOfCorrectedRatios)) {
            meanOfCorrectedRatios = 0.0;
        }
        return meanOfCorrectedRatios;
    }

    /**
     * @return the stdDevOfCorrectedRatios
     */
    public double getStdDevOfCorrectedRatios() {
        return stdDevOfCorrectedRatios;
    }

    /**
     * @return the stdErrOfMeanCorrectedRatios
     */
    public double getStdErrOfMeanCorrectedRatios() {
        if (!Double.isFinite(stdErrOfMeanCorrectedRatios)) {
            stdErrOfMeanCorrectedRatios = 0.0;
        }
        return stdErrOfMeanCorrectedRatios;
    }

    /**
     * @return the logRatioFitFunctionsNoOD
     */
    @Override
    public Map<String, AbstractFunctionOfX> getFitFunctions() {
        //return logRatioFitFunctionsNoOD;
        Map<String, AbstractFunctionOfX> fitFunctions = null;

        if (overDispersionSelected) {
            fitFunctions = logRatioFitFunctionsWithOD;
        } else {
            fitFunctions = logRatioFitFunctionsNoOD;
        }

        return fitFunctions;
    }

    /**
     * @param logRatioFitFunctionsNoOD the logRatioFitFunctionsNoOD to set
     */
    public void setLogRatioFitFunctionsNoOD(Map<String, AbstractFunctionOfX> logRatioFitFunctionsNoOD) {
        this.logRatioFitFunctionsNoOD = logRatioFitFunctionsNoOD;
    }

    /**
     * @return the selectedFitFunctionType
     */
    @Override
    public FitFunctionTypeEnum getSelectedFitFunctionType() {
        return selectedFitFunctionType;
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractFunctionOfX getSelectedFitFunction() {
        AbstractFunctionOfX fitFunc;
        if (overDispersionSelected) {
            fitFunc = logRatioFitFunctionsWithOD.get(selectedFitFunctionType.getName());
        } else {
            fitFunc = logRatioFitFunctionsNoOD.get(selectedFitFunctionType.getName());
        }

        // march 2014
        if (fitFunc == null) {
            selectedFitFunctionType = FitFunctionTypeEnum.MEAN;
//            fitFunc = getSelectedFitFunction();
            if (overDispersionSelected) {
                fitFunc = logRatioFitFunctionsWithOD.get(selectedFitFunctionType.getName());
            } else {
                fitFunc = logRatioFitFunctionsNoOD.get(selectedFitFunctionType.getName());
            }

        }
        return fitFunc;
    }

    @Override
    public AbstractFunctionOfX getSelectedDownHoleFitFunction() {
        AbstractFunctionOfX fitFunc;
        if (overDispersionSelectedDownHole) {
            fitFunc = logRatioFitFunctionsWithOD.get(FitFunctionTypeEnum.MEAN_DH.getName());
        } else {
            fitFunc = logRatioFitFunctionsNoOD.get(FitFunctionTypeEnum.MEAN_DH.getName());
        }
        return fitFunc;
    }

    /**
     * @param selectedFitFunctionType the selectedFitFunctionType to set
     */
    @Override
    public void setSelectedFitFunctionType(FitFunctionTypeEnum selectedFitFunctionType) {
        this.selectedFitFunctionType = selectedFitFunctionType;
    }

    /**
     * @return the fitFunctionLogValues
     */
    public double[] getFitFunctionLogValues() {
        AbstractFunctionOfX fitFunc = getSelectedFitFunction();

        for (int i = 0; i < fitFunctionLogValues.length; i++) {
            try {
                fitFunctionLogValues[i] = fitFunc.f(topIsotope.getNormalizedOnPeakAquireTimes()[i]);
            } catch (Exception e) {
                fitFunctionLogValues[i] = 0.0;
            }
        }
        return fitFunctionLogValues;
    }

    /**
     * @return the fitFunctionLogValues
     */
    public double[] getDownHoleFitFunctionLogValues() {
        AbstractFunctionOfX fitFunc = getSelectedDownHoleFitFunction();

        for (int i = 0; i < fitFunctionLogValues.length; i++) {
            try {
                fitFunctionLogValues[i] = fitFunc.f(topIsotope.getNormalizedOnPeakAquireTimes()[i]);
            } catch (Exception e) {
                fitFunctionLogValues[i] = 0.0;
            }
        }
        return fitFunctionLogValues;
    }

    /**
     * @param meanOfCorrectedRatios the meanOfCorrectedRatios to set
     */
    public void setMeanOfCorrectedRatios(double meanOfCorrectedRatios) {
        this.meanOfCorrectedRatios = meanOfCorrectedRatios;
    }

    /**
     * @param stdErrOfMeanCorrectedRatios the stdErrOfMeanCorrectedRatios to set
     */
    public void setStdErrOfMeanCorrectedRatios(double stdErrOfMeanCorrectedRatios) {
        this.stdErrOfMeanCorrectedRatios = stdErrOfMeanCorrectedRatios;
    }

    /**
     * @return the standardValue
     */
    @Override
    public double getStandardValue() {
        return standardValue;
    }

    /**
     * @param usedForFractionationCorrections the
     * usedForFractionationCorrections to set
     */
    public void setUsedForFractionationCorrections(boolean usedForFractionationCorrections) {
        this.usedForFractionationCorrections = usedForFractionationCorrections;
    }

    /**
     * @return the belowDetection
     */
    @Override
    public boolean isBelowDetection() {
        return belowDetection;
    }

    /**
     * @param belowDetection the belowDetection to set
     */
    public void setBelowDetection(boolean belowDetection) {
        this.belowDetection = belowDetection;
    }

    /**
     * @param resetMatrix the value of resetMatrix
     * @return the Jama.Matrix
     */
    public Matrix getSlogRatioX_Y(boolean resetMatrix) {
        if ((resetMatrix) || (SlogRatioX_Y == null)) {
            calculateSlogRatioX_Y();
        }
        return SlogRatioX_Y;
    }

    /**
     * @param SlogRatioX_Y the SlogRatioX_Y to set
     */
    public void setSlogRatioX_Y(Matrix SlogRatioX_Y) {
        this.SlogRatioX_Y = SlogRatioX_Y;
    }

    /**
     * @return the matrixSxyod
     */
    public Matrix getMatrixSxyod() {
        return matrixSxyod;
    }

    /**
     * @param matrixSxyod the matrixSxyod to set
     */
    public void setMatrixSxyod(Matrix matrixSxyod) {
        this.matrixSxyod = matrixSxyod;
    }

    /**
     * @return the logRatios
     */
    public double[] getLogRatios() {
        return logRatios;
    }

    /**
     * @param logRatios the logRatios to set
     */
    public void setLogRatios(double[] logRatios) {
        this.logRatios = logRatios;
    }

    /**
     *
     * @param activeCount
     * @param dataActiveCommonMap the value of dataActiveCommonMap
     * @return the double[]
     */
    public double[] getActiveLogRatios(int activeCount, boolean[] dataActiveCommonMap) {
        double[] activeLogatios = new double[activeCount];
        int index = 0;
        for (int i = 0; i < dataActiveCommonMap.length; i++) {
            if (dataActiveCommonMap[i]) {
                activeLogatios[index] = logRatios[i];
                index++;
            }
        }

        return activeLogatios;
    }

    /**
     *
     * @return
     */
    public boolean hasTwoIdenticalIonCounters() {
        return (topIsotope.getCollectorModel()//
                .equals(botIsotope.getCollectorModel()));
    }

    /**
     *
     * @return
     */
    public double getdLr_dDt() {
        return ((RawIntensityDataModel) topIsotope).getOnPeakVirtualCollector()//
                .getSumOfCorrectedOnPeakIntensities()//
                - ((RawIntensityDataModel) botIsotope).getOnPeakVirtualCollector()//
                .getSumOfCorrectedOnPeakIntensities();
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isCalculatedInitialFitFunctions() {
        return calculatedInitialFitFunctions;
    }

    /**
     *
     * @param fitFunctionType
     * @return
     */
    @Override
    public boolean containsFitFunction(FitFunctionTypeEnum fitFunctionType) {
        boolean contains = false;

        if (fitFunctionType.compareTo(FitFunctionTypeEnum.MEAN_DH) == 0) {
            if (overDispersionSelectedDownHole) {
                contains = logRatioFitFunctionsWithOD.get(fitFunctionType.getName()) != null;
            } else {
                contains = logRatioFitFunctionsNoOD.get(fitFunctionType.getName()) != null;
            }
        } else if (overDispersionSelected) {
            contains = logRatioFitFunctionsWithOD.get(fitFunctionType.getName()) != null;
        } else {
            contains = logRatioFitFunctionsNoOD.get(fitFunctionType.getName()) != null;
        }
        return contains;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isOverDispersionSelected() {
        return overDispersionSelected;
    }

    @Override
    public void setOverDispersionSelected(boolean overDispersionSelected) {
        this.overDispersionSelected = overDispersionSelected;
    }

    /**
     *
     * @param fitFunctionType
     * @return
     */
    @Override
    public boolean doesFitFunctionTypeHaveOD(FitFunctionTypeEnum fitFunctionType) {

        boolean retVal = false;
        AbstractFunctionOfX fitFunc = logRatioFitFunctionsWithOD.get(fitFunctionType.getName());
        if (fitFunc != null) {
            retVal = fitFunc.isOverDispersionSelected();
        }
        return retVal;

    }

    /**
     *
     * @param fitFunctionType
     * @return
     */
    @Override
    public double getXIforFitFunction(FitFunctionTypeEnum fitFunctionType) {
        double retVal = 0.0;
        if (doesFitFunctionTypeHaveOD(fitFunctionType)) {
            return Math.sqrt(logRatioFitFunctionsWithOD.get(fitFunctionType.getName()).getOverDispersion());
        }

        return retVal;

    }

    /**
     *
     * @return
     */
    @Override
    public AbstractCollectorModel getCollectorModel() {
        return null;
    }

    /**
     * @param usedForCommonLeadCorrections the usedForCommonLeadCorrections to
     * set
     */
    public void setUsedForCommonLeadCorrections(boolean usedForCommonLeadCorrections) {
        this.usedForCommonLeadCorrections = usedForCommonLeadCorrections;
    }

    /**
     * @return the usedForCommonLeadCorrections
     */
    @Override
    public boolean isUsedForCommonLeadCorrections() {
        return usedForCommonLeadCorrections;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isForceMeanForCommonLeadRatios() {
//        System.out.println("BOTTOM " + botIsotope.getDataModelName());
        return botIsotope.isForceMeanForCommonLeadRatios();
    }

    /**
     * @return the topSopbclr
     */
    public Matrix getTopSopbclr() {
        return topSopbclr;
    }

    /**
     * @return the botSopbclr
     */
    public Matrix getBotSopbclr() {
        return botSopbclr;
    }

    /**
     * @return the USING_FULL_PROPAGATION
     */
    public boolean isUSING_FULL_PROPAGATION() {
        return USING_FULL_PROPAGATION;
    }

    /**
     * @param USING_FULL_PROPAGATION the USING_FULL_PROPAGATION to set
     */
    public void setUSING_FULL_PROPAGATION(boolean USING_FULL_PROPAGATION) {
        this.USING_FULL_PROPAGATION = USING_FULL_PROPAGATION;
    }

    /**
     * @return the logDifferencesFromWeightedMean
     */
    public double[] getLogDifferencesFromWeightedMean() {
        return logDifferencesFromWeightedMean;
    }

    /**
     * @return the overDispersionSelectedDownHole
     */
    public boolean isOverDispersionSelectedDownHole() {
        return overDispersionSelectedDownHole;
    }

    /**
     * @param overDispersionSelectedDownHole the overDispersionSelectedDownHole
     * to set
     */
    public void setOverDispersionSelectedDownHole(boolean overDispersionSelectedDownHole) {
        this.overDispersionSelectedDownHole = overDispersionSelectedDownHole;
    }

    /**
     * @return the downHoleFitFunction
     */
    public AbstractFunctionOfX getDownHoleFitFunction() {
        return downHoleFitFunction;
    }

    /**
     * @param downHoleFitFunction the downHoleFitFunction to set
     */
    public void setDownHoleFitFunction(AbstractFunctionOfX downHoleFitFunction) {
        this.downHoleFitFunction = downHoleFitFunction;
    }

    /**
     * @return the uncertaintyOneSigmaAbsRatios
     */
    public double[] getUncertaintyOneSigmaAbsRatios() {
        return uncertaintyOneSigmaAbsRatios;
    }

    /**
     * @param uncertaintyOneSigmaAbsRatios the uncertaintyOneSigmaAbsRatios to
     * set
     */
    public void setUncertaintyOneSigmaAbsRatios(double[] uncertaintyOneSigmaAbsRatios) {
        this.uncertaintyOneSigmaAbsRatios = uncertaintyOneSigmaAbsRatios;
    }

}

/*
 * ProjectOfLegacySamplesImporterFromTSVFile_DIBBs_Useries_A.java
 *
 * Copyright 2006-2015 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.projects.projectImporters.UThProjectImporters;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.UTh_Redux.fractions.UThFraction;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.dataDictionaries.FileDelimiterTypesEnum;
import org.earthtime.dataDictionaries.SampleAnalysisTypesEnum;
import org.earthtime.dataDictionaries.SampleTypesEnum;
import org.earthtime.dataDictionaries.UThAnalysisMeasures;
import org.earthtime.dataDictionaries.UThCompositionalMeasures;
import org.earthtime.exceptions.ETException;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.projects.projectImporters.AbstractProjectImporterFromLegacyDelimitedTextFile;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring This importer services UC Santa Barbara Laser
 * Ablation Split Stream legacy data starting 20 June 2014.
 */
public class ProjectOfLegacySamplesImporterFromTSVFile_DIBBs_Useries_A extends AbstractProjectImporterFromLegacyDelimitedTextFile {

    public ProjectOfLegacySamplesImporterFromTSVFile_DIBBs_Useries_A(FileDelimiterTypesEnum fileDelimiter) {
        super(fileDelimiter);
    }

    /**
     * Reads tsv files generated from Andrea Dutton's excel file of coral
     * analyses of May 2015
     *
     * @param project
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    @Override
    protected ProjectInterface extractProjectFromDelimitedTextFile(ProjectInterface project, File file)
            throws FileNotFoundException {

        ArrayList<SampleInterface> projectSamples = new ArrayList<>();

        project.setProjectSamples(projectSamples);

        SampleInterface currentSample = null;
        AliquotInterface currentAliquot = null;

        List<String> fractionData = null;
        try {
            fractionData = Files.readLines(file, Charsets.ISO_8859_1);

            // ignore first 4 lines
            // we only allow one source ID for now
            // we allow multiple sampleIDs
            String savedSourceID = "";
            String savedSampleID = "";
            for (int i = 4; i < fractionData.size(); i++) {
                @SuppressWarnings("UseOfObsoleteCollectionType")
                Vector<String> myFractionData = processLegacyTSVLine(fractionData.get(i));
                if (!myFractionData.get(0).equals("0")) {
                    String sourceID = myFractionData.get(1);
                    if (savedSourceID.equalsIgnoreCase("")) {
                        savedSourceID = sourceID;
                    }
                    if (savedSourceID.equalsIgnoreCase(sourceID)) {

                        String sampleID = myFractionData.get(2);
                        if (!savedSampleID.equalsIgnoreCase(sampleID)) {
                            savedSampleID = sampleID;

                            // process existing if not first;
                            if ((currentSample != null) && (currentAliquot != null)) {
                                processSuperSample(project.getSuperSample(), currentSample, currentAliquot);
                                currentSample = null;
                                currentAliquot = null;
                            }

                            // test if sample already exists
                            for (SampleInterface sample : projectSamples) {
                                if (sample.getSampleName().equalsIgnoreCase(sampleID)) {
                                    currentSample = sample;
                                    // there is only one aliquotin this USeries scenario
                                    currentAliquot = sample.getAliquots().get(0);
                                    break;
                                }
                            }
                            if ((currentSample == null) && (currentAliquot == null)) {
                                // new sample
                                try {
                                    currentSample = new Sample(//
                                            //
                                            sampleID, //
                                            SampleTypesEnum.LEGACY.getName(), //
                                            SampleAnalysisTypesEnum.USERIES.getName(), //
                                            ReduxLabData.getInstance(), //
                                            ReduxConstants.ANALYSIS_PURPOSE.SingleAge, "UPb");

                                    projectSamples.add(currentSample);

                                    currentAliquot = currentSample.addNewAliquot(sampleID);

                                } catch (BadLabDataException badLabDataException) {
                                } catch (ETException eTException) {
                                }
                            }
                        }

                        if (currentSample != null) {
                            // process fractions
                            ETFractionInterface myFraction = new UThFraction();//=================================
                            myFraction.setFractionID(myFractionData.get(3));
                            myFraction.setGrainID(myFractionData.get(3));

                            myFraction.setSampleName(currentSample.getSampleName());
                            // in case a sample occurs out of order
                            if (currentSample.getFractions().size() > 0) {
                                myFraction.setAliquotNumber(currentSample.getFractions().get(0).getAliquotNumber());
                            }
                            currentSample.addFraction(myFraction);
                            ((ReduxAliquotInterface) currentAliquot).getAliquotFractions().add(myFraction);

                            // TODO: add uncertainty columns
                            // column 43 is conc232Th in ppb
                            String ratioName = UThCompositionalMeasures.conc232Th.getName();
                            myFraction.getCompositionalMeasureByName(ratioName)//
                                    .setValue(readDelimitedTextCell(myFractionData.get(40)).//
                                            movePointLeft(9));

                            // column 44 is conc232Th uncertainty in ppb
                            // convert 2-sigma to 1-sigma
                            BigDecimal oneSigmaAbs = readDelimitedTextCell(myFractionData.get(41)).
                                    divide(new BigDecimal(2.0).//
                                            movePointLeft(9));
                            myFraction.getCompositionalMeasureByName(ratioName)//
                                    .setOneSigma(oneSigmaAbs);

                            // column 45 is ar230Th_232Thfc 
                            ratioName = UThAnalysisMeasures.ar230Th_232Thfc.getName();
                            myFraction.getAnalysisMeasure(ratioName)//
                                    .setValue(readDelimitedTextCell(myFractionData.get(44)));

                            // column 46 is ar232Th_238Ufc * 10^5
                            ratioName = UThAnalysisMeasures.ar232Th_238Ufc.getName();
                            myFraction.getAnalysisMeasure(ratioName)//
                                    .setValue(readDelimitedTextCell(myFractionData.get(43)).//
                                            movePointLeft(5));

                            // column 47 is conc238U in ppm
                            ratioName = UThCompositionalMeasures.conc238U.getName();
                            myFraction.getCompositionalMeasureByName(ratioName)//
                                    .setValue(readDelimitedTextCell(myFractionData.get(44)).//
                                            movePointLeft(6));

                            // column 48 is conc238U uncertainty in ppm
                            // convert 2-sigma to 1-sigma
                            oneSigmaAbs = readDelimitedTextCell(myFractionData.get(45)).
                                    divide(new BigDecimal(2.0).//
                                            movePointLeft(6));
                            myFraction.getCompositionalMeasureByName(ratioName)//
                                    .setOneSigma(oneSigmaAbs);

                            // column 51 is ar230Th_238Ufc 
                            ratioName = UThAnalysisMeasures.ar230Th_238Ufc.getName();
                            myFraction.getAnalysisMeasure(ratioName)//
                                    .setValue(readDelimitedTextCell(myFractionData.get(48)));

                            // column 52 is ar230Th_238Ufc uncertainty 
                            // convert 2-sigma to 1-sigma
                            oneSigmaAbs = readDelimitedTextCell(myFractionData.get(49)).
                                    divide(new BigDecimal(2.0));
                            myFraction.getAnalysisMeasure(ratioName)//
                                    .setOneSigma(oneSigmaAbs);

                            // column 53 is ar234U_238Ufc 
                            ratioName = UThAnalysisMeasures.ar234U_238Ufc.getName();
                            myFraction.getAnalysisMeasure(ratioName)//
                                    .setValue(readDelimitedTextCell(myFractionData.get(50)));

                            // column 54 is ar234U_238Ufc uncertainty 
                            // convert 2-sigma to 1-sigma
                            oneSigmaAbs = readDelimitedTextCell(myFractionData.get(51)).
                                    divide(new BigDecimal(2.0));
                            myFraction.getAnalysisMeasure(ratioName)//
                                    .setOneSigma(oneSigmaAbs);

                        }

                    }// end of checking if same source
                }
            }

            // end of file
            if ((currentSample != null) && (currentAliquot != null)) {
                processSuperSample(project.getSuperSample(), currentSample, currentAliquot);
            }

        } catch (IOException iOException) {
        }

        return project;
    }

    private void processSuperSample(SampleInterface superSample, SampleInterface currentSample, AliquotInterface currentAliquot) {
        // this forces population of aliquot fractions
        // check if supersample already has this aliquot
        AliquotInterface existingSuperSampleAliquot = //
                superSample.getAliquotByNameForProjectSuperSample(currentSample.getSampleName() + "::" + currentAliquot.getAliquotName());
        if (existingSuperSampleAliquot == null) {
            SampleInterface.copyAliquotIntoSample(currentSample.getAliquotByNameForProjectSuperSample(currentAliquot.getAliquotName()), superSample);
        } else {
            for (ETFractionInterface fraction : ((ReduxAliquotInterface) currentAliquot).getAliquotFractions()) {
                if (!((ReduxAliquotInterface) existingSuperSampleAliquot).getAliquotFractions().contains(fraction)) {
                    ((ReduxAliquotInterface) existingSuperSampleAliquot).getAliquotFractions().add(fraction);
                    superSample.getFractions().add(fraction);
                }
            }
        }
    }

}

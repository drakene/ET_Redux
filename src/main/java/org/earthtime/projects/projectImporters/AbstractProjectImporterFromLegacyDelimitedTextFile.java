/*
 * AbstractProjectImporterFromLegacyDelimitedTextFile.java
 *
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
package org.earthtime.projects.projectImporters;

import com.google.common.base.Splitter;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.exceptions.BadImportedCSVLegacyFileException;
import org.earthtime.UPb_Redux.filters.LegacyDelimitedFileFilter;
import org.earthtime.dataDictionaries.FileDelimiterTypesEnum;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.utilities.FileHelper;

/**
 * Converts contents of a delimited text file to samples with aliquots of fractions. 
 * The delimited file (comma, tab) must
 * have the fields in a specific order as specified by the template found at
 * ****************
 *
 * @author James F. Bowring
 */
public abstract class AbstractProjectImporterFromLegacyDelimitedTextFile {

    // instance attributes
    private File mruFolder;
    private FileDelimiterTypesEnum fileDelimiter;
 
    /**
     *
     * @param fileDelimiter the value of fileDelimiter
     */
    public AbstractProjectImporterFromLegacyDelimitedTextFile (FileDelimiterTypesEnum fileDelimiter) {
        this.fileDelimiter = fileDelimiter;
    }

    /**
     *
     * @param project
     * @return @throws FileNotFoundException
     * @throws BadImportedCSVLegacyFileException
     */
    public ProjectInterface readInProjectSamples (ProjectInterface project)
            throws FileNotFoundException, BadImportedCSVLegacyFileException {

        File dataFile = openDelimitedTextFile( mruFolder );
        mruFolder = dataFile.getParentFile();

        extractProjectFromDelimitedTextFile(project, dataFile );
        project.setLocationOfDataImportFile(dataFile);

        return project;

    }
    
    /**
     *
     * @param project
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    protected abstract ProjectInterface extractProjectFromDelimitedTextFile (ProjectInterface project, File file )
            throws FileNotFoundException;

    private File openDelimitedTextFile ( File location )
            throws FileNotFoundException {
        String dialogTitle = "Select a LEGACY " + fileDelimiter.getName() +"-delimited file to OPEN: *." + fileDelimiter.getDefaultFileExtension();
        final String fileExtension = "."+ fileDelimiter.getDefaultFileExtension();
        FileFilter nonMacFileFilter = new LegacyDelimitedFileFilter(fileDelimiter);

        File returnFile =
                FileHelper.AllPlatformGetFile( dialogTitle, location, fileExtension, nonMacFileFilter, false, new JFrame() )[0];

        if ( returnFile != null ) {
            return returnFile;
        } else {
            throw new FileNotFoundException();
        }
    }

    /**
     *
     * @param cellContents
     * @return
     */
    protected BigDecimal readDelimitedTextCell ( String cellContents ) {
        BigDecimal retVal;

        try {
            retVal = new BigDecimal( cellContents, ReduxConstants.mathContext15 );
        } catch (Exception e) {
            retVal = BigDecimal.ZERO;
        }

        return retVal;
    }

    /**
     *
     * @param line
     * @return
     */
    protected Vector<String> processLegacyCSVLine ( String line ) {
        Vector<String> myLine = new Vector<>();

        //use a second Scanner to parse the content of each line

        // remove all quotes
        String aLine = line.replaceAll( "\"", "" );
        
        // capture empty lines : leading comma : '0' is flag to ignore line
        if ((aLine == null) || (aLine.length() == 0) ||  ( aLine.startsWith( "," ) )) {
            myLine.add( "0" );
        }
        Scanner s = new Scanner( aLine );
        s.useDelimiter( "," );
        
        while (s.hasNext()) {
            myLine.add( s.next().trim() );
        }
        
        s.close();
        
        // add dummy fields to handle possible missing last values
        if ( !myLine.get( 0 ).equalsIgnoreCase( "0" ) ) {
            int size = myLine.size();
            for (int i = size; i < 32; i ++) {
                myLine.add( "0" );
            }
        }
        
        return myLine;
    }
    
    protected Vector<String> processLegacyTSVLine(String Line){
               Vector<String> myLine = new Vector<>();

        //use a second Scanner to parse the content of each line

        // remove all quotes
        String aLine = Line.replaceAll( "\"", "" );
        
        // capture empty lines : leading comma : '0' is flag to ignore line
        if ((aLine == null) || (aLine.length() == 0) ||  ( aLine.startsWith( "," ) )) {
            myLine.add( "0" );
        }
        
        // July 2015
        Iterable<String> splitLine = Splitter.on("\t").trimResults().split(aLine);
        for (String string : splitLine){
            myLine.add(string);
        }

        return myLine;
    }

    /**
     *
     * @param lineContents
     * @return
     */
    public static boolean lineHasOnlyFirstElement ( Vector<String> lineContents ) {
        boolean retVal = false;
        if ( (lineContents.get( 0 ).length() == 0) || (lineContents.get( 0 ).equalsIgnoreCase( "0" )) ) {
            retVal = false;
        } else {
            retVal = true;
            int cellCountToCheck = Math.min(6, lineContents.size());
            for (int i = 1; i < cellCountToCheck; i ++) {
                retVal = retVal && (lineContents.get( i ).length() == 0);
            }
        }


        return retVal;
    }

    /**
     * @return the mruFolder
     */
    public File getMruFolder () {
        return mruFolder;
    }

    /**
     * @param mruFolder the mruFolder to set
     */
    public void setMruFolder ( File mruFolder ) {
        this.mruFolder = mruFolder;
    }

}

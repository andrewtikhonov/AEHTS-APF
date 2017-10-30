/**
 * Copyright 2012-2017 Functional Genomics Development Team, European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * @author Andrew Tikhonov <andrew.tikhonov@gmail.com>
 **/
package test.wiggle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import test.wiggle.bam.BAMReader;
import test.wiggle.bam.BAMBlock;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 24/05/2012
 * Time: 14:29
 * To change this template use File | Settings | File Templates.
 */
public class WiggleCreatorTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String mainLocation = "/Users/andrew/project/aehts-apf/test";
    private final String bamName = "E-GEOD-30206.SRR299029.bam";

    public WiggleCreatorTest(){

        try {
            final PrintWriter out = new PrintWriter(
                    new FileWriter(new File(mainLocation, "myfile.wig")));

            // ENSG00000235828_E-MTAB-197_individual_NA12892
            final String geneId = "ENSG00000235828_E"; //allParams[0];
            final String accession = "E-MTAB-197"; //allParams[1];
            final String factorName = "individual"; //URLDecoder.decode(URLDecoder.decode(allParams[2]));
            final String factorValue = "NA12892"; //param3.substring(0, param3.length() - 4);

            final String chromosomeId = "1"; // anno.chromosomeId();
            long geneStart = 1; //anno.geneStart();
            long geneEnd = 1000000000000000000L; //anno.geneEnd();

            if (chromosomeId == null || geneStart == -1 || geneEnd == -1) {
                log.error("A region for gene " + geneId + " not found");
                return;
            }

            final long delta = (geneEnd - geneStart) / 5;
            geneStart -= delta;
            if (geneStart < 1) {
                geneStart = 1;
            }
            geneEnd += delta;

            final WigCreator creator = new WigCreator(out, chromosomeId, geneStart, geneEnd);
            final String wiggleName =
                    "EBI Expression Atlas (GXA) Experiment " +
                            accession + " - " + factorName + " - " + factorValue;

            out.println("track" +
                    " type=wiggle_0" +
                    " name=\"" + wiggleName + "\"" +
                    " description=\"" + wiggleName + "\"" +
                    " visibility=full" +
                    " autoScale=on" +
                    " color=68,68,68" +
                    " yLineMark=11.76" +
                    " yLineOnOff=on" +
                    " priority=10"
            );

            final ArrayList<Read> allReads = new ArrayList<Read>();
            //for (Assay assay : assaysToGet) {
                //final File aDir = new File(assaysDir, assay.getAccession());
                final BAMReader reader = new BAMReader(new File(mainLocation, bamName));
                try {
                    for (BAMBlock b : reader.readBAMBlocks(chromosomeId, geneStart, geneEnd)) {
                        for (int i = b.from; i < b.to;) {
                            Read read = new Read(b.buffer, i, b.to);
                            i += read.blockSize;
                            if (!read.isValid) {
                                log.error("Invalid read record has been found for " + bamName);
                                break;
                            }
                            //if ((read.start <= geneEnd) && (read.end >= geneStart)) {
                                allReads.add(read);
                            //}
                        }
                    }
                } catch (IOException e) {
                    log.error(bamName + ":" + e.getMessage());
                }
            //}
            Collections.sort(allReads);
            for (Read read : allReads) {
                creator.init(read.start);
                creator.fillByZeroes(read.end);
                creator.removeZeroes(read.start);
                creator.addRegion(read.start, read.end);
                creator.printRegions(read.start);
            }

            out.close();

        } catch (Exception ex) {
            log.error("Error!", ex);
        }
    }

    public static void main(String[] args) {
        new WiggleCreatorTest();
    }

}




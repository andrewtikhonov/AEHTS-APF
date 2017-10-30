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

import static com.google.common.io.CharStreams.readLines;
import static com.google.common.io.Closeables.closeQuietly;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 25/05/2012
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 */
class GeneAnnotation {
    @SuppressWarnings({"FieldCanBeLocal"})
    private final Logger log = LoggerFactory.getLogger(getClass());

    private String chromosomeId = null;
    private long geneStart = -1;
    private long geneEnd = -1;

    class ExtensionFilter implements FileFilter {
        private String extension;
        public ExtensionFilter(String extension){
            this.extension = extension;
        }
        public boolean accept(java.io.File file) {
            return file.getAbsolutePath().endsWith(extension);
        }
    }

    GeneAnnotation(File annotationDir, String geneId, String accession) {
        BufferedReader reader = null;
        try {

            final File[] annotationFiles = annotationDir.listFiles(new ExtensionFilter("anno")); // extension("anno", false)
            if (annotationFiles == null || annotationFiles.length == 0) {
                log.error("No annotation file for experiment " + accession);
                return;
            }
            if (annotationFiles.length > 1) {
                log.error("Several annotation files for experiment " + accession);
                log.error(annotationFiles[0].getName() + " will be used");
            }
            reader = new BufferedReader(new FileReader(annotationFiles[0]));

            for (String line : readLines(reader)) {
                final String[] fields = line.split("\t");
                if (geneId.equals(fields[0])) {
                    chromosomeId = fields[1];
                    try {
                        geneStart = Long.parseLong(fields[2]);
                        geneEnd = Long.parseLong(fields[3]);
                    } catch (NumberFormatException e) {
                        log.error("Invalid line: {}", line);
                    }
                    break;
                }
            }
        } catch (IOException e) {
            log.error("Cannot read gene mapping file");
        } finally {
            closeQuietly(reader);
        }
    }

    long geneStart() {
        return this.geneStart;
    }

    long geneEnd() {
        return this.geneEnd;
    }

    String chromosomeId() {
        return this.chromosomeId;
    }
}

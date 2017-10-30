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
package processor.handler;

import db.DAL;
import db.DALDefaults;
import db.DataConst;
import db.SettingsConst;
import db.data.SubmDataDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processor.factory.RCloudFactory;
import processor.util.ProcessWatchDog;
import processor.util.TheSettings;
import processor.util.Util;
import uk.ac.ebi.rcloud.server.RServices;
import uk.ac.ebi.rcloud.server.RType.*;

import java.util.Arrays;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 10/04/2012
 * Time: 11:39
 * To change this template use File | Settings | File Templates.
 */
public class SubProcessor4Submitted extends SubProcessorBase {

    private final static Logger log = LoggerFactory.getLogger(SubProcessor4Submitted.class);

    public void processSubmittedSubmission(SubmDataDB subm, DAL dal, RServices r, String servername) throws Exception {

        // update db records

        subm.getMap().put(SubmDataDB.STATUS, DataConst.CHECKING);
        subm.getMap().put(SubmDataDB.MASTER_NAME, servername);

        dal.updateSubm(subm);
        dal.updateSubmStartTime(subm);

        // say something
        //
        log.info("Processing {}:{}", subm.getSubmissionId(), subm.getExperimentId());

        // source the resources
        //

        RObject object = r.getObject("ArrayExpressHTS::validate.one.accession(\"" + subm.getExperimentId() + "\")");

        if (object != null && object instanceof RList) {

            RList info = (RList) object;

            String metadata = "";
            String run_metadata = "";

            //
            // VALIDATION INFO
            //

            String exp_status = DataConst.CHECKED_NOT_SUITABLE;

            boolean exp_type_supported    = extractLogical(info, "exp_type_supported");
            boolean sdrf_found            = extractLogical(info, "sdrf_found");
            boolean sdrf_organism_found   = extractLogical(info, "sdrf_organism_found");
            boolean ena_run_found         = extractLogical(info, "ena_run_found");
            boolean library_found         = extractLogical(info, "library_found");
            boolean library_supported     = extractLogical(info, "library_supported");
            boolean sample_found          = extractLogical(info, "sample_found");
            boolean reference_found       = extractLogical(info, "reference_found");
            boolean reference_in_ensembl  = extractLogical(info, "reference_in_ensembl");
            double  valid_runs            = extractNumeric(info, "valid_runs");

            metadata += " EXPT/SDRF/SDRFORG/RUN/LIBF/LIBS/ORGF/REFF/REFENS/VRUNS: (";

            metadata += BooleanToLetter(exp_type_supported) + " ";
            metadata += BooleanToLetter(sdrf_found) + " ";
            metadata += BooleanToLetter(sdrf_organism_found) + " ";
            metadata += BooleanToLetter(ena_run_found) + " ";
            metadata += BooleanToLetter(library_found) + " ";
            metadata += BooleanToLetter(library_supported) + " ";
            metadata += BooleanToLetter(sample_found) + " ";
            metadata += BooleanToLetter(reference_found) + " ";
            metadata += BooleanToLetter(reference_in_ensembl) + " ";
            metadata += (int) (valid_runs) + ")";

            if (exp_type_supported && sdrf_found && sdrf_organism_found && ena_run_found &&
                    library_found && library_supported && sample_found && valid_runs > 0) {

                if (reference_found) {
                    //
                    //
                    exp_status = DataConst.CHECKED_SUITABLE;
                }

                if (reference_in_ensembl) {
                    //
                    //
                    exp_status = DataConst.CHECKED_NO_REFERENCE;
                }


                /*

                RObject run_info_robj = info.getValueByName("run_info");

                if (run_info_robj != null && run_info_robj instanceof RList) {
                    RList run_info = (RList) run_info_robj;

                    //
                    // RUN INFO
                    //

                    String[] run_names = run_info.getNames();

                    //
                    // EXPLANATORY RUN INFO

                    for (String runid : run_names) {
                        RList single_run_info = (RList) run_info.getValueByName(runid);

                        boolean run_valid                = extractLogical(single_run_info, "valid");
                        boolean run_reference_found      = extractLogical(single_run_info, "reference_found");
                        boolean run_reference_in_ensembl = extractLogical(single_run_info, "reference_in_ensembl");

                        boolean run_sample_found    = extractLogical(single_run_info, "sample_found");
                        boolean run_fastq_found     = extractLogical(single_run_info, "fastq_found");
                        boolean run_layout_found    = extractLogical(single_run_info, "layout_found");
                        boolean run_insert_found    = extractLogical(single_run_info, "insert_found");
                        boolean run_deviation_found = extractLogical(single_run_info, "deviation_found");

                        run_metadata += " " + runid + " (";
                        run_metadata += BooleanToLetter(run_valid);
                        run_metadata += BooleanToLetter(run_sample_found);
                        run_metadata += BooleanToLetter(run_reference_found);
                        run_metadata += BooleanToLetter(run_reference_in_ensembl);
                        run_metadata += BooleanToLetter(run_fastq_found);
                        run_metadata += BooleanToLetter(run_layout_found);
                        run_metadata += BooleanToLetter(run_insert_found);
                        run_metadata += BooleanToLetter(run_deviation_found) + ")";

                        if (run_valid && !run_reference_found && run_reference_in_ensembl) {
                            exp_status = DataConst.CHECKED_NO_REFERENCE;
                        }
                    }
                }

                */
            }

            //
            // EXPLANATORY INFO
            //

            //
            // EXP TYPE
            //

            metadata += " EXP_TYPE: ";

            RObject o1 = info.getValueByName("exp_type");
            if (o1 != null && o1 instanceof RChar) {
                RChar exp_type = (RChar) o1;
                String[] exp_type_values = exp_type.getValue();

                metadata += Arrays.toString(exp_type_values);
            }

            //
            // LIBRARY
            //

            metadata += " LIBRARY: ";

            RObject o2 = info.getValueByName("library_strategy");

            if (o2 != null && o2 instanceof RChar) {
                RChar library = (RChar) o2;
                String[] library_values = library.getValue();

                metadata += Arrays.toString(library_values);
            }

            //
            // ORGANISM
            //

            metadata += " ORGANISM: ";

            RObject o3 = info.getValueByName("sample_names");

            if (o3 != null && o3 instanceof RChar) {
                RChar organism = (RChar) o3;
                String[] organism_values = organism.getValue();

                metadata += Arrays.toString(organism_values);
            }

            //
            // RUN METADATA
            //

            metadata += run_metadata;

            //
            // SAVE METADATA

            subm.getMap().put(SubmDataDB.METADATA, metadata);
            subm.getMap().put(SubmDataDB.STATUS, exp_status);

            dal.updateSubm(subm);
            dal.updateSubmMetadata(subm);

        } else {
            log.error(subm.getExperimentId() + " R object is null or is not RList");
        }
    }

    private String BooleanToLetter(boolean l) {
        return (l ? "T" : "F");
    }

    private boolean extractLogical(RList l, String name) {
        return ((RLogical) l.getValueByName(name)).getValue()[0];
    }

    private double extractNumeric(RList l, String name) {
        return ((RNumeric) l.getValueByName(name)).getValue()[0];
    }

    private RServices getRServer(RCloudFactory rFactory) throws Exception {

        // get settings
        //
        String poolname = TheSettings.getSetting(SettingsConst.DEFAULT_POOLNAME);
        String owner = TheSettings.getSetting(SettingsConst.PROCESS_USERNAME);


        log.info("Allocating an R Server from " + poolname);

        RServices r = rFactory.getRServices(poolname);

        if (r == null) {
            return null;
        }

        //String servername = r.getServantName();

        // set process owner
        r.setOwner(owner);

        log.error("Initializing R server");


        // initialize
        //
        r.consoleSubmit("library(ArrayExpressHTS)");

        //
        r.consoleSubmit("ArrayExpressHTS::init_available_organism_reference()");

        //
        r.consoleSubmit("ArrayExpressHTS::setPipelineOptions('supportedLibrary' = c('RNA-Seq', 'WGS', 'OTHER'))");



        return r;
    }

    private Thread currentThread = null;

    public void processNewSubmissions() {

        log.info("Processing New Submissions");

        RServices r            = null;
        RCloudFactory rFactory = new RCloudFactory(false); // true - listen events
        ProcessWatchDog dog    = null;

        try {
            // get register DAL
            //
            DAL dal                = DALDefaults.getDAL();

            r = getRServer(rFactory);

            if (r == null) {
                log.error("No R servers provided");
                return;
            }

            String servername = r.getServantName();

            // get what to process
            //
            Vector<SubmDataDB> toprocess = dal.listSubms(
                    Util.makeStringMap(SubmDataDB.STATUS, DataConst.PROCESSING_REQUESTED));

            currentThread = Thread.currentThread();

            if (toprocess.size() > 0) {

                log.info(toprocess.size() + " new submissions found");

                dog = new ProcessWatchDog(1000 * 320, new Runnable() {
                    @Override
                    public void run() {

                        log.error("Detected a hanging job");

                        if (currentThread != null) {
                            currentThread.interrupt();
                        }
                    }
                }); // 10 seconds

                dog.start();

                // process
                //
                for (SubmDataDB subm : toprocess) {
                    try {
                        processSubmittedSubmission(subm, dal, r, servername);
                    } catch (Exception ex) {
                        // reallocate R server
                        r = getRServer(rFactory);
                    } finally {
                    }
                    dog.reset();
                }

            } else {
                log.info("No new submissions found");
            }
        } catch (Exception ex) {
            log.error("Error!", ex);
        } finally {
            if (dog != null) {
                dog.stop();
            }

            if (r != null) {

                log.info("Releasing R server");

                // leave it running
                //
                rFactory.freeRServices();

                log.info("Released");
            }
        }
    }

}

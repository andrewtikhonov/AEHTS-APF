# resources

expTask = function(expid) {


    # redirect output
    #
    outputfolder = "{DESTFOLDER}/output";
    suppressWarnings({ dir.create(outputfolder,recursive=TRUE) });

    psrfolder = "{PSRFOLDER}";
    suppressWarnings({ dir.create(psrfolder,recursive=TRUE) });

    outfile = paste(outputfolder, "/", "output.txt", sep="");
    outcon <- file(outfile, open = "w");
    sink(outcon);
    sink(outcon, type = "message");

    message("submission {SUBM_ID}");
    message("experiment {EXP_ID}");
    message("processing location {DESTFOLDER}");
    message("reference location {REFFOLDER}");
    message("PSR folder {PSRFOLDER}");


    putExpId("{EXP_ID}");
    putStatusFolder("{PSRFOLDER}");

    #
    #
    writeExpStatus("PREPARATION", "STARTED", "Creating tasks");

    tasks = c("SRR0034", "SRR0035", "SRR0037", "SRR0038", "SRR0054");

    writeExpStatus("PREPARATION", "OK", "Done");


    #
    #
    writeExpStatus("LOADING_LIBRARIES", "STARTED", "Loading libs");

    nnodes = 5;

    library(snow);

    writeExpStatus("LOADING_LIBRARIES", "OK", "Done");

    #
    #
    writeExpStatus("CREATING_CLUSTER", "STARTED", "Creating a cluster");

    cl = makeCluster(nnodes, type="RCLOUD");

    on.exit({ stopCluster(cl); rm(cl); cleanupClusters(); });

    clusterExport(cl, "initTask");
    clusterExport(cl, "runTask");
    clusterExport(cl, "putStatusFolder");
    clusterExport(cl, "getStatusFolder");
    clusterExport(cl, "putRunId");
    clusterExport(cl, "getRunId");
    clusterExport(cl, "writeExpStatus");
    clusterExport(cl, "writeRunStatus");
    clusterExport(cl, "writeStatus");

    writeExpStatus("CREATING_CLUSTER", "OK", "Created");

    message(date()," init tasks");

    #
    #
    writeExpStatus("INIT_TASKS", "STARTED", "Initializing tasks");

    result = clusterApply(cl, tasks, initTask, psrfolder)

    writeExpStatus("INIT_TASKS", "OK", "Yep");

    for(cnt in 1:10) {

        message(date()," starting step ", cnt);

        # run task
        #
        message(date(),"  ---  updating status");

        writeExpStatus(paste("TASK ", cnt, sep=""), "STARTED", "");

        message(date(),"  ---  launching step");

        result = clusterApply(cl, tasks, runTask, cnt, psrfolder)

        message(date(),"  ---  updating status");

        writeExpStatus(paste("TASK ", cnt, sep=""), "OK", "Completed");

    }

    message(date()," tasks completed");

    #
    #
    writeExpStatus("ASSEMBLING", "STARTED", "Assembling the results");

    sapply(result, message);

    writeExpStatus("ASSEMBLING", "OK", "Completed");

    writeExpStatus("EXP_STATUS", "OK", "Completed");

}

initTask = function(runid, folder) {

    putStatusFolder(folder);

    outputfolder = "{DESTFOLDER}/output";
    # suppressWarnings({ dir.create(outputfolder,recursive=TRUE) });
    dir.create(outputfolder,recursive=TRUE);

    outfile = paste(outputfolder,"/",runid,".output.txt", sep="");
    outcon <- file(outfile, open = "w");
    sink(outcon);
    sink(outcon, type = "message");

}

runTask = function(runid, step, folder) {

    putRunId(runid);

    outputfolder = "{DESTFOLDER}/output";
    dir.create(outputfolder,recursive=TRUE);

    #for(step in 1:10) {

        message(date()," step=",step);

        runstep = paste("RUN_STEP_",step,sep="");

        writeRunStatus(runstep, "STARTED", "Starting job");

        for(i in 1:25) {

            if (i %% 10 == 0) {
                message("    i=",i);
            }

            Sys.sleep(1);
        }

        writeRunStatus(runstep, "OK", "Done");
    #}

    writeRunStatus("RUN_STATUS", "OK", "Yep");

    return(paste(runid, " step ", step, " complete", sep=""));
}

putStatusFolder = function(folder) {
    assign('.status', folder, envir=.GlobalEnv);
}

getStatusFolder = function() {
    get('.status');
}

putRunId = function(runid) {
    assign('.runid', runid, envir=.GlobalEnv);
}

getRunId = function() {
    return(get('.runid'));
}

putExpId = function(expid) {
    assign('.expid', expid, envir=.GlobalEnv);
}

getExpId = function() {
    return(get('.expid'));
}

writeExpStatus = function(step, status, text) {
    expid = getExpId();
    folder = getStatusFolder();
    fname = paste(folder, "/", expid, ".EXP.STATUS", sep="");
    writeStatus(fname, step, status, text);
}

writeRunStatus = function(step, status, text) {
    runid = getRunId();
    folder = getStatusFolder();
    fname = paste(folder, "/", runid, ".RUN.STATUS", sep="");
    writeStatus(fname, step, status, text);
}

writeStatus = function(filename, step, status, text) {
    df0 = data.frame("step" = c(step), "status" = c(status), "text" = c(text));
    write.table(df0, file = filename, append = TRUE,
            quote = FALSE, sep = "\t", row.names=FALSE, col.names=FALSE);
}

expTask();

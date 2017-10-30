# resources

runIt = function() {

    # redirect output
    #
    # outputfolder = "{PROCFOLDER}/{EXPID}/{SUBMID}/output";

    outputfolder = "{PROCFOLDER}/{EXPID}/{SUBMID}/output";
    suppressWarnings({ dir.create(outputfolder,recursive=TRUE) });

    psrfolder = "{PROCFOLDER}/{EXPID}/{SUBMID}/{PSRFOLDER}";
    suppressWarnings({ dir.create(psrfolder,recursive=TRUE) });

    outfile = paste(outputfolder, "/", "output.txt", sep="");
    outcon <- file(outfile, open = "w");
    sink(outcon);
    sink(outcon, type = "message");

    message("submission {SUBMID}");
    message("experiment {EXPID}");
    message("processing location {PROCFOLDER}");
    message("reference location {REFFOLDER}");
    message("PSR folder {PSRFOLDER}");

    try( detach('package:ArrayExpressHTS', unload = TRUE), silent=TRUE );

    library(ArrayExpressHTS)

    options("AEHTS.SUBM_ID" = "{SUBMID}")

    setPipelineOptions('supportedLibrary' = c('RNA-Seq', 'WGS', 'OTHER'));

    eset = ArrayExpressHTS(accession="{EXPID}",

        rcloudoptions = list (
                nnodes         = "automatic",
                memory_in_Megs = "automatic",
                type           = "RCLOUD",
                nretries       = 4 ),

        dir="{PROCFOLDER}",
        refdir="{REFFOLDER}" {OPTIONS});

}

runIt();


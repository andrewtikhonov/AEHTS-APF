rm(postProcessingResult);

postProcessing = function() {

    # aligners according to priority
    aligners  = c("tophat2", "bowtie2", "bwa", "iSAAC", "tophat", "bowtie");

    call.system = function(cmd) {

        # make sure output is not collected
        cmd0 = paste(cmd, " 1>/dev/null");
        message(cmd0);

        rc0 = try({ system(cmd0, intern = TRUE) });
        if (length(rc0) > 0) {
            stop(call.=FALSE, rc0);
        }
    }

    gatefolder = "{GATEFOLDER}";
    prodfolder = "{PRODFOLDER}";

    datafolder = "{PROCFOLDER}/{EXPID}/data";
    submfolder = "{PROCFOLDER}/{EXPID}/{SUBMID}";

    expname = unlist(strsplit("{EXPID}", "-"))[2];

    dirlist = dir(submfolder, pattern="SRR|ERR")

    for(dir in dirlist) {

        to_deliver = c("accepted_hits.sorted.bam", "accepted_hits.sorted.bam.bai", "accepted_hits.sorted.bam.prop");
        extension  = c(".bam", ".bam.bai", ".bam.prop");

        for (findex in 1:length(to_deliver)) {

            for (aln in aligners) {

                src_folder = paste(submfolder, "/", dir, "/",aln,"_out", sep="");

                if (!file.exists(src_folder)) {
                    next;
                }

                src_fname = paste(src_folder, "/", to_deliver[findex], sep="");
                src_ext   = extension[findex];

                if (!file.exists(src_fname)) {
                    message0 = paste(src_fname, " is missing", sep="");
                    message("--- ERROR ---");
                    message(message0);

                    stop(call.=FALSE, message0);
                }

                dst11 = paste(gatefolder, "/{EXPID}.BAM.", dir, src_ext, sep="");
                dst12 = paste(prodfolder, "/", expname, "/{EXPID}/{EXPID}.BAM.", dir, src_ext, sep="");

                cmd11 = paste("cp ", src_fname, " ", dst11, sep="");
                cmd12 = paste("chmod a+w ", dst11, sep="");
                cmd13 = paste("cp ", src_fname, " ", dst12, sep="");

                call.system(cmd11);
                call.system(cmd12);
                call.system(cmd13);

                break;
            }
        }
    }

    return("");
}

postProcessingResult = try({ postProcessing() }, silent = TRUE);


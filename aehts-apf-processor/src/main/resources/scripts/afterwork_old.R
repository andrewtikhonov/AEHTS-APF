
workIt_Old = function() {

    aligners  = c("tophat", "tophat2", "bowtie", "bowtie2", "bwa", "iSAAC");

    call.system = function(cmd) {

        message(cmd);

        rc = 0;
        rc = try({ system(cmd) });
        if (inherits(rc, "try-error")) {
            message(" Failed");
        }
    }

    gatefolder = "{GATEFOLDER}";
    prodfolder = "{PRODFOLDER}";

    datafolder = "{PROCFOLDER}/{EXPID}/data";
    submfolder = "{PROCFOLDER}/{EXPID}/{SUBMID}";

    expname = unlist(strsplit("{EXPID}", "-"))[2];

    dirlist = dir(submfolder, pattern="SRR|ERR")

    for(dir in dirlist) {

        src1 = paste(submfolder, "/", dir, "/tophat_out/accepted_hits.sorted.bam", sep="");
        src2 = paste(submfolder, "/", dir, "/tophat_out/accepted_hits.sorted.bam.bai", sep="");
        src3 = paste(submfolder, "/", dir, "/tophat_out/accepted_hits.sorted.bam.prop", sep="");

        if (!file.exists(src1)) {
            message("**** ERROR ****\n");
            message("\t", src1," is missing\n\n");
        } else {

            dst11 = paste(gatefolder, "/{EXPID}.BAM.",dir,".bam", sep="");
            dst12 = paste(prodfolder, "/", expname, "/{EXPID}/{EXPID}.BAM.",dir,".bam", sep="");

            cmd11 = paste("cp ", src1, " ", dst11, sep="");
            cmd12 = paste("chmod a+w ", dst11, sep="");
            cmd13 = paste("cp ", src1, " ", dst12, sep="");

            call.system(cmd11);
            call.system(cmd12);
            call.system(cmd13);
        }

        if (!file.exists(src2)) {
            message("**** ERROR ****\n");
            message("\t", src2," is missing\n\n");
        } else {

            dst21 = paste(gatefolder, "/{EXPID}.BAM.",dir,".bam.bai", sep="");
            dst22 = paste(prodfolder, "/", expname, "/{EXPID}/{EXPID}.BAM.",dir,".bam.bai", sep="");

            cmd21 = paste("cp ", src2, " ", dst21, sep="");
            cmd22 = paste("chmod a+w ", dst21, sep="");
            cmd23 = paste("cp ", src2, " ", dst22, sep="");

            call.system(cmd21);
            call.system(cmd22);
            call.system(cmd23);
        }

        if (!file.exists(src3)) {
            message("**** ERROR ****\n");
            message("\t", src3," is missing\n\n");
        } else {

            dst31 = paste(gatefolder, "/{EXPID}.BAM.",dir,".bam.prop", sep="");
            dst32 = paste(prodfolder, "/", expname, "/{EXPID}/{EXPID}.BAM.",dir,".bam.prop", sep="");

            cmd31 = paste("cp ", src3, " ", dst31, sep="");
            cmd32 = paste("chmod a+w ", dst31, sep="");
            cmd33 = paste("cp ", src3, " ", dst32, sep="");

            call.system(cmd31);
            call.system(cmd32);
            call.system(cmd33);
        }
    }
}

workIt();


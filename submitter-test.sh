ACCESSION=$@

JVMOPTS0="-Daccession='${ACCESSION}' -Dsubmitter='Vasja Pupkin' -Drelver=current"
JVMOPTS1="-Dcomment='Comment' -Doptions='align=on&count=null&eset=null&reports=null'"
JAR=./aehts-apf-db/target/aehts-apf-db-1.0-aehts-apf-jar-with-dependencies.jar
CLASS=submitter.Submitter

sh -c "java ${JVMOPTS0} ${JVMOPTS1} -cp ${JAR} ${CLASS}"


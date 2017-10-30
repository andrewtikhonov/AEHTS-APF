
    update SUBMISSION set STATUS = 'PROCESSING_REQUESTED', START_TIME = null,
      FINISH_TIME = null, MASTER_NAME = null, FILE_TIME = 0, NOTIFIED = '0'
        where STATUS != 'PROCESSING_REQUESTED';

    delete from RUN;
    delete from RUNSTEP;
    delete from EXPSTEP;
    delete from STEP;

    COMMIT;


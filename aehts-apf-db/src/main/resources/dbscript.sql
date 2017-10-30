
    DROP SEQUENCE submission_seq;
    DROP TABLE STEP;
    DROP TABLE RUN;
    DROP TABLE RUNSTEP;
    DROP TABLE EXPSTEP;
    DROP TABLE SUBMISSION;

    CREATE TABLE SUBMISSION (
        SUBM_ID VARCHAR2 (50) NOT NULL,
        EXP_ID VARCHAR2 (50) NOT NULL,
        STATUS VARCHAR2 (100) NOT NULL,
        SUBMITTER VARCHAR2 (100),
        USER_NOTES VARCHAR2 (1000),
        OPTIONS VARCHAR2 (4000),
        SUBMIT_TIME TIMESTAMP(6),
        START_TIME TIMESTAMP(6),
        FINISH_TIME TIMESTAMP(6),
        MASTER_NAME VARCHAR2 (50),
        ENSEMBL_REL_VER VARCHAR2 (50),
        FILE_TIME NUMBER(38),
        NOTIFIED INTEGER,
        COPIED INTEGER,

        CONSTRAINT pk_SUBM PRIMARY KEY (SUBM_ID)
    );

    CREATE TABLE RUN (
        SUBM_ID VARCHAR2 (50) NOT NULL,
        RUN_ID VARCHAR2 (50) NOT NULL,
        EXP_ID VARCHAR2 (50),
        STATUS VARCHAR2 (100),
        STATUS_TEXT VARCHAR2 (500),
        FILE_TIME NUMBER(38),

        CONSTRAINT pk_SUBM_RUN PRIMARY KEY (SUBM_ID, RUN_ID),
        CONSTRAINT fk_SUBM_RUN FOREIGN KEY (SUBM_ID) REFERENCES SUBMISSION(SUBM_ID)
    );

    CREATE TABLE RUNSTEP (
        SUBM_ID VARCHAR2 (50) NOT NULL,
        RUN_ID VARCHAR2 (50) NOT NULL,
        STEP_ID VARCHAR2 (100) NOT NULL,
        STATUS VARCHAR2 (100),
        STATUS_TEXT VARCHAR2 (500),

        CONSTRAINT pk_SUBM_RUN_STEP PRIMARY KEY (SUBM_ID, RUN_ID, STEP_ID),
        CONSTRAINT fk_SUBM_RUN_STEP FOREIGN KEY (SUBM_ID) REFERENCES SUBMISSION(SUBM_ID)
    );


    CREATE TABLE EXPSTEP (
        SUBM_ID VARCHAR2 (50) NOT NULL,
        EXP_ID VARCHAR2 (50) NOT NULL,
        STEP_ID VARCHAR2 (100) NOT NULL,
        STATUS VARCHAR2 (100),
        STATUS_TEXT VARCHAR2 (500),

        CONSTRAINT pk_SUBM_EXP_STEP PRIMARY KEY (SUBM_ID, EXP_ID, STEP_ID),
        CONSTRAINT fk_SUBM_EXP_STEP FOREIGN KEY (SUBM_ID) REFERENCES SUBMISSION(SUBM_ID)
    );

    CREATE TABLE STEP (
        STEP_ID VARCHAR2 (100) NOT NULL,
        DESCRIPTION VARCHAR2 (1000),

        CONSTRAINT UNIQUE_STEP_ID UNIQUE (STEP_ID)
    );

    CREATE SEQUENCE submission_seq
        MINVALUE 1
        START WITH 1
        INCREMENT BY 1
        CACHE 20;


    CREATE TABLE CONFIG (
        OPTION_NAME VARCHAR2 (200) NOT NULL,
        OPTION_VALUE VARCHAR2 (4000),

        CONSTRAINT UNIQUE_OPTION_NAME UNIQUE (OPTION_NAME)
    );

    insert into config (OPTION_NAME, OPTION_VALUE) values ('STATUS_FOLDER_NAME', 'PSR');
    insert into config (OPTION_NAME, OPTION_VALUE) values ('PROCESS_USERNAME', 'aehts-apf');
    insert into config (OPTION_NAME, OPTION_VALUE) values ('DEFAULT_POOLNAME', 'poolname');
    insert into config (OPTION_NAME, OPTION_VALUE) values ('DEFAULT_SERVERNAME', 'UNDEFINED');
    insert into config (OPTION_NAME, OPTION_VALUE) values ('PROCESSING_LOCATION', '/processing');
    insert into config (OPTION_NAME, OPTION_VALUE) values ('REFERENCE_LOCATION', '/reference');

    COMMIT;


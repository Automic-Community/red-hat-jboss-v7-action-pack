*** Settings ***
Documentation     NOTE:
...               add note here
Suite Setup       Suite Setup
Force Tags        DELETE_DATASOURCE    win    unix
Test Template     Delete Datasource Template
Resource          ../../resources/keywords.txt

*** Test Cases ***    MODE          PROFILE                          NAME                  DSTYPE           FAIL IF MISSING    RETURN CODE    RETURN STATUS
Delete xa datasource
                      [Setup]       Create Xa Datasource Template    xa_to_delete_${OS}
                      Standalone    testds                           xa_to_delete_${OS}    XA datasource    NO                 0              ${ENDED_OK}

Delete tx datasource
                      [Setup]       Create Tx Datasource Template    tx_to_delete_${OS}
                      Standalone    profiletx                        tx_to_delete_${OS}    TX datasource    NO                 0              ${ENDED_OK}

*** Keywords ***
Delete Datasource Template
    [Arguments]    ${mode}    ${profile}    ${name}    ${dstype}    ${fail_ifmissing}    ${returnCode}
    ...    ${returnStatus}
    Init Action    ${ACTION_DELETE_DATASOURCE}
    Action Set    ${PRT_DELETE_DATASOURCE_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_DELETE_DATASOURCE_JBOSS_PROFILE}    ${profile}
    Action Set    ${PRT_DELETE_DATASOURCE_JBOSS_NAME}    ${name}
    Action Set    ${PRT_DELETE_DATASOURCE_JBOSS_DS_TYPE}    ${dstype}
    Action Set    ${PRT_DELETE_DATASOURCE_JBOSS_FAIL_MISSING}    ${fail_ifmissing}
    JBOSS Common Setting With User
    Action Execute
    Action Report Should Be Found
    Action Return Code Should Be    ${returnCode}
    Action Return Status Should Be    ${returnStatus}

Create Xa Datasource Template
    [Arguments]    ${name}
    Init Action    ${ACTION_CREATE_XA_DATASOURCE}
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_OPERATING_MODE}    Standalone
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_PROFILE}    testds
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_NAME}    ${name}
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_JNDI_NAME}    java:jboss/datasources/${name}
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_DRIVER_NAME}    h2
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_DS_CLASS}    org.h2.Driver
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_DS_USERNAME}    testds
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_DS_PASSWORD}    testds
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_MAX_POOL_SIZE}    100
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_MIN_POOL_SIZE}    200
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_PROPERTIES}    testproperty
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_JTA_INTEGRATION}    YES
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_FAIL_EXISTING}    NO
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_UPDATE_EXISTING}    NO
    JBOSS Common Setting With User
    Action Execute

Create Tx Datasource Template
    [Arguments]    ${name}
    Init Action    ${ACTION_CREATE_TX_DATASOURCE}
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_OPERATING_MODE}    Standalone
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_PROFILE}    profiletx
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_NAME}    ${name}
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_JNDI_NAME}    java:jboss/datasources/${name}
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_DRIVER_NAME}    h2
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_URL}    jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_DRIVER_CLASS}    org.h2.Driver
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_DS_USERNAME}    usertx
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_DS_PASSWORD}    passtx
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_MAX_POOL_SIZE}    200
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_MIN_POOL_SIZE}    100
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_PROPERTIES}    testproperties
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_JTA_INTEGRATION}    YES
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_FAIL_EXISTING}    NO
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_UPDATE_EXISTING}    YES
    JBOSS Common Setting With User
    Action Execute

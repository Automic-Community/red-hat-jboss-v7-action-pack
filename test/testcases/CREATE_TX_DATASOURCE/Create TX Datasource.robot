*** Settings ***
Documentation     NOTE:
...               add note here
Suite Setup       Suite Setup
Force Tags        CREATE_TX_DATASOURCE    win    unix
Test Template     Create Tx Datasource Template
Resource          ../../resources/keywords.txt

*** Test Cases ***    MODE          PROFILE      NAME            JNDI_NAME                              DRIVER_NAME    URL      DRIVER_CLASS     DS_USERNAME    DS_PASSWORD    MAX_POOL    MIN_POOL    PROPERTIES        JTA    FAIL_EXISTING    UPDATE_EXISTING    RETURN CODE    RETURN STATUS
Create TX Datasource
                      Standalone    profiletx    nametx_${OS}    java:jboss/datasources/nametx_${OS}    h2             urltx    org.h2.Driver    usertx         passtx         200         100         testproperties    YES    NO               YES                0              ${ENDED_OK}

*** Keywords ***
Create Tx Datasource Template
    [Arguments]    ${mode}    ${profile}    ${name}    ${jndi_name}    ${driver_name}    ${url}
    ...    ${driver_class}    ${ds_username}    ${ds_password}    ${max_pool}    ${min_pool}    ${properties}
    ...    ${jta}    ${fail_existing}    ${update_existing}    ${returnCode}    ${returnStatus}
    Init Action    ${ACTION_CREATE_TX_DATASOURCE}
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_PROFILE}    ${profile}
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_NAME}    ${name}
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_JNDI_NAME}    ${jndi_name}
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_DRIVER_NAME}    ${driver_name}
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_URL}    ${url}
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_DRIVER_CLASS}    ${driver_class}
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_DS_USERNAME}    ${ds_username}
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_DS_PASSWORD}    ${ds_password}
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_MAX_POOL_SIZE}    ${max_pool}
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_MIN_POOL_SIZE}    ${min_pool}
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_PROPERTIES}    ${properties}
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_JTA_INTEGRATION}    ${jta}
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_FAIL_EXISTING}    ${fail_existing}
    Action Set    ${PRT_CREATE_TX_DATASOURCE_JBOSS_UPDATE_EXISTING}    ${update_existing}
    JBOSS Common Setting With User
    Action Execute
    Action Report Should Be Found
    Action Return Code Should Be    ${returnCode}
    Action Return Status Should Be    ${returnStatus}
    Init Action    ${ACTION_DELETE_DATASOURCE}
    Action Set    ${PRT_DELETE_DATASOURCE_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_DELETE_DATASOURCE_JBOSS_PROFILE}    ${profile}
    Action Set    ${PRT_DELETE_DATASOURCE_JBOSS_NAME}    ${name}
    Action Set    ${PRT_DELETE_DATASOURCE_JBOSS_DS_TYPE}    TX datasource
    Action Set    ${PRT_DELETE_DATASOURCE_JBOSS_FAIL_MISSING}    NO
    JBOSS Common Setting With User
    Action Execute

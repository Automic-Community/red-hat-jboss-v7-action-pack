*** Settings ***
Documentation     NOTE:
...               add note here
Suite Setup       Suite Setup
Force Tags        CREATE_XA_DATASOURCE    win    unix
Test Template     Create Xa Datasource Template
Resource          ../../resources/keywords.txt

*** Test Cases ***    MODE          PROFILE    NAME            JNDI NAME                              DRIVER NAME    DS CLASS         DS USERNAME    DS PASSWORD    MIN POOL SIZE    MAX POOL SIZE    PROPERTIES      JTA    FAIL IF EXIST    UPDATE EXISTING    RETURN CODE    RETURN STATUS
Create XA Datasource
                      Standalone    testds     testds_${OS}    java:jboss/datasources/testds_${OS}    h2             org.h2.Driver    testds         testds         100              200              testproperty    YES    NO               YES                0              ${ENDED_OK}

*** Keywords ***
Create Xa Datasource Template
    [Arguments]    ${mode}    ${profile}    ${name}    ${jndi_name}    ${driver_name}    ${ds_class}
    ...    ${ds_username}    ${ds_password}    ${min_pool_size}    ${max_pool_size}    ${properties}    ${jta_integration}
    ...    ${fail_existing}    ${update_existing}    ${returnCode}    ${returnStatus}
    Init Action    ${ACTION_CREATE_XA_DATASOURCE}
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_PROFILE}    ${profile}
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_NAME}    ${name}
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_JNDI_NAME}    ${jndi_name}
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_DRIVER_NAME}    ${driver_name}
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_DS_CLASS}    ${ds_class}
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_DS_USERNAME}    ${ds_username}
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_DS_PASSWORD}    ${ds_password}
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_MAX_POOL_SIZE}    ${max_pool_size}
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_MIN_POOL_SIZE}    ${min_pool_size}
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_PROPERTIES}    ${properties}
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_JTA_INTEGRATION}    ${jta_integration}
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_FAIL_EXISTING}    ${fail_existing}
    Action Set    ${PRT_CREATE_XA_DATASOURCE_JBOSS_UPDATE_EXISTING}    ${update_existing}
    JBOSS Common Setting With User
    Action Execute
    Action Report Should Be Found
    Action Return Code Should Be    ${returnCode}
    Action Return Status Should Be    ${returnStatus}
    Init Action    ${ACTION_DELETE_DATASOURCE}
    Action Set    ${PRT_DELETE_DATASOURCE_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_DELETE_DATASOURCE_JBOSS_PROFILE}    ${profile}
    Action Set    ${PRT_DELETE_DATASOURCE_JBOSS_NAME}    ${name}
    Action Set    ${PRT_DELETE_DATASOURCE_JBOSS_DS_TYPE}    XA datasource
    Action Set    ${PRT_DELETE_DATASOURCE_JBOSS_FAIL_MISSING}    NO
    JBOSS Common Setting With User
    Action Execute

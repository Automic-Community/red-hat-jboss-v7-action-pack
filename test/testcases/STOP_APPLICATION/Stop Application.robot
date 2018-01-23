*** Settings ***
Documentation     NOTE:
...               add note here
Suite Setup       Suite Setup
Force Tags        STOP_APPLICATION    unix    win
Test Template     Stop Application Template
Resource          ../../resources/keywords.txt

*** Test Cases ***    MODE          SERVER GROUP    APP NAME         FAIL IF STOPPED    RETURN CODE    RETURN STATUS
Stop Application Normal
                      [Tags]        SMOKE_TEST
                      Standalone    ${EMPTY}        ${_JBOSS_APP}    NO                 0              ENDED_OK - ended normally

*** Keywords ***
Stop Application Template
    [Arguments]    ${mode}    ${server_group}    ${app_name}    ${fail_if_stopped}    ${returnCode}    ${returnStatus}
    Init Action    ${ACTION_DEPLOY_APPLICATION}
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_APP_NAME}    ${app_name}
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_FAIL_EXISTING}    NO
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_UPDATE}    YES
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_SERVER_GROUPS}    ${EMPTY}
    JBOSS Common Setting With User
    JBOSS Setting Package
    Action Execute
    Init Action    ${ACTION_START_APPLICATION}
    Action Set    ${PRT_START_APPLICATION_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_START_APPLICATION_JBOSS_SERVER_GROUPS}    ${EMPTY}
    Action Set    ${PRT_START_APPLICATION_JBOSS_APP_NAME}    ${app_name}
    Action Set    ${PRT_START_APPLICATION_JBOSS_FAIL_IF_RUNNING}    NO
    JBOSS Common Setting With User
    Action Execute
    Init Action    ${ACTION_STOP_APPLICATION}
    Action Set    ${PRT_STOP_APPLICATION_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_STOP_APPLICATION_JBOSS_SERVER_GROUPS}    ${server_group}
    Action Set    ${PRT_STOP_APPLICATION_JBOSS_APP_NAME}    ${app_name}
    Action Set    ${PRT_STOP_APPLICATION_JBOSS_FAIL_IF_STOPPED}    ${fail_if_stopped}
    JBOSS Common Setting With User
    Action Execute
    Action Report Should Be Found
    Action Return Code Should Be    ${returnCode}
    Action Return Status Should Be    ${returnStatus}
    Init Action    ${ACTION_UNDEPLOY_APPLICATION}
    Action Set    ${PRT_UNDEPLOY_APPLICATION_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_UNDEPLOY_APPLICATION_JBOSS_APP_NAME}    ${app_name}
    Action Set    ${PRT_UNDEPLOY_APPLICATION_JBOSS_FAIL_MISSING}    NO
    Action Set    ${PRT_UNDEPLOY_APPLICATION_JBOSS_SERVER_GROUPS}    ${EMPTY}
    JBOSS Common Setting With User
    Action Execute

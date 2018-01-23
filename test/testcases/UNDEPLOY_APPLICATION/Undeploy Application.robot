*** Settings ***
Documentation     NOTE:
...               add note here
Suite Setup       Suite Setup
Force Tags        UNDEPLOY_APPLICATION    unix    win
Test Template     Undeploy Application Template
Resource          ../../resources/keywords.txt

*** Test Cases ***    MODE          APP           FAIL_IF_MISSING    RETURN CODE    RETURN STATUS
Undeploy Application
                      Standalone    ${_JBOSS_APP}    NO                 0              ENDED_OK - ended normally

*** Keywords ***
Undeploy Application Template
    [Arguments]    ${mode}    ${app}    ${fail_if_missing}    ${returnCode}    ${returnStatus}
    Init Action    ${ACTION_DEPLOY_APPLICATION}
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_APP_NAME}    ${app}
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_FAIL_EXISTING}    NO
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_UPDATE}    YES
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_SERVER_GROUPS}    ${EMPTY}
    JBOSS Common Setting With User
    JBOSS Setting Package
    Action Execute
    Init Action    ${ACTION_UNDEPLOY_APPLICATION}
    Action Set    ${PRT_UNDEPLOY_APPLICATION_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_UNDEPLOY_APPLICATION_JBOSS_APP_NAME}    ${app}
    Action Set    ${PRT_UNDEPLOY_APPLICATION_JBOSS_FAIL_MISSING}    ${fail_if_missing}
    Action Set    ${PRT_UNDEPLOY_APPLICATION_JBOSS_SERVER_GROUPS}    ${EMPTY}
    JBOSS Common Setting With User
    Action Execute
    Action Report Should Be Found
    Action Return Code Should Be    ${returnCode}
    Action Return Status Should Be    ${returnStatus}

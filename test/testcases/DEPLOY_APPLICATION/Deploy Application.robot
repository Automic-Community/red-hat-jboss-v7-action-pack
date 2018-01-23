*** Settings ***
Documentation     NOTE:
...               add note here
Suite Setup       Suite Setup
Force Tags        DEPLOY_APPLICATION    unix    win
Test Template     Deploy Application Template
Resource          ../../resources/keywords.txt

*** Test Cases ***    MODE          APP           FAIL EXISTING    UPDATE EXISTING    GROUP       RETURN CODE    RETURN STATUS
Deploy Application Normal
                      Standalone    ${_JBOSS_APP}    NO               NO                 ${EMPTY}    0              ENDED_OK - ended normally

*** Keywords ***
Deploy Application Template
    [Arguments]    ${mode}    ${app}    ${fail_existing}    ${update_existing}    ${group}    ${returnCode}
    ...    ${returnStatus}
    Init Action    ${ACTION_DEPLOY_APPLICATION}
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_APP_NAME}    ${app}
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_FAIL_EXISTING}    ${fail_existing}
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_UPDATE}    ${update_existing}
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_SERVER_GROUPS}    ${group}
    JBOSS Common Setting With User
    JBOSS Setting Package
    Action Execute
    Action Report Should Be Found
    Action Return Code Should Be    ${returnCode}
    Action Return Status Should Be    ${returnStatus}
    Init Action    ${ACTION_UNDEPLOY_APPLICATION}
    Action Set    ${PRT_UNDEPLOY_APPLICATION_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_UNDEPLOY_APPLICATION_JBOSS_APP_NAME}    ${app}
    Action Set    ${PRT_UNDEPLOY_APPLICATION_JBOSS_FAIL_MISSING}    NO
    Action Set    ${PRT_UNDEPLOY_APPLICATION_JBOSS_SERVER_GROUPS}    ${EMPTY}
    JBOSS Common Setting With User
    Action Execute

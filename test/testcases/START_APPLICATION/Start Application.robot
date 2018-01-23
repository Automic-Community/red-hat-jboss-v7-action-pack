*** Settings ***
Documentation     NOTE:
...               add note here
Suite Setup       Suite Setup
Force Tags        START_APPLICATION    unix    win
Test Template     Start Application Template
Resource          ../../resources/keywords.txt

*** Test Cases ***    MODE          APPNAME       FAIL IF RUNNING    RETURN CODE    RETURN STATUS
Start Application Normal
                      Standalone    ${_JBOSS_APP}    NO                 0              ENDED_OK - ended normally

*** Keywords ***
Start Application Template
    [Arguments]    ${mode}    ${appname}    ${failifrunning}    ${returnCode}    ${returnStatus}
    Init Action    ${ACTION_DEPLOY_APPLICATION}
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_APP_NAME}    ${appname}
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_FAIL_EXISTING}    NO
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_UPDATE}    YES
    Action Set    ${PRT_DEPLOY_APPLICATION_JBOSS_SERVER_GROUPS}    ${EMPTY}
    JBOSS Common Setting With User
    JBOSS Setting Package
    Action Execute
    Init Action    ${ACTION_START_APPLICATION}
    Action Set    ${PRT_START_APPLICATION_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_START_APPLICATION_JBOSS_SERVER_GROUPS}    ${EMPTY}
    Action Set    ${PRT_START_APPLICATION_JBOSS_APP_NAME}    ${appname}
    Action Set    ${PRT_START_APPLICATION_JBOSS_FAIL_IF_RUNNING}    ${failifrunning}
    JBOSS Common Setting With User
    Action Execute
    Action Report Should Be Found
    Action Return Code Should Be    ${returnCode}
    Action Return Status Should Be    ${returnStatus}
    Init Action    ${ACTION_UNDEPLOY_APPLICATION}
    Action Set    ${PRT_UNDEPLOY_APPLICATION_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_UNDEPLOY_APPLICATION_JBOSS_APP_NAME}    ${appname}
    Action Set    ${PRT_UNDEPLOY_APPLICATION_JBOSS_FAIL_MISSING}    NO
    Action Set    ${PRT_UNDEPLOY_APPLICATION_JBOSS_SERVER_GROUPS}    ${EMPTY}
    JBOSS Common Setting With User
    Action Execute

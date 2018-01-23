*** Settings ***
Documentation     NOTE:
...               add note here
Suite Setup       Suite Setup
Force Tags        START_SERVER    win    unix
Test Template     Start Server Template
Resource          ../../resources/keywords.txt

*** Test Cases ***    MODE          RETURN CODE    RETURN STATUS
Start Server Normal
                      Standalone    0              ${ENDED_OK}

*** Keywords ***
Start Server Template
    [Arguments]    ${mode}    ${returnCode}    ${returnStatus}
    Init Action    ${ACTION_STOP_SERVER}
    Action Set    ${PRT_STOP_SERVER_JBOSS_OPERATING_MODE}    ${mode}
    JBOSS Common Setting No User
    Action Execute
    Init Action    ${ACTION_START_SERVER}
    Action Set    ${PRT_START_SERVER_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_START_SERVER_JBOSS_SYSTEM_PROPS}    ${EMPTY}
    JBOSS Common Setting No User
    Action Execute
    Action Report Should Be Found
    Action Return Code Should Be    ${returnCode}
    Action Return Status Should Be    ${returnStatus}

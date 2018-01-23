*** Settings ***
Documentation     NOTE:
...               add note here
Suite Setup       Suite Setup
Force Tags        STOP_SERVER    win    unix
Test Template     Stop Server Template
Resource          ../../resources/keywords.txt

*** Test Cases ***    MODE          RETURN CODE    RETURN STATUS
Stop Server Normal    Standalone    0              ${ENDED_OK}

*** Keywords ***
Stop Server Template
    [Arguments]    ${mode}    ${returnCode}    ${returnStatus}
    Init Action    ${ACTION_STOP_SERVER}
    Action Set    ${PRT_STOP_SERVER_JBOSS_OPERATING_MODE}    ${mode}
    JBOSS Common Setting No User
    Action Execute
    Action Report Should Be Found
    Action Return Code Should Be    ${returnCode}
    Action Return Status Should Be    ${returnStatus}

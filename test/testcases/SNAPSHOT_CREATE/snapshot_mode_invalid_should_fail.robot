*** Settings ***
Documentation     Test JBoss create snapshot action with invalid input of Snapshot Mode.
...               Possible valid input is:
...               + APPLICATION
...               + SERVER
...               + BOTH (default)
...               Test case will not using above values.
Suite Setup       Suite Setup
Suite Teardown    Run Keywords    Delete folder    ${AGENT_DIR}${/}snapshot    AND    Stop Server    Standalone
Force Tags        SNAPSHOT_CREATE    unix55    unix60    unix70    win55    win60    win70
Test Template     Test Snapshot Mode
Resource          ../../resources/keywords.txt
Resource          shared_template.robot

*** Test Cases ***    SNAPSHOT_MODE        RETURN STATUS      RETURN CODE
Empty input           ${EMPTY}             ${ENDED_NOT_OK}    1

Special Character     $pecial Ch@r@ct3r    ${ENDED_NOT_OK}    1

Not match case 1      application          ${ENDED_NOT_OK}    1

Not match case 2      server               ${ENDED_NOT_OK}    1

Not match case 3      both                 ${ENDED_NOT_OK}    1

Out of options        APPLICATION1         ${ENDED_NOT_OK}    1

*** Key Words ***
Test Snapshot Mode
    [Arguments]    ${snapshot_mode}    ${returnStatus}    ${returnCode}
    Config a valid application workflow
    Action Set    &UC4RB_SNAPSHOT_MODE#    ${snapshot_mode}
    Action Execute
    Action Report Should Not Be Found

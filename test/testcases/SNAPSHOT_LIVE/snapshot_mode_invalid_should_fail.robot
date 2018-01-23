*** Settings ***
Documentation     Test JBoss create snapshot action with invalid input of Snapshot Mode.
...               Possible valid input is:
...               + APPLICATION
...               + SERVER
...               + BOTH (default)
...               Test case will not using above values.
Suite Setup       Suite Setup
Suite Teardown    Run Keywords    Delete folder    ${AGENT_DIR}${/}snapshot    AND    Stop Server    Standalone
Force Tags        SNAPSHOT_LIVE    unix55    unix60    unix70    win55    win60    win70
Test Template     Test Snapshot Mode
Resource          ../../resources/keywords.txt
Resource          shared_template.robot

*** Test Cases ***    DESCRIPTOR_PATH      RETURN STATUS        RETURN CODE
Empty input           ${EMPTY}             ${ENDED_JP_ABEND}    999

Special Character     $pecial Ch@r@ct3r    ${ENDED_JP_ABEND}    999

Not match case 1      application          ${ENDED_OK}          0

Not match case 2      server               ${ENDED_OK}          0

Not match case 3      both                 ${ENDED_OK}          0

Out of options        APPLICATION1         ${ENDED_JP_ABEND}    999

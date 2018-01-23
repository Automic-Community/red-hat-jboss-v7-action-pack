*** Settings ***
Documentation     Test JBoss create snapshot action with invalid input of Server Filter Exclude.
...               Possible valid input follow Unix/Window folder name convention OR just is EMPTY value.
...               Default value is "temp/, logs/, work/, bin/".
...               Test case will not using above values.
Suite Setup       Suite Setup
Suite Teardown    Run Keywords    Delete folder    ${AGENT_DIR}${/}snapshot    AND    Stop Server    Standalone
Force Tags        SNAPSHOT_CREATE    unix55    unix60    unix70    win55    win60    win70
Test Template     Test Server Filter Exclude
Resource          ../../resources/keywords.txt
Resource          shared_template.robot

*** Test Cases ***    FOLDER NAME           RETURN STATUS    RETURN CODE
Special Character     $pec\a/ Ch@r@ct3r?    ${ENDED_OK}      0

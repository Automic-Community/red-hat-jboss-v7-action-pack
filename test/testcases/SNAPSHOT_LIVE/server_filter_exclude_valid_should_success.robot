*** Settings ***
Documentation     Test JBoss create snapshot action with invalid input of Server Filter Exclude.
...               Possible valid input follow Unix/Window folder name convention OR just is EMPTY value.
...               Default value is "temp/, logs/, work/, bin/".
...               Test case will not using above values.
Suite Setup       Suite Setup
Suite Teardown    Run Keywords    Delete folder    ${AGENT_DIR}${/}snapshot    AND    Stop Server    Standalone
Force Tags        SNAPSHOT_LIVE    unix55    unix60    unix70    win55    win60    win70
Test Template     Test Server Filter Exclude
Resource          ../../resources/keywords.txt
Resource          shared_template.robot

*** Test Cases ***    FOLDER NAME                                                            RETURN STATUS    RETURN CODE
Empty value           ${EMPTY}                                                               ${ENDED_OK}      0

Existed folder        temp/                                                                  ${ENDED_OK}      0

Non existed folder    Non Existed folder/                                                    ${ENDED_OK}      0

Multi existed folder
                      tmp/, log/                                                             ${ENDED_OK}      0

Multi non existed folder
                      Non Existed folder 1/, Non Existed folder 2/, Non Existed folder 3/    ${ENDED_OK}      0

Dupplicate folder     temp/, temp/, temp/                                                    ${ENDED_OK}      0

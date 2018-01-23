*** Settings ***
Documentation     Test JBoss create snapshot action with invalid input of App Filter Exclude.
...               Possible valid input follow Unix/Window folder name convention OR just is EMPTY value.
...               Test case will using above values.
Suite Setup       Suite Setup
Suite Teardown    Run Keywords    Delete folder    ${AGENT_DIR}${/}snapshot    AND    Stop Server    Standalone
Force Tags        SNAPSHOT_CREATE    unix55    unix60    unix70    win55    win60    win70
Test Template     Test App Filter Exclude
Resource          ../../resources/keywords.txt
Resource          shared_template.robot

*** Test Cases ***    FOLDER NAME                                                            RETURN STATUS    RETURN CODE
Empty value           ${EMPTY}                                                               ${ENDED_OK}      0

Existed folder        WEB-INF/                                                               ${ENDED_OK}      0

Non existed folder    Non Existed folder/                                                    ${ENDED_OK}      0

Multi existed folder
                      websocket/, WEB-INF/                                                   ${ENDED_OK}      0

Multi non existed folder
                      Non Existed folder 1/, Non Existed folder 2/, Non Existed folder 3/    ${ENDED_OK}      0

Dupplicate folder     WEB-INF/, WEB-INF/, WEB-INF/                                           ${ENDED_OK}      0

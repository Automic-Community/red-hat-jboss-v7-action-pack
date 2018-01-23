*** Settings ***
Documentation     Test JBoss create snapshot action with invalid input of App Filter Exclude.
...               Possible valid input follow Unix/Window folder name convention OR just is EMPTY value.
...               Test case will not using above values.
Suite Setup       Suite Setup
Suite Teardown    Run Keywords    Delete folder    ${AGENT_DIR}${/}snapshot    AND    Stop Server    Standalone
Force Tags        SNAPSHOT_COMPARE    unix55    unix60    unix70    win55    win60    win70
Test Template     archive_path_valid_should_success
Resource          ../../resources/keywords.txt
Resource          shared_template.robot

*** Test Cases ***    RETURN STATUS    RETURN CODE
Normal                ${ENDED_OK}      0

*** Key Words ***
archive_path_valid_should_success
    [Arguments]    ${returnStatus}    ${returnCode}
    @{snapshot_info}=    Config a valid application workflow
    Common testsuites execute and assert    ${returnStatus}    ${returnCode}
    [Teardown]    Run Keyword Unless    '@{snapshot_info}[0]' == '${EMPTY}'    Clean Snapshot    @{snapshot_info}[0]    @{snapshot_info}[1]

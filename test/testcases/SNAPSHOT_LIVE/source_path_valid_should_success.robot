*** Settings ***
Documentation     Test JBoss create snapshot action with invalid input.
...               Test data MUST fill in deployment descriptor xml file, which will be using
...               for execute Application Workflow via UC4 apis.
Suite Setup       Suite Setup
Suite Teardown    Run Keywords    Delete folder    ${AGENT_DIR}${/}snapshot    AND    Stop Server    Standalone
Force Tags        SNAPSHOT_LIVE    unix55    unix60    unix70    win55    win60    win70
Test Template     source_path_valid_should_success
Resource          ../../resources/keywords.txt
Resource          shared_template.robot

*** Test Cases ***    RETURN STATUS    RETURN CODE
Valid case 1          ${ENDED_OK}      0

*** Key Words ***
source_path_valid_should_success
    [Arguments]    ${returnStatus}    ${returnCode}
    Config a valid application workflow
    Common testsuites execute and assert    ${returnStatus}    ${returnCode}
    ${archive_path}=    Action Get Variable    &UC4RB_ARCHIVE_PATH#
    ${guid}=    Action Get Variable    &UC4RB_OUT_TARGET_GUID#
    [Teardown]    Run Keyword Unless    '${archive_path}' == '${EMPTY}'    Clean Snapshot    ${archive_path}    ${guid}

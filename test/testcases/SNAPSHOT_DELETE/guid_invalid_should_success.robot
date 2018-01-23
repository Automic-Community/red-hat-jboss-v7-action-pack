*** Settings ***
Documentation     Test JBoss delete snapshot.
Suite Setup       Suite Setup
Suite Teardown    Run Keywords    Delete folder    ${AGENT_DIR}${/}snapshot
...               AND    Stop Server    Standalone
Force Tags        SNAPSHOT_DELETE    unix55    unix60    unix70    win55    win60    win70
Test Template     guid_invalid_should_fail
Resource          ../../resources/keywords.txt
Resource          shared_template.robot

*** Test Cases ***    GUID                RETURN STATUS    RETURN CODE
Guid not correct      [Tags]              PCK_BOND-2       knownbug
                      not_correct_guid    ${ENDED_OK}      0

*** Key Words ***
guid_invalid_should_fail
    [Arguments]    ${guid}    ${returnStatus}    ${returnCode}
    @{snapshot_info}=    Config a valid application workflow
    Add Custom Variable    @snapshot/guid    ${guid}
    Common testsuites execute and assert    ${returnStatus}    ${returnCode}
    [Teardown]    Run Keyword Unless    '@{snapshot_info}[0]' == '${EMPTY}'    Clean Snapshot    @{snapshot_info}[0]    @{snapshot_info}[1]

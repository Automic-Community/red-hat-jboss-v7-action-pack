*** Settings ***
Documentation     This is action level shared template
...               that will be used in SNAPSHOT_CREATE test suites.

*** Key Words ***
Config a valid application workflow
    Start Server    Standalone
    Deploy test app
    Init Application Workflow    ${ACTION_SNAPSHOT_CREATE}

Test Snapshot Mode
    [Arguments]    ${snapshot_mode}    ${returnStatus}    ${returnCode}
    Config a valid application workflow
    Action Set    &UC4RB_SNAPSHOT_MODE#    ${snapshot_mode}
    Common testsuites execute and assert    ${returnStatus}    ${returnCode}
    ${archive_path}=    Action Get Variable    &UC4RB_ARCHIVE_PATH#
    ${guid}=    Action Get Variable    &UC4RB_OUT_TARGET_GUID#
    [Teardown]    Run Keyword Unless    '${archive_path}' == '${EMPTY}'    Clean Snapshot    ${archive_path}    ${guid}

Test App Filter Exclude
    [Arguments]    ${filter}    ${returnStatus}    ${returnCode}
    Config a valid application workflow
    Add Custom Variable    /jboss/application_name    ${_JBOSS_APP}
    Action Set    ${PRT_CREATE_SNAPSHOT_APP_FILTER_EXCLUDE}    ${filter}
    Common testsuites execute and assert    ${returnStatus}    ${returnCode}
    ${archive_path}=    Action Get Variable    &UC4RB_ARCHIVE_PATH#
    ${guid}=    Action Get Variable    &UC4RB_OUT_TARGET_GUID#
    [Teardown]    Run Keyword Unless    '${archive_path}' == '${EMPTY}'    Clean Snapshot    ${archive_path}    ${guid}

Test Server Filter Exclude
    [Arguments]    ${filter}    ${returnStatus}    ${returnCode}
    Config a valid application workflow
    Action Set    &UC4RB_SERVER_FILTER_EXCLUDE#    ${filter}
    Common testsuites execute and assert    ${returnStatus}    ${returnCode}
    ${archive_path}=    Action Get Variable    &UC4RB_ARCHIVE_PATH#
    ${guid}=    Action Get Variable    &UC4RB_OUT_TARGET_GUID#
    [Teardown]    Run Keyword Unless    '${archive_path}' == '${EMPTY}'    Clean Snapshot    ${archive_path}    ${guid}

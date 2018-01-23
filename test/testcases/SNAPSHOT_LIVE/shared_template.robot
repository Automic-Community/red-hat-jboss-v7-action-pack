*** Settings ***
Documentation     This is action level shared template
...               that will be used in SNAPSHOT_CREATE test suites.

*** Key Words ***
Config a valid application workflow
    Start Server    Standalone
    Deploy test app
    Init Application Workflow    ${ACTION_BOND_HOOK_SNAPSHOT_LIVE}
    Add Custom Variable    @snapshot/UC4RB_SNAPSHOT_MODE    BOTH
    Add Custom Variable    @snapshot/UC4RB_JBOSS_HOME    ${_JBOSS_HOME}
    Add Custom Variable    @snapshot/UC4RB_JBOSS_HOST    ${_JBOSS_HOST}
    Add Custom Variable    @snapshot/UC4RB_JBOSS_PORT    ${_JBOSS_PORT}
    Add Custom Variable    @snapshot/UC4RB_JBOSS_USERNAME    ${_JBOSS_USERNAME}
    Add Custom Variable    @snapshot/UC4RB_JBOSS_PASSWORD    ${_JBOSS_PASSWORD}
    Add Custom Variable    @snapshot/UC4RB_JBOSS_APP_NAME    ${_JBOSS_APP}
    Add Custom Variable    @snapshot/UC4RB_JBOSS_HOST_CONTROLLER    ${EMPTY}
    Add Custom Variable    @snapshot/UC4RB_JBOSS_SERVER_NAME    ${EMPTY}

Test Snapshot Mode
    [Arguments]    ${mode}    ${returnStatus}    ${returnCode}
    Config a valid application workflow
    Add Custom Variable    @snapshot/UC4RB_SNAPSHOT_MODE    ${mode}
    Common testsuites execute and assert    ${returnStatus}    ${returnCode}
    ${archive_path}=    Action Get Variable    &UC4RB_ARCHIVE_PATH#
    ${guid}=    Action Get Variable    &UC4RB_OUT_TARGET_GUID#
    [Teardown]    Run Keyword Unless    '${archive_path}' == '${EMPTY}'    Clean Snapshot    ${archive_path}    ${guid}

Test App Filter Exclude
    [Arguments]    ${filter}    ${returnStatus}    ${returnCode}
    Config a valid application workflow
    Add Custom Variable    @snapshot/UC4RB_APP_FILTER_EXCLUDE    ${filter}
    Common testsuites execute and assert    ${returnStatus}    ${returnCode}
    ${archive_path}=    Action Get Variable    &UC4RB_ARCHIVE_PATH#
    ${guid}=    Action Get Variable    &UC4RB_OUT_TARGET_GUID#
    [Teardown]    Run Keyword Unless    '${archive_path}' == '${EMPTY}'    Clean Snapshot    ${archive_path}    ${guid}

Test Server Filter Exclude
    [Arguments]    ${filter}    ${returnStatus}    ${returnCode}
    Config a valid application workflow
    Add Custom Variable    @snapshot/UC4RB_SERVER_FILTER_EXCLUDE    ${filter}
    Common testsuites execute and assert    ${returnStatus}    ${returnCode}
    ${archive_path}=    Action Get Variable    &UC4RB_ARCHIVE_PATH#
    ${guid}=    Action Get Variable    &UC4RB_OUT_TARGET_GUID#
    [Teardown]    Run Keyword Unless    '${archive_path}' == '${EMPTY}'    Clean Snapshot    ${archive_path}    ${guid}

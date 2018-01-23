*** Settings ***
Documentation     This is action level shared template
...               that will be used in SNAPSHOT_CREATE test suites.

*** Key Words ***
Config a valid application workflow
    Start Server    Standalone
    Deploy test app
    @{snapshot_info}=    Create Test Snapshot
    Init Application Workflow Standalone    ${ACTION_BOND_HOOK_SNAPSHOT_COMPARE}
    Add Custom Variable    @snapshot/variables/archivepath    @{snapshot_info}[0]
    Add Custom Variable    @snapshot/guid    @{snapshot_info}[1]
    Add Custom Variable    @snapshot/variables/excludefilter    ${EMPTY}
    Add Custom Variable    @snapshot/variables/home    ${_JBOSS_HOME}
    Add Custom Variable    @snapshot/variables/host    ${_JBOSS_HOST}
    Add Custom Variable    @snapshot/variables/port    ${_JBOSS_PORT}
    Add Custom Variable    @snapshot/variables/username    ${_JBOSS_USERNAME}
    Add Custom Variable    @snapshot/variables/password    ${_JBOSS_PASSWORD}
    [Return]    @{snapshot_info}

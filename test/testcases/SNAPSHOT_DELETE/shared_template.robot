*** Settings ***
Documentation     This is action level shared template
...               that will be used in SNAPSHOT_DELETE test suites.

*** Key Words ***
Config a valid application workflow
    Start Server    Standalone
    Deploy test app
    @{snapshot_info}=    Create Test Snapshot
    Init Application Workflow Standalone    ${ACTION_BOND_HOOK_SNAPSHOT_DELETE}
    Add Custom Variable    @snapshot/variables/archivepath    @{snapshot_info}[0]
    Add Custom Variable    @snapshot/guid    @{snapshot_info}[1]
    [Return]    @{snapshot_info}

*** Settings ***
Documentation     NOTE:
...               add note here
Suite Setup       Suite Setup
Force Tags        DELETE_MQT    win
Test Template     Delete Mqt Template
Resource          ../../resources/keywords.txt

*** Test Cases ***    MODE          PROFILE       NAME                   TYPE     FAIL IF MISSING    RETURN CODE    RETURN STATUS
Delete MQT Normal     [Setup]       Create Mqt    mqt_to_delete_${OS}    Queue
                      Standalone    mqtprofile    mqt_to_delete_${OS}    Queue    NO                 0              ${ENDED_OK}

*** Keywords ***
Delete Mqt Template
    [Arguments]    ${mode}    ${profile}    ${name}    ${type}    ${fail_if_missing}    ${returnCode}
    ...    ${returnStatus}
    Init Action    ${ACTION_DELETE_MQT}
    Action Set    ${PRT_DELETE_MQT_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_DELETE_MQT_JBOSS_PROFILE}    ${profile}
    Action Set    ${PRT_DELETE_MQT_JBOSS_NAME}    ${name}
    Action Set    ${PRT_DELETE_MQT_JBOSS_TYPE}    ${type}
    Action Set    ${PRT_DELETE_MQT_JBOSS_FAIL_MISSING}    ${fail_if_missing}
    JBOSS Common Setting With User
    Action Execute
    Action Report Should Be Found
    Action Return Code Should Be    ${returnCode}
    Action Return Status Should Be    ${returnStatus}

Create Mqt
    [Arguments]    ${name}    ${type}
    Init Action    ${ACTION_CREATE_MQT}
    Action Set    ${PRT_CREATE_MQT_JBOSS_OPERATING_MODE}    Standalone
    Action Set    ${PRT_CREATE_MQT_JBOSS_PROFILE}    mqtprofile
    Action Set    ${PRT_CREATE_MQT_JBOSS_NAME}    ${name}
    Action Set    ${PRT_CREATE_MQT_JBOSS_JNDI_NAME}    java:/jms/queue/${name}
    Action Set    ${PRT_CREATE_MQT_JBOSS_MAX_ATTEMPTS}    ${EMPTY}
    Action Set    ${PRT_CREATE_MQT_JBOSS_RE_DELAY}    ${EMPTY}
    Action Set    ${PRT_CREATE_MQT_JBOSS_DLA}    jms.queue.mqt
    Action Set    ${PRT_CREATE_MQT_JBOSS_EXPIRY}    ${EMPTY}
    Action Set    ${PRT_CREATE_MQT_JBOSS_SIZE_MAX}    ${EMPTY}
    Action Set    ${PRT_CREATE_MQT_JBOSS_SIZE_PAGE}    ${EMPTY}
    Action Set    ${PRT_CREATE_MQT_JBOSS_SIZE_PAGE_CACHE}    ${EMPTY}
    Action Set    ${PRT_CREATE_MQT_JBOSS_ADDR_FULL_POLICY}    PAGE
    Action Set    ${PRT_CREATE_MQT_JBOSS_REDIS_DELAY}    ${EMPTY}
    Action Set    ${PRT_CREATE_MQT_JBOSS_TYPE}    ${type}
    Action Set    ${PRT_CREATE_MQT_JBOSS_FAIL_EXISTING}    NO
    Action Set    ${PRT_CREATE_MQT_JBOSS_UPDATE_EXISTING}    NO
    JBOSS Common Setting With User
    Action Execute

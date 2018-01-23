*** Settings ***
Documentation     NOTE:
...               add note here
Suite Setup       Suite Setup
Force Tags        CREATE_MQT    win    unix
Test Template     Create Mqt Template
Resource          ../../resources/keywords.txt

*** Test Cases ***    MODE          PROFILE       NAME                         JNDI NAME                                    DLA              POLICY    TYPE     FAIL EXISTING    UPDATE EXISTING    RETURN CODE    RETURN STATUS
Create MQT Page       Standalone    mqtprofile    mqtname_Page_Queue_${OS}     java:/jms/queue/mqtname_Page_Queue_${OS}     jms.queue.mqt    PAGE      Queue    NO               NO                 0              ${ENDED_OK}

Create MQT BLOCK      Standalone    mqtprofile    mqtname_Block_Queue_${OS}    java:/jms/queue/mqtname_Block_Queue_${OS}    jms.queue.mqt    BLOCK     Queue    NO               NO                 0              ${ENDED_OK}

Create MQT DROP       Standalone    mqtprofile    mqtname_Drop_Queue_${OS}     java:/jms/queue/mqtname_Drop_Queue_${OS}     jms.queue.mqt    DROP      Queue    NO               NO                 0              ${ENDED_OK}

Create MQT Page Topic
                      Standalone    mqtprofile    mqtname_Page_Topic_${OS}     java:/jms/queue/mqtname_Page_Topic_${OS}     jms.queue.mqt    PAGE      Topic    NO               NO                 0              ${ENDED_OK}

Create MQT BLOCK Topic
                      Standalone    mqtprofile    mqtname_Block_topic_${OS}    java:/jms/queue/mqtname_Block_topic_${OS}    jms.queue.mqt    BLOCK     Topic    NO               NO                 0              ${ENDED_OK}

Create MQT DROP Topic
                      Standalone    mqtprofile    mqtname_Drop_Topic_${OS}     java:/jms/queue/mqtname_Drop_Topic_${OS}     jms.queue.mqt    DROP      Topic    NO               NO                 0              ${ENDED_OK}

*** Keywords ***
Create Mqt Template
    [Arguments]    ${mode}    ${profile}    ${name}    ${jndi_name}    ${dla}    ${policy}
    ...    ${type}    ${fail_existing}    ${update_existing}    ${returnCode}    ${returnStatus}
    Init Action    ${ACTION_DELETE_MQT}
    Action Set    ${PRT_DELETE_MQT_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_DELETE_MQT_JBOSS_PROFILE}    ${profile}
    Action Set    ${PRT_DELETE_MQT_JBOSS_NAME}    ${name}
    Action Set    ${PRT_DELETE_MQT_JBOSS_TYPE}    ${type}
    Action Set    ${PRT_DELETE_MQT_JBOSS_FAIL_MISSING}    NO
    JBOSS Common Setting With User
    Action Execute
    Init Action    ${ACTION_CREATE_MQT}
    Action Set    ${PRT_CREATE_MQT_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_CREATE_MQT_JBOSS_PROFILE}    ${profile}
    Action Set    ${PRT_CREATE_MQT_JBOSS_NAME}    ${name}
    Action Set    ${PRT_CREATE_MQT_JBOSS_JNDI_NAME}    ${jndi_name}
    Action Set    ${PRT_CREATE_MQT_JBOSS_MAX_ATTEMPTS}    ${EMPTY}
    Action Set    ${PRT_CREATE_MQT_JBOSS_RE_DELAY}    ${EMPTY}
    Action Set    ${PRT_CREATE_MQT_JBOSS_DLA}    ${dla}
    Action Set    ${PRT_CREATE_MQT_JBOSS_EXPIRY}    ${EMPTY}
    Action Set    ${PRT_CREATE_MQT_JBOSS_SIZE_MAX}    ${EMPTY}
    Action Set    ${PRT_CREATE_MQT_JBOSS_SIZE_PAGE}    ${EMPTY}
    Action Set    ${PRT_CREATE_MQT_JBOSS_SIZE_PAGE_CACHE}    ${EMPTY}
    Action Set    ${PRT_CREATE_MQT_JBOSS_ADDR_FULL_POLICY}    ${policy}
    Action Set    ${PRT_CREATE_MQT_JBOSS_REDIS_DELAY}    ${EMPTY}
    Action Set    ${PRT_CREATE_MQT_JBOSS_TYPE}    ${type}
    Action Set    ${PRT_CREATE_MQT_JBOSS_FAIL_EXISTING}    ${fail_existing}
    Action Set    ${PRT_CREATE_MQT_JBOSS_UPDATE_EXISTING}    ${update_existing}
    JBOSS Common Setting With User
    Action Execute
    Action Report Should Be Found
    Action Return Code Should Be    ${returnCode}
    Action Return Status Should Be    ${returnStatus}
    Init Action    ${ACTION_DELETE_MQT}
    Action Set    ${PRT_DELETE_MQT_JBOSS_OPERATING_MODE}    ${mode}
    Action Set    ${PRT_DELETE_MQT_JBOSS_PROFILE}    ${profile}
    Action Set    ${PRT_DELETE_MQT_JBOSS_NAME}    ${name}
    Action Set    ${PRT_DELETE_MQT_JBOSS_TYPE}    ${type}
    Action Set    ${PRT_DELETE_MQT_JBOSS_FAIL_MISSING}    NO
    JBOSS Common Setting With User
    Action Execute

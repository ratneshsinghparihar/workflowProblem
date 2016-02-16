
@Entity
@Table(name="tbl_work_flow_task",indexes = {@Index(name="IDX_createdby_pushnotificationtype", columnList = "createdby_user_id,push_notification_type")},schema = CommonConstants.common_SCHEMA_NAME)
public class WorkFlowTask extends TrackableEntity {

    @Lob
    @Column(name="data", length=8000)
    private String data;

    @Lob
    @Column(name="message", length=8000)
    private String message;


    @Column(name="entity_type")
    private String entityType;


    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    @Column(name="entity_id")
    private Integer entityId;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public WorkFlowTaskTypeEnum getTaskType() {
        return taskType;
    }

    public void setTaskType(WorkFlowTaskTypeEnum taskType) {
        this.taskType = taskType;
    }

    public PushNotificationTypeEnum getPushNotificationType() {
        return pushNotificationType;
    }

    public void setPushNotificationType(PushNotificationTypeEnum pushNotificationType) {
        this.pushNotificationType = pushNotificationType;
    }

    //it can be Bmr Save, Mpr update, Process update and so on
    @Enumerated(value=EnumType.STRING)
    @Column(name="task_type")
    private WorkFlowTaskTypeEnum taskType;

    //it can be Task, Notification or Alert
    @Enumerated(value=EnumType.STRING)
    @Column(name="push_notification_type")
    private  PushNotificationTypeEnum pushNotificationType;

    public String getUniqueKeyForWorkFlowTask()
    {
        String key="";
        key=this.getEntityType()+ this.getEntityId()+ this.getStatus()+
                this.getCreatedBy().getUserId()+ this.getTaskType()+
                this.getPushNotificationType();
        return key;
    }
}

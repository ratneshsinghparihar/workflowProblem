
@Entity
@Table(name="tbl_datasource",schema = CommonConstants.building_blocks)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
/*@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@DiscriminatorValue(value = "ALL")
@DiscriminatorOptions(force=true)*/
public class DataSource extends DataSourceBase {

    @OneToMany(cascade = CascadeType.ALL, mappedBy="dataSource")
    private List<DataSourceParam> dataSourceParams;



    @Column(name = "original_datasource_id")
    @JsonIgnore
    private Integer originalDataSourceId;


    @Column(name = "datasource_version")
    private Integer dataSourceVersion=1;

    public Integer getDataSourceVersion() {
        return dataSourceVersion;
    }

    public void setDataSourceVersion(Integer dataSourceVersion) {
        this.dataSourceVersion = dataSourceVersion;
    }

    public Integer getCurAction() {
        return curAction;
    }

    public void setCurAction(Integer curAction) {
        this.curAction = curAction;
    }

    @Column(name = "cur_action")
    private Integer curAction;

    public Integer getOriginalDataSourceId() {
        return originalDataSourceId;
    }

    public void setOriginalDataSourceId(Integer originalDataSourceId) {
        this.originalDataSourceId = originalDataSourceId;
    }

    @JsonIgnore
    public List<DataSourceParam> getDataSourceParams() {
        return dataSourceParams;
    }

    public void setDataSourceParams(List<DataSourceParam> dataSourceParams) {
        this.dataSourceParams = dataSourceParams;
    }



    /*public ebmr.webservice.models.mpr.buildingblocks.Table getParent(){
            return table;
        }*/
    public List<DataSourceParam> getChildren(){
        return dataSourceParams;
    }

    @Override
    public String getPushMessage(WorkFlowTaskTypeEnum workFlowTaskTypeEnum,PushNotificationTypeEnum pushNotificationTypeEnum) {
        String message = "";
        if(pushNotificationTypeEnum == PushNotificationTypeEnum.task && workFlowTaskTypeEnum == WorkFlowTaskTypeEnum.entity_approval_required){
            message = "Approve the dataSource: "+this.getName();
        }else if(pushNotificationTypeEnum == PushNotificationTypeEnum.notification && workFlowTaskTypeEnum == WorkFlowTaskTypeEnum.entity_approval_required){
            message = "Approval Request Has been Sent to Mpr reviewer for dataSource "+ this.getName();
        }else if(pushNotificationTypeEnum == PushNotificationTypeEnum.task && workFlowTaskTypeEnum == WorkFlowTaskTypeEnum.entity_approved){
            message = "The dataSource: "+ this.getName()+" is Approved";
        }else if(pushNotificationTypeEnum == PushNotificationTypeEnum.notification && workFlowTaskTypeEnum == WorkFlowTaskTypeEnum.entity_approved){
            message = "The dataSource: "+ this.getName()+" is Approved";
        }else if(pushNotificationTypeEnum == PushNotificationTypeEnum.task && workFlowTaskTypeEnum == WorkFlowTaskTypeEnum.entity_unApproved){
            message = "The dataSource: "+ this.getName()+" is UnApproved";
        }else if(pushNotificationTypeEnum == PushNotificationTypeEnum.notification && workFlowTaskTypeEnum == WorkFlowTaskTypeEnum.entity_unApproved){
            message = "The dataSource: "+ this.getName()+" is UnApproved";
        }
        return message;
    }

    @Override
    public HashMap<String, String> getNotificationData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", this.getId().toString());
        map.put("type", "dataSource");
        return map;
    }
}

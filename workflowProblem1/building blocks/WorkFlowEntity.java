

/**
 * Created by ratnesh on 12/22/2014.
 */
@MappedSuperclass
public class WorkFlowEntity extends SecurableEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkFlowEntity.class);

    /*@Transient
    private HashMap<String, Object> treeViewMap;*/

    @Enumerated(value = EnumType.ORDINAL)
    @Column(name = "status")
    private StatusEnum status;

    public StatusEnum getPreviuosStatus() {
        if (previuosStatus == null) return status;
        return previuosStatus;
    }

    public void setPreviuosStatus(StatusEnum previuosStatus) {
        this.previuosStatus = previuosStatus;
    }

    @Column(name = "approval_status_role_wise" )
    //@JsonIgnore
    private String approvalStatusRoleWise;


    public String getApprovalStatusRoleWise() {
        return approvalStatusRoleWise;
    }

    public void setApprovalStatusRoleWise(String approvalStatusRoleWise) {
        this.approvalStatusRoleWise = approvalStatusRoleWise;
    }

    @JsonIgnore
    public void resetApproveStatusRoleWiseCascaded(){
        this.setApprovalStatusRoleWise(null);
        if(this.getChildren()==null) return;
        this.getChildren().forEach(x -> ((WorkFlowEntity)x).resetApproveStatusRoleWiseCascaded());
    }

    @JsonIgnore
    public Boolean getApprovedByRole(List<Integer> roleids) {
        if(this.getApproved()!=null)
            return  this.getApproved();

        if(approvalStatusRoleWise==null)
            return false;


        HashMap<String,Integer> statusList=AccessControlMaskUtil.getHashMapFromUserAccess(approvalStatusRoleWise);

        java.util.Optional<java.util.Map.Entry<String,Integer>> workAbleEntity=
                statusList.entrySet().stream().filter(x->x.getValue()
                        >= ApprovalStatusEnum.APPROVED.getValue()).findFirst();

        if(workAbleEntity!=null && workAbleEntity.isPresent()) {
            Integer role=Integer.parseInt(workAbleEntity.get().getKey());
            if( roleids.contains(role)){
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public Boolean canApprovedByRole( List<Integer> roleids) {

            if(approvalStatusRoleWise==null)
            return false;

        LinkedHashMap<String,Integer> statusList=AccessControlMaskUtil.getHashMapFromUserAccess(approvalStatusRoleWise);

        java.util.Optional<java.util.Map.Entry<String,Integer>> workAbleEntity=
                statusList.entrySet().stream().filter(x->x.getValue()
                        ==ApprovalStatusEnum.REQUIRED.getValue()).findFirst();

        if(workAbleEntity!=null && workAbleEntity.isPresent()) {
            Integer role=Integer.parseInt(workAbleEntity.get().getKey());
            if( roleids.contains(role)){
                return true;
            }
        }

        return false;
    }


    @Column(name = "is_mark_for_stop_after_completion")
    @JsonIgnore
    private Boolean markForStopAfterCompletion;

    public Boolean getMarkForStopAfterCompletion() {
        return markForStopAfterCompletion;
    }

    public void setMarkForStopAfterCompletion(Boolean markForStopAfterCompletion) {
        this.markForStopAfterCompletion = markForStopAfterCompletion;
    }

    @JsonIgnore
    public boolean checkIfApprovedByAllRoles()
    {
        /*if(approvalStatusRoleWise==null)
            return true;

        HashMap<String,Integer> statusList=AccessControlMaskUtil.getHashMapFromUserAccess(approvalStatusRoleWise);
        statusList.values().removeAll(Collections.singleton(null));
        statusList.values().removeAll(Collections.singleton(0));
        long totRoles=statusList.entrySet().stream().count();

        if(statusList.entrySet().stream().filter(x->x.getValue()==ApprovalStatusEnum.APPROVED.getValue()).count()==totRoles)
            return true;

        return false;*/
        return checkApprovalStatus() == ApprovalStatusEnum.APPROVED;

    }

    @JsonIgnore
    public ApprovalStatusEnum checkApprovalStatus(){
        if(approvalStatusRoleWise==null) {
            return ApprovalStatusEnum.NOT_REQUIRED;
        }

        HashMap<String,Integer> statusList=AccessControlMaskUtil.getHashMapFromUserAccess(approvalStatusRoleWise);
        statusList.values().removeAll(Collections.singleton(null));
        statusList.values().removeAll(Collections.singleton(0));
        long totRoles=statusList.entrySet().stream().count();

        if(totRoles==0) {
            return ApprovalStatusEnum.NOT_REQUIRED;
        }else if(statusList.entrySet().stream().filter(x->x.getValue()==ApprovalStatusEnum.UNAPPROVED.getValue()).count()>0) {
            return ApprovalStatusEnum.UNAPPROVED;
        }else if(statusList.entrySet().stream().filter(x->x.getValue()==ApprovalStatusEnum.REQUIRED.getValue()).count()>0) {
            return ApprovalStatusEnum.REQUIRED;
        }else if(statusList.entrySet().stream().filter(x->x.getValue()==ApprovalStatusEnum.APPROVED.getValue()).count()==totRoles) {
            return ApprovalStatusEnum.APPROVED;
        }
        return ApprovalStatusEnum.NOT_REQUIRED;
    }

    public void reStoreApprovalStatusRoleWise()
    {
        if(approvalStatusRoleWise==null || approvalStatusRoleWise=="") return;
        HashMap<String,Integer> statusList=AccessControlMaskUtil.getHashMapFromUserAccess(approvalStatusRoleWise);
        if(statusList.isEmpty()) return;
        for(String key: statusList.keySet()){
            statusList.replace(key, 1);
        }
        approvalStatusRoleWise=AccessControlMaskUtil.getStringFromHashMap(statusList);
        this.setApprovalStatusRoleWise(approvalStatusRoleWise);

    }

    //@Autowired
    //BmrService bmrService ;

    @JsonIgnore
    public Boolean setApprovedByRole(List<Integer> roleids,boolean approved) {
        if(approvalStatusRoleWise==null)
            return false;
        HashMap<String,Integer> statusList=AccessControlMaskUtil.getHashMapFromUserAccess(approvalStatusRoleWise);

        java.util.Optional<java.util.Map.Entry<String,Integer>> workAbleEntity= statusList.entrySet().stream().filter(x->x.getValue()==ApprovalStatusEnum.REQUIRED.getValue()).findFirst();

        if(workAbleEntity!=null && workAbleEntity.isPresent()) {
            Integer role=Integer.parseInt(workAbleEntity.get().getKey());
            if( roleids.contains(role)){
                if(approved)
                    workAbleEntity.get().setValue(ApprovalStatusEnum.APPROVED.getValue());
                else
                    workAbleEntity.get().setValue(ApprovalStatusEnum.UNAPPROVED.getValue());
                approvalStatusRoleWise=AccessControlMaskUtil.getStringFromHashMap(statusList);
                if(checkIfApprovedByAllRoles()) {
                    this.setApprovedLocally(true);
                    // Strat the MPR report workflow. Final approval takes place.

                }
                if(!checkIfApprovedByAllRoles() && approved){
                    this.setApprovedLocally(null);
                }
               if(approved==false)
                {
                    this.setApprovedLocally(false);
                }
                return true;
            }
        }
        return false;

    }

    @JsonIgnore
    private StatusEnum previuosStatus;

    public StatusEnum getStatus() {
        return status;
    }


    @JsonIgnore
    public Map<String, Object> getTreeViewMap(Map<String, Object> parentMap){
        Map<String, Object> treeViewMap = null;
        if(this instanceof TreeView && ((TreeView)this).isInTreeView()) {
            treeViewMap = new LinkedHashMap<>();
            treeViewMap.put("id", this.getId());
            treeViewMap.put("name", ((TreeView) this).getTreeNodeName());
            treeViewMap.put("status", this.getStatus());
            if(!this.isValid()) {
                treeViewMap.put("error", this.getValidationError());
            }
            if(this instanceof BmrTableColumn) {
                treeViewMap.put("error", ((BmrTableColumn) this).isInvalidTreeNode());
            }
            if((this instanceof BmrProcess && ((BmrProcess)this).getCurrentMprTest()!=null)) {
                treeViewMap.put("running", ((BmrProcess)this).getCurrentMprTest().isRunning());
            }else if(this instanceof BmrTest){
                treeViewMap.put("running", ((BmrTest)this).isRunning());
            }
            Boolean approved = this.getApproved();
            if (approved == null || !approved) {
                approved = parentMap != null && parentMap.get("approved") != null && (Boolean) parentMap.get("approved");
            }
            treeViewMap.put("approved", approved);
            treeViewMap.put("type", this.getClass().getSimpleName());
            /*treeViewMap.put("access", (this.getAccessMask() & 1)==1);*/
            treeViewMap.put("access", AccessControlMaskUtil.HasReadPermission(this.getGlobalAccessMask()!=null?this.getGlobalAccessMask():0) ||
                    AccessControlMaskUtil.HasReadPermission(this.getUsersAccessMask(), Utils.getCurrentUser().getId()) ||
                    AccessControlMaskUtil.HasReadPermission(this.getRolesAccessMask(), Utils.getCurrentUser().getRoleGroupIds()));
            /*treeViewMap.put("children", new ArrayList<Map<String, Object>>());*/

            if (parentMap != null) {
                List<Map<String, Object>> childrenOfParent = (List<Map<String, Object>>) parentMap.get("children");
                if(childrenOfParent==null){
                    childrenOfParent = new ArrayList<>();
                    parentMap.put("children", childrenOfParent);
                }
                //SpecialCase: Find existing for BmrTableColumn as we don't have BmrTableRow entity
                Optional<Map<String, Object>> existingTreeViewMap = Optional.empty();
                if(treeViewMap.get("type").equals("BmrTableColumn")){
                    final Map<String, Object> finalTreeViewMap = treeViewMap;
                    existingTreeViewMap =childrenOfParent.stream().filter(x -> x.get("name").equals(finalTreeViewMap.get("name"))).findFirst();
                }
                if(existingTreeViewMap.isPresent()){
                    treeViewMap = existingTreeViewMap.get();
                }else {
                    childrenOfParent.add(treeViewMap);
                }
            }
        }
        if(this.getSortedChildren()!=null && !this.getSortedChildren().isEmpty()) {
            for (WorkFlowEntity workFlowEntity : this.getSortedChildren()) {
                if (treeViewMap != null) {
                    workFlowEntity.getTreeViewMap(treeViewMap);
                } else {
                    workFlowEntity.getTreeViewMap(parentMap);
                }
            }
        }
        //set approval based on children status.
        if(treeViewMap!=null) {
            Boolean approved =  (Boolean)treeViewMap.get("approved");
            if (approved == null || !approved) {
                List<Map<String, Object>> children = (List<Map<String, Object>>) treeViewMap.get("children");
                if (children != null && !children.isEmpty()) {
                    long unApprovedChildrenCount = children.stream().filter(x -> x.get("approved")== null || x.get("approved").equals(false)).count();
                    treeViewMap.put("approved", unApprovedChildrenCount==0);
                }
            }
        }
        return treeViewMap;
    }

    //    @Transient
//    private final Lock writeLock= new ReentrantLock();
    //@Transactional(isolation = Isolation.)
    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    @JsonIgnore
    public void setStatuswitoutCascade(StatusEnum status) {
        if (getStatus() == status && this.previuosStatus == status ) return;
        this.previuosStatus = status;
        this.status = status;
    }

    //TODO : need to remove later



    @JsonIgnore
    public <C extends WorkFlowEntity> List<C> getSortedChildren() {

        if (this.getChildren() == null) return null;
        this.getChildren().removeAll(Collections.singleton(null));
        if (this.getChildren().isEmpty()) return null;
        Collections.sort(this.getChildren(), (p1, p2) -> (p1.getSeq()!=null && p2.getSeq()!=null) ? p1.getSeq().compareTo(p2.getSeq()) : 0);
        return this.getChildren();
    }

    @JsonIgnore
    @Transient
    public <C extends WorkFlowEntity> List<C> getNext() {
        List<C> workFlowEntities = new ArrayList<>();

        WorkFlowEntity parent = getParent();
        if (parent == null) return workFlowEntities;

        List<C> sortedCollection = parent.getSortedChildren();
        if (sortedCollection == null)
            return workFlowEntities;

        java.util.Optional<C> nextSibiling = sortedCollection.stream().filter(x -> x.getCurSeq() == getCurSeq() + 1).findFirst();
        if (nextSibiling.isPresent()){
            workFlowEntities.add(nextSibiling.get());
        }

        if (workFlowEntities.isEmpty())
            return parent.getNext();

        return workFlowEntities;
    }


    @JsonIgnore
    @Transient
    public <C extends WorkFlowEntity> List<C> getPreviuos() {
        List<C> workFlowEntities = new ArrayList<>();

        WorkFlowEntity parent = getParent();
        if (parent == null) return workFlowEntities;

        List<C> sortedCollection = parent.getSortedChildren();
        if (sortedCollection == null)
            return workFlowEntities;

        java.util.Optional<C> prevSibiling = sortedCollection.stream().filter(x -> x.getCurSeq() == getCurSeq() - 1).findFirst();
        if (prevSibiling.isPresent()){
            workFlowEntities.add(prevSibiling.get());
        }

        if (workFlowEntities.isEmpty())
            return parent.getPreviuos();

        return workFlowEntities;
    }

    @JsonIgnore
    public Boolean restore()
    {
        return true;
    }

    @JsonIgnore
    public int getEntityPropertiesMask() {

        return 0;
    }

    @JsonIgnore
    public Boolean isCritical() {

        return false;
    }

    @Transient
    private ApproverTypeEnum curUserApproverType;

    @Transient
    private ApprovalStatusEnum approvalStatus;

    @JsonProperty
    public ApproverTypeEnum getCurUserApproverType() {
        return curUserApproverType;
    }

    @JsonIgnore
    public void setCurUserApproverType(ApproverTypeEnum curUserApproverType) {
        this.curUserApproverType = curUserApproverType;
    }

    @JsonProperty
    public ApprovalStatusEnum getApprovalStatus() {
        return approvalStatus;
    }

    @JsonIgnore
    public void setApprovalStatus(ApprovalStatusEnum approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
}

@Entity
@Table(name = "tbl_process",schema= CommonConstants.mpr_SCHEMA_NAME)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MprProcess extends ProcessBase implements Cloneable {

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name="process_type_id")
    private MasterDataValue processType;

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "mprProcess")
    private MprTest mprTest;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name = "checklist_id",nullable = true)
    private CheckList checkList;

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "mprProcess1")
    private MprTable mprTable;

    @JsonIgnore
    public List<MprTest> getMprTests() {
        List<MprTest> mprTests = new ArrayList<>();
        if(mprTest!=null) mprTests.add(mprTest);
        return mprTests;
    }

    public void setMprTests(List<MprTest> mprTests){
        if(mprTests==null || mprTests.isEmpty()) return;
        this.mprTest = mprTests.get(0);
    }

    @Mapping("mprTables")
    @JsonIgnore
    public List<MprTable> getMprTables() {
        List<MprTable> mprTables = new ArrayList<>();
        if(mprTable!=null) mprTables.add(mprTable);
        return mprTables;
    }

    public void setMprTables(List<MprTable> mprTables) {
        if(mprTables==null || mprTables.isEmpty()) return;
        this.mprTable = mprTables.get(0);
    }

    @Enumerated(value = EnumType.STRING)
    @Column(name = "checklist_type")
    private CheckListItemTypeEnum checklistType;

   /* @ManyToOne//(cascade={CascadeType.ALL})
    @JoinColumn(name="parent_process_id")
    private MprProcess parentProcess;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="parentProcess")
    private List<MprProcess> childProcesses;*/

    @ManyToMany(cascade={CascadeType.ALL})
    @JoinTable(name="tbl_process_flow",
            schema = CommonConstants.mpr_SCHEMA_NAME,
            joinColumns={@JoinColumn(name="process_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="next_process_id", referencedColumnName="id")})
    private List<MprProcess> nextProcesses = new ArrayList<>();

    @ManyToMany(mappedBy="nextProcesses", cascade={CascadeType.ALL})
    private List<MprProcess> prevProcesses = new ArrayList<>();

    @ManyToOne//(cascade={CascadeType.ALL})
    @JoinColumn(name="mpr_stage_id")
    private MprStage mprStage;

    @NotAudited
    public MasterDataValue getEmbeddedProcessType() {
        return processType;
    }
    public MasterDataValue getProcessType() {
        return processType;
    }

    public void setProcessType(MasterDataValue processType) {
        this.processType = processType;
    }

    @NotAudited
    public MprTest getEmbeddedMprTest() {
        return mprTest;
    }

    @NotAudited
    public MprTable getEmbeddedMprTable() {
        return mprTable;
    }

    @Mapping("dozerMprTest")
    @NotAudited
    public MprTest getMprTest() {
        return mprTest;
    }

    public void setMprTest(MprTest mprTest) {
        this.mprTest = mprTest;
    }

    @JsonIgnore
    public MprTest getDozerMprTest() {
        return mprTest;
    }

    public void setDozerMprTest(MprTest mprTest){
    }

    @Mapping("dozerMprTable")
    @NotAudited
    public MprTable getMprTable() {
        return mprTable;
    }

    public void setMprTable(MprTable mprTable) {
        this.mprTable = mprTable;
    }

    @JsonIgnore
    public MprTable getDozerMprTable() {
        return mprTable;
    }

    public void setDozerMprTable(MprTable mprTable) {

    }

    /*@JsonIgnore
    @Transient
    public MprTest getMprTest() {
        if(mprTests==null || mprTests.isEmpty()) return null;
        return mprTests.get(0);
    }

    @JsonIgnore
    @Transient
    public void setMprTest(MprTest mprTest) {
        if(mprTests==null) mprTests = new ArrayList<>();
        if(!mprTests.isEmpty()) mprTests.clear();
        this.mprTests.add(0, mprTest);
    }

    @JsonIgnore
    @Transient
    public MprTable getMprTable() {
        if(mprTables==null || mprTables.isEmpty()) return null;
        return mprTables.get(0);
    }

    @JsonIgnore
    @Transient
    public void setMprTable(MprTable mprTable) {
        if(mprTables==null) mprTables = new ArrayList<>();
        if(!mprTables.isEmpty()) mprTables.clear();
        this.mprTables.add(0, mprTable);
    }*/

    @NotAudited
    public List<MprProcess> getNextProcesses() {
        //if(nextProcesses.isEmpty()) return mprStage.getMprProcesses().stream().filter(x -> x.getCurSeq() == getCurSeq() + 1).collect(Collectors.toList());
        return nextProcesses;
    }

    public void setNextProcesses(List<MprProcess> nextProcesses) {
        this.nextProcesses = nextProcesses;
    }

    @NotAudited
    public List<MprProcess> getPrevProcesses() {
        //if(prevProcesses.isEmpty()) return mprStage.getMprProcesses().stream().filter(x -> x.getCurSeq() == getCurSeq() - 1).collect(Collectors.toList());
        return prevProcesses;
    }

    public void setPrevProcesses(List<MprProcess> prevProcesses) {
        this.prevProcesses = prevProcesses;
    }
//    public void setParentProcess(MprProcess parentProcess) {
//        this.parentProcess = parentProcess;
//    }

    @Transient
    List<Integer> nextProcessIds;
    @NotAudited
    public List<Integer> getNextProcessIds(){
        if((nextProcessIds==null || nextProcessIds.isEmpty()) && nextProcesses!=null && !nextProcesses.isEmpty()) {
            return nextProcesses.stream().filter(x -> x != null).map(MprProcess::getId).collect(Collectors.toList());
        }
        return nextProcessIds;
    }
    public void setNextProcessIds(List<Integer> nextProcessIds) {
        this.nextProcessIds = nextProcessIds;
    }
//    public List<MprProcess> getChildProcesses() {
//        return childProcesses;
//    }
//
//    public void setChildProcesses(List<MprProcess> childProcesses) {
//        this.childProcesses = childProcesses;
//    }
    @Transient
    List<Integer> prevProcessIds;
    @NotAudited
    public List<Integer> getPrevProcessIds() {
        if((prevProcessIds==null || prevProcessIds.isEmpty()) && prevProcesses!=null && !prevProcesses.isEmpty()) {
            return prevProcesses.stream().filter(x -> x != null).map(MprProcess::getId).collect(Collectors.toList());
        }
        return prevProcessIds;
    }
    public void setPrevProcessIds(List<Integer> prevProcessIds) {
        this.prevProcessIds = prevProcessIds;
    }

    public MprStage getMprStage() {
        return mprStage;
    }

    @JsonIgnore
    public boolean isProcessCheckList(){
        return this.getProcessType().getValue().equals(ProcessType.CHECKLIST);
    }

    public CheckListItemTypeEnum getChecklistType() {
        return checklistType;
    }

    public void setChecklistType(CheckListItemTypeEnum checklistType) {
        this.checklistType = checklistType;
    }

    public void setMprStage(MprStage mprStage) {
        this.mprStage = mprStage;
    }

    public CheckList getCheckList() {
        return checkList;
    }

    public void setCheckList(CheckList checkList) {
        if(checkList != null) this.checkList = checkList;
    }

    @NotAudited
    public Integer getEmbeddedCheckList() {
        return (this.checkList != null) ? checkList.getId() : null;
    }

    @JsonIgnore
    public MprStage getParent(){
        return mprStage;
    }

    @JsonIgnore
    public List<TrackableEntity> getChildren(){
        if(mprTable==null && mprTest==null)
            return null;

        List children = new ArrayList<>();
        if(mprTable==null)
            children.add(mprTest);
        if(mprTest==null)
            children.add(mprTable);
        return children;
    }

    @JsonIgnore
    public List<TrackableEntity> getSiblings(){
        List allPChildren = mprStage.getMprProcesses();
        //allPChildren.remove(this);
        return allPChildren;
    }

    @Override
    @JsonIgnore
    public Object clone() {
        try {
            Object clone= super.clone();
            if(mprTest!=null) {
                MprTest newTest=(MprTest) mprTest.clone();
                ((MprProcess) clone).setMprTest(newTest);
                newTest.setMprProcess((MprProcess) clone);
            }
            if(mprTable!=null) {
                MprTable newTable=(MprTable) mprTable.clone();
                ((MprProcess) clone).setMprTable(newTable);
                newTable.setMprProcess((MprProcess) clone);
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}

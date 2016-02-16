
@Entity
@Table(name = "tbl_test",schema= CommonConstants.mpr_SCHEMA_NAME)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Test extends MprTestBase implements Cloneable {

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ManyToOne(cascade=CascadeType.PERSIST)
    @JoinColumn(name = "test_id",nullable = true)
    private Test test;

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "mprTest")
    private List<MprTestParam> mprTestParams;

    @OneToOne//(cascade = CascadeType.ALL)
    @JoinColumn(name="mpr_process_id")
    private MprProcess mprProcess;

    @OneToOne//(cascade = CascadeType.ALL)
    @JoinColumn(name="mpr_table_column_id")
    private MprTableColumn mprTableColumn;

    //START: Interlinking---------
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name="linked_mpr_test_id")
    private MprTest linkedMprTest;
    //STOP: Interlinking---------

    @NotAudited
    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    /*@NotAudited
    public List<MprTestParam> getEmbeddedMprTestParams() {
        return mprTestParams;
    }*/
    @NotAudited
    public List<MprTestParam> getMprTestParams() {
        return mprTestParams;
    }


    @Mapping("mprTestParams")
    @JsonIgnore
    public List<MprTestParam> getMprTestParamsInstanceWise() {
        if(mprTestParams==null) return new ArrayList<>();
        MprProcess parentProcess=this.getMprProcess();
        if(parentProcess==null)
        {
            if(this.getMprTableColumn()!=null)
            {
                parentProcess=this.getMprTableColumn().getMprTable().getMprProcess();
            }
        }
        if(parentProcess!=null && parentProcess.getMprStage()!=null && parentProcess.getMprStage().getMpr()!=null
         && parentProcess.getMprStage().getMpr().isMprToMprConversion()) return mprTestParams;
        List<MprTestParam> instanceparams=new ArrayList<MprTestParam>();
        final int[] seq = {0};
        for(int instanceCount=0;instanceCount<this.getIntancesPerExecution();instanceCount++) {
            final int finalInstanceCount = instanceCount;
            mprTestParams.forEach(x -> {
                int execSeq= finalInstanceCount +1;
                MprTestParam curParam=(MprTestParam)x.clone();
                seq[0]++;
                curParam.setSeq(seq[0]);
                curParam.setExecutionSeq(execSeq);
                instanceparams.add(curParam);
            });
        }
        return instanceparams;
    }

    public void setMprTestParamsInstanceWise(List<MprTestParam> mprTestParams) {

    }

    public void setMprTestParams(List<MprTestParam> mprTestParams) {
        this.mprTestParams = mprTestParams;
    }

    @NotAudited
    public MprProcess getMprProcess() {
        return mprProcess;
    }

    @JsonIgnore
    public MprProcess getProcess() {
        return mprProcess!=null ? mprProcess : mprTableColumn.getMprTable().getMprProcess1();
    }

    public void setMprProcess(MprProcess mprProcess) {
        this.mprProcess = mprProcess;
    }

    @NotAudited
    public MprTableColumn getMprTableColumn() {
        return mprTableColumn;
    }

    public void setMprTableColumn(MprTableColumn mprTableColumn) {
        this.mprTableColumn = mprTableColumn;
    }

    //START: Interlinking---------
    @NotAudited
    public MprTest getEmbeddedLinkedMprTest() {
        return linkedMprTest;
    }

    @NotAudited
    public MprTest getLinkedMprTest() {
        return linkedMprTest;
    }

    public void setLinkedMprTest(MprTest linkedMprTest) {
        this.linkedMprTest = linkedMprTest;
    }
    //STOP: Interlinking---------

    @NotAudited
    public Test getEmbeddedTest() {
        return test;
    }

    @Override
    @NotAudited
    public TrackableEntity getParent(){
        if(mprProcess!=null) return mprProcess;
        if(mprTableColumn!=null) return mprTableColumn;
        return null;
    }

    @Override
    @NotAudited
    public List<MprTestParam> getChildren(){

        return getMprTestParams();
    }

    @Override
    @NotAudited
    public Object clone() {

        try {
            MprTest clone = (MprTest) super.clone();
            if(mprTestParams!=null) {
                List<MprTestParam> params=new ArrayList<>();
                mprTestParams.forEach(x ->params.add((MprTestParam)x.clone()));
                params.forEach(x->x.setMprTest(clone));
                clone.setMprTestParams(params);
            }
            return clone;

        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @JsonIgnore
    public boolean validate(){
        super.validate();
        if(getId()!=null && (getChildren()==null || getChildren().isEmpty())) {
            appendValidationError("mprTestParams", Constraint.SIZE, "test params should not be empty");
        }
        if(!getTest().isAuto()){
            if(getIntancesPerExecution() < 1){
                appendValidationError("intancesPerExecution", Constraint.VALUE, "instances per execution should not be negative or zero");
            }
        }
        if(isFrequencyEnabled() && test.getLinkedTest()==null){
            /*int freqInterval = getFreqInterval();
            int freqPlusMinus = getFreqPlusMinus();
            if(freqInterval == 0){
                appendValidationError("freqInterval", Constraint.EMPTY, "frequency duration should not be empty");
            }else if(freqInterval < 0) {
                appendValidationError("freqInterval", Constraint.VALUE, "frequency duration should not be negative");
            }

            if(freqPlusMinus == 0){
                appendValidationError("freqPlusMinus", Constraint.EMPTY, "frequency tolerance should not be empty");
            }else if(freqPlusMinus < 0) {
                appendValidationError("freqPlusMinus", Constraint.VALUE, "frequency tolerance should not be negative");
            }*/
        }
        return this.isValid();
    }
}

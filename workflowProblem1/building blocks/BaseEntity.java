


/**
 * Created by ratnesh on 12/22/2014.
 */
@MappedSuperclass
@OptimisticLocking(type = OptimisticLockType.VERSION)
public class BaseEntity   {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "origin_id")
    @NotAudited
    private Integer originId;

    public Integer getOriginId() {
        return originId;
    }

    public void setOriginId(Integer originId) {
        this.originId = originId;
    }

    @Column(name = "seq")
    private Integer seq;

    @Version
    @JsonIgnore
    @Column(name = "version", columnDefinition = "int DEFAULT 0")
    private int version;

    public int getVersion() {
        return version;
    }

    @JsonIgnore
    public int getCurSeq() {
        if (getSeq() == null) return 0;
        return getSeq().intValue();
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    @Column(name = "id")
    @Mapping("originId")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @JsonIgnore
    @Transient
    public <P extends BaseEntity> P getParent() {
        return null;
    }

    @JsonIgnore
    @Transient
    public <C extends BaseEntity> List<C> getChildren() {
        return null;
    }

    @JsonIgnore
    @Transient
    public <C extends BaseEntity> List<C> getSiblings(){
        BaseEntity parent = getParent();
        return parent!=null ? parent.getChildren() : null;
    }

    @JsonIgnore
    @Transient
    public void sortChildrenBySequenceCascaded()
    {
        if(getChildren()==null || getChildren().size()==1) return;
        List<BaseEntity> children = getChildren().stream().filter(x -> x!=null && x.getSeq()!=null).collect(Collectors.toList());
        children.sort((p1, p2) -> p1.getSeq().compareTo(p2.getSeq()));
        children.stream().forEach(x->x.sortChildrenBySequenceCascaded());
    }

    @JsonIgnore
    @Transient
    public void resetOriginIdCascaded()
    {
        setOriginId(null);
        if(getChildren()==null) return;
        getChildren().stream().forEach(x -> x.resetOriginIdCascaded());
    }

    @JsonIgnore
    @Transient
    public void resetId(){
        setOriginId(getId());
        setId(null);
    }

    @JsonIgnore
    @Transient
    public void resetIdCascaded(){
        setId(null);
        if(getChildren()==null) return;
        getChildren().stream().forEach(x->x.resetIdCascaded());
    }

    @JsonIgnore
    @Transient
    public void refreshCache(){
        refreshCache(false);
    }

    @JsonIgnore
    @Transient
    public void refreshCache(boolean isDelete){
        if(this.getParent()!=null) {
            List<BaseEntity> siblings = this.getSiblings();
            if(siblings!=null) {
                if(isDelete && !siblings.isEmpty()) siblings.remove(this);
                else if(siblings.isEmpty()){
                    siblings.add(this);
                }else {
                    siblings.remove(this);
                    siblings.add(this);
                }
            }
        }
    }

    @Transient
    @JsonIgnore
    private boolean isUpdatedByService;

    @JsonIgnore
    @Transient
    public boolean getIsUpdatedByService(){
        boolean isUpdatedByService = this.isUpdatedByService;
        BaseEntity entity = this;
        while (entity.getParent() != null){
            isUpdatedByService = isUpdatedByService || entity.getParent().getIsUpdatedByService();
            entity = entity.getParent();
        }
        return isUpdatedByService;
    }

    public void setIsUpdatedByService(boolean isUpdatedByService){
        this.isUpdatedByService = isUpdatedByService;
    }

    @NotAudited
    @Column(name = "valid", columnDefinition = "bit default true")
    private boolean valid = true;

    @NotAudited
    @Column(name = "validation_error", length = 2000)
    private String validationError;

    /*@ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="validation_errors")
    @MapKeyColumn(name="constraint")
    @Column(name="validation_error")*/
    @Transient
    @NotAudited
    @JsonIgnore
    private Map<String, Map<String, Map<String,List<String>>>> validationErrors = new LinkedHashMap<>();

    @Transient
    private boolean validationEnabled = true;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        if (this.valid == valid)
            return;

        this.valid = valid;
    }

    public String getValidationError() {
        return validationError;
    }

    public void setValidationError(String validationError) {
        //if(validationError.length()>1000){}
        this.validationError = validationError;
    }

    @JsonIgnore
    public Map<String, Map<String, Map<String,List<String>>>> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, Map<String, Map<String,List<String>>>> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public void resetValidationErrors() {
        setValidationError(null);
        setValidationErrors(new LinkedHashMap<>());
    }

    public void appendValidationError(String constraint, String error) {
        appendValidationError("global", constraint, error);
    }

    public void appendValidationError(String field, String constraint, String error) {
        error.replace("\\", "");
        String entity = this.getClass().getSimpleName();
        if (!validationErrors.containsKey(entity)) {
            validationErrors.put(entity, new LinkedHashMap<>());
        }
        if (!validationErrors.get(entity).containsKey(field)) {
            validationErrors.get(entity).put(field, new LinkedHashMap<>());
        }
        if(!validationErrors.get(entity).get(field).containsKey(constraint)) {
            validationErrors.get(entity).get(field).put(constraint, new ArrayList<>());
        }

        if(!validationErrors.get(entity).get(field).get(constraint).contains(error)) {
            validationErrors.get(entity).get(field).get(constraint).add(error);
        }

        this.setValidationError(new Gson().toJson(validationErrors.get(entity)));
        this.setValid(false);
    }


    public boolean isValidationEnabled() {
        return validationEnabled;
    }

    public void setValidationEnabled(boolean validationEnabled) {
        this.validationEnabled = validationEnabled;
    }

    public void disableAllValidation() {
        BaseEntity rootEntity = this;
        while(rootEntity.getParent()!=null){
            rootEntity = rootEntity.getParent();
        }
        rootEntity.setValidationChildrenCascaded(false);
    }

    public void disableValidation() {
        this.setValidationChildrenCascaded(false);
    }

    private void setValidationChildrenCascaded(boolean validationEnabled) {
        this.setValidationEnabled(validationEnabled);
        if(getChildren()==null || getChildren().isEmpty()) return;
        getChildren().stream().forEach(x->x.setValidationChildrenCascaded(validationEnabled));
    }

    /*@Transient
    private boolean setValidationChildrenCascaded;
    public boolean isValidationEnabledChildrenCascaded() {
        return setValidationChildrenCascaded;
    }*/

    public boolean validate(){
        return false;
    }

    @Transient
    private boolean auditEnabled = true;

    public boolean isAuditEnabled() {
        return auditEnabled;
    }

    public void setAuditEnabled(boolean auditEnabled) {
        this.auditEnabled = auditEnabled;
    }
    @Transient
    @NotAudited
    @JsonIgnore
    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isUpdatedByService() {
        return isUpdatedByService;
    }

    public void setUpdatedByService(boolean updatedByService) {
        isUpdatedByService = updatedByService;
    }

    @Transient
    @NotAudited
    private List<UsedByEntity> usedByEntities = new ArrayList<>();

    public List<UsedByEntity> getUsedByEntities() {
        return usedByEntities;
    }

    public void setUsedByEntities(List<UsedByEntity> usedByEntities) {
        this.usedByEntities = usedByEntities;
    }

    public void clearUsedByEntities() {
        this.usedByEntities = new ArrayList<>();
    }
}
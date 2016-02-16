

/**
 * Created by ratnesh on 12/22/2014.
 */
@MappedSuperclass
public class AuditableEntity extends BaseEntity {


    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "is_inactive")
    private Boolean isInactive;

    @Column(name = "is_approved")
    @NotAudited
    private Boolean approved;

    public Boolean getPreviousApprovedStaus() {
        return previousApprovedStaus;
    }

    public void setPreviousApprovedStaus(Boolean previousApprovedStaus) {
        this.previousApprovedStaus = previousApprovedStaus;
    }

    @JsonIgnore
    @Column(name = "previous_approved_status")
    private Boolean previousApprovedStaus;

    @Column(name = "reason_for_unapproval")
    private String reasonForUnApproval;

    @ManyToOne
    @JoinColumn(name = "createdby_user_id")
    private User createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @NotAudited
    @Column(name = "created_date")
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "last_modified_by")
    private User lastModifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date")
    @NotAudited
    private Date lastModifiedDate;

    @Column(name = "approvals", length = 600)
    @Convert(converter = JpaConverterJson.class)
    @JsonIgnore
    private List<Approval> approvals;

    @JsonIgnore
    public void addApproval(CurrentUser currentUser, boolean approved) {
        List<Approval> approvals = this.getApprovals();
        if(approvals==null)
            approvals=new ArrayList<>();

        Approval newApproval = new Approval();
        newApproval.setApproved(approved);
        newApproval.setApprovedByUserId(currentUser.getId());
        newApproval.setApprovedDate(new Date());
        approvals.add(newApproval);
        this.setApprovals(approvals);
    }

    @JsonIgnore
    public Approval getLastApproval() {
        List<Approval> approvals = getApprovals();
        if(approvals==null || approvals.isEmpty()) return null;
        return approvals.get(approvals.size()-1);
    }

    public Integer getApprovedByUserId() {
        Approval approval = getLastApproval();
        if(approval==null) return null;
        return approval.getApprovedByUserId();
    }

    public Date getApprovedDate() {
        Approval approval = getLastApproval();
        if(approval==null) return null;
        return approval.getApprovedDate();
    }

    /*@ManyToOne
    @JoinColumn(name = "origin_createdby_user_id")
    private User originCreatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @NotAudited
    @Column(name = "origin_created_date")
    private Date originCreatedDate;

    private User originLastModifiedBy;
    private Date originLastModifiedDate;

    @Column(name = "origin_approvals")
    @Convert(converter = JpaConverterJson.class)
    private List<Approval> originApprovals;*/

    @JsonIgnore
    public User getOriginCreatedBy() {
        //return originCreatedBy;
        return null;
    }

    public void setOriginCreatedBy(User originCreatedBy) {
        //this.originCreatedBy = originCreatedBy;
    }

    @JsonIgnore
    @Mapping("createdBy")
    public User getDozerCreatedBy() {
        //return originCreatedBy;
        return null;
    }

    public void setDozerCreatedBy(User dozerCreatedBy) {
        this.createdBy = Utils.getCurrentUser();
    }

    @JsonIgnore
    public Date getOriginCreatedDate() {
        //return originCreatedDate;
        return new Date();
    }

    public void setOriginCreatedDate(Date originCreatedDate) {
        //this.originCreatedDate = originCreatedDate;
    }

    @JsonIgnore
    @Mapping("lastModifiedBy")
    public User getOriginLastModifiedBy() {
        //return originLastModifiedBy;
        return null;//Utils.getCurrentUser();
    }

    public void setOriginLastModifiedBy(User originLastModifiedBy) {
        //this.originLastModifiedBy = originLastModifiedBy;
    }

    @JsonIgnore
    public Date getOriginLastModifiedDate() {
        //return originLastModifiedDate;
        return new Date();
    }

    public void setOriginLastModifiedDate(Date originLastModifiedDate) {
        //this.originLastModifiedDate = originLastModifiedDate;
    }

    @JsonIgnore
    public List<Approval> getOriginApprovals() {
        //return originApprovals;
        return new ArrayList<>();
    }

    public void setOriginApprovals(List<Approval> originApprovals) {
        //this.originApprovals = originApprovals;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Boolean getIsInactive() {
        return isInactive;
    }

    public void setIsInactive(Boolean isInactive) {
        this.isInactive = isInactive;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    @JsonIgnore
    public void resetApproveCascaded(){
        this.setApproved(null);
        if(this.getChildren()==null) return;
        this.getChildren().forEach(x -> ((AuditableEntity)x).resetApproveCascaded());
    }

    @JsonIgnore
    public void setApprovedLocally(Boolean approved) {
        this.approved = approved;
        this.previousApprovedStaus=approved;
    }


    public String getReasonForUnApproval() {
        return reasonForUnApproval;
    }

    public void setReasonForUnApproval(String reasonForUnApproval) {
        this.reasonForUnApproval = reasonForUnApproval;
    }

    //@Mapping("originCreatedBy")
    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    @Mapping("originApprovals")
    public List<Approval> getApprovals() {
        return approvals;
    }

    public void setApprovals(List<Approval> approvals) {
        this.approvals = approvals;
    }

    //@Mapping("originLastModifiedBy")
    public User getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(User lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Mapping("originCreatedDate")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Mapping("originLastModifiedDate")
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @JsonIgnore
    public void updateSessionBasedPropeties(CurrentUser currentUser){
        return;
    }

    public void resetAudit(User createdBy, Date createdDate){
        this.isDeleted = null;
        this.isInactive = null;
        this.approved = null;
        this.previousApprovedStaus = null;
        this.reasonForUnApproval = null;
        this.createdBy = createdBy;
        this.approvals = null;
        this.lastModifiedBy = null;
        this.createdDate = createdDate;
        this.lastModifiedDate = null;
    }
    public void resetAuditCascaded(User createdBy, Date createdDate){
        this.resetAudit(createdBy, createdDate);
        if(this.getChildren()==null) return;
        this.getChildren().forEach(x -> ((AuditableEntity)x).resetAuditCascaded(createdBy, createdDate));
    }

}


/**
 * Created by ratnesh on 12/22/2014.
 */
@MappedSuperclass
public class SecurableEntity extends AuditableEntity {


    @Column(name = "global_access_mask")
    @NotAudited
    private Integer globalAccessMask;

    @Column(name = "users_access_mask")
    @NotAudited
    private String usersAccessMask;


    public Integer getGlobalAccessMask() {
        return globalAccessMask;
    }

    @JsonIgnore
    public boolean cascadeAccessMaskToChildren() {
        return true;
    }

    @JsonIgnore
    public void setGlobalAccessMask(Integer globalAccessMask) {
        if (this.globalAccessMask == globalAccessMask)
            return;
        this.globalAccessMask = globalAccessMask;

//        TrackableEntity parent=this.getParent();
//        if(parent!=null) parent.setGlobalAccessMask(globalAccessMask);

        List<SecurableEntity> children = this.getChildren();
        // Don't update already approved Test in case Test is the child for TableColumn.
        if (this instanceof TableColumn && ((TableColumn) this).getTest() != null) return;
        if (children == null)
            return;
        for (SecurableEntity c : children) {
            if (c == null) {
                continue;
            }
            c.setGlobalAccessMask(globalAccessMask);
        }
    }

    public String getUsersAccessMask() {
        return usersAccessMask;
    }

    // @JsonIgnore
    public void setUsersAccessMask(String usersAccessMask) {
        if (this.usersAccessMask == usersAccessMask)
            return;
        setUserAccessMaskWithoutCascade(usersAccessMask);
        List<SecurableEntity> children = this.getChildren();
        // Don't update already approved Test in case Test is the child for TableColumn.
        if (this instanceof TableColumn && ((TableColumn) this).getTest() != null) return;
        if (children != null) {
            for (SecurableEntity c : children) {
                if (c == null) {
                    continue;
                }
                c.setUsersAccessMask(usersAccessMask);
            }
        }
        setUsersAccessMaskWithMerge(usersAccessMask);
    }

    public void mergeUsersAccessMaskCascaded(String usersAccessMask) {
        if (this.usersAccessMask == usersAccessMask)
            return;
        //setUserAccessMaskWithoutCascade(usersAccessMask);
        setUserAccessMaskWithoutCascade(AccessControlMaskUtil.GetMergeUserAccessString(this.getUsersAccessMask(), usersAccessMask));
        List<SecurableEntity> children = this.getChildren();
        if (children != null) {
            for (SecurableEntity c : children) {
                if (c == null) {
                    continue;
                }
                c.setUsersAccessMask(AccessControlMaskUtil.GetMergeUserAccessString(c.getUsersAccessMask(), usersAccessMask));
            }
        }
        setUsersAccessMaskWithMerge(usersAccessMask);
    }

    public void setUserAccessMaskWithoutCascade(String usersAccessMask) {
        if (this.usersAccessMask == usersAccessMask)
            return;
        this.previousUserAccessMask = usersAccessMask;
        this.usersAccessMask = usersAccessMask;
    }

    @JsonIgnore
    public void setUsersAccessMaskWithMerge(String usersAccessMask) {
        setUserAccessMaskWithoutCascade(AccessControlMaskUtil.GetMergeUserAccessString(this.getUsersAccessMask(), usersAccessMask));

        SecurableEntity parent = this.getParent();
        if (parent != null) parent.setUsersAccessMaskWithMerge(usersAccessMask);

    }


    @JsonIgnore
    private String previousUserAccessMask;

    public String getPreviousUserAccessMask() {
        if (this.previousUserAccessMask == null) return this.usersAccessMask;
        return previousUserAccessMask;
    }

    public void setPreviousUserAccessMask(String previousUserAccessMask) {
        this.previousUserAccessMask = previousUserAccessMask;
    }

    @Transient
    private int accessMask;

    public int getAccessMask() {
        return accessMask;
    }

    public void setAccessMask(int accessmask) {
        this.accessMask = accessmask;
    }


    //@JsonIgnore
//    @Column(name="accessMaskType")
//    private int accessMaskType= AccessMaskTypeEnum.user.getValue();
//
//    public int getAccessMaskType() {
//        return accessMaskType;
//    }
//
//    public void setAccessMaskType(int accessMaskType) {
//        this.accessMaskType = accessMaskType;
//    }
//
//    @JsonIgnore
//    public void addAccessMaskType(int accessMaskType) {
//        this.accessMaskType += accessMaskType;
//    }


    //@JsonIgnore
    @Column(name = "roles_access_mask")
    @NotAudited
    private String rolesAccessMask;

    public String getRolesAccessMask() {
        return rolesAccessMask;
    }

    //@JsonIgnore
    public void setRolesAccessMask(String rolesAccessMask) {
        if (rolesAccessMask == null || rolesAccessMask.equals(this.rolesAccessMask))
            return;

        // if new roleAccessMask access permission is less then don't allow to set.
        if(this.rolesAccessMask != null) {
            if(isRolesAccessPermissionDowngrading(rolesAccessMask))
                return;
        }

        setRoleAccessMaskWithoutCascade(rolesAccessMask);
        // Don't update already approved Test in case Test is the child for TableColumn.
        if(this instanceof TableColumn && ((TableColumn)this).getTest()!=null) return;
        List<SecurableEntity> children = this.getChildren();
        if (children != null) {
            for (SecurableEntity c : children) {
                if (c == null ) {
                    continue;
                }
                c.setRolesAccessMask(rolesAccessMask);
            }
        }
        setrolesAccessMaskWithMerge(rolesAccessMask);
    }

    private boolean isRolesAccessPermissionDowngrading(String rolesAccessMask) {
        if(this.rolesAccessMask == null)
            return false;
        String[] orgRoleAccessMaskList = this.rolesAccessMask.split(",");
        String[] newRoleAccessMaskList = rolesAccessMask.split(",");
        for (String orgRoleAccessMask : orgRoleAccessMaskList) {
            for (String newRoleAccessMask : newRoleAccessMaskList) {
                if (!rolesAccessMask.equals(this.rolesAccessMask)) {
                    if (newRoleAccessMask.contains(":") && orgRoleAccessMask.contains(":")) {
                        String[] orgStr = orgRoleAccessMask.split(":");
                        String[] newStr = newRoleAccessMask.split(":");
                        if (orgStr.length > 1 && newStr.length > 1) {
                            if ((Integer.parseInt(orgStr[0]) == Integer.parseInt(newStr[0]))
                                    && (Integer.parseInt(orgStr[1]) > Integer.parseInt(newStr[1]))) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private void setRoleAccessMaskWithoutCascade(String rolesAccessMask) {
        if (this.rolesAccessMask == rolesAccessMask)
            return;
        this.previousRolesAccessMask = this.rolesAccessMask;
        this.rolesAccessMask = rolesAccessMask;
    }

    @Transient
    @JsonIgnore
    private String previousRolesAccessMask;

    public String getPreviousRolesAccessMask() {
        if (this.previousRolesAccessMask == null) return this.rolesAccessMask;
        return previousRolesAccessMask;
    }

    public void setPreviousRolesAccessMask(String previousRolesAccessMask) {
        this.previousRolesAccessMask = previousRolesAccessMask;
    }


    @JsonIgnore
    public void setrolesAccessMaskWithOutCascade(String rolesAccessMask) {
        setRoleAccessMaskWithoutCascade(rolesAccessMask);
    }

    @JsonIgnore
    public void setrolesAccessMaskWithMerge(String rolesAccessMask) {
        setRoleAccessMaskWithoutCascade(AccessControlMaskUtil.GetMergeUserAccessString(this.getRolesAccessMask(), rolesAccessMask));
        SecurableEntity parent = this.getParent();
        if (parent != null){
            parent.setrolesAccessMaskWithMerge(rolesAccessMask);
        }
    }

    @JsonIgnore
    public void addRolesAccessMask(String accessMaskType) {
        setRoleAccessMaskWithoutCascade(AccessControlMaskUtil.GetMergeUserAccessString(this.rolesAccessMask, accessMaskType));
    }

    @JsonIgnore
    public void removeRolesAccessMask(String accessMaskType) {
        setRoleAccessMaskWithoutCascade(AccessControlMaskUtil.GetRemoveUserAccessString(this.rolesAccessMask, accessMaskType));
    }

    @JsonIgnore
    @Transient
    public String getPushMessage(WorkFlowTaskTypeEnum workFlowTaskTypeEnum,PushNotificationTypeEnum pushNotificationTypeEnum) {

        return null;
    }
    @JsonIgnore
    @Transient
    public HashMap<String, String> getNotificationData() {

        return null;
    }

    /*@Transient
    private Integer approvedByUserId;

    public Integer getApprovedByUserId() {
        return approvedByUserId;
    }

    public void setApprovedByUserId(Integer approvedByUserId) {
        this.approvedByUserId = approvedByUserId;
    }*/

    @Transient
    private Integer createdByUserId;

    public Integer getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Integer createdByUserId) {
        this.createdByUserId = createdByUserId;
    }


    @Transient
    private Boolean canApprove;

    public Boolean getCanApprove() {
        return canApprove;
    }

    public void setCanApprove(Boolean canApprove) {
        this.canApprove = canApprove;
    }


    @JsonIgnore
    public boolean checkIfApprovalIsInEditMode(User user)
    {
        return false;
    }
    //    @Transient
//    private Date createdDate;
//
//    @Override
//    public Date getCreatedDate() {
//        return createdDate;
//    }
//
//    @Override
//    public void setCreatedDate(Date createdDate) {
//        this.createdDate = createdDate;
//    }

    @JsonIgnore
    @Override
    public void updateSessionBasedPropeties(CurrentUser currentUser){
        /*if(this.getApprovedBy() != null)
            this.setApprovedByUserId(this.getApprovedBy().getUserId());*/
        if(this.getCreatedBy() != null)
            this.setCreatedByUserId(this.getCreatedBy().getUserId());
//        if(this.getCreatedDate() != null)
//            this.setCreatedDate(this.getCreatedDate());
        return;
    }

    @JsonIgnore
    public void mergeAccessMaskFrom(SecurableEntity entity){
        if(entity==null) return;
        this.setGlobalAccessMask(entity.getGlobalAccessMask());
        this.setrolesAccessMaskWithMerge(entity.getRolesAccessMask());
        this.setUsersAccessMaskWithMerge(entity.getUsersAccessMask());
    }

    public String getName(){
        return null;
    }

}
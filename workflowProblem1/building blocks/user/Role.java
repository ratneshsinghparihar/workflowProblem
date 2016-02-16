


/**
 * The persistent class for the tblrole database table.
 * 
 */
@Entity
@Table(name="tbl_role", schema = CommonConstants.common_SCHEMA_NAME)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Role extends TrackableEntity {
	private static final long serialVersionUID = 1L;

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;

	@ManyToOne
	@JoinColumn(name="access_control_id")
	private MasterDataValue accessControl;

	@ManyToOne
	@JoinColumn(name="permission_id")
	private MasterDataValue permission;

	@ManyToMany(cascade = CascadeType.ALL, mappedBy = "roles")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@NotAudited
    private List<RoleGroup> roleGroup;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public MasterDataValue getAccessControl() {
		return accessControl;
	}

	public void setAccessControl(MasterDataValue accessControl) {
		this.accessControl = accessControl;
	}

	public MasterDataValue getPermission() {
		return permission;
	}

	public void setPermission(MasterDataValue permission) {
		this.permission = permission;
	}

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<RoleGroup> getRoleGroup() {
        return roleGroup;
    }

    public void setRoleGroup(List<RoleGroup> roleGroup) {
        this.roleGroup = roleGroup;
    }

    @Column(name = "id")
    public Integer getId() {
        return super.getId();
    }
}
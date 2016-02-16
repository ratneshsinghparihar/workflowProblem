
@Entity
@Table(name="tbl_role_group", schema = CommonConstants.common_SCHEMA_NAME)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RoleGroup extends TrackableEntity {

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;

    @ManyToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinTable (
        name="tbl_rolegroup_role",
        schema = CommonConstants.common_SCHEMA_NAME,
        joinColumns={ @JoinColumn(name="role_group_id", referencedColumnName="id") },
        inverseJoinColumns={ @JoinColumn(name="role_id", referencedColumnName="id")}
    )
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @NotAudited
    private List<Role> roles;

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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Column(name = "id")
    public Integer getId() {
        return super.getId();
    }
}

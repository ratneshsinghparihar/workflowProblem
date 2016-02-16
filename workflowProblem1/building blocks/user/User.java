
@Entity
@Table(name="tbl_user", schema = CommonConstants.common_SCHEMA_NAME)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    //@GenericGenerator(name = "ebmr_id_gen", strategy = "eBMRWebService.util.eBMRIdGenerator")
    //@GeneratedValue(generator = "ebmr_id_gen")
    @Column(name="id")
    private int id;

    @Column(name="is_locked")
    private String isLocked;

    @Column(name="first_name")
    private String firstName;
//
//	public List<UserAccessLog> getUserAccessLogs() {
//		return userAccessLogs;
//	}
//
//	public void setUserAccessLogs(List<UserAccessLog> userAccessLogs) {
//		this.userAccessLogs = userAccessLogs;
//	}

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="last_login_date")
    private Date lastLoginDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="last_logout_date")
    private Date lastLogoutDate;

    @Column(name="last_login_ip")
    private String lastLoginIp;

    @Column(name="last_name")
    private String lastName;

    private String password;

    @Column(name="user_name" ,unique=true)
    private String userName;

    //bi-directional many-to-one association to TbluserAccessLog
//	@OneToMany(mappedBy="tbluser")
//	private List<UserAccessLog> userAccessLogs;

    @Column(name="email_id" ,unique=true)
    private String emailId;

    @Column(name="designation")
    private String designation;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    @Column(name="employee_id" ,unique=true)
    private String employeeId;

    public boolean isPasswordReset() {
        return passwordReset;
    }

    public void setPasswordReset(boolean passwordReset) {
        this.passwordReset = passwordReset;
    }

    @Column(name="is_password_reset")
    private boolean passwordReset;

    public Date getJoiningdate() {
        return joiningdate;
    }

    public void setJoiningdate(Date joiningdate) {
        this.joiningdate = joiningdate;
    }

    @Column(name="joining_date")
    private Date joiningdate;

    @ManyToOne
    @JoinColumn(name="department_id")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Department department;

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getDepartmentName(){
        if(department==null) return null;
        return department.getName();
    }

    @ManyToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinTable (
            name="tbl_roleGrp_user",
            schema = CommonConstants.common_SCHEMA_NAME,
            joinColumns={ @JoinColumn(name="user_id", referencedColumnName="id") },
            inverseJoinColumns={ @JoinColumn(name="role_group_id", referencedColumnName="id")}
    )
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<RoleGroup> rolegroups;

/*    @ManyToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<BmrAssignment> bmrAssignments;*/

    //private List<RoleGroup> dozerRolegroups;

    public List<RoleGroup> getDozerRolegroups() {
        return null;
    }

    public void setDozerRolegroups(List<RoleGroup> dozerRolegroups) {
        //this.dozerRolegroups = dozerRolegroups;
    }

    @Mapping("dozerRolegroups")
    @NotAudited
    public List<RoleGroup> getRolegroups() {
        return rolegroups;
    }

    public void setRolegroups(List<RoleGroup> rolegroups) {
        this.rolegroups = rolegroups;
    }

    public String getRoleGroupName() {
        if(rolegroups==null) return null;
        if(rolegroups.size() == 0)return null;
        List<String> roles=rolegroups.stream().map(x -> x. getName()).collect(Collectors.toList());
        return String.join(",",roles);
    }

    @JsonIgnore
    public List<Integer> getRoleGroupIds() {
        if(rolegroups==null) return new ArrayList<>();
        if(rolegroups.size() == 0)return new ArrayList<>();
        List<Integer> roles=rolegroups.stream().map(x -> x.getId()).collect(Collectors.toList());
        return roles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return this.id;
    }

    public void setUserId(int userId) {
        this.id = userId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getIsLocked() {
        return this.isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    public Date getLastLoginDate() {
        return this.lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonIgnore
    public String getPassword() {
        return this.password;
    }

    @JsonProperty
    public void setPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
        this.passwordString = password;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonIgnore
    public String getFullName() {
        String fullName = "";
        if(firstName == null || firstName.isEmpty()){
            return lastName;
        }
        if(lastName == null || lastName.isEmpty()){
            return firstName;
        }
        return firstName + " " + lastName;
    }

    public String getName(){
        return getFullName();
    }

    public Date getLastLogoutDate() {
        return lastLogoutDate;
    }

    public void setLastLogoutDate(Date lastLogoutDate) {
        this.lastLogoutDate = lastLogoutDate;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }


    @Transient
   /* @Pattern(regexp = ("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{5,10}"),
            message = "Invalid password format - Password should be at least 8 characters and have at least 1 Capital letter, " +
                    "1 Special character, 1 number and should contain no spaces.")*/
    public String passwordString;

}
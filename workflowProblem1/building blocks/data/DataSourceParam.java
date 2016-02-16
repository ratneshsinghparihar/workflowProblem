
@Entity
@Table(name="tbl_datasource_param",schema = CommonConstants.building_blocks)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DataSourceParam extends DataSourceParamBase {

    @ManyToOne
    @JoinColumn(name = "data_source_id")
    private DataSource dataSource;



    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }



    public DataSource getParent(){
        return dataSource;
    }
    /*public List<DataSourceParam> getChildren(){
        return dataSourceParams;
    }*/
}

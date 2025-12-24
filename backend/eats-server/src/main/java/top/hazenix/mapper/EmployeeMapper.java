package top.hazenix.mapper;

import com.github.pagehelper.Page;
import top.hazenix.dto.EmployeePageQueryDTO;
import top.hazenix.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    void insert(Employee employee);

    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 根据参数动态更新
     * @param emp
     */
    void update(Employee emp);

    Employee selectById(Integer id);
}

package top.hazenix.user.mapper;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.hazenix.user.domain.dto.EmployeePageQueryDTO;
import top.hazenix.user.domain.entity.Employee;


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

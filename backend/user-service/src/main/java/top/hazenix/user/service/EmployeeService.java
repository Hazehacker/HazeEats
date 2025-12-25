package top.hazenix.user.service;


import top.hazenix.result.PageResult;
import top.hazenix.user.domain.dto.EmployeeDTO;
import top.hazenix.user.domain.dto.EmployeeLoginDTO;
import top.hazenix.user.domain.dto.EmployeePageQueryDTO;
import top.hazenix.user.domain.entity.Employee;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);


    void save(EmployeeDTO employeeDTO);

    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    void changeStatus(Integer status, Long id);

    Employee getById(Integer id);

    void update(EmployeeDTO employeeDTO);
}

package top.hazenix.user.service;

import top.hazenix.dto.EmployeeDTO;
import top.hazenix.dto.EmployeeLoginDTO;
import top.hazenix.dto.EmployeePageQueryDTO;
import top.hazenix.entity.Employee;
import top.hazenix.result.PageResult;

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

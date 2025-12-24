package top.hazenix.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import top.hazenix.constant.MessageConstant;
import top.hazenix.constant.PasswordConstant;
import top.hazenix.constant.StatusConstant;
import top.hazenix.context.BaseContext;
import top.hazenix.dto.EmployeeDTO;
import top.hazenix.dto.EmployeeLoginDTO;
import top.hazenix.dto.EmployeePageQueryDTO;
import top.hazenix.entity.Employee;
import top.hazenix.exception.AccountLockedException;
import top.hazenix.exception.AccountNotFoundException;
import top.hazenix.exception.PasswordErrorException;
import top.hazenix.mapper.EmployeeMapper;
import top.hazenix.result.PageResult;
import top.hazenix.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);

        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        String processedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!processedPassword.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }
        //


        //3、返回实体对象
        return employee;
    }

    @Override
    public void save(EmployeeDTO employeeDTO) {
//        String name = employeeDTO.getName();
//        if(employeeMapper.selectByUsername(name) != null){
//            throw new RuntimeException("用户已存在");
//        }
        Employee employee = new Employee();
        //把前面的拷贝到后面
        BeanUtils.copyProperties(employeeDTO,employee);
        employee.setStatus(StatusConstant.ENABLE);
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        Long currentId = BaseContext.getCurrentId();
        employee.setCreateUser(currentId);
        employee.setUpdateUser(currentId);
        // TODO BaseContext的id什么时候移除

        employeeMapper.insert(employee);

    }

    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {

        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        long total = page.getTotal();
        List<Employee> records = page.getResult();
        return new PageResult(total, records);
    }

    @Override
    public void changeStatus(Integer status, Long id) {
        Employee emp = Employee.builder()
                .status(status)
                .id(id)
                .build();
        employeeMapper.update(emp);

        return ;
    }

    @Override
    public Employee getById(Integer id) {

        Employee employee = employeeMapper.selectById(id);
        employee.setPassword("****");

        return employee;
    }

    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
        //update要传入的是employee对象，所以属性拷贝一下
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.update(employee);
    }

}

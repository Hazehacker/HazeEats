package top.hazenix.controller.admin;

import top.hazenix.constant.JwtClaimsConstant;
import top.hazenix.dto.EmployeeDTO;
import top.hazenix.dto.EmployeeLoginDTO;
import top.hazenix.dto.EmployeePageQueryDTO;
import top.hazenix.entity.Employee;
import top.hazenix.properties.JwtProperties;
import top.hazenix.result.PageResult;
import top.hazenix.result.Result;
import top.hazenix.service.EmployeeService;
import top.hazenix.utils.JwtUtil;
import top.hazenix.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static top.hazenix.result.Result.success;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();
        log.info("登录成功");
        return success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return success();
    }


    /*
     * 新增员工
     */
    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDTO){

        employeeService.save(employeeDTO);

        return success();
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工信息分页查询：{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return success(pageResult);
    }
    /**
     * 启用/禁用员工账号
     */
    @PostMapping("/status/{status}")
    public Result changeStatus(@PathVariable Integer status,Long id) {
        employeeService.changeStatus(status,id);
        return success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id){
        Employee emp = employeeService.getById(id);
        return Result.success(emp);
    }

    @PutMapping
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        employeeService.update(employeeDTO);
        return Result.success();
    }




}

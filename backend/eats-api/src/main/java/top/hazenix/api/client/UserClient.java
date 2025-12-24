package top.hazenix.api.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import top.hazenix.api.domain.entity.AddressBook;
import top.hazenix.api.domain.vo.DishVO;
import top.hazenix.api.domain.vo.SetmealVO;
import top.hazenix.result.Result;

import java.time.LocalDateTime;

@FeignClient(name = "user-service")
public interface UserClient {
    // ---------------------地址相关接口-----------------------------

    @GetMapping("/user/addressBook/{id}")
    public Result<AddressBook> getById(@PathVariable Long id);

    /**
     * 统计指定时间区间内用户数量
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/user/user/sum")
    Integer sumByDateTime(@RequestParam LocalDateTime beginTime, @RequestParam LocalDateTime endTime);

    // -----------------------------------------------------------
}

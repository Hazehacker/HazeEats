package top.hazenix.service.impl;


import top.hazenix.dto.GoodsSalesDTO;
import top.hazenix.entity.Orders;
import top.hazenix.mapper.OrderMapper;
import top.hazenix.mapper.UserMapper;
import top.hazenix.service.ReportService;
import top.hazenix.service.WorkspaceService;
import top.hazenix.vo.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;


    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //dateList存放范围内的日期
        List<LocalDate> dateList = new ArrayList<>();
        while (begin.isBefore(end)) {
            dateList.add(begin);
            begin = begin.plusDays(1);
        }

        List<Double> turnoverList = new ArrayList<>();//存放每天的营业额
        for (LocalDate date : dateList) {
            //查询date日期对应的营业额数据，营业额是指，状态为“已完成”的订单金额合计
             LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
             LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //select sum(amount) from orders where order_time >= ? and order_time < ? and status = 5

            Double turnover = orderMapper.sumByStatusAndOrderTime(Orders.COMPLETED,beginTime,endTime);
            turnoverList.add(turnover == null ? 0.0 : turnover);//【细节细节】

        }


        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        LocalDateTime firstDateTime = dateList.get(0).atStartOfDay();
        Integer initialNum = userMapper.sumByDateTime(null,firstDateTime);//[同一个mapper方法的复用]
        Integer totalNum = initialNum;

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer dailyNum = null;
            try {
                dailyNum = userMapper.sumByDateTime(beginTime,endTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
            newUserList.add(dailyNum);
            totalNum += dailyNum;
            totalUserList.add(totalNum);
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();
    }

    /**
     * 统计指定时间区间内的订单数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();//日期列表
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> orderCntList = new ArrayList<>();//订单数列表
        List<Integer> validOrderCntList = new ArrayList<>();//有效订单数列表
        Double finishPercent = 0.0;
        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //获取每日订单数
            Integer dailyCount = null;
            try {
                dailyCount = orderMapper.getCountByDateAndStatus(beginTime,endTime,null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            orderCntList.add(dailyCount);
            totalOrderCount+=dailyCount;
            //获取每日有效订单数
            Integer dailyValidCount = orderMapper.getCountByDateAndStatus(beginTime,endTime,Orders.COMPLETED);
            validOrderCntList.add(dailyValidCount);
            validOrderCount+=dailyValidCount;


        }
        if (totalOrderCount != 0) {
            finishPercent = validOrderCount*1.0/totalOrderCount;
        }
        //

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCompletionRate(finishPercent)
                .orderCountList(StringUtils.join(orderCntList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .validOrderCountList(StringUtils.join(validOrderCntList,","))
                .build();







    }

    /**
     * 返回销量排名top10的数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10Statistics(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin,LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end,LocalTime.MAX);

        List<GoodsSalesDTO> list = orderMapper.getSalesTop10(beginTime,endTime);
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        for (GoodsSalesDTO goodsSalesDTO : list) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numberList,","))
                .build();
    }

    /**
     * 导出运营数据报表
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //1. 查询数据库，获取营业数据（最近30天）

        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);
        //概览数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));
        //2. 通过POI将数据写入到excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);//基于模版文件创建excel文件
            //填充数据
            //获取标签页
            XSSFSheet sheet = excel.getSheet("Sheet1");
            //填充时间数据
             sheet.getRow(1).getCell(1).setCellValue("时间："+dateBegin+"至"+dateEnd);
            //第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            //第5行
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());
            
            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());

            }

            //3. 通过输出流将excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭数据
            out.close();
            excel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }





    }
}

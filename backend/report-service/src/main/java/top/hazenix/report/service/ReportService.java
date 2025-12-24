package top.hazenix.report.service;



import top.hazenix.report.domain.vo.OrderReportVO;
import top.hazenix.report.domain.vo.SalesTop10ReportVO;
import top.hazenix.report.domain.vo.TurnoverReportVO;
import top.hazenix.report.domain.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);

    SalesTop10ReportVO getSalesTop10Statistics(LocalDate begin, LocalDate end);

    void exportBusinessData(HttpServletResponse response);
}

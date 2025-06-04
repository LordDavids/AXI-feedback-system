package me.abdul.axi_api.controllers;

import me.abdul.axi_api.dtos.Report.ReportDto;
import me.abdul.axi_api.services.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RequestMapping()
@RestController
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping(value = "/team/{teamId}/report/user/{userId}/pdf", produces = "application/pdf")
    public ResponseEntity<byte[]> getReport(@PathVariable int teamId , @PathVariable int userId, @RequestParam String startDate, @RequestParam String endDate, @RequestParam int categoryId) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date start = formatter.parse(startDate);
        Date end = formatter.parse(endDate);
        byte[] report = reportService.getUserReportPdf(userId, teamId, start, end, categoryId);
        return ResponseEntity.ok(report);

    }

    @GetMapping("/team/{teamId}/report/all")
    public ResponseEntity<List<ReportDto>> getAllReports(@PathVariable int teamId, @RequestParam String startDate, @RequestParam String endDate, @RequestParam int categoryId) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date start = formatter.parse(startDate);
        Date end = formatter.parse(endDate);
        List<ReportDto> answers = reportService.getAllReports(teamId, start, end, categoryId);
        return ResponseEntity.ok(answers);
    }

    @GetMapping(value = "/team/{teamId}/report/all/pdf", produces = "application/pdf")
    public ResponseEntity<byte[]> getAllReportsPdf(@PathVariable int teamId, @RequestParam String startDate, @RequestParam String endDate, @RequestParam int categoryId) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date start = formatter.parse(startDate);
        Date end = formatter.parse(endDate);
        byte[] pdf = reportService.getAllReportsPdf(teamId, start, end, categoryId);
        return ResponseEntity.ok(pdf);
    }

}

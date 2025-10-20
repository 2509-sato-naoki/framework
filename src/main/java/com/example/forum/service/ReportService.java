package com.example.forum.service;

import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.ReportRepository;
import com.example.forum.repository.entity.Report;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Getter
@Setter
public class ReportService {
    @Autowired
    ReportRepository reportRepository;

    /*
     * レコード全件取得処理
     */
    public List<ReportForm> findAllReport(LocalDateTime startDate, LocalDateTime endDate) {
//        List<Report> results = reportRepository.findAllByOrderByIdDesc();
        List<Report> results = reportRepository.findByCreatedDateBetweenOrderByUpdatedDateDesc(startDate, endDate);
        List<ReportForm> reports = setReportForm(results);
        return reports;
    }
    /*
     * DBから取得したデータをFormに設定
     */
    public List<ReportForm> setReportForm(List<Report> results) {
        List<ReportForm> reports = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            ReportForm report = new ReportForm();
            Report result = results.get(i);
            report.setId(result.getId());
            report.setContent(result.getContent());
//            report.setCreatedDate(result.getCreatedDate());
//            report.setUpdatedDate(result.getUpdatedDate());
            reports.add(report);
        }
        return reports;
    }

    /*
     * レコード追加
     */
    public void saveReport(ReportForm reqReport) {
        Report saveReport = setReportEntity(reqReport);
        reportRepository.save(saveReport);
    }

    /*
     * リクエストから取得した情報をEntityに設定
     */
    private Report setReportEntity(ReportForm reqReport) {
        Report report = new Report();
        report.setId(reqReport.getId());
        report.setContent(reqReport.getContent());
        report.setUpdatedDate(reqReport.getUpdatedDate());
        return report;
    }

    /*
    * レコード削除処理
     */
    //idだけ受け取れたら問題ない
    public void deleteReport(int reqReport) {
        reportRepository.deleteById(reqReport);
    }

    /*
     * 投稿編集画面表示処理
     */
    public Optional<Report> selectReport(int id) {
        Optional<Report> reportForm = reportRepository.findById(id);
        Report reportNull = reportForm.orElse(null);
        if (reportNull == null) {
            return null;
        } else {
            return reportForm;
        }
    }
}

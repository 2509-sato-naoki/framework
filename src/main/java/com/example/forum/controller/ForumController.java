package com.example.forum.controller;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.entity.Report;
import com.example.forum.service.CommentService;
import com.example.forum.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Conventions;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ForumController {
    @Autowired
    ReportService reportService;

    @Autowired
    CommentService commentService;

    /*
     * 投稿内容表示処理
     */
    @GetMapping
    public ModelAndView top(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss") LocalDate startDate,
                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss") LocalDate  endDate) {
        ModelAndView mav = new ModelAndView();
        // 投稿を全権取得
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        if (startDate == null) {
            startDateTime = LocalDateTime.of(2020, 1, 1, 0, 0);
        } else {
            startDateTime = startDate.atStartOfDay();
        }
        if (endDate == null) {
            endDateTime = LocalDateTime.now();
        } else {
            endDateTime = endDate.atTime(23, 59, 59);
        }
        List<ReportForm> contentData = reportService.findAllReport(startDateTime, endDateTime);
        // 返信を全権取得
        List<CommentForm> commentData = commentService.findAllComment();
        // 画面遷移先を指定
        mav.setViewName("/top");
        // 投稿データオブジェクトを補完
        mav.addObject("contents", contentData);
        mav.addObject("comments", commentData);
        return mav;
    }

    /*
     * 新規投稿画面表示
     */
    @GetMapping("/new")
    public ModelAndView newContent() {
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        ReportForm reportForm = new ReportForm();
        // 画面遷移先を指定
        mav.setViewName("/new");
        // 準備した空のFormを保管
        mav.addObject("formModel", reportForm);
        return mav;
    }

    /*
     * 新規投稿処理
     */
    @PostMapping("/add")
    public ModelAndView addContent(@ModelAttribute("formModel") @Validated ReportForm reportForm, BindingResult
            result){

        //バリデーションの結果がBindingReslutに格納される
        if (result.hasErrors()) {
            //この中に
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/new");
            return mav;
        } else {
            // 投稿をテーブルに格納
            reportService.saveReport(reportForm);
            // rootへリダイレクト
            return new ModelAndView("redirect:/");
        }

    }

    /*
     * 投稿削除処理
     */
    //具体的に投稿のIDを渡せるように　idの値は変われるように
    //htmlのフォームはdeleteはサポートしていない
    @PostMapping("delete/{contentId}")
    //アノテーション　@pathvariable
    //パスに変数を指定した時にそれを受け取るためにpathvariableを使う
    //@PathVariable()の中に受け取りたい変数名を描く
    public ModelAndView deleteContent(@PathVariable("contentId") int contentId){
        //投稿の削除処理
        reportService.deleteReport(contentId);
        //リダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿編集画面表示処理
     */
    @GetMapping("/edit/{contentId}")
    public ModelAndView editContent(@PathVariable("contentId") int contentId,
                                    @ModelAttribute("formModel") ReportForm formModel) {
        ModelAndView mav = new ModelAndView();
        // 画面遷移先を指定
        mav.setViewName("edit");
        if (formModel.getId() == 0) {
            Optional<Report> reportForm = reportService.selectReport(contentId);
            Report optionalReportForm = reportForm.get();
            ReportForm form = new ReportForm();
            form.setId(optionalReportForm.getId());
            form.setContent(optionalReportForm.getContent());
            form.setUpdatedDate(optionalReportForm.getUpdatedDate());
            mav.addObject("formModel", form);
        } else {
            mav.addObject("formModel", formModel);
        }

        return mav;
    }

    /*
     * 投稿編集処理
     */
    @PostMapping("/update/{contentId}")
    public ModelAndView updateContent(@PathVariable("contentId") int contentId,
                                      @ModelAttribute("formModel") @Validated ReportForm formModel,
                                      BindingResult result, RedirectAttributes redirectAttributes) {
        // 送る用の空のentitty
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(formModel); // (1)
            redirectAttributes.addFlashAttribute(
                    BindingResult.MODEL_KEY_PREFIX +
                            Conventions.getVariableName(formModel), result);
            return new ModelAndView("redirect:/edit/" + contentId);
        } else {
            formModel.setId(contentId);
            LocalDateTime localDatetime = LocalDateTime.now();
            formModel.setUpdatedDate(localDatetime);
            reportService.saveReport(formModel);
            return new ModelAndView("redirect:/");
        }
    }

    /*
    * コメント返信処理
     */
    @PostMapping("/comment")
    public ModelAndView commentContent(@RequestParam("comment")  @Validated String comment, BindingResult result,
                                       @RequestParam("reportId") int reportId) {
        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/");
            return mav;
        } else {
            CommentForm commentForm = new CommentForm();
            commentForm.setComment(comment);
            commentForm.setReportId(reportId);
            commentService.saveComment(commentForm);

            //⽇付で投稿を絞り込むことができる機能を追加
            Optional<Report> optionalReport = reportService.selectReport(reportId);
            Report report = optionalReport.get();
            List<Report> reportList = new ArrayList<>();
            reportList.add(report);
            List<ReportForm> reportFormList = new ArrayList<>();
            reportFormList = reportService.setReportForm(reportList);
            ReportForm reportForm = reportFormList.get(0);
            LocalDateTime nowDate = LocalDateTime.now();
            reportForm.setUpdatedDate(nowDate);
            reportService.saveReport(reportForm);
            return new ModelAndView("redirect:/");
        }
    }

    /*
    * コメント編集画面表示処理
     */
    @GetMapping("/commentEdit/{commentId}")
    public ModelAndView commentEditContent(@PathVariable("commentId") int commentId) {
        ModelAndView mav = new ModelAndView();
        //画面遷移先を指定
        mav.setViewName("/commentEdit");
        CommentForm commentForm = commentService.editComment(commentId);
        mav.addObject("formModel", commentForm);
        return mav;
    }

    /*
    * コメント編集処理
     */
    @PostMapping("/updateComment/{id}")
    public ModelAndView updateCommentContent(@PathVariable("id") int id, @RequestParam("reportId") int reportId,
                                             @ModelAttribute("formModel") CommentForm commentForm) {
        commentForm.setId(id);
        commentForm.setReportId(reportId);
        commentService.saveComment(commentForm);
        return new ModelAndView("redirect:/");
    }

    /*
    *　コメント削除処理
     */
    @DeleteMapping("/commentDelete/{id}")
    public ModelAndView commentDeleteContent(@PathVariable("id") int id) {
        commentService.deleteComment(id);
        return new ModelAndView("redirect:/");
    }
}

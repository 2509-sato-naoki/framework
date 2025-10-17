package com.example.forum.controller;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.entity.Report;
import com.example.forum.service.CommentService;
import com.example.forum.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
    public ModelAndView top() {
        ModelAndView mav = new ModelAndView();
        // 投稿を全権取得
        List<ReportForm> contentData = reportService.findAllReport();
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
    public ModelAndView addContent(@ModelAttribute("formModel") ReportForm reportForm){
        // 投稿をテーブルに格納
        reportService.saveReport(reportForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
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
    public ModelAndView editContent(@PathVariable("contentId") int contentId) {
        ModelAndView mav = new ModelAndView();
        // 画面遷移先を指定
        mav.setViewName("/edit");
        Optional<Report> reportForm = reportService.selectReport(contentId);
        Report optionalReportForm = reportForm.get();
        mav.addObject("formModel", optionalReportForm);
        return mav;
    }

    /*
     * 投稿編集処理
     */
    @PostMapping("/update/{contentId}")
    public ModelAndView updateContent(@PathVariable("contentId") int contentId, @ModelAttribute("formModel") ReportForm reportForm) {
        // 送る用の空のentitty
        reportForm.setId(contentId);
        reportService.saveReport(reportForm);
        return new ModelAndView("redirect:/");
    }

    /*
    * コメント返信処理
     */
    @PostMapping("/comment")
    public ModelAndView commentContent(@RequestParam("comment") String comment, @RequestParam("reportId") int reportId) {
        CommentForm commentForm = new CommentForm();
        commentForm.setComment(comment);
        commentForm.setReportId(reportId);
        commentService.saveComment(commentForm);
        return new ModelAndView("redirect:/");
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

package com.example.forum.service;
import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.CommentRepository;
import com.example.forum.repository.entity.Comment;
import com.example.forum.repository.entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@Setter
public class CommentService {
    @Autowired
    CommentRepository commentRepository;
    public void saveComment(CommentForm commentForm) {
        //このままではだめなので、commentFormからentityのCommentに変更する
        Comment comment = setCommentEntity(commentForm);
        commentRepository.save(comment);
    }

    public Comment setCommentEntity(CommentForm commentForm) {
        Comment comment = new Comment();
        comment.setId(commentForm.getId());
        comment.setReportId(commentForm.getReportId());
        comment.setContent(commentForm.getComment());
        return comment;
    }

    /*
     * レコード全件取得処理
     */
    public List<CommentForm> findAllComment() {
        List<Comment> comments = commentRepository.findAllByOrderByIdDesc();
        List<CommentForm> commentForm = setCommentForm(comments);
        return commentForm;
    }
    /*
     * DBから取得したデータをFormに設定
     */
    private List<CommentForm> setCommentForm(List<Comment> results) {
        List<CommentForm> comments = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            CommentForm comment = new CommentForm();
            Comment result = results.get(i);
            comment.setId(result.getId());
            comment.setComment(result.getContent());
            comment.setReportId(result.getReportId());
            comments.add(comment);
        }
        return comments;
    }

    public CommentForm editComment(int commentId) {
        List<Comment> comments = new ArrayList<>();
        comments.add(commentRepository.findById(commentId).orElse(null));
        List<CommentForm> commentForm = setCommentForm(comments);
        return commentForm.get(0);
    }

    public void deleteComment(int id) {
        commentRepository.deleteById(id);
    }

}

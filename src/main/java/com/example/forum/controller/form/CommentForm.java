package com.example.forum.controller.form;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {
    private int id;
    private int reportId;

    @NotBlank(message = "コメント内容を入力してください")
    private String comment;
}

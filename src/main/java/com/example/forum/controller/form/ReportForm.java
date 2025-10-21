package com.example.forum.controller.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReportForm {
    private int id;

    @NotBlank(message = "投稿内容を入力してください") //これ単体では動かない　エラーの基準、チェックの基準だけを与える
    private String content;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}

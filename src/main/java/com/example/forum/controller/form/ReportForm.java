package com.example.forum.controller.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReportForm {
    private int id;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}

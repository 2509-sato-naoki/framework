package com.example.forum.controller.form;

import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReportForm {
    private int id;

    @NotNull
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}

package com.palette.common;

import lombok.Getter;
import lombok.Setter;

/**
 * TODO: valid 혹은 default 값 명시 필요
 */
@Getter
@Setter
public class PageInput {

    private Integer diaryOffset;
    private Integer diarySize;
    private Integer historyOffset;
    private Integer historySize;
    private Integer pageOffset;
    private Integer pageSize;

}

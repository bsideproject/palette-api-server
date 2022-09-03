package com.palette.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageInput {

    private Integer page;
    private Integer size;
    private Integer skip;
}

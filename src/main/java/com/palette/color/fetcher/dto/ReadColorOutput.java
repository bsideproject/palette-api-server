package com.palette.color.fetcher.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReadColorOutput {

    private Long id;
    private String startCode;
    private String endCode;
    private Integer order;

}

package com.palette.diary;

import com.palette.resolver.LoginUser;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DiaryFetcherDto {

    private Integer pageOffset;
    private Integer pageSize;
    private Integer historyOffset;
    private Integer historySize;
    private LoginUser loginUser;
}

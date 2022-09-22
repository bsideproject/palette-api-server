package com.palette.diary.fetcher.dto;

import com.palette.diary.domain.Page;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PastHistory {

    private List<Page> pages;
}

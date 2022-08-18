package com.palette.color.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.palette.color.domain.Color;
import com.palette.color.fetcher.dto.ReadColorOutput;
import com.palette.color.repository.ColorRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ColorFetcher {

    private final ColorRepository colorRepository;

    @DgsQuery(field = "color")
    public List<ReadColorOutput> findColor() {
        List<Color> colors = colorRepository.findAll(Sort.by(Direction.ASC, "order"));
        return colors.stream()
            .map(color -> ReadColorOutput.builder()
                .id(color.getId())
                .startCode(color.getStartCode())
                .endCode(color.getEndCode())
                .order(color.getOrder())
                .build()
            )
            .collect(Collectors.toList());
    }

}

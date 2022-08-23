package com.palette.infra.fcm;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Note {
    private String title;
    private String body;

    @Builder.Default
    private Map<String, String> data = new HashMap<>();
}

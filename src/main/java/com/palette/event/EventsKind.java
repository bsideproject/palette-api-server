package com.palette.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventsKind {
    CREATE_DIARY,
    CREATE_HISTORY,
    CREATE_PAGE,
    OUT_DIARY
}

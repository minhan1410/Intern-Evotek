package com.example.demo.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortEnum {
    ASC("asc"), DESC("desc");
    private final String value;
}

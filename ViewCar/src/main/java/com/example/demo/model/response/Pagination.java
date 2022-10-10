package com.example.demo.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Pagination {
    private int page;

    @JsonProperty("page_size")
    private int pageSize;

    @JsonProperty("total_page")
    private int totalPage;

    @JsonProperty("total_elements")
    private long totalElements;

}

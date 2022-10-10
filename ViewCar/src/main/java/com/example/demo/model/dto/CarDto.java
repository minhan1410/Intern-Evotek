package com.example.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CarDto {
    private Long id;

    @NotBlank(message = "ten khong dc de trong")
    private String name;

    @NotBlank(message = "engine khong dc de trong")
    private String engine;

    @JsonProperty("count_View")
    @Min(value = 1, message = "countView k duoi 1")
    private Integer countView;

    @JsonProperty("product_launch_date")
    private LocalDateTime productLaunchDate;

    private String img;
}

package com.example.demo.model.entity;

import com.example.demo.constants.StatusEnum;
import com.example.demo.model.dto.CarDto;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "car")
public class CarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true)
    private String name;

    private String engine;

//    @Column(unique = true)
    private String img;

    @Column(columnDefinition = "integer default 0")
    private Integer countView;

    private StatusEnum status;

    private LocalDateTime productLaunchDate;

    public CarEntity mapperDto(CarDto carDto) {
        this.name = carDto.getName();
        this.engine = carDto.getEngine();
//        this.img = carDto.getImg();
        this.productLaunchDate = carDto.getProductLaunchDate();

        return this;
    }
}

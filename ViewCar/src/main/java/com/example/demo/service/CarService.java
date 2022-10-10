package com.example.demo.service;

import com.example.demo.constants.SortEnum;
import com.example.demo.model.dto.CarDto;
import com.example.demo.model.response.Data;
import com.example.demo.model.response.ListData;
import org.springframework.web.multipart.MultipartFile;

public interface CarService {
    ListData findAllByStatusIsTrue(int page, int pageSize);

    ListData findByNameContaining(String name, SortEnum sortEnum, int page, int pageSize);

    ListData findByEngine(String engine, SortEnum sortEnum, int page, int pageSize);

    ListData sortCountView(SortEnum sortEnum, int page, int pageSize);

    ListData topCountView(int top);

    Data add(CarDto carDto, MultipartFile multipartFile);

    Data update(CarDto carDto, MultipartFile multipartFile);

    Data delete(Long id);

    Data countClick(Long id);

    Data registerNotice(Long id);

}

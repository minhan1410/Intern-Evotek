package com.example.demo.controller;

import com.example.demo.constants.SortEnum;
import com.example.demo.model.dto.CarDto;
import com.example.demo.model.response.Data;
import com.example.demo.model.response.ListData;
import com.example.demo.service.CarService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

@RestController
public class CarController {

    private final CarService carService;
    private final Bucket bucket;

    public CarController(CarService carService) {
//      Đối với giới hạn tốc độ là 5 yêu cầu mỗi phút, chúng tôi sẽ tạo một nhóm có dung lượng 5 và tốc độ nạp lại là 5 mã thông báo mỗi phút
        this.carService = carService;

        Bandwidth limit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));
        this.bucket = Bucket4j.builder().addLimit(limit).build();
    }

    @GetMapping("/cars")
    public ResponseEntity<ListData> findAllByStatusIsTrue(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(carService.findAllByStatusIsTrue(page, pageSize));
    }

    @GetMapping("/cars/find_name")
    public ResponseEntity<ListData> findByNameContaining(@RequestParam(defaultValue = "") String name,
                                                         @RequestParam(defaultValue = "ASC") SortEnum sortEnum,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(carService.findByNameContaining(name, sortEnum, page, pageSize));
    }

    @GetMapping("/cars/findBy_engine")
    public ResponseEntity<ListData> findByEngine(@RequestParam(defaultValue = "") String engine,
                                                 @RequestParam(defaultValue = "ASC") SortEnum sortEnum,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(carService.findByEngine(engine, sortEnum, page, pageSize));
    }

    @GetMapping("/cars/sort_count_view")
    public ResponseEntity<ListData> sortCountView(@RequestParam(defaultValue = "ASC") SortEnum sortEnum,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(carService.sortCountView(sortEnum, page, pageSize));
    }

    @GetMapping("/cars/top_count_view")
    public ResponseEntity<ListData> topCountView(@RequestParam(defaultValue = "10") int top) {
        return ResponseEntity.ok(carService.topCountView(top));
    }

//    * trong trường hợp MultipartFile, bạn không thể sử dụng dữ liệu JSON nên bạn không thể sử dụng @RequestBody. Hãy thử với chú thích @ModelAttribute

    @PostMapping("/car")
    public ResponseEntity<Data> add(@ModelAttribute CarDto carDto, @RequestParam(required = false) MultipartFile multipartFile) {
        Data data = carService.add(carDto, multipartFile);
        if (data.isSuccess()) return ResponseEntity.ok(data);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
    }

    @PutMapping("/car")
    public ResponseEntity<Data> update(@ModelAttribute CarDto carDto, @RequestParam(required = false) MultipartFile multipartFile) {
        Data data = carService.update(carDto, multipartFile);
        if (data.isSuccess()) return ResponseEntity.ok(data);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
    }

    @DeleteMapping("/car/{id}")
    public ResponseEntity<Data> delete(@PathVariable Long id) {
        Data data = carService.delete(id);
        if (data.isSuccess()) return ResponseEntity.ok(data);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
    }

    @GetMapping("/count_click/{id}")
    public ResponseEntity<Data> countClick(@PathVariable Long id) {
//       * Chỉ tính 5 lần 1 phút
        if (bucket.tryConsume(1)) return ResponseEntity.ok(carService.countClick(id));
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @GetMapping("/register_notice/{id}")
    public ResponseEntity<Data> registerNotice(@PathVariable Long id) {
        Data data = carService.registerNotice(id);
        if (data.isSuccess()) return ResponseEntity.ok(data);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
    }

}

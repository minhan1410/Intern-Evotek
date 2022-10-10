package com.example.demo.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.constants.RoleEnum;
import com.example.demo.constants.SortEnum;
import com.example.demo.constants.StatusEnum;
import com.example.demo.model.CustomUserDetails;
import com.example.demo.model.dto.CarDto;
import com.example.demo.model.entity.CarEntity;
import com.example.demo.model.entity.UserEntity;
import com.example.demo.model.response.Data;
import com.example.demo.model.response.ListData;
import com.example.demo.model.response.Pagination;
import com.example.demo.repository.CarRepository;
import com.example.demo.service.CarService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;
    private final Scheduler scheduler;
    private final MailServiceImpl mailService;

    private final Cloudinary cloudinary;

    public CarServiceImpl(CarRepository carRepository, ModelMapper modelMapper, Scheduler scheduler, MailServiceImpl mailService, Cloudinary cloudinary) {
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
        this.scheduler = scheduler;
        this.mailService = mailService;
        this.cloudinary = cloudinary;
    }

    @Override
    public ListData findAllByStatusIsTrue(int page, int pageSize) {
        Page<CarEntity> carEntities = carRepository.findAllByStatus(StatusEnum.ACTIVE, PageRequest.of(page, pageSize));

        return new ListData(true, "find all success",
                new Pagination(carEntities.getNumber(), carEntities.getSize(), carEntities.getTotalPages(), carEntities.getTotalElements()),
                carEntities.stream().map(carEntity -> modelMapper.map(carEntity, CarDto.class)).toList()
        );
    }

    @Override
    public ListData findByNameContaining(String name, SortEnum sortEnum, int page, int pageSize) {
        Page<CarEntity> carEntities = carRepository.findByNameContainingAndStatus(name, StatusEnum.ACTIVE,
                PageRequest.of(page, pageSize, sortByFiled("name", sortEnum)));

        return new ListData(true, "find by name containing success",
                new Pagination(carEntities.getNumber(), carEntities.getSize(), carEntities.getTotalPages(), carEntities.getTotalElements()),
                carEntities.stream().map(carEntity -> modelMapper.map(carEntity, CarDto.class)).toList()
        );
    }

    @Override
    public ListData findByEngine(String engine, SortEnum sortEnum, int page, int pageSize) {
        Page<CarEntity> carEntities = carRepository.findByEngineAndStatus(engine, StatusEnum.ACTIVE,
                PageRequest.of(page, pageSize, sortByFiled("name", sortEnum)));

        return new ListData(true, "findByEngineSortByName success",
                new Pagination(carEntities.getNumber(), carEntities.getSize(), carEntities.getTotalPages(), carEntities.getTotalElements()),
                carEntities.stream().map(carEntity -> modelMapper.map(carEntity, CarDto.class)).toList()
        );
    }

    @Override
    public ListData sortCountView(SortEnum sortEnum, int page, int pageSize) {
        Page<CarEntity> carEntities = carRepository.findAllByStatus(StatusEnum.ACTIVE,
                PageRequest.of(page, pageSize, sortByFiled("countView", sortEnum)));

        return new ListData(true, "sortCountView success",
                new Pagination(carEntities.getNumber(), carEntities.getSize(), carEntities.getTotalPages(), carEntities.getTotalElements()),
                carEntities.stream().map(carEntity -> modelMapper.map(carEntity, CarDto.class)).toList()
        );
    }

    @Override
    public ListData topCountView(int top) {
        Page<CarEntity> carEntities = carRepository.findAllByStatus(StatusEnum.ACTIVE, PageRequest.of(0, top, sortByFiled("countView", SortEnum.DESC)));

        return new ListData(true, "topCountView success",
                new Pagination(carEntities.getNumber(), carEntities.getSize(), carEntities.getTotalPages(), carEntities.getTotalElements()),
                carEntities.stream().map(carEntity -> modelMapper.map(carEntity, CarDto.class)).toList()
        );
    }

    @Override
    public Data add(CarDto carDto, MultipartFile multipartFile) {
        String imgUrl = getImageUrl(multipartFile);

        carDto.setId(null);
        if (checkNameAlreadyExist(carDto.getName(), carDto.getId())) return new Data(false, "name already exist", null);
//        if (checkImgAlreadyExist(imgUrl, carDto.getId())) return new Data(false, "img already exist", null);

        CarEntity carEntity = new CarEntity().mapperDto(carDto);
        carEntity.setCountView(0);
        carEntity.setStatus(StatusEnum.ACTIVE);
        carEntity.setImg(imgUrl);

        return new Data(true, "add success", modelMapper.map(carRepository.save(carEntity), CarDto.class));
    }

    @Override
    public Data update(CarDto carDto, MultipartFile multipartFile) {
        Optional<CarEntity> optionalCarEntity = carRepository.findById(carDto.getId());
        if (optionalCarEntity.isEmpty()) return new Data(false, "id not found", null);

//        String imgUrl = getImageUrl(multipartFile);
        CarEntity carEntity = optionalCarEntity.get();

        if (checkNameAlreadyExist(carDto.getName(), carEntity.getId()))
            return new Data(false, "name already exist", null);
//        if (checkImgAlreadyExist(imgUrl, carEntity.getId())) return new Data(false, "img already exist", null);

        carEntity.mapperDto(carDto);
        carEntity.setImg(getImageUrl(multipartFile));

        return new Data(true, "update success", modelMapper.map(carRepository.save(carEntity), CarDto.class));
    }

    @Override
    public Data delete(Long id) {
        Optional<CarEntity> optionalCarEntity = carRepository.findById(id);
        if (optionalCarEntity.isEmpty()) return new Data(false, "id not found", null);

        CarEntity carEntity = optionalCarEntity.get();
        carEntity.setStatus(StatusEnum.INACTIVE);
        return new Data(true, "delete success", modelMapper.map(carRepository.save(carEntity), CarDto.class));
    }

    @Override
    public Data countClick(Long id) {
        Optional<CarEntity> optionalCarEntity = carRepository.findById(id);
        if (optionalCarEntity.isEmpty()) return new Data(false, "id not found", null);
        CarEntity carEntity = optionalCarEntity.get();

        Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (o instanceof String || ((CustomUserDetails) o).getUser().getRole().equals(RoleEnum.USER)) {
            carEntity.setCountView(carEntity.getCountView() + 1);
            return new Data(true, "countClick success", modelMapper.map(carRepository.save(carEntity), CarDto.class));
        }

        return new Data(false, "admin k tinh view", null);
    }

    @Override
    public Data registerNotice(Long id) {
        Optional<CarEntity> optionalCarEntity = carRepository.findById(id);
        if (optionalCarEntity.isEmpty()) return new Data(false, "id not found", null);

        Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity userEntity = ((CustomUserDetails) o).getUser();

        CarEntity carEntity = optionalCarEntity.get();
        return productLaunchDate(carEntity, userEntity);
    }

    private Data productLaunchDate(CarEntity carEntity, UserEntity userEntity) {
        try {
            ZonedDateTime dateTime = ZonedDateTime.of(carEntity.getProductLaunchDate(), ZoneId.of("Asia/Saigon"));
            if (dateTime.isBefore(ZonedDateTime.now()))
                return new Data(false, "has passed the product launch date", null);

            JobDetail jobDetail = mailService.buildJobDetail(userEntity.getEmail(), userEntity.getUsername(), carEntity.getName());
            Trigger trigger = mailService.buildJobTrigger(jobDetail, dateTime);
            scheduler.scheduleJob(jobDetail, trigger);

            return new Data(true, "Email Scheduled Successfully!", null);
        } catch (SchedulerException e) {
            log.error("Error scheduling email", e);
            return new Data(false, "Error scheduling email. Please try later!", null);
        }
    }

    private Sort sortByFiled(String field, SortEnum sortEnum) {
        if (sortEnum.name().equalsIgnoreCase("ASC"))
            return Sort.by(field).ascending();

        return Sort.by(field).descending();
    }

    private boolean checkNameAlreadyExist(String name, Long id) {
        Optional<CarEntity> optionalCarEntity = carRepository.findByName(name);
        return optionalCarEntity.isPresent() && !optionalCarEntity.get().getId().equals(id);
    }

    private boolean checkImgAlreadyExist(String img, Long id) {
        Optional<CarEntity> optionalCarEntity = carRepository.findByImg(img);
        return optionalCarEntity.isPresent() && !optionalCarEntity.get().getId().equals(id);
    }

    private String getImageUrl(MultipartFile multipartFile) {
        if (multipartFile == null)
            return "https://res.cloudinary.com/dpvehgfmo/image/upload/v1664851327/nightfury5387113c0adc6_bik2si.png";
        try {
            Map map = this.cloudinary.uploader().upload(multipartFile.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
            return map.get("url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Upload fail");
        }
    }

}

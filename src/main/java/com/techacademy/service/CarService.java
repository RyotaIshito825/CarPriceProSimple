package com.techacademy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.techacademy.entity.Car;
import com.techacademy.repository.CarRepository;

@Service
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }


    // 車両一覧表示
    public List<Car> findAll() {
        return carRepository.findAll();
    }
}

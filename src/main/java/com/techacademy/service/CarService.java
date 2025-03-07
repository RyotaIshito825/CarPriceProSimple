package com.techacademy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
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

    // 車両1件取得
    public Car findById(Integer id) {
        // findByIdで検索
        Optional<Car> option = carRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Car car = option.orElse(null);
        return car;
    }

    // 車両情報更新
    @Transactional
    public ErrorKinds updateCar(Car car) {
        Car c = findById(car.getId());

        // メーカー桁数チェック
        if (car.getMaker().length() > 20) {
            return ErrorKinds.TEXT_RANGECHECK_ERROR;
        }
        // 車種桁数チェックエラー
        if (car.getMaker().length() > 20) {
            return ErrorKinds.TEXT_RANGECHECK_ERROR;
        }
        // グレード桁数チェックエラー
        if (car.getGrade().length() > 20) {
            return ErrorKinds.TEXT_RANGECHECK_ERROR;
        }
        return ErrorKinds.SUCCESS;
    }

    // 車両更新登録
    public void updateCarSave(Car car) {
        carRepository.save(car);
    }

    // 車両新規登録
    @Transactional
    public ErrorKinds createCar(Car car) {
     // メーカー桁数チェック
        if (car.getMaker().length() > 20) {
            return ErrorKinds.TEXT_RANGECHECK_ERROR;
        }
        // 車種桁数チェックエラー
        if (car.getMaker().length() > 20) {
            return ErrorKinds.TEXT_RANGECHECK_ERROR;
        }
        // グレード桁数チェックエラー
        if (car.getGrade().length() > 20) {
            return ErrorKinds.TEXT_RANGECHECK_ERROR;
        }
        return ErrorKinds.SUCCESS;
    }
    // 車両新規登録処理
    public void createCarSave(Car car) {
        carRepository.save(car);
    }

}

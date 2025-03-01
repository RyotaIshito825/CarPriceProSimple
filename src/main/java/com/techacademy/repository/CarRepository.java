package com.techacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Car;

public interface CarRepository extends JpaRepository<Car, Integer>{


}

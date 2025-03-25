package com.techacademy.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techacademy.entity.Car;

public interface CarRepository extends JpaRepository<Car, Integer>{

    @Query("SELECT c FROM Car c WHERE (c.maker LIKE %:keyword% OR c.carModel LIKE %:keyword% OR c.grade LIKE %:keyword%) AND c.price BETWEEN :minPrice AND :maxPrice")
    Page<Car> searchByKeyword(@Param("keyword") String keyword, @Param("minPrice") int minPrice, @Param("maxPrice") int maxPrice, Pageable pageable);

    Page<Car> findAll(Pageable pageble);
}

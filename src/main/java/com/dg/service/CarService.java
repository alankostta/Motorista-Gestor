package com.dg.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dg.dto.CarDTO;
import com.dg.model.Car;
import com.dg.model.User;
import com.dg.repository.CarRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class CarService {

    @Autowired
    private CarRepository carRepository;

    public List<Car> findAllByUser(Long userId) {
    	List<Car> cars = carRepository.findByUserIdOrderByCreatedDateDesc(userId);
    	return cars;
    }
    
    public Car save(CarDTO carDto, User user) {
        Car car = new Car();
        if (carDto.getId() != null) {
            car = carRepository.findByIdAndUserId(carDto.getId(), user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Carro não encontrado"));
        }
        
        car.setBrand(carDto.getBrand());
        car.setModel(carDto.getModel());
        car.setYear(carDto.getYear());
        car.setLicensePlate(carDto.getLicensePlate());
        car.setFuelType(carDto.getFuelType());
        car.setAvgConsumption(carDto.getAvgConsumption());
        car.setUser(user);
        
        return carRepository.save(car);
    }
    
    public Car findByIdAndUser(Long id, Long userId) {
        return carRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new EntityNotFoundException("Carro não encontrado"));
    }
    
    public void deleteByIdAndUser(Long id, Long userId) {
        carRepository.deleteByIdAndUserId(id, userId);
    }
    
    public Long countByUser(Long userId) {
        return carRepository.countByUserId(userId);
    }
    public Long countByCars(Long userId) {
    	return carRepository.countByCarsId(userId);
    }
}

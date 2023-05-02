package com.api.parkingcontrol.controller;

import com.api.parkingcontrol.DTO.ParkingSpotDTO;
import com.api.parkingcontrol.model.ParkingSpotModel;
import com.api.parkingcontrol.services.ParkingSpotService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins="*", maxAge=3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {

    @Autowired
    private ParkingSpotService parkingSpotService;




    @GetMapping("/find-all")
    public ResponseEntity<Object> getAllParkingSpot(){
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll());
    }
    @GetMapping("/find-byId/{id}")
    public ResponseEntity<Object> getParkingSpotById(@PathVariable(value="id") UUID id){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if(!parkingSpotModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll());
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateParkingSpot(@PathVariable(value="id") UUID id,@RequestBody @Valid ParkingSpotDTO parkingSpotDTO ){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (!parkingSpotModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
        }
        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDTO, parkingSpotModel);
        parkingSpotModel.setId(parkingSpotModelOptional.get().getId());
        parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate());
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));

    };
    @PostMapping("/save")
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDTO parkingSpotDTO){

        if(parkingSpotService.existsByLicensePlateCar(parkingSpotDTO.getLicensePlateCar())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is already in use!");
        }
        if(parkingSpotService.existsByParkingSpotNumber(parkingSpotDTO.getParkingSpotNumber())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot is already in use!");
        }
        if(parkingSpotService.existsByApartmentAndBlock(parkingSpotDTO.getApartment(), parkingSpotDTO.getBlock())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot already registered for this apartment/block!");
        }
        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDTO,parkingSpotModel);
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
    }
   @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteParkingSpotById(@PathVariable(value="id") UUID id){
       Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
       if(!parkingSpotModelOptional.isPresent()){
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found!");
       }
       return ResponseEntity.status(HttpStatus.OK).body("Parking Spot deleted successfully.");
   };


};

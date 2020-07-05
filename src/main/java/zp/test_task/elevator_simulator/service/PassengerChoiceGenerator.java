package zp.test_task.elevator_simulator.service;

import org.springframework.stereotype.Service;
import zp.test_task.elevator_simulator.models.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static zp.test_task.elevator_simulator.models.Direction.*;

@Service
public class PassengerChoiceGenerator {

    public int getRandomInt(int min, int max) {
        if (min < 0 || min > max)
            return 0;
        max++;
        return (int) (Math.random() * (max - min)) + min;
    }

    public int passengersChoiceFloor(int currentFloor, Building building) {
        int quantityFloorsAtBuilding = building.getQUANTITY_LEVELS_BUILDING();
        if (currentFloor < 0 || currentFloor > quantityFloorsAtBuilding)
            return 0;
        int selectedFloor = getRandomInt(1, quantityFloorsAtBuilding);

        while (currentFloor == selectedFloor)
            selectedFloor = getRandomInt(1, quantityFloorsAtBuilding);

        return selectedFloor;
    }

    public Direction selectDirectional(int selectedFloor, int currentFloor) {
        return selectedFloor > currentFloor ? UP : DOWN;
    }

    public void makeChoiceDirectionalAtFloorAfterElevatorGone(Floor floor) {
        if (floor.getPassengers().size() == 0) {
            floor.setDirection(Collections.singletonList(NOT_CHOSEN));
            return;
        }
        List<Direction> directionsPassengersAtFloor = floor.getPassengers()
                .stream()
                .map(Passenger::getDirection)
                .distinct()
                .collect(Collectors.toList());

        floor.setDirection(directionsPassengersAtFloor);
    }

    public void makeChoiceDirectionalFloorAtBuilding(Building building) {
        List<Floor> floors = building.getFloors();

        for (Floor floor : floors) {
            if (floor.getPassengers().size() == 0) {
                floor.setDirection(Collections.singletonList(NOT_CHOSEN));
            } else {
                List<Direction> directionsPassengersAtFloor = floor.getPassengers()
                        .stream()
                        .map(Passenger::getDirection)
                        .distinct()
                        .collect(Collectors.toList());
                floor.setDirection(directionsPassengersAtFloor);
            }
        }
        building.setFloors(floors);
    }

    public Direction choicePriorityDirectionOfPassengers(Floor floor) {
        long countUp = floor.getPassengers().stream().filter(f -> UP.equals(f.getDirection())).count();
        long countDown = floor.getPassengers().stream().filter(p -> DOWN.equals(p.getDirection())).count();
        return countUp > countDown ?
                UP : DOWN;
    }
}

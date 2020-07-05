package zp.test_task.elevator_simulator.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import zp.test_task.elevator_simulator.models.*;

import java.util.*;

import static zp.test_task.elevator_simulator.models.Direction.*;

@Service
@Data
@AllArgsConstructor
public class CreateTestDataService {

    private PassengerChoiceGenerator choiceGenerator;

    public Building createBuilding() {
        Building building = Building.builder()
                .QUANTITY_LEVELS_BUILDING(choiceGenerator.getRandomInt(5, 20))
                .elevator(Elevator.builder()
                        .maxSelectedFloor(0)
                        .direction(NOT_CHOSEN)
                        .currentFloor(1)
                        .passengers(new LinkedList<>()).build())
                .floors(Collections.EMPTY_LIST)
                .build();
        building.setFloors(createFloors(building.getQUANTITY_LEVELS_BUILDING(), building));
        choiceGenerator.makeChoiceDirectionalFloorAtBuilding(building);
        return building;
    }

    private List<Floor> createFloors(int quantityFloorsAtBuilding, Building building) {
        List<Floor> temp = new ArrayList<>();
        for (int n = 1; n <= quantityFloorsAtBuilding; n++) {

            temp.add(
                    Floor.builder()
                            .numberOfFloor(n)
                            .passengers(createPassengers(n, building))
                            .direction(Collections.singletonList(NOT_CHOSEN))
                            .build());
        }
        return temp;
    }

    private Queue<Passenger> createPassengers(int currentFloor, Building building) {
        Queue<Passenger> temp = new LinkedList<>();
        int tempSelectedFloor;
        for (int i = 0; i < choiceGenerator.getRandomInt(0, 10); i++) {
            tempSelectedFloor = choiceGenerator.passengersChoiceFloor(currentFloor, building);
            temp.add(
                    Passenger.builder()
                            .currentFloor(currentFloor)
                            .selectedFloor(tempSelectedFloor)
                            .direction(choiceGenerator.selectDirectional(tempSelectedFloor, currentFloor))
                            .build());
        }
        return temp;
    }
}

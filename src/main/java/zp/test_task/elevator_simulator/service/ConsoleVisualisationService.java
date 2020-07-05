package zp.test_task.elevator_simulator.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import zp.test_task.elevator_simulator.models.Building;
import zp.test_task.elevator_simulator.models.Direction;
import zp.test_task.elevator_simulator.models.Elevator;
import zp.test_task.elevator_simulator.models.Passenger;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import static zp.test_task.elevator_simulator.models.Direction.NOT_CHOSEN;
import static zp.test_task.elevator_simulator.models.Direction.UP;

@Service
public class ConsoleVisualisationService {

    public void consoleVisualisation(Building building, int countStep) {
        int quantityLevelsAtBuilding = building.getQUANTITY_LEVELS_BUILDING();
        int indexFloor = quantityLevelsAtBuilding - 1;
        int numberFloor = quantityLevelsAtBuilding;

        Elevator elevator = building.getElevator();
        Queue<Passenger> passengersInElevator = elevator.getPassengers();

        System.out.println("\t\t*** STEP " + countStep + " ***");

        for (int i = quantityLevelsAtBuilding; i > 0; i--) {

            long quantityPassengersAtFloor = building.getFloors().get(indexFloor).getPassengers().size();

            List<Integer> integers = building.getFloors().get(indexFloor).getPassengers().stream().map(Passenger::getSelectedFloor).collect(Collectors.toList());

            List<Direction> directionsAtFloor = new ArrayList<>(building.getFloors().get(indexFloor).getDirection());

            System.out.print(numberFloor + ")\t" + quantityPassengersAtFloor + "  " + "|\t\t\t");

            if (elevator.getCurrentFloor() == numberFloor) {

                String directionalElevator = UP.equals(elevator.getDirection()) ? "^" : "v";
                if (NOT_CHOSEN.equals(elevator.getDirection()))
                    directionalElevator = "N";

                if (passengersInElevator.size() > 0) {
                    System.out.print("\b\b\b" + directionalElevator
                            + passengersInElevator.stream().map(Passenger::getSelectedFloor).collect(Collectors.toList()).toString()
                            + directionalElevator + "\t");

                } else {
                    System.out.print("\b\b\b" + directionalElevator + "[EMPTY]" + directionalElevator + "\t");
                }
            }
            System.out.print("\b\t| " + integers.toString() + directionsAtFloor.toString() + "\n");
            numberFloor--;
            indexFloor--;
        }
    }
}

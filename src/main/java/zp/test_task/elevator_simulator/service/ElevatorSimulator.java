package zp.test_task.elevator_simulator.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import zp.test_task.elevator_simulator.models.*;

import java.util.*;
import java.util.stream.Collectors;

import static zp.test_task.elevator_simulator.models.Direction.*;
import static zp.test_task.elevator_simulator.models.Elevator.*;

@Service
@AllArgsConstructor
public class ElevatorSimulator {
    private ConsoleVisualisationService consoleService;
    private PassengerChoiceGenerator choiceGenerator;
    private static final Logger LOGGER = LoggerFactory.getLogger(ElevatorSimulator.class);

    public Boolean elevatorStartWork(int quantityIteration, Building building) {
        if (quantityIteration < 1 || building == null) {
            LOGGER.error("Incorrect data in start simulation. Quantity must be natural number more then 0 - " + quantityIteration);
            return false;
        }
        if (building.getFloors().isEmpty()) {
            LOGGER.error("In Building not create the floors" + building.getFloors());
            return false;
        }

        Elevator elevator = building.getElevator();
        List<Floor> floors = building.getFloors();
        int count = 1;

        while (count <= quantityIteration) {
            consoleService.consoleVisualisation(building, count);
            callElevator(building);
            loadingPassengers(building);
            elevatingAtNextFloor(elevator, floors);
            unloadingPassengers(building);
            count++;
        }
        return true;
    }

    private void callElevator(Building building) {
        Elevator elevator = building.getElevator();
        List<Floor> floors = building.getFloors();
        int quantityFloorsAtBuilding = building.getQUANTITY_LEVELS_BUILDING();
        if (!NOT_CHOSEN.equals(elevator.getDirection()))
            return;

        int indexCurrentFloor = currentFloorByCurrentElevatorFloor(elevator.getCurrentFloor());
        int incrementFloor = indexCurrentFloor;
        int decrementFloor = indexCurrentFloor - 1;

        for (int i = 0; i < quantityFloorsAtBuilding; i++) {

            if (incrementFloor < quantityFloorsAtBuilding) {
                if (floors.get(incrementFloor).getDirection().stream().noneMatch(NOT_CHOSEN::equals)) {
                    elevator.setCurrentFloor(floors.get(incrementFloor).getNumberOfFloor());
                    return;
                }
                incrementFloor++;
            }

            if (decrementFloor > -1) {
                if (floors.get(decrementFloor).getDirection().stream().noneMatch(NOT_CHOSEN::equals)) {
                    elevator.setCurrentFloor(floors.get(decrementFloor).getNumberOfFloor());
                    return;
                }
                decrementFloor--;
            }
        }
    }

    private void loadingPassengers(Building building) {
        Queue<Passenger> passengersAddToElevator, passengersToSort, finalSortingQueueOfPassengers;
        Elevator elevator = building.getElevator();
        List<Floor> floors = building.getFloors();

        int currentNumberFloor = currentFloorByCurrentElevatorFloor(elevator.getCurrentFloor());
        Floor currentFloor = floors.get(currentNumberFloor);
        long freePlacesInElevator = MAX_PASSENGERS - elevator.getPassengers().size();
        int quantityPassengersAtFloor = currentFloor.getPassengers().size();

        Direction directionElevator = NOT_CHOSEN.equals(elevator.getDirection())
                ? choiceGenerator.choicePriorityDirectionOfPassengers(currentFloor)
                : elevator.getDirection();
        elevator.setDirection(directionElevator);
        boolean isCoincidenceDirectional = currentFloor.getDirection().stream()
                .anyMatch(directionElevator::equals);

        if (freePlacesInElevator == 0 || quantityPassengersAtFloor == 0 || !isCoincidenceDirectional) {
            return;
        }

        passengersAddToElevator = pollPassengersFromFloorToElevator(currentFloor.getPassengers(), building
                , currentNumberFloor, freePlacesInElevator, directionElevator);

        elevator.getPassengers().addAll(passengersAddToElevator);
        passengersToSort = elevator.getPassengers();
        finalSortingQueueOfPassengers = sortingQueueByDirectionalWay(passengersToSort, directionElevator);
        elevator.setPassengers(finalSortingQueueOfPassengers);

        choiceGenerator.makeChoiceDirectionalAtFloorAfterElevatorGone(currentFloor);
    }

    private void elevatingAtNextFloor(Elevator elevator, List<Floor> floors) {
        if (elevator.getPassengers().size() != MAX_PASSENGERS) {
            movingIncompleteElevator(elevator, floors);
        } else {
            movingFullElevator(elevator);
        }
    }

    private void unloadingPassengers(Building building) {
        Elevator elevator = building.getElevator();
        List<Floor> floors = building.getFloors();
        if (elevator.getPassengers().size() == 0) {
            return;
        }
        Passenger passenger;
        boolean isElevatorHavePassengers = true;
        Floor floorWhereArrivalPassengers = floors.get(currentFloorByCurrentElevatorFloor(elevator.getCurrentFloor()));

        while (isElevatorHavePassengers && elevator.getPassengers().element().getSelectedFloor() == floorWhereArrivalPassengers.getNumberOfFloor()) {
            passenger = elevator.getPassengers().remove();
            passenger.setCurrentFloor(elevator.getCurrentFloor());
            passenger.setSelectedFloor(choiceGenerator.passengersChoiceFloor(passenger.getCurrentFloor(), building));
            passenger.setDirection(choiceGenerator.selectDirectional(passenger.getSelectedFloor(), passenger.getCurrentFloor()));
            floorWhereArrivalPassengers.getPassengers().add(passenger);

            if (elevator.getPassengers().size() == 0) {
                elevator.setDirection(NOT_CHOSEN);
                isElevatorHavePassengers = false;
            }
        }
        choiceGenerator.makeChoiceDirectionalAtFloorAfterElevatorGone(floorWhereArrivalPassengers);
    }


    private void movingIncompleteElevator(Elevator elevator, List<Floor> floors) {
        Passenger firstPassengerToUnloading;
        Floor firstFloorPassengerToUnloading, floorWhichElevatorWillMove;

        if (elevator.getPassengers().size() > 0) {
            firstPassengerToUnloading = elevator.getPassengers().element();
            firstFloorPassengerToUnloading = floors.get(firstPassengerToUnloading.getSelectedFloor() - 1);
        } else {
            return;
        }

        if (UP.equals(elevator.getDirection())) {
            floorWhichElevatorWillMove = floors.stream()
                    .skip(elevator.getCurrentFloor())
                    .filter(floor -> floor.getDirection().stream().anyMatch(UP::equals))
                    .findFirst()
                    .orElse(firstFloorPassengerToUnloading);
            defineNearestFloorWithDirection(UP, floorWhichElevatorWillMove, firstFloorPassengerToUnloading, elevator);
        } else {
            floorWhichElevatorWillMove = floors.stream()
                    .limit(elevator.getCurrentFloor())
                    .filter(floor -> floor.getDirection().stream().anyMatch(DOWN::equals))
                    .max(Comparator.comparing(Floor::getNumberOfFloor))
                    .orElse(firstFloorPassengerToUnloading);
            defineNearestFloorWithDirection(DOWN, floorWhichElevatorWillMove, firstFloorPassengerToUnloading, elevator);
        }
    }

    private void movingFullElevator(Elevator elevator) {
        elevator.setCurrentFloor(elevator.getPassengers().element().getSelectedFloor());
    }

    private int currentFloorByCurrentElevatorFloor(int currentElevatorFloor) {
        return currentElevatorFloor - 1;
    }

    private Queue<Passenger> pollPassengersFromFloorToElevator(Queue<Passenger> queueFromFloor, Building building
            , int currentBuildingFloor, long freePlacesInElevator, Direction directionElevator) {
        if (queueFromFloor == null || freePlacesInElevator == 0 || directionElevator == null) {
            return new LinkedList<>();
        }
        Queue<Passenger> queueToElevator = new LinkedList<>();
        Queue<Passenger> sortedQueue = UP.equals(directionElevator)

                ? queueFromFloor.stream()
                .sorted(Comparator.comparing(Passenger::getDirection).reversed())
                .collect(Collectors.toCollection(LinkedList::new))

                : queueFromFloor.stream()
                .sorted(Comparator.comparing(Passenger::getDirection))
                .collect(Collectors.toCollection(LinkedList::new));

        while (queueToElevator.size() < freePlacesInElevator
                && sortedQueue.size() > 0
                && directionElevator.equals(sortedQueue.element().getDirection())) {
            queueToElevator.add(sortedQueue.remove());
        }

        queueToElevator = queueToElevator.stream()
                .sorted(Comparator.comparing(Passenger::getSelectedFloor))
                .collect(Collectors.toCollection(LinkedList::new));

        building.getFloors().get(currentBuildingFloor).setPassengers(sortedQueue);
        return queueToElevator;
    }

    private Queue<Passenger> sortingQueueByDirectionalWay(Queue<Passenger> passengerQueue, Direction directionElevator) {
        return UP.equals(directionElevator)

                ? passengerQueue.stream()
                .sorted(Comparator.comparing(Passenger::getSelectedFloor))
                .collect(Collectors.toCollection(LinkedList::new))

                : passengerQueue.stream()
                .sorted(Comparator.comparing(Passenger::getSelectedFloor).reversed())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private void defineNearestFloorWithDirection(Direction directionElevator, Floor floorWhichElevatorWillMove, Floor firstFloorPassengerToUnloading, Elevator elevator) {
        if (UP.equals(directionElevator)) {
            elevator.setCurrentFloor(Math.min(floorWhichElevatorWillMove.getNumberOfFloor(), firstFloorPassengerToUnloading.getNumberOfFloor()));
        } else {
            elevator.setCurrentFloor(Math.max(floorWhichElevatorWillMove.getNumberOfFloor(), firstFloorPassengerToUnloading.getNumberOfFloor()));
        }
    }
}

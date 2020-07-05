package zp.test_task.elevator_simulator.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zp.test_task.elevator_simulator.models.Building;
import zp.test_task.elevator_simulator.models.Direction;
import zp.test_task.elevator_simulator.models.Floor;
import zp.test_task.elevator_simulator.models.Passenger;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static zp.test_task.elevator_simulator.models.Direction.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PassengerChoiceGeneratorTest {
    private Building building;
    private Passenger passenger1;
    private Passenger passenger2;
    private Passenger passenger3;
    private Passenger passenger4;
    private Passenger passenger5;
    private Passenger passenger6;

    @Autowired
    CreateTestDataService dataService;
    @Autowired
    private PassengerChoiceGenerator choiceGenerator;

    @BeforeAll
    public void createData() {
        building = dataService.createBuilding();
        passenger1 = Passenger.builder()
                .selectedFloor(0)
                .currentFloor(1)
                .direction(UP).build();
        passenger2 = Passenger.builder()
                .selectedFloor(0)
                .currentFloor(1)
                .direction(UP).build();
        passenger3 = Passenger.builder()
                .selectedFloor(0)
                .currentFloor(1)
                .direction(DOWN).build();
        passenger4 = Passenger.builder()
                .selectedFloor(0)
                .currentFloor(1).direction(UP).build();
        passenger5 = Passenger.builder()
                .selectedFloor(0)
                .currentFloor(1)
                .direction(DOWN).build();
        passenger6 = Passenger.builder()
                .selectedFloor(0)
                .currentFloor(1)
                .direction(DOWN).build();
    }


    @Test
    void getRandomInt() {
        int randomInt;
        for (int i = 0; i < 10000; i++) {
            randomInt = choiceGenerator.getRandomInt(0, 10);
            if (randomInt < 0 | randomInt > 10)
                fail();
        }
    }

    @Test
    void getRandomIntWhenMinLessZeroOrMinMoreMaxThenReturnZero() {
        int minIsNegative = choiceGenerator.getRandomInt(-1, 10);
        ;
        int maxIsZero = choiceGenerator.getRandomInt(5, 0);
        ;

        assertEquals(0, minIsNegative);
        assertEquals(0, maxIsZero);
    }

    @Test
    void passengersChoiceFloorThenNumberFloorBetweenCurrentFloorAndQuantityFloorsInBuilding() {
        int choiceFloorForPassenger;

        for (int i = 0; i < 10000; i++) {
            choiceFloorForPassenger = choiceGenerator.passengersChoiceFloor(1, building);
            if (choiceFloorForPassenger < 1 | building.getQUANTITY_LEVELS_BUILDING() > 20)
                fail();
        }
    }

    @Test
    void selectDirectionalWhenCurrentFloorLessSelectedThenReturnUP() {
        Direction result = choiceGenerator.selectDirectional(5, 2);
        assertEquals(UP, result);
    }

    @Test
    void makeChoiceDirectionalAtFloorWhenPassengersZeroThenFloorDirectionNotChosen() throws Exception {
        Floor floor = Floor.builder()
                .direction(Collections.singletonList(NOT_CHOSEN))
                .numberOfFloor(1)
                .passengers(new LinkedList<>()).build();
        choiceGenerator.makeChoiceDirectionalAtFloorAfterElevatorGone(floor);
        Direction result = floor.getDirection().stream().distinct().findFirst().orElseThrow(Exception::new);
        assertEquals(NOT_CHOSEN, result);

    }

    @Test
    void makeChoiceDirectionalAtFloorWhenPassengersMakeChoiceThenListDirectionsSetToFloor() {
        List<Direction> expectedDirections = List.of(UP, DOWN);
        Floor floor = Floor.builder()
                .direction(Collections.singletonList(NOT_CHOSEN))
                .numberOfFloor(1)
                .passengers(new LinkedList<>(List.of(passenger1, passenger3))).build();

        choiceGenerator.makeChoiceDirectionalAtFloorAfterElevatorGone(floor);

        List<Direction> result = floor.getDirection().stream().distinct().collect(Collectors.toList());

        assertEquals(expectedDirections, result);
    }

    @Test
    void makeChoiceDirectionalFloorAtBuilding() {
        //need only for create test data
    }

    @Test
    void choicePriorityDirectionOfPassengersWhenPriorityDirectionAtFlorIsUpThenReturnUpAndReversed() {


        Floor floorWithUpPriority = Floor.builder()
                .direction(Collections.singletonList(NOT_CHOSEN))
                .numberOfFloor(1)
                .passengers(new LinkedList<>(List.of(passenger1, passenger2, passenger3))).build();

        Direction resultUp = choiceGenerator.choicePriorityDirectionOfPassengers(floorWithUpPriority);


        Floor floorWithDownPriority = Floor.builder()
                .direction(Collections.singletonList(NOT_CHOSEN))
                .numberOfFloor(1)
                .passengers(new LinkedList<>(List.of(passenger4, passenger5, passenger6))).build();

        Direction resultDown = choiceGenerator.choicePriorityDirectionOfPassengers(floorWithDownPriority);

        assertEquals(UP, resultUp);
        assertEquals(DOWN, resultDown);
    }
}
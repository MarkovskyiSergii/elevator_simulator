package zp.test_task.elevator_simulator.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zp.test_task.elevator_simulator.models.*;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ElevatorSimulatorTest {
    @Autowired
    private ElevatorSimulator elevatorSimulator;
    @Autowired
    CreateTestDataService dataService;

    @Test
    void elevatorStartWork() {
        Building building = dataService.createBuilding();
        boolean result = elevatorSimulator.elevatorStartWork(10, building);
        assertTrue(result);
    }

}

package zp.test_task.elevator_simulator;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import zp.test_task.elevator_simulator.models.Building;
import zp.test_task.elevator_simulator.service.CreateTestDataService;
import zp.test_task.elevator_simulator.service.ElevatorSimulator;

@SpringBootApplication
@AllArgsConstructor
public class ElevatorSimulatorApplication {

    private CreateTestDataService createTestData;
    private ElevatorSimulator simulator;

    private static final Logger LOGGER = LoggerFactory.getLogger(ElevatorSimulatorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ElevatorSimulatorApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void run() {

//		Creating random data for checking program
        Building building = createTestData.createBuilding();
        if (building == null) {
            LOGGER.error("Incorrect data in start simulation - Building is not create");
            return;
        }
//		Quantity of iteration must be any natural number
        simulator.elevatorStartWork(100, building);

    }
}

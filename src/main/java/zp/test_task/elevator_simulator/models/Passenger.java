package zp.test_task.elevator_simulator.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Passenger {
    @NonNull
    private Integer selectedFloor;
    @NonNull
    private int currentFloor;
    @NonNull
    private Direction direction;


}

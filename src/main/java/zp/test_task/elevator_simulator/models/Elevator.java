package zp.test_task.elevator_simulator.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Queue;

@Builder
@Data
public class Elevator {
    public final static int MAX_PASSENGERS = 5;
    @NonNull
    private int currentFloor;
    @NonNull
    private Direction direction;
    @NonNull
    private Queue<Passenger> passengers;
    @NonNull
    private int maxSelectedFloor;

}

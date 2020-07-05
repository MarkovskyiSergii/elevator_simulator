package zp.test_task.elevator_simulator.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.Queue;

@Data
@Builder
public class Floor {
    @NonNull
    private List<Direction> direction;
    @NonNull
    private int numberOfFloor;
    @NonNull
    private Queue<Passenger> passengers;

}

package zp.test_task.elevator_simulator.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;


@Data
@Builder
public class Building {
    @NonNull
    public final int QUANTITY_LEVELS_BUILDING;
    @NonNull
    private List<Floor> floors;
    @NonNull
    private Elevator elevator;


}

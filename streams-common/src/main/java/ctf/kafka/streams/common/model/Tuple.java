package ctf.kafka.streams.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Tuple<T, U> {

    private T value1;
    private U value2;

}

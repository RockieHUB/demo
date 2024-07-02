package taskdua.demo.object;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chicken {
    private UUID chickenId;

    private ZonedDateTime chickenLayEggDateTime;
    private Integer eggQuantity;
    private ZonedDateTime chickenEggExpireDateTime;
    private Double price;
}

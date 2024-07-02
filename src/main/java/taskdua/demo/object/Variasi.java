package taskdua.demo.object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Variasi {
    private Double price;
    private Integer quantityAvailable;
}

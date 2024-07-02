package taskdua.demo.object;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reserve {
    private UUID reserveId;

    private String customerName;
    private LocalDate reservedDateTime;
    private String reservedDay;
    private LocalDate actualReservedDateTime;
    private String actualReservedDay;
    private Integer reserveSeat;
    private BigDecimal price;
}

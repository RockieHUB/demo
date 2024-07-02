package taskdua.demo.object;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tickets {
    private UUID ticketId;

    private String customerName;
    private ZonedDateTime saleDateTime;
}

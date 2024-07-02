package taskdua.demo.controller;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
import taskdua.demo.object.Tickets;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class RekapRenang {
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true);

    private File ticketFile = new File("src/main/resources/data/tickets.json");

    private List<Tickets> loadTickets() throws IOException {
        if (!ticketFile.exists() || ticketFile.length() == 0) { // Check for empty file
            return new ArrayList<>();
        }
        return objectMapper.readValue(ticketFile, new TypeReference<List<Tickets>>() {
        });
    }

    private void saveTickets(List<Tickets> tickets) throws IOException {
        objectMapper.writeValue(ticketFile, tickets);
    }

    @PostMapping("/tiketrenang/tambah")
    public ResponseEntity<?> recordTicketSale(@RequestBody Tickets tiket) {
        log.info("Akses Tambah Tiket Renang :" + tiket.getCustomerName());
        try {
            List<Tickets> tickets = loadTickets();

            Tickets ticket = new Tickets();
            ticket.setTicketId(UUID.randomUUID());
            ticket.setCustomerName(tiket.getCustomerName());

            ZoneId wibZone = ZoneId.of("Asia/Jakarta");
            ZonedDateTime wibDateTime = ZonedDateTime.now(wibZone);
            ticket.setSaleDateTime(wibDateTime);

            tickets.add(ticket);
            saveTickets(tickets);
            return new ResponseEntity<>(ticket, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error adding ticket:", e);
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tiketrenang/baca")
    public ResponseEntity<?> getAllTickets() {
        log.info("Akses Baca Tiket Renang");
        try {
            List<Tickets> tickets = loadTickets();

            if (tickets.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(tickets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

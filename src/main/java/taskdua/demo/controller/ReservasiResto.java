package taskdua.demo.controller;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
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
import taskdua.demo.object.Reserve;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class ReservasiResto {
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true);

    private File restoFile = new File("src/main/resources/data/reservasi.json");

    private List<Reserve> loadRestos() throws IOException {
        if (!restoFile.exists() || restoFile.length() == 0) { // Check for empty file
            return new ArrayList<>();
        }
        return objectMapper.readValue(restoFile, new TypeReference<List<Reserve>>() {
        });
    }

    private void saveRestosRecord(List<Reserve> pesan) throws IOException {
        objectMapper.writeValue(restoFile, pesan);
    }

    @PostMapping("/reserveresto/add")
    public ResponseEntity<?> recordRestoReservation(@RequestBody Reserve pesan) {
        log.info("Akses Tambah Pemesanan Resto");
        try {
            List<Reserve> listReserve = loadRestos();

            Reserve newReserve = new Reserve();
            newReserve.setCustomerName(pesan.getCustomerName());
            newReserve.setReserveId(UUID.randomUUID());
            newReserve.setReserveSeat(pesan.getReserveSeat());
            newReserve.setPrice(pesan.getPrice());

            LocalDate wibDate = LocalDate.now(ZoneId.of("Asia/Jakarta"));
            newReserve.setReservedDateTime(wibDate);
            newReserve.setReservedDay(wibDate.getDayOfWeek().toString());

            // Menghitung Kapan Hari Reservasi yang tersedia
            LocalDate actualReservedDate = wibDate;
            while (true) {
                final LocalDate temp = actualReservedDate;
                long reservationsOnDay = listReserve.stream()
                        .filter(r -> r.getActualReservedDateTime().equals(temp))
                        .count();

                if (reservationsOnDay < 2 && // Belum Limit
                        actualReservedDate.getDayOfWeek() != DayOfWeek.WEDNESDAY && // Bukan Rabu
                        actualReservedDate.getDayOfWeek() != DayOfWeek.FRIDAY) { // Bukan Jumat
                    break; // Ketemu
                } else {
                    actualReservedDate = actualReservedDate.plusDays(1); // Check the next day
                }
            }
            newReserve.setActualReservedDateTime(actualReservedDate);
            newReserve.setActualReservedDay(actualReservedDate.getDayOfWeek().toString());

            listReserve.add(newReserve);
            saveRestosRecord(listReserve);
            return new ResponseEntity<>(newReserve, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error adding Reservation Record:", e);
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/reserveresto/read")
    public ResponseEntity<?> getAllReservationsRecord() {
        log.info("Akses Baca Reservasi Restoran");
        try {
            List<Reserve> restos = loadRestos();

            if (restos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(restos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

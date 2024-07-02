package taskdua.demo.controller;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.LinkedHashMap;

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
import taskdua.demo.object.Chicken;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class RekapAyam {
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true);

    private File rekapAyamFile = new File("src/main/resources/data/ayam.json");

    private List<Chicken> loadChickens() throws IOException {
        if (!rekapAyamFile.exists() || rekapAyamFile.length() == 0) { // Check for empty file
            return new ArrayList<>();
        }
        return objectMapper.readValue(rekapAyamFile, new TypeReference<List<Chicken>>() {
        });
    }

    private void saveChickensRecord(List<Chicken> ayam) throws IOException {
        objectMapper.writeValue(rekapAyamFile, ayam);
    }

    @PostMapping("/rekapayam/tambah")
    public ResponseEntity<?> recordChickenSale(@RequestBody Chicken ayam) {
        log.info("Akses Tambah Record Ayam");
        try {
            List<Chicken> listAyam = loadChickens();

            Chicken ayamBaru = new Chicken();
            ayamBaru.setChickenId(UUID.randomUUID());
            ayamBaru.setEggQuantity(ayam.getEggQuantity());
            ayamBaru.setPrice(ayam.getPrice());

            ZoneId wibZone = ZoneId.of("Asia/Jakarta");
            ZonedDateTime wibDateTime = ZonedDateTime.now(wibZone);
            ayamBaru.setChickenLayEggDateTime(wibDateTime);
            ayamBaru.setChickenEggExpireDateTime(wibDateTime.plusDays(30));

            listAyam.add(ayamBaru);
            saveChickensRecord(listAyam);
            return new ResponseEntity<>(ayamBaru, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error adding Chicken Record:", e);
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/rekapayam/bacalist")
    public ResponseEntity<?> getAllChickenRecord() {
        log.info("Akses Baca Record Ayam");
        try {
            List<Chicken> ayamList = loadChickens();

            if (ayamList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(ayamList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/rekapayam/bacalaporan")
    public ResponseEntity<?> getChickenSaleRecap() {
        log.info("Akses Baca Rekap Ayam");
        try {
            List<Chicken> ayamList = loadChickens();

            if (ayamList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Integer totalEgg = ayamList.stream().mapToInt(Chicken::getEggQuantity).sum();

            Double totalPrice = ayamList.stream().mapToDouble(Chicken::getPrice).sum();

            Long layingChicken = ayamList.stream().filter(c -> c.getEggQuantity() > 0).count();
            Long notLayingChicken = ayamList.size() - layingChicken;

            LinkedHashMap<String, Object> report = new LinkedHashMap<>();
            report.put("Ayam Petelur", layingChicken);
            report.put("Ayam bukan Petelur", notLayingChicken);
            report.put("Total Telur", totalEgg);
            report.put("Total Harga", totalPrice);

            return new ResponseEntity<>(report, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

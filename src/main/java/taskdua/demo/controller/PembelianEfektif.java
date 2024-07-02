package taskdua.demo.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import taskdua.demo.object.Produk;
import taskdua.demo.object.Variasi;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class PembelianEfektif {
    private List<Produk> products;

    public PembelianEfektif() {
        products = new ArrayList<>();
        products.add(new Produk("Keyboard", List.of(
                new Variasi(124000.0, 10),
                new Variasi(70000.0, 5),
                new Variasi(40000.0, 3),
                new Variasi(16000.0, 6))));
        products.add(new Produk("Mouse", List.of(
                new Variasi(35000.0, 15),
                new Variasi(20000.0, 8),
                new Variasi(12000.0, 5))));
    }

    @GetMapping("/pembelianefektif")
    public ResponseEntity<Map<String, Double>> purchase(@RequestParam Double amount) {
        log.info("Mengakses Pembelian Efektif");
        Map<String, Double> purchaseResult = calculateEfficientPurchase(amount);
        if (purchaseResult.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(purchaseResult, HttpStatus.OK);
        }
    }

    private LinkedHashMap<String, Double> calculateEfficientPurchase(Double amount) {
        LinkedHashMap<String, Double> bestCombination = new LinkedHashMap<>();
        Double remainingAmount = amount;

        // Prioritas Mendapatkan 1 Keyboard dan Mouse
        for (Produk product : products) {
            for (Variasi variation : product.getVariations()) {
                if (remainingAmount >= variation.getPrice() && variation.getQuantityAvailable() > 0) {
                    bestCombination.put(product.getName() + " " + variation.getPrice(), 1.0);
                    remainingAmount -= variation.getPrice();
                    break;
                }
            }
        }

        // Mencari Kombinasi Terbaik dari sisa uang
        double closestAmount = 0.0;
        for (Produk product : products) {
            for (Variasi variation : product.getVariations()) {
                LinkedHashMap<String, Double> currentCombination = new LinkedHashMap<>();

                while (remainingAmount >= variation.getPrice() && variation.getQuantityAvailable() > 0) {
                    currentCombination.merge(product.getName() + " " + variation.getPrice(), 1.0, Double::sum);
                    remainingAmount -= variation.getPrice();
                }

                // Hitung newClosestAmount untuk mencari kombinasi terbaik di iterasi ini
                double newClosestAmount = amount - remainingAmount;

                // Update bestCombination jika kombinasinya lebih dekat dengan target uang
                if (newClosestAmount > closestAmount) {
                    closestAmount = newClosestAmount;

                    // Merge currentCombination ke bestCombination
                    currentCombination.forEach((key, value) -> bestCombination.merge(key, value, Double::sum));
                }
            }
        }
        bestCombination.put("Sisa Uang", remainingAmount);
        return bestCombination;
    }
}

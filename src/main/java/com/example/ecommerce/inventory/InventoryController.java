package com.example.ecommerce.inventory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.NotFoundException;
import com.example.ecommerce.model.Inventory;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryRepository inventoryRepository;

    public InventoryController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping("/{product_id}")
    public Inventory getInventory(@PathVariable("product_id") Long productId) {
        return inventoryRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Inventory not found"));
    }

    @PutMapping("/{product_id}")
    public Inventory updateInventory(@PathVariable("product_id") Long productId,
                                     @RequestBody InventoryUpdateRequest update) {
        Inventory inventory = inventoryRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Inventory not found"));

        inventory.setQuantity(update.quantity());
        inventory.setLastUpdated(LocalDateTime.now(ZoneOffset.UTC));
        return inventoryRepository.save(inventory);
    }

    @PostMapping("/{product_id}/reserve")
    public Inventory reserveInventory(@PathVariable("product_id") Long productId,
                                      @RequestBody ReserveRequest reserve) {
        Inventory inventory = inventoryRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Inventory not found"));

        int available = inventory.getQuantity() - inventory.getReserved();
        if (available < reserve.quantity()) {
            throw new BadRequestException(
                    "Insufficient inventory. Available: " + available + ", Requested: " + reserve.quantity());
        }

        inventory.setReserved(inventory.getReserved() + reserve.quantity());
        inventory.setLastUpdated(LocalDateTime.now(ZoneOffset.UTC));
        return inventoryRepository.save(inventory);
    }

    public record InventoryUpdateRequest(int quantity) {}

    public record ReserveRequest(int quantity) {}
}

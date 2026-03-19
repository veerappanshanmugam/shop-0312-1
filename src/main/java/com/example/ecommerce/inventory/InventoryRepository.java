package com.example.ecommerce.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ecommerce.model.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}

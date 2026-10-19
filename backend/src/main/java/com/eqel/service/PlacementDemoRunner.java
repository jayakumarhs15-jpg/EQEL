package com.eqel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Executes placement demo data seeding on startup only when explicitly enabled via:
 * --eqel.placement-demo.enabled=true or EQEL_PLACEMENT_DEMO_ENABLED=true
 * 
 * Disabled by default.
 */
@Component
@ConditionalOnProperty(name = "eqel.placement-demo.enabled", havingValue = "true")
public class PlacementDemoRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PlacementDemoRunner.class);
    private final PlacementDemoDataSeeder seeder;

    public PlacementDemoRunner(PlacementDemoDataSeeder seeder) {
        this.seeder = seeder;
    }

    @Override
    public void run(String... args) {
        log.info("PlacementDemoRunner: Triggering explicit placement demo seeding...");
        PlacementDemoDataSeeder.SeedResult result = seeder.seedPlacementDemoData();
        log.info("PlacementDemoRunner result: {} (Total added: {})", result.getMessage(), result.getTotalRowsAdded());
    }
}

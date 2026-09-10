package io.freelance.kjm.react.controllers;

import io.freelance.kjm.react.dto.PricingRequest;
import io.freelance.kjm.react.dto.PricingResponse;
import io.freelance.kjm.react.services.DroolsCoordinatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller dispatching international pricing inquiries to the Drools rule coordinator.
 */
@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final DroolsCoordinatorService coordinatorService;

    public PricingController(DroolsCoordinatorService coordinatorService) {
        this.coordinatorService = coordinatorService;
    }

    @PostMapping
    public ResponseEntity<PricingResponse> computePricing(@RequestBody PricingRequest request) {
        PricingResponse response = coordinatorService.coordinatePricing(request);
        return ResponseEntity.ok(response);
    }
}

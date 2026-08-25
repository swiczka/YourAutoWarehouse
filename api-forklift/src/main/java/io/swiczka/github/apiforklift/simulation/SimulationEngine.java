package io.swiczka.github.apiforklift.simulation;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SimulationEngine {
    @Scheduled(fixedRate = 500)
    void tick(){
        System.out.println("Computing...");
    }
}

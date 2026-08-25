package io.swiczka.github.apiforklift;

import io.swiczka.github.apiforklift.domain.Forklift;
import io.swiczka.github.apiforklift.domain.ForkliftTask;
import io.swiczka.github.apiforklift.dto.ForkliftCreateDto;
import io.swiczka.github.apiforklift.dto.ForkliftTaskCreateDto;
import io.swiczka.github.apiforklift.exceptions.ForkliftNotFoundException;
import io.swiczka.github.apiforklift.simulation.ForkliftRegistry;
import io.swiczka.github.apiforklift.simulation.ForkliftTaskRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//controller used only for pre-async com tests
@RestController
@RequestMapping("/api/forklift")
public class ForkliftController {

    private final ForkliftRegistry forkliftRegistry;
    private final ForkliftTaskRegistry taskRegistry;

    @Autowired
    public ForkliftController(ForkliftRegistry forkliftRegistry, ForkliftTaskRegistry taskRegistry) {
        this.forkliftRegistry = forkliftRegistry;
        this.taskRegistry = taskRegistry;
    }

    @PostMapping("/")
    public Forklift addNew(@RequestBody final ForkliftCreateDto dto){
        return forkliftRegistry.add(dto);
    }

    @GetMapping("/{id}")
    public Forklift getById(@PathVariable final Long id){
        return forkliftRegistry.getById(id)
                .orElseThrow(() -> new ForkliftNotFoundException(String.format("No forklift found with given id: %d", id)));
    }

    @PostMapping("/tasks")
    public List<ForkliftTask> addTasks(@RequestBody final List<ForkliftTaskCreateDto> dtos){
        return taskRegistry.add(dtos);

    }

}

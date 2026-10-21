package com.example.fantasy.service;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.entity.Manager;
import com.example.fantasy.exception.ApiException;
import com.example.fantasy.repository.ManagerRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ManagerService {
    private final ManagerRepository managers;

    public Manager manager(Long id) { return managers.findById(id).orElseThrow(() -> ApiException.missing("Менеджер")); }

    public ManagerView create(ManagerInput input) {
        Manager value = new Manager();
        value.setCreatedAt(Instant.now());
        apply(value, input);
        return Views.manager(managers.save(value));
    }

    public ManagerView update(Long id, ManagerInput input) {
        Manager value = manager(id);
        apply(value, input);
        return Views.manager(value);
    }

    private void apply(Manager value, ManagerInput input) {
        value.setUsername(input.username());
        value.setEmail(input.email());
    }

    public void delete(Long id) { managers.delete(manager(id)); managers.flush(); }
    @Transactional(readOnly = true)
    public ManagerView get(Long id) { return Views.manager(manager(id)); }
    @Transactional(readOnly = true)
    public Page<ManagerView> list(Pageable pageable) { return managers.findAll(pageable).map(Views::manager); }
}

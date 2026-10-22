package com.example.fantasy.service;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.entity.RealClub;
import com.example.fantasy.exception.ApiException;
import com.example.fantasy.repository.RealClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {
    private final RealClubRepository clubs;

    public RealClub club(Long id) { return clubs.findById(id).orElseThrow(() -> ApiException.missing("Клуб")); }

    public ClubView create(ClubInput input) {
        RealClub value = new RealClub();
        apply(value, input);
        return Views.club(clubs.save(value));
    }

    public ClubView update(Long id, ClubInput input) {
        RealClub value = club(id);
        apply(value, input);
        return Views.club(value);
    }

    private void apply(RealClub value, ClubInput input) {
        value.setName(input.name());
        value.setShortName(input.shortName());
        value.setCountry(input.country());
    }

    public void delete(Long id) { clubs.delete(club(id)); clubs.flush(); }
    @Transactional(readOnly = true)
    public ClubView get(Long id) { return Views.club(club(id)); }
    @Transactional(readOnly = true)
    public Page<ClubView> list(Pageable pageable) { return clubs.findAll(pageable).map(Views::club); }
}

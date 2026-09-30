package tn.esprit.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.backend.entity.Projet;
import tn.esprit.backend.entity.ProjetDetaille;
import tn.esprit.backend.repository.ProjetDetailleRepository;
import tn.esprit.backend.repository.ProjetRepository;
import tn.esprit.backend.service.impl.ProjetDetailleServiceImpl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjetDetailleServiceTest {

    @Mock
    private ProjetDetailleRepository projetDetailleRepository;

    @Mock
    private ProjetRepository projetRepository;

    @InjectMocks
    private ProjetDetailleServiceImpl projetDetailleService;

    private ProjetDetaille projetDetaille;
    private Projet projet;

    @BeforeEach
    void setUp() {
        projet = Projet.builder()
                .id(1L)
                .sujet("Migration Cloud")
                .build();

        projetDetaille = ProjetDetaille.builder()
                .id(1L)
                .description("Détails techniques")
                .technologie("Spring Boot 3")
                .coutProvisoire(5000.0)
                .dateDebut(LocalDate.of(2026, 1, 15))
                .projet(projet)
                .build();
    }

    @Test
    void testGetAllProjetsDetailles() {
        when(projetDetailleRepository.findAll()).thenReturn(Arrays.asList(projetDetaille));

        List<ProjetDetaille> result = projetDetailleService.getAllProjetsDetailles();

        assertEquals(1, result.size());
        assertEquals("Spring Boot 3", result.get(0).getTechnologie());
        verify(projetDetailleRepository, times(1)).findAll();
    }

    @Test
    void testGetProjetDetailleById() {
        when(projetDetailleRepository.findById(1L)).thenReturn(Optional.of(projetDetaille));

        ProjetDetaille found = projetDetailleService.getProjetDetailleById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        assertEquals(5000.0, found.getCoutProvisoire());
        verify(projetDetailleRepository, times(1)).findById(1L);
    }

    @Test
    void testAddProjetDetaille() {
        when(projetDetailleRepository.save(any(ProjetDetaille.class))).thenReturn(projetDetaille);

        ProjetDetaille saved = projetDetailleService.addProjetDetaille(projetDetaille);

        assertNotNull(saved);
        assertEquals("Détails techniques", saved.getDescription());
        verify(projetDetailleRepository, times(1)).save(projetDetaille);
    }

    @Test
    void testUpdateProjetDetaille() {
        ProjetDetaille updated = ProjetDetaille.builder()
                .id(1L)
                .description("Détails mis à jour")
                .technologie("Spring Boot 4")
                .coutProvisoire(7000.0)
                .dateDebut(LocalDate.of(2026, 2, 1))
                .build();

        when(projetDetailleRepository.save(any(ProjetDetaille.class))).thenReturn(updated);

        ProjetDetaille result = projetDetailleService.updateProjetDetaille(updated);

        assertNotNull(result);
        assertEquals("Spring Boot 4", result.getTechnologie());
        verify(projetDetailleRepository, times(1)).save(updated);
    }

    @Test
    void testDeleteProjetDetaille() {
        doNothing().when(projetDetailleRepository).deleteById(1L);

        projetDetailleService.deleteProjetDetaille(1L);

        verify(projetDetailleRepository, times(1)).deleteById(1L);
    }
}

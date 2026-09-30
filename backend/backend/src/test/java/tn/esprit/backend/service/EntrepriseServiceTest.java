package tn.esprit.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.repository.EntrepriseRepository;
import tn.esprit.backend.service.impl.EntrepriseServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntrepriseServiceTest {

    @Mock
    private EntrepriseRepository entrepriseRepository;

    @InjectMocks
    private EntrepriseServiceImpl entrepriseService;

    private Entreprise entreprise;

    @BeforeEach
    void setUp() {
        entreprise = Entreprise.builder()
                .id(1L)
                .nom("TechCorp")
                .adresse("Tunis")
                .build();
    }

    @Test
    void testGetAllEntreprises() {
        when(entrepriseRepository.findAll()).thenReturn(Arrays.asList(entreprise));

        List<Entreprise> result = entrepriseService.getAllEntreprises();

        assertEquals(1, result.size());
        assertEquals("TechCorp", result.get(0).getNom());
        assertEquals("Tunis", result.get(0).getAdresse());
        verify(entrepriseRepository, times(1)).findAll();
    }

    @Test
    void testGetEntrepriseById() {
        when(entrepriseRepository.findById(1L)).thenReturn(Optional.of(entreprise));

        Entreprise found = entrepriseService.getEntrepriseById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        assertEquals("TechCorp", found.getNom());
        verify(entrepriseRepository, times(1)).findById(1L);
    }

    @Test
    void testAddEntreprise() {
        when(entrepriseRepository.save(any(Entreprise.class))).thenReturn(entreprise);

        Entreprise saved = entrepriseService.addEntreprise(entreprise);

        assertNotNull(saved);
        assertEquals("TechCorp", saved.getNom());
        verify(entrepriseRepository, times(1)).save(entreprise);
    }

    @Test
    void testUpdateEntreprise() {
        Entreprise updated = Entreprise.builder()
                .id(1L)
                .nom("TechCorpUpdated")
                .adresse("Sfax")
                .build();

        when(entrepriseRepository.save(any(Entreprise.class))).thenReturn(updated);

        Entreprise result = entrepriseService.updateEntreprise(updated);

        assertNotNull(result);
        assertEquals("TechCorpUpdated", result.getNom());
        verify(entrepriseRepository, times(1)).save(updated);
    }

    @Test
    void testDeleteEntreprise() {
        doNothing().when(entrepriseRepository).deleteById(1L);

        entrepriseService.deleteEntreprise(1L);

        verify(entrepriseRepository, times(1)).deleteById(1L);
    }
}

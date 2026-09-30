package tn.esprit.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.entity.Equipe;
import tn.esprit.backend.repository.EntrepriseRepository;
import tn.esprit.backend.repository.EquipeRepository;
import tn.esprit.backend.repository.ProjetRepository;
import tn.esprit.backend.service.impl.EquipeServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipeServiceTest {

    @Mock
    private EquipeRepository equipeRepository;

    @Mock
    private EntrepriseRepository entrepriseRepository;

    @Mock
    private ProjetRepository projetRepository;

    @InjectMocks
    private EquipeServiceImpl equipeService;

    private Equipe equipe;
    private Entreprise entreprise;

    @BeforeEach
    void setUp() {
        entreprise = Entreprise.builder()
                .id(1L)
                .nom("TechCorp")
                .adresse("Tunis")
                .build();

        equipe = Equipe.builder()
                .id(1L)
                .nom("TeamA")
                .specialite("Java")
                .entreprise(entreprise)
                .projets(new ArrayList<>())
                .build();
    }

    @Test
    void testGetAllEquipes() {
        when(equipeRepository.findAll()).thenReturn(Arrays.asList(equipe));

        List<Equipe> result = equipeService.getAllEquipes();

        assertEquals(1, result.size());
        assertEquals("TeamA", result.get(0).getNom());
        verify(equipeRepository, times(1)).findAll();
    }

    @Test
    void testGetEquipeById() {
        when(equipeRepository.findById(1L)).thenReturn(Optional.of(equipe));

        Equipe found = equipeService.getEquipeById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        verify(equipeRepository, times(1)).findById(1L);
    }

    @Test
    void testAddEquipe() {
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe);

        Equipe saved = equipeService.addEquipe(equipe);

        assertNotNull(saved);
        assertEquals("TeamA", saved.getNom());
        verify(equipeRepository, times(1)).save(equipe);
    }

    @Test
    void testUpdateEquipe() {
        Equipe updated = Equipe.builder()
                .id(1L).nom("TeamAUpdated").specialite("Spring").build();

        when(equipeRepository.save(any(Equipe.class))).thenReturn(updated);

        Equipe result = equipeService.updateEquipe(updated);

        assertEquals("TeamAUpdated", result.getNom());
        verify(equipeRepository, times(1)).save(updated);
    }

    @Test
    void testDeleteEquipe() {
        doNothing().when(equipeRepository).deleteById(1L);

        equipeService.deleteEquipe(1L);

        verify(equipeRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetEquipesByEntreprise() {
        when(equipeRepository.findByEntrepriseId(1L)).thenReturn(Arrays.asList(equipe));

        List<Equipe> result = equipeService.getEquipesByEntreprise(1L);

        assertEquals(1, result.size());
        assertEquals("TeamA", result.get(0).getNom());
        verify(equipeRepository, times(1)).findByEntrepriseId(1L);
    }
}

package tn.esprit.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.backend.entity.Projet;
import tn.esprit.backend.repository.ProjetRepository;
import tn.esprit.backend.service.impl.ProjetServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjetServiceTest {

    @Mock
    private ProjetRepository projetRepository;

    @InjectMocks
    private ProjetServiceImpl projetService;

    private Projet projet;

    @BeforeEach
    void setUp() {
        projet = Projet.builder()
                .id(1L)
                .sujet("Migration Cloud")
                .build();
    }

    @Test
    void testGetAllProjets() {
        when(projetRepository.findAll()).thenReturn(Arrays.asList(projet));

        List<Projet> result = projetService.getAllProjets();

        assertEquals(1, result.size());
        assertEquals("Migration Cloud", result.get(0).getSujet());
        verify(projetRepository, times(1)).findAll();
    }

    @Test
    void testGetProjetById() {
        when(projetRepository.findById(1L)).thenReturn(Optional.of(projet));

        Projet found = projetService.getProjetById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        assertEquals("Migration Cloud", found.getSujet());
        verify(projetRepository, times(1)).findById(1L);
    }

    @Test
    void testAddProjet() {
        when(projetRepository.save(any(Projet.class))).thenReturn(projet);

        Projet saved = projetService.addProjet(projet);

        assertNotNull(saved);
        assertEquals("Migration Cloud", saved.getSujet());
        verify(projetRepository, times(1)).save(projet);
    }

    @Test
    void testUpdateProjet() {
        Projet updated = Projet.builder()
                .id(1L)
                .sujet("Migration Cloud v2")
                .build();

        when(projetRepository.save(any(Projet.class))).thenReturn(updated);

        Projet result = projetService.updateProjet(updated);

        assertNotNull(result);
        assertEquals("Migration Cloud v2", result.getSujet());
        verify(projetRepository, times(1)).save(updated);
    }

    @Test
    void testDeleteProjet() {
        doNothing().when(projetRepository).deleteById(1L);

        projetService.deleteProjet(1L);

        verify(projetRepository, times(1)).deleteById(1L);
    }
}

package com.faz.salles.test;

import com.faz.salles.dao.SalleFazRepositoryImpl;
import com.faz.salles.dao.modele.SalleFaz;
import com.faz.salles.dao.modele.ReservationFaz;
import com.faz.salles.dao.service.SalleFazService;
import com.faz.salles.dao.service.SalleFazServiceImpl;
import com.faz.salles.dao.utilitaire.PaginationFaz;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class SalleFazServiceTest {

    private EntityManagerFactory emfFaz;
    private EntityManager gestionnaireEntiteFaz;
    private SalleFazService serviceSallesFaz;

    @Before
    public void setUp() {
        emfFaz = Persistence.createEntityManagerFactory("UniteSallesFaz");
        gestionnaireEntiteFaz = emfFaz.createEntityManager();

        // 1. NETTOYER LA BASE AVANT CHAQUE TEST
        gestionnaireEntiteFaz.getTransaction().begin();
        gestionnaireEntiteFaz.createQuery("DELETE FROM ReservationFaz").executeUpdate();
        gestionnaireEntiteFaz.createQuery("DELETE FROM SalleFaz").executeUpdate();
        gestionnaireEntiteFaz.getTransaction().commit();

        // 2. Initialiser le service
        SalleFazRepositoryImpl depotFaz = new SalleFazRepositoryImpl(gestionnaireEntiteFaz);
        serviceSallesFaz = new SalleFazServiceImpl(depotFaz);

        // 3. Insérer les données de test
        serviceSallesFaz.ajouterSalleFaz(new SalleFaz("Salle Alpha", 15, 1, true));
        serviceSallesFaz.ajouterSalleFaz(new SalleFaz("Salle Beta", 25, 2, false));
        serviceSallesFaz.ajouterSalleFaz(new SalleFaz("Salle Gamma", 8, 1, true));

        // 4. Créer une réservation sur Salle Alpha
        SalleFaz salleAlpha = serviceSallesFaz.rechercherSallesFaz(
                "Alpha", null, null, null, 1, 10
        ).get(0);

        gestionnaireEntiteFaz.getTransaction().begin();
        ReservationFaz reservationFaz = new ReservationFaz(
                salleAlpha,
                LocalDateTime.of(2025, 6, 1, 10, 0),
                LocalDateTime.of(2025, 6, 1, 12, 0)
        );
        reservationFaz.setMotifFaz("Reunion");
        gestionnaireEntiteFaz.persist(reservationFaz);
        gestionnaireEntiteFaz.getTransaction().commit();
    }

    @After
    public void tearDown() {
        gestionnaireEntiteFaz.close();
        emfFaz.close();
    }

    // ==================== TEST 1 : JPQL DISPONIBILITE ====================

    @Test
    public void testRechercheSallesDisponiblesFaz() {
        LocalDateTime debutFaz = LocalDateTime.of(2025, 6, 1, 10, 0);
        LocalDateTime finFaz = LocalDateTime.of(2025, 6, 1, 12, 0);

        List<SalleFaz> sallesLibresFaz = serviceSallesFaz.obtenirSallesDisponiblesFaz(debutFaz, finFaz);
        assertEquals(2, sallesLibresFaz.size());
        assertFalse(sallesLibresFaz.stream().anyMatch(s -> s.getNomFaz().equals("Salle Alpha")));
    }

    // ==================== TEST 2 : CRITERIA MULTI-CRITERES ====================

    @Test
    public void testRechercheMultiCriteresFaz() {
        List<SalleFaz> sallesFaz = serviceSallesFaz.rechercherSallesFaz(
                "Salle", 20, null, null, 1, 10
        );
        assertEquals(1, sallesFaz.size());
        assertEquals("Salle Beta", sallesFaz.get(0).getNomFaz());
    }

    // ==================== TEST 3 : PAGINATION ====================

    @Test
    public void testPaginationFaz() {
        PaginationFaz<SalleFaz> page1Faz = serviceSallesFaz.rechercherSallesPagineesAvecMetadataFaz(
                null, null, null, null, 1, 2
        );
        assertEquals(2, page1Faz.getElementsFaz().size());
        assertEquals(3, page1Faz.getTotalElementsFaz());
        assertEquals(2, page1Faz.getNombreTotalPagesFaz());
        assertTrue(page1Faz.estPremierePageFaz());
        assertFalse(page1Faz.estDernierePageFaz());
        assertTrue(page1Faz.aPageSuivanteFaz());

        PaginationFaz<SalleFaz> page2Faz = serviceSallesFaz.rechercherSallesPagineesAvecMetadataFaz(
                null, null, null, null, 2, 2
        );
        assertEquals(1, page2Faz.getElementsFaz().size());
        assertFalse(page2Faz.estPremierePageFaz());
        assertTrue(page2Faz.estDernierePageFaz());
    }

    // ==================== TEST 4 : COMPTAGE ====================

    @Test
    public void testCompterSallesFaz() {
        long totalFaz = serviceSallesFaz.compterSallesFaz(null, null, null, null);
        assertEquals(3, totalFaz);
        long totalCapaciteFaz = serviceSallesFaz.compterSallesFaz(null, 20, null, null);
        assertEquals(1, totalCapaciteFaz);
    }

    // ==================== TEST 5 : VALIDATION METIER ====================

    @Test(expected = IllegalArgumentException.class)
    public void testAjoutSalleSansNomFaz() {
        serviceSallesFaz.ajouterSalleFaz(new SalleFaz("", 10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAjoutSalleCapaciteNegativeFaz() {
        serviceSallesFaz.ajouterSalleFaz(new SalleFaz("Salle Invalide", -5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRechercheDateIncoherenteFaz() {
        LocalDateTime debutFaz = LocalDateTime.of(2025, 6, 1, 12, 0);
        LocalDateTime finFaz = LocalDateTime.of(2025, 6, 1, 10, 0);
        serviceSallesFaz.obtenirSallesDisponiblesFaz(debutFaz, finFaz);
    }
}
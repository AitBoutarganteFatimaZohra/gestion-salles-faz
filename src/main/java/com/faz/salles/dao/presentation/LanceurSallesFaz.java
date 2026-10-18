package com.faz.salles.dao.presentation;

import com.faz.salles.dao.SalleFazRepositoryImpl;
import com.faz.salles.dao.modele.ReservationFaz;
import com.faz.salles.dao.modele.SalleFaz;
import com.faz.salles.dao.service.SalleFazService;
import com.faz.salles.dao.service.SalleFazServiceImpl;
import com.faz.salles.dao.utilitaire.PaginationFaz;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Classe principale de test du projet gestion-salles-faz.
 * Teste : la persistance, les requetes JPQL, les requetes Criteria,
 * la pagination et les regles metier du service.
 */
public class LanceurSallesFaz {

    public static void main(String[] args) {

        System.out.println("==================================================");
        System.out.println("   LANCEUR - GESTION DES SALLES (FAZ)             ");
        System.out.println("==================================================\n");

        // 1. Initialisation de JPA
        EntityManagerFactory emfFaz = Persistence.createEntityManagerFactory("UniteSallesFaz");
        EntityManager gestionnaireEntiteFaz = emfFaz.createEntityManager();

        try {
            // 2. Creation des composants (injection manuelle)
            SalleFazRepositoryImpl depotSallesFaz = new SalleFazRepositoryImpl(gestionnaireEntiteFaz);
            SalleFazService serviceSallesFaz = new SalleFazServiceImpl(depotSallesFaz);

            // ====================================================
            // PARTIE 1 : INSERTION DES DONNEES DE TEST
            // ====================================================
            System.out.println("---- PARTIE 1 : Insertion des salles ----");

            SalleFaz salle1Faz = new SalleFaz("Salle Alpha", 15, 1, true);
            SalleFaz salle2Faz = new SalleFaz("Salle Beta", 25, 2, false);
            SalleFaz salle3Faz = new SalleFaz("Salle Gamma", 8, 1, true);
            SalleFaz salle4Faz = new SalleFaz("Salle Delta", 40, 3, true);
            SalleFaz salle5Faz = new SalleFaz("Salle Epsilon", 12, 2, false);

            serviceSallesFaz.ajouterSalleFaz(salle1Faz);
            serviceSallesFaz.ajouterSalleFaz(salle2Faz);
            serviceSallesFaz.ajouterSalleFaz(salle3Faz);
            serviceSallesFaz.ajouterSalleFaz(salle4Faz);
            serviceSallesFaz.ajouterSalleFaz(salle5Faz);

            System.out.println("5 salles inserees.\n");

            // Insertion d'une reservation pour tester la disponibilite
            gestionnaireEntiteFaz.getTransaction().begin();
            ReservationFaz reservationFaz = new ReservationFaz(
                    salle1Faz,
                    LocalDateTime.of(2025, 6, 1, 10, 0),
                    LocalDateTime.of(2025, 6, 1, 12, 0)
            );
            reservationFaz.setMotifFaz("Reunion projet");
            gestionnaireEntiteFaz.persist(reservationFaz);
            gestionnaireEntiteFaz.getTransaction().commit();
            System.out.println("1 reservation creee pour Salle Alpha (10h-12h le 01/06/2025).\n");

            // ====================================================
            // PARTIE 2 : TEST DES REQUETES JPQL (DISPONIBILITE)
            // ====================================================
            System.out.println("---- PARTIE 2 : Salles disponibles le 01/06/2025 (10h-12h) ----");
            LocalDateTime debutFaz = LocalDateTime.of(2025, 6, 1, 10, 0);
            LocalDateTime finFaz = LocalDateTime.of(2025, 6, 1, 12, 0);

            List<SalleFaz> sallesDisponiblesFaz = serviceSallesFaz.obtenirSallesDisponiblesFaz(debutFaz, finFaz);
            for (SalleFaz s : sallesDisponiblesFaz) {
                System.out.println("  - " + s.getNomFaz() + " (capacite=" + s.getCapaciteFaz() + ")");
            }
            System.out.println();

            // ====================================================
            // PARTIE 3 : TEST DES REQUETES CRITERIA (MULTI-CRITERES)
            // ====================================================
            System.out.println("---- PARTIE 3 : Recherche multi-criteres ----");
            System.out.println("Critere : nom contient 'salle', capacite >= 20");

            List<SalleFaz> sallesCritereesFaz = serviceSallesFaz.rechercherSallesFaz(
                    "salle", 20, null, null, 1, 10
            );
            for (SalleFaz s : sallesCritereesFaz) {
                System.out.println("  - " + s.getNomFaz() + " (capacite=" + s.getCapaciteFaz() + ")");
            }
            System.out.println();

            // ====================================================
            // PARTIE 4 : TEST DE LA PAGINATION
            // ====================================================
            System.out.println("---- PARTIE 4 : Pagination (page 1, taille 2) ----");

            PaginationFaz<SalleFaz> page1Faz = serviceSallesFaz.rechercherSallesPagineesAvecMetadataFaz(
                    null, null, null, null, 1, 2
            );
            System.out.println("Page " + page1Faz.getPageCouranteFaz()
                    + " / " + page1Faz.getNombreTotalPagesFaz()
                    + " - total elements = " + page1Faz.getTotalElementsFaz());
            for (SalleFaz s : page1Faz.getElementsFaz()) {
                System.out.println("  - " + s.getNomFaz());
            }

            System.out.println("\n---- Pagination (page 2, taille 2) ----");
            PaginationFaz<SalleFaz> page2Faz = serviceSallesFaz.rechercherSallesPagineesAvecMetadataFaz(
                    null, null, null, null, 2, 2
            );
            System.out.println("Page " + page2Faz.getPageCouranteFaz()
                    + " / " + page2Faz.getNombreTotalPagesFaz());
            for (SalleFaz s : page2Faz.getElementsFaz()) {
                System.out.println("  - " + s.getNomFaz());
            }

            System.out.println("\n---- Navigation pagination ----");
            System.out.println("Est premiere page ? " + page1Faz.estPremierePageFaz());
            System.out.println("Est derniere page ? " + page1Faz.estDernierePageFaz());
            System.out.println("A page suivante ? " + page1Faz.aPageSuivanteFaz());
            System.out.println("Page suivante : " + page1Faz.prochainNumeroPageFaz());
            System.out.println();

            // ====================================================
            // PARTIE 5 : TEST DES REGLES METIER (VALIDATION)
            // ====================================================
            System.out.println("---- PARTIE 5 : Test des regles metier ----");
            try {
                serviceSallesFaz.ajouterSalleFaz(new SalleFaz("", 10));
            } catch (IllegalArgumentException e) {
                System.out.println("Validation OK : " + e.getMessage());
            }
            try {
                serviceSallesFaz.rechercherSallesFaz(null, null, debutFaz, null, 1, 10);
            } catch (IllegalArgumentException e) {
                System.out.println("Validation OK : " + e.getMessage());
            }

            System.out.println("\n==================================================");
            System.out.println("   FIN DU LANCEMENT                               ");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("ERREUR : " + e.getMessage());
            e.printStackTrace();
        } finally {
            gestionnaireEntiteFaz.close();
            emfFaz.close();
        }
    }
}
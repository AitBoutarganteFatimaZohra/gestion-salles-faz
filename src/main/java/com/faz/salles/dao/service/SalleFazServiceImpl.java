package com.faz.salles.dao.service;

import com.faz.salles.dao.SalleFazRepository;
import com.faz.salles.dao.modele.SalleFaz;
import com.faz.salles.dao.utilitaire.PaginationFaz;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation du service metier pour les salles.
 *
 * Cette couche ajoute des REGLES METIER (validation, calculs) avant ou apres
 * l'appel au Repository.
 */
public class SalleFazServiceImpl implements SalleFazService {

    private final SalleFazRepository depotSallesFaz;

    public SalleFazServiceImpl(SalleFazRepository depotSallesFaz) {
        this.depotSallesFaz = depotSallesFaz;
    }

    // ==================== METHODES DE L'INTERFACE ====================

    @Override
    public List<SalleFaz> obtenirSallesDisponiblesFaz(LocalDateTime debutFaz, LocalDateTime finFaz) {
        // REGLE METIER : verifier que le creneau est coherent
        if (debutFaz == null || finFaz == null) {
            throw new IllegalArgumentException("Les dates de debut et de fin sont obligatoires.");
        }
        if (!debutFaz.isBefore(finFaz)) {
            throw new IllegalArgumentException("La date de debut doit etre avant la date de fin.");
        }

        System.out.println("[Service] Recherche de salles disponibles du " + debutFaz + " au " + finFaz);

        List<SalleFaz> sallesLibresFaz = depotSallesFaz.rechercherSallesLibresFaz(debutFaz, finFaz);

        System.out.println("[Service] " + sallesLibresFaz.size() + " salle(s) disponible(s).");
        return sallesLibresFaz;
    }

    @Override
    public List<SalleFaz> rechercherSallesFaz(
            String critereNomFaz, Integer critereCapaciteFaz,
            LocalDateTime critereDebutFaz, LocalDateTime critereFinFaz,
            int indexPageFaz, int dimensionPageFaz) {

        // REGLE METIER : validation de la pagination
        if (indexPageFaz < 1) {
            throw new IllegalArgumentException("Le numero de page doit etre >= 1.");
        }
        if (dimensionPageFaz < 1 || dimensionPageFaz > 100) {
            throw new IllegalArgumentException("La taille de page doit etre entre 1 et 100.");
        }

        // REGLE METIER : validation du creneau
        if ((critereDebutFaz == null) != (critereFinFaz == null)) {
            throw new IllegalArgumentException("Les deux dates (debut et fin) doivent etre renseignees ensemble.");
        }
        if (critereDebutFaz != null && !critereDebutFaz.isBefore(critereFinFaz)) {
            throw new IllegalArgumentException("La date de debut doit etre avant la date de fin.");
        }

        System.out.println("[Service] Recherche paginee (page " + indexPageFaz
                + ", taille " + dimensionPageFaz + ")");

        return depotSallesFaz.rechercherSallesFaz(
                critereNomFaz, critereCapaciteFaz,
                critereDebutFaz, critereFinFaz,
                indexPageFaz, dimensionPageFaz
        );
    }

    @Override
    public long compterSallesFaz(
            String critereNomFaz, Integer critereCapaciteFaz,
            LocalDateTime critereDebutFaz, LocalDateTime critereFinFaz) {

        return depotSallesFaz.denombrerSallesFaz(
                critereNomFaz, critereCapaciteFaz,
                critereDebutFaz, critereFinFaz
        );
    }

    @Override
    public int calculerNombreTotalPagesFaz(
            String critereNomFaz, Integer critereCapaciteFaz,
            LocalDateTime critereDebutFaz, LocalDateTime critereFinFaz,
            int dimensionPageFaz) {

        long totalFaz = compterSallesFaz(
                critereNomFaz, critereCapaciteFaz,
                critereDebutFaz, critereFinFaz
        );

        return (int) Math.ceil((double) totalFaz / dimensionPageFaz);
    }

    @Override
    public PaginationFaz<SalleFaz> rechercherSallesPagineesAvecMetadataFaz(
            String critereNomFaz, Integer critereCapaciteFaz,
            LocalDateTime critereDebutFaz, LocalDateTime critereFinFaz,
            int indexPageFaz, int dimensionPageFaz) {

        // 1. Recuperer les elements de la page
        List<SalleFaz> elementsFaz = rechercherSallesFaz(
                critereNomFaz, critereCapaciteFaz,
                critereDebutFaz, critereFinFaz,
                indexPageFaz, dimensionPageFaz
        );

        // 2. Compter le total
        long totalFaz = compterSallesFaz(
                critereNomFaz, critereCapaciteFaz,
                critereDebutFaz, critereFinFaz
        );

        // 3. Construire l'objet pagine
        return new PaginationFaz<>(elementsFaz, indexPageFaz, dimensionPageFaz, totalFaz);
    }

    @Override
    public void ajouterSalleFaz(SalleFaz salleFaz) {
        // REGLE METIER : validation avant persistance
        if (salleFaz == null) {
            throw new IllegalArgumentException("La salle ne peut pas etre null.");
        }
        if (salleFaz.getNomFaz() == null || salleFaz.getNomFaz().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la salle est obligatoire.");
        }
        if (salleFaz.getCapaciteFaz() <= 0) {
            throw new IllegalArgumentException("La capacite doit etre strictement positive.");
        }

        System.out.println("[Service] Ajout de la salle : " + salleFaz.getNomFaz());
        depotSallesFaz.sauvegarderSalleFaz(salleFaz);
    }
}
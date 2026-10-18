package com.faz.salles.dao.service;

import com.faz.salles.dao.modele.SalleFaz;
import com.faz.salles.dao.utilitaire.PaginationFaz;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrat du service metier pour les salles.
 * Encapsule la logique metier et delegue l'acces aux donnees au Repository.
 */
public interface SalleFazService {

    /**
     * Recupere les salles disponibles sur un creneau donne.
     */
    List<SalleFaz> obtenirSallesDisponiblesFaz(LocalDateTime debutFaz, LocalDateTime finFaz);

    /**
     * Recherche multi-criteres avec pagination.
     */
    List<SalleFaz> rechercherSallesFaz(
            String critereNomFaz,
            Integer critereCapaciteFaz,
            LocalDateTime critereDebutFaz,
            LocalDateTime critereFinFaz,
            int indexPageFaz,
            int dimensionPageFaz
    );

    /**
     * Compte le nombre total de salles correspondant aux criteres.
     */
    long compterSallesFaz(
            String critereNomFaz,
            Integer critereCapaciteFaz,
            LocalDateTime critereDebutFaz,
            LocalDateTime critereFinFaz
    );

    /**
     * Calcule le nombre total de pages.
     */
    int calculerNombreTotalPagesFaz(
            String critereNomFaz,
            Integer critereCapaciteFaz,
            LocalDateTime critereDebutFaz,
            LocalDateTime critereFinFaz,
            int dimensionPageFaz
    );

    /**
     * Recherche paginee qui retourne un objet PaginationFaz complet.
     */
    PaginationFaz<SalleFaz> rechercherSallesPagineesAvecMetadataFaz(
            String critereNomFaz,
            Integer critereCapaciteFaz,
            LocalDateTime critereDebutFaz,
            LocalDateTime critereFinFaz,
            int indexPageFaz,
            int dimensionPageFaz
    );

    /**
     * Persiste une salle (avec regles metier).
     */
    void ajouterSalleFaz(SalleFaz salleFaz);
}
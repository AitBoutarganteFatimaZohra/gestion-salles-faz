package com.faz.salles.dao;

import com.faz.salles.dao.modele.SalleFaz;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrat du Repository pour l'entite SalleFaz.
 * Encapsule la logique d'acces aux donnees.
 *
 * Principe : separation des responsabilites (SRP).
 * - L'interface definit les operations metier.
 * - L'implementation contient les requetes JPQL/Criteria.
 */
public interface SalleFazRepository {

    /**
     * Recherche les salles libres sur un creneau donne.
     */
    List<SalleFaz> rechercherSallesLibresFaz(LocalDateTime debutFaz, LocalDateTime finFaz);

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
     * Compte le nombre de salles correspondant aux criteres.
     */
    long denombrerSallesFaz(
            String critereNomFaz,
            Integer critereCapaciteFaz,
            LocalDateTime critereDebutFaz,
            LocalDateTime critereFinFaz
    );

    /**
     * Persiste une salle.
     */
    void sauvegarderSalleFaz(SalleFaz salleFaz);

    /**
     * Recherche une salle par ID.
     */
    SalleFaz retrouverSalleParIdFaz(Long idSalleFaz);

    /**
     * Liste toutes les salles.
     */
    List<SalleFaz> obtenirToutesLesSallesFaz();
}
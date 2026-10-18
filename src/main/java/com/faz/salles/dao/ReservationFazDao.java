package com.faz.salles.dao;

import com.faz.salles.dao.modele.ReservationFaz;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationFazDao {

    private final EntityManager gestionnaireEntite;

    public ReservationFazDao(EntityManager gestionnaireEntite) {
        this.gestionnaireEntite = gestionnaireEntite;
    }

    /**
     * Persiste une reservation.
     */
    public void conserverReservationFaz(ReservationFaz reservationFaz) {
        gestionnaireEntite.getTransaction().begin();
        gestionnaireEntite.persist(reservationFaz);
        gestionnaireEntite.getTransaction().commit();
    }

    /**
     * Recherche les reservations d'une salle.
     */
    public List<ReservationFaz> recupererReservationsParSalleFaz(Long salleIdFaz) {
        TypedQuery<ReservationFaz> requeteFaz = gestionnaireEntite.createQuery(
                "SELECT r FROM ReservationFaz r WHERE r.salleFaz.id = :salleIdFaz",
                ReservationFaz.class
        );
        requeteFaz.setParameter("salleIdFaz", salleIdFaz);
        return requeteFaz.getResultList();
    }

    /**
     * Recherche les reservations en chevauchement.
     */
    public List<ReservationFaz> detecterChevauchementsFaz(
            LocalDateTime debutFaz, LocalDateTime finFaz) {
        TypedQuery<ReservationFaz> requeteFaz = gestionnaireEntite.createQuery(
                "SELECT r FROM ReservationFaz r WHERE " +
                        "(:debut < r.finFaz AND :fin > r.debutFaz)",
                ReservationFaz.class
        );
        requeteFaz.setParameter("debut", debutFaz);
        requeteFaz.setParameter("fin", finFaz);
        return requeteFaz.getResultList();
    }

    /**
     * Compte les reservations d'une salle.
     */
    public long compterReservationsParSalleFaz(Long salleIdFaz) {
        return gestionnaireEntite.createQuery(
                "SELECT COUNT(r) FROM ReservationFaz r WHERE r.salleFaz.id = :salleIdFaz",
                Long.class
        ).setParameter("salleIdFaz", salleIdFaz).getSingleResult();
    }

    /**
     * Recherche les reservations par motif.
     */
    public List<ReservationFaz> trouverReservationsParMotifFaz(String motifRechercheFaz) {
        return gestionnaireEntite.createQuery(
                "SELECT r FROM ReservationFaz r WHERE r.motifFaz LIKE :motifRechercheFaz",
                ReservationFaz.class
        ).setParameter("motifRechercheFaz", "%" + motifRechercheFaz + "%").getResultList();
    }
}
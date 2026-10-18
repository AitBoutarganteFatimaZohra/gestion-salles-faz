package com.faz.salles.dao;

import com.faz.salles.dao.modele.ReservationFaz;
import com.faz.salles.dao.modele.SalleFaz;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour l'entite SalleFaz.
 * Contient des requetes JPQL et Criteria personnalisees.
 */
public class SalleFazDao {

    private final EntityManager gestionnaireEntite;

    public SalleFazDao(EntityManager gestionnaireEntite) {
        this.gestionnaireEntite = gestionnaireEntite;
    }

    // ==================== ETAPE 5 : JPQL disponibilite ====================

    /**
     * Recupere les salles libres sur un intervalle donne (JPQL).
     */
    public List<SalleFaz> filtrerSallesLibresFaz(LocalDateTime debutFaz, LocalDateTime finFaz) {
        String requeteJpqlFaz = "SELECT s FROM SalleFaz s WHERE s.id NOT IN (" +
                "SELECT r.salleFaz.id FROM ReservationFaz r WHERE " +
                "(:debut < r.finFaz AND :fin > r.debutFaz)" +
                ")";
        TypedQuery<SalleFaz> requeteFaz = gestionnaireEntite.createQuery(requeteJpqlFaz, SalleFaz.class);
        requeteFaz.setParameter("debut", debutFaz);
        requeteFaz.setParameter("fin", finFaz);
        return requeteFaz.getResultList();
    }

    // ==================== ETAPE 6 : Criteria multi-criteres ====================

    /**
     * Recherche multi-criteres dynamique (nom, capacite, disponibilite).
     */
    public List<SalleFaz> chercherSallesDynamiquementFaz(
            String critereNomFaz, Integer critereCapaciteFaz,
            LocalDateTime critereDebutFaz, LocalDateTime critereFinFaz) {

        CriteriaBuilder generateurFaz = gestionnaireEntite.getCriteriaBuilder();
        CriteriaQuery<SalleFaz> requeteFaz = generateurFaz.createQuery(SalleFaz.class);
        Root<SalleFaz> racineFaz = requeteFaz.from(SalleFaz.class);

        List<Predicate> filtresFaz = preparerFiltresFaz(
                generateurFaz, requeteFaz, racineFaz,
                critereNomFaz, critereCapaciteFaz, critereDebutFaz, critereFinFaz
        );

        if (!filtresFaz.isEmpty()) {
            requeteFaz.select(racineFaz).where(
                    generateurFaz.and(filtresFaz.toArray(new Predicate[0]))
            );
        } else {
            requeteFaz.select(racineFaz);
        }

        return gestionnaireEntite.createQuery(requeteFaz).getResultList();
    }

    // ==================== ETAPE 7 : Pagination ====================

    /**
     * Recherche paginee avec criteres.
     */
    public List<SalleFaz> extraireSallesParPageFaz(
            String critereNomFaz, Integer critereCapaciteFaz,
            LocalDateTime critereDebutFaz, LocalDateTime critereFinFaz,
            int indexPageFaz, int dimensionPageFaz) {

        CriteriaBuilder generateurFaz = gestionnaireEntite.getCriteriaBuilder();
        CriteriaQuery<SalleFaz> requeteFaz = generateurFaz.createQuery(SalleFaz.class);
        Root<SalleFaz> racineFaz = requeteFaz.from(SalleFaz.class);

        List<Predicate> filtresFaz = preparerFiltresFaz(
                generateurFaz, requeteFaz, racineFaz,
                critereNomFaz, critereCapaciteFaz, critereDebutFaz, critereFinFaz
        );

        if (!filtresFaz.isEmpty()) {
            requeteFaz.select(racineFaz).where(
                    generateurFaz.and(filtresFaz.toArray(new Predicate[0]))
            );
        } else {
            requeteFaz.select(racineFaz);
        }

        TypedQuery<SalleFaz> requeteFinaleFaz = gestionnaireEntite.createQuery(requeteFaz);
        requeteFinaleFaz.setFirstResult((indexPageFaz - 1) * dimensionPageFaz);
        requeteFinaleFaz.setMaxResults(dimensionPageFaz);

        return requeteFinaleFaz.getResultList();
    }

    /**
     * Compte le nombre total de salles correspondant aux criteres.
     */
    public long denombrerSallesFaz(
            String critereNomFaz, Integer critereCapaciteFaz,
            LocalDateTime critereDebutFaz, LocalDateTime critereFinFaz) {

        CriteriaBuilder generateurFaz = gestionnaireEntite.getCriteriaBuilder();
        CriteriaQuery<Long> requeteComptageFaz = generateurFaz.createQuery(Long.class);
        Root<SalleFaz> racineFaz = requeteComptageFaz.from(SalleFaz.class);

        List<Predicate> filtresFaz = preparerFiltresFaz(
                generateurFaz, null, racineFaz,
                critereNomFaz, critereCapaciteFaz, critereDebutFaz, critereFinFaz
        );

        requeteComptageFaz.select(generateurFaz.count(racineFaz));

        if (!filtresFaz.isEmpty()) {
            requeteComptageFaz.where(
                    generateurFaz.and(filtresFaz.toArray(new Predicate[0]))
            );
        }

        return gestionnaireEntite.createQuery(requeteComptageFaz).getSingleResult();
    }

    // ==================== METHODES UTILITAIRES ====================

    /**
     * Prepare la liste des predicats (filtres) pour les requetes Criteria.
     */
    private List<Predicate> preparerFiltresFaz(
            CriteriaBuilder generateurFaz, CriteriaQuery<?> requeteFaz,
            Root<SalleFaz> racineFaz,
            String critereNomFaz, Integer critereCapaciteFaz,
            LocalDateTime critereDebutFaz, LocalDateTime critereFinFaz) {

        List<Predicate> filtresFaz = new ArrayList<>();

        if (critereNomFaz != null && !critereNomFaz.isEmpty()) {
            filtresFaz.add(generateurFaz.like(
                    generateurFaz.lower(racineFaz.get("nomFaz")),
                    "%" + critereNomFaz.toLowerCase() + "%"
            ));
        }

        if (critereCapaciteFaz != null) {
            filtresFaz.add(generateurFaz.ge(
                    racineFaz.get("capaciteFaz"), critereCapaciteFaz
            ));
        }

        if (critereDebutFaz != null && critereFinFaz != null && requeteFaz != null) {
            Subquery<Long> sousRequeteFaz = requeteFaz.subquery(Long.class);
            Root<ReservationFaz> racineReservationFaz = sousRequeteFaz.from(ReservationFaz.class);
            sousRequeteFaz.select(racineReservationFaz.get("salleFaz").get("id"));

            Predicate chevauchementFaz = generateurFaz.and(
                    generateurFaz.lessThan(racineReservationFaz.get("debutFaz"), critereFinFaz),
                    generateurFaz.greaterThan(racineReservationFaz.get("finFaz"), critereDebutFaz)
            );
            sousRequeteFaz.where(chevauchementFaz);

            filtresFaz.add(generateurFaz.not(racineFaz.get("id").in(sousRequeteFaz)));
        }

        return filtresFaz;
    }

    // ==================== METHODES SUPPLEMENTAIRES (BONUS) ====================

    /**
     * Recupere toutes les salles.
     */
    public List<SalleFaz> obtenirToutesSallesFaz() {
        return gestionnaireEntite.createQuery(
                "SELECT s FROM SalleFaz s", SalleFaz.class
        ).getResultList();
    }

    /**
     * Recherche les salles par capacite minimale.
     */
    public List<SalleFaz> selectionnerSallesParCapaciteFaz(int seuilCapaciteFaz) {
        return gestionnaireEntite.createQuery(
                "SELECT s FROM SalleFaz s WHERE s.capaciteFaz >= :seuilCapaciteFaz", SalleFaz.class
        ).setParameter("seuilCapaciteFaz", seuilCapaciteFaz).getResultList();
    }

    /**
     * Recherche les salles equipees.
     */
    public List<SalleFaz> identifierSallesEquipeesFaz() {
        return gestionnaireEntite.createQuery(
                "SELECT s FROM SalleFaz s WHERE s.equipeeFaz = true", SalleFaz.class
        ).getResultList();
    }

    /**
     * Recherche les salles par etage.
     */
    public List<SalleFaz> localiserSallesParEtageFaz(int numeroEtageFaz) {
        return gestionnaireEntite.createQuery(
                "SELECT s FROM SalleFaz s WHERE s.etageFaz = :numeroEtageFaz", SalleFaz.class
        ).setParameter("numeroEtageFaz", numeroEtageFaz).getResultList();
    }

    /**
     * Persiste une salle.
     */
    public void sauvegarderSalleFaz(SalleFaz salleFaz) {
        gestionnaireEntite.getTransaction().begin();
        gestionnaireEntite.persist(salleFaz);
        gestionnaireEntite.getTransaction().commit();
    }

    /**
     * Recherche une salle par son ID.
     */
    public SalleFaz retrouverSalleParIdFaz(Long idSalleFaz) {
        return gestionnaireEntite.find(SalleFaz.class, idSalleFaz);
    }

    /**
     * Supprime une salle.
     */
    public void effacerSalleFaz(SalleFaz salleFaz) {
        gestionnaireEntite.getTransaction().begin();
        gestionnaireEntite.remove(
                gestionnaireEntite.contains(salleFaz) ? salleFaz : gestionnaireEntite.merge(salleFaz)
        );
        gestionnaireEntite.getTransaction().commit();
    }
}
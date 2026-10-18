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
 * Implementation du Repository SalleFaz.
 *
 * NOTE : dans le document original, cette classe utilisait @Stateless et
 * @PersistenceContext (EJB/JTA). Ici, on est en Java SE pur, donc on injecte
 * manuellement l'EntityManager via le constructeur.
 */
public class SalleFazRepositoryImpl implements SalleFazRepository {

    private final EntityManager gestionnaireEntite;

    public SalleFazRepositoryImpl(EntityManager gestionnaireEntite) {
        this.gestionnaireEntite = gestionnaireEntite;
    }

    // ==================== ETAPE 5 : JPQL disponibilite ====================

    @Override
    public List<SalleFaz> rechercherSallesLibresFaz(LocalDateTime debutFaz, LocalDateTime finFaz) {
        String requeteJpqlFaz = "SELECT s FROM SalleFaz s WHERE s.id NOT IN (" +
                "SELECT r.salleFaz.id FROM ReservationFaz r WHERE " +
                "(:debut < r.finFaz AND :fin > r.debutFaz)" +
                ")";
        return gestionnaireEntite.createQuery(requeteJpqlFaz, SalleFaz.class)
                .setParameter("debut", debutFaz)
                .setParameter("fin", finFaz)
                .getResultList();
    }

    // ==================== ETAPE 6 + 7 : Criteria + Pagination ====================

    @Override
    public List<SalleFaz> rechercherSallesFaz(
            String critereNomFaz,
            Integer critereCapaciteFaz,
            LocalDateTime critereDebutFaz,
            LocalDateTime critereFinFaz,
            int indexPageFaz,
            int dimensionPageFaz) {

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

    @Override
    public long denombrerSallesFaz(
            String critereNomFaz,
            Integer critereCapaciteFaz,
            LocalDateTime critereDebutFaz,
            LocalDateTime critereFinFaz) {

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

    // ==================== AUTRES METHODES ====================

    @Override
    public void sauvegarderSalleFaz(SalleFaz salleFaz) {
        gestionnaireEntite.getTransaction().begin();
        gestionnaireEntite.persist(salleFaz);
        gestionnaireEntite.getTransaction().commit();
    }

    @Override
    public SalleFaz retrouverSalleParIdFaz(Long idSalleFaz) {
        return gestionnaireEntite.find(SalleFaz.class, idSalleFaz);
    }

    @Override
    public List<SalleFaz> obtenirToutesLesSallesFaz() {
        return gestionnaireEntite.createQuery(
                "SELECT s FROM SalleFaz s", SalleFaz.class
        ).getResultList();
    }

    // ==================== METHODE UTILITAIRE ====================

    private List<Predicate> preparerFiltresFaz(
            CriteriaBuilder generateurFaz,
            CriteriaQuery<?> requeteFaz,
            Root<SalleFaz> racineFaz,
            String critereNomFaz,
            Integer critereCapaciteFaz,
            LocalDateTime critereDebutFaz,
            LocalDateTime critereFinFaz) {

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
}
package com.faz.salles.dao.modele;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entite representant une reservation d'une salle sur un creneau horaire.
 * Chaque reservation est liee a une seule salle.
 */
@Entity
@Table(name = "reservation_faz")
public class ReservationFaz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation_faz")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_faz_id", nullable = false)
    private SalleFaz salleFaz;

    @Column(name = "debut_faz", nullable = false)
    private LocalDateTime debutFaz;

    @Column(name = "fin_faz", nullable = false)
    private LocalDateTime finFaz;

    @Column(name = "motif_faz")
    private String motifFaz;

    // Constructeurs
    public ReservationFaz() {}

    public ReservationFaz(SalleFaz salleFaz, LocalDateTime debutFaz, LocalDateTime finFaz) {
        this.salleFaz = salleFaz;
        this.debutFaz = debutFaz;
        this.finFaz = finFaz;
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SalleFaz getSalleFaz() { return salleFaz; }
    public void setSalleFaz(SalleFaz salleFaz) { this.salleFaz = salleFaz; }

    public LocalDateTime getDebutFaz() { return debutFaz; }
    public void setDebutFaz(LocalDateTime debutFaz) { this.debutFaz = debutFaz; }

    public LocalDateTime getFinFaz() { return finFaz; }
    public void setFinFaz(LocalDateTime finFaz) { this.finFaz = finFaz; }

    public String getMotifFaz() { return motifFaz; }
    public void setMotifFaz(String motifFaz) { this.motifFaz = motifFaz; }

    // Methodes metier personnalisees
    public boolean estEnConflitAvecFaz(ReservationFaz autre) {
        return this.debutFaz.isBefore(autre.finFaz) && this.finFaz.isAfter(autre.debutFaz);
    }

    public long obtenirDureeMinutesFaz() {
        return java.time.Duration.between(debutFaz, finFaz).toMinutes();
    }

    @Override
    public String toString() {
        return "ReservationFaz [id=" + id
                + ", salle=" + (salleFaz != null ? salleFaz.getNomFaz() : "aucune")
                + ", debut=" + debutFaz
                + ", fin=" + finFaz + "]";
    }
}
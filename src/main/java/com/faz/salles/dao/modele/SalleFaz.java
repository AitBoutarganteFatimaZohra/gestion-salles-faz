package com.faz.salles.dao.modele;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entite representant une salle de reunion.
 * Une salle peut avoir plusieurs reservations.
 */
@Entity
@Table(name = "salle_faz")
public class SalleFaz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_salle_faz")
    private Long id;

    @Column(name = "nom_salle_faz", nullable = false, unique = true)
    private String nomFaz;

    @Column(name = "capacite_salle_faz", nullable = false)
    private int capaciteFaz;

    @Column(name = "etage_salle_faz")
    private int etageFaz;

    @Column(name = "equipee_salle_faz")
    private boolean equipeeFaz;

    @OneToMany(mappedBy = "salleFaz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ReservationFaz> reservationsFaz = new HashSet<>();

    // Constructeurs
    public SalleFaz() {}

    public SalleFaz(String nomFaz, int capaciteFaz) {
        this.nomFaz = nomFaz;
        this.capaciteFaz = capaciteFaz;
    }

    public SalleFaz(String nomFaz, int capaciteFaz, int etageFaz, boolean equipeeFaz) {
        this.nomFaz = nomFaz;
        this.capaciteFaz = capaciteFaz;
        this.etageFaz = etageFaz;
        this.equipeeFaz = equipeeFaz;
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomFaz() { return nomFaz; }
    public void setNomFaz(String nomFaz) { this.nomFaz = nomFaz; }

    public int getCapaciteFaz() { return capaciteFaz; }
    public void setCapaciteFaz(int capaciteFaz) { this.capaciteFaz = capaciteFaz; }

    public int getEtageFaz() { return etageFaz; }
    public void setEtageFaz(int etageFaz) { this.etageFaz = etageFaz; }

    public boolean isEquipeeFaz() { return equipeeFaz; }
    public void setEquipeeFaz(boolean equipeeFaz) { this.equipeeFaz = equipeeFaz; }

    public Set<ReservationFaz> getReservationsFaz() { return reservationsFaz; }
    public void setReservationsFaz(Set<ReservationFaz> reservationsFaz) { this.reservationsFaz = reservationsFaz; }

    // Methodes metier personnalisees
    public boolean estGrandeSalleFaz() {
        return capaciteFaz > 20;
    }

    public String obtenirDescriptionFaz() {
        return nomFaz + " (capacite=" + capaciteFaz + ", etage=" + etageFaz + ")";
    }

    @Override
    public String toString() {
        return "SalleFaz [id=" + id + ", nom=" + nomFaz
                + ", capacite=" + capaciteFaz
                + ", etage=" + etageFaz
                + ", equipee=" + equipeeFaz + "]";
    }
}
package com.faz.salles.dao.utilitaire;

import java.util.List;

/**
 * Classe generique pour representer un resultat pagine.
 * Contient les elements de la page courante et les metadonnees
 * necessaires a la navigation (page courante, taille, total).
 *
 * @param <T> Le type des elements paginee.
 */
public class PaginationFaz<T> {

    private List<T> elementsFaz;
    private int pageCouranteFaz;
    private int taillePageFaz;
    private int nombreTotalPagesFaz;
    private long totalElementsFaz;

    /**
     * Constructeur principal.
     *
     * @param elementsFaz Les elements de la page courante.
     * @param pageCouranteFaz Le numero de la page (1-based).
     * @param taillePageFaz La taille de la page.
     * @param totalElementsFaz Le nombre total d'elements.
     */
    public PaginationFaz(List<T> elementsFaz, int pageCouranteFaz, int taillePageFaz, long totalElementsFaz) {
        this.elementsFaz = elementsFaz;
        this.pageCouranteFaz = pageCouranteFaz;
        this.taillePageFaz = taillePageFaz;
        this.totalElementsFaz = totalElementsFaz;
        this.nombreTotalPagesFaz = (int) Math.ceil((double) totalElementsFaz / taillePageFaz);
    }

    // ==================== GETTERS ====================

    public List<T> getElementsFaz() { return elementsFaz; }
    public int getPageCouranteFaz() { return pageCouranteFaz; }
    public int getTaillePageFaz() { return taillePageFaz; }
    public int getNombreTotalPagesFaz() { return nombreTotalPagesFaz; }
    public long getTotalElementsFaz() { return totalElementsFaz; }

    // ==================== METHODES UTILITAIRES BONUS ====================

    /**
     * Indique si c'est la premiere page.
     */
    public boolean estPremierePageFaz() {
        return pageCouranteFaz == 1;
    }

    /**
     * Indique si c'est la derniere page.
     */
    public boolean estDernierePageFaz() {
        return pageCouranteFaz >= nombreTotalPagesFaz;
    }

    /**
     * Indique s'il y a une page suivante.
     */
    public boolean aPageSuivanteFaz() {
        return pageCouranteFaz < nombreTotalPagesFaz;
    }

    /**
     * Indique s'il y a une page precedente.
     */
    public boolean aPagePrecedenteFaz() {
        return pageCouranteFaz > 1;
    }

    /**
     * Indique si le resultat est vide.
     */
    public boolean estVideFaz() {
        return elementsFaz == null || elementsFaz.isEmpty();
    }

    /**
     * Retourne le numero de la page suivante (ou la meme si derniere page).
     */
    public int prochainNumeroPageFaz() {
        return aPageSuivanteFaz() ? pageCouranteFaz + 1 : pageCouranteFaz;
    }

    /**
     * Retourne le numero de la page precedente (ou la meme si premiere page).
     */
    public int precedentNumeroPageFaz() {
        return aPagePrecedenteFaz() ? pageCouranteFaz - 1 : pageCouranteFaz;
    }

    /**
     * Retourne l'index du premier element de la page (0-based).
     */
    public int indexPremierElementFaz() {
        return (pageCouranteFaz - 1) * taillePageFaz;
    }

    /**
     * Retourne l'index du dernier element de la page (0-based).
     */
    public int indexDernierElementFaz() {
        int dernier = indexPremierElementFaz() + taillePageFaz - 1;
        return (int) Math.min(dernier, totalElementsFaz - 1);
    }

    @Override
    public String toString() {
        return "PaginationFaz [page=" + pageCouranteFaz + "/" + nombreTotalPagesFaz
                + ", taille=" + taillePageFaz
                + ", total=" + totalElementsFaz
                + ", elements=" + (elementsFaz != null ? elementsFaz.size() : 0) + "]";
    }
}
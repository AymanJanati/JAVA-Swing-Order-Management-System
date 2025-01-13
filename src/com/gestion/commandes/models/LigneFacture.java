package com.gestion.commandes.models;

public class LigneFacture {
    private int idLigne;
    private int idFacture;
    private int idProduit;
    private int quantite;
    private double sousTotal;

    // Constructor
    public LigneFacture(int idLigne, int idFacture, int idProduit, int quantite, double sousTotal) {
        this.idLigne = idLigne;
        this.idFacture = idFacture;
        this.idProduit = idProduit;
        this.quantite = quantite;
        this.sousTotal = sousTotal;
    }

    // Getters and Setters
    public int getIdLigne() {
        return idLigne;
    }

    public void setIdLigne(int idLigne) {
        this.idLigne = idLigne;
    }

    public int getIdFacture() {
        return idFacture;
    }

    public void setIdFacture(int idFacture) {
        this.idFacture = idFacture;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getSousTotal() {
        return sousTotal;
    }

    public void setSousTotal(double sousTotal) {
        this.sousTotal = sousTotal;
    }
}
package com.gestion.commandes.models;

public class LigneCommande {
    private int idLigneCommande;
    private int idCommande;
    private int idProduit;
    private int quantite;
    private double sousTotal;

    // Constructor
    public LigneCommande(int idLigneCommande, int idCommande, int idProduit, int quantite, double sousTotal) {
        this.idLigneCommande = idLigneCommande;
        this.idCommande = idCommande;
        this.idProduit = idProduit;
        this.quantite = quantite;
        this.sousTotal = sousTotal;
    }

    // Getters and Setters
    public int getIdLigneCommande() {
        return idLigneCommande;
    }

    public void setIdLigneCommande(int idLigneCommande) {
        this.idLigneCommande = idLigneCommande;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
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
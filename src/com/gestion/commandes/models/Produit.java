package com.gestion.commandes.models;

public class Produit {
    private int idProduit;
    private String nom;
    private double prix;
    private int quantiteEnStock;

    // Constructor
    public Produit(int idProduit, String nom, double prix, int quantiteEnStock) {
        this.idProduit = idProduit;
        this.nom = nom;
        this.prix = prix;
        this.quantiteEnStock = quantiteEnStock;
    }

    // Getters and Setters
    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getQuantiteEnStock() {
        return quantiteEnStock;
    }

    public void setQuantiteEnStock(int quantiteEnStock) {
        this.quantiteEnStock = quantiteEnStock;
    }
}
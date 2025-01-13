package com.gestion.commandes.models;

import java.util.List;

public class Commande {
    private int idCommande;
    private String date;
    private int idClient;
    private List<LigneCommande> lignesCommande;

    // Constructor
    public Commande(int idCommande, String date, int idClient, List<LigneCommande> lignesCommande) {
        this.idCommande = idCommande;
        this.date = date;
        this.idClient = idClient;
        this.lignesCommande = lignesCommande;
    }

    // Getters and Setters
    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public List<LigneCommande> getLignesCommande() {
        return lignesCommande;
    }

    public void setLignesCommande(List<LigneCommande> lignesCommande) {
        this.lignesCommande = lignesCommande;
    }
}
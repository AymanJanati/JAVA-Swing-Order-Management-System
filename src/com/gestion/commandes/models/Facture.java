package com.gestion.commandes.models;

import java.util.List;

public class Facture {
    private int idFacture;
    private String date;
    private double montantTotal;
    private int idClient;
    private List<LigneFacture> lignesFacture;

    // Constructor
    public Facture(int idFacture, String date, double montantTotal, int idClient, List<LigneFacture> lignesFacture) {
        this.idFacture = idFacture;
        this.date = date;
        this.montantTotal = montantTotal;
        this.idClient = idClient;
        this.lignesFacture = lignesFacture;
    }

    // Getters and Setters
    public int getIdFacture() {
        return idFacture;
    }

    public void setIdFacture(int idFacture) {
        this.idFacture = idFacture;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public List<LigneFacture> getLignesFacture() {
        return lignesFacture;
    }

    public void setLignesFacture(List<LigneFacture> lignesFacture) {
        this.lignesFacture = lignesFacture;
    }
}
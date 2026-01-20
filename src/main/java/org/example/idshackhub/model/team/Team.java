package org.example.idshackhub.model.team;

public class Team extends AbstractTeam {
    private String iban;

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getIban() {
        return iban;
    }
}
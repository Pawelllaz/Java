package com.company;

public class TestClass {
    private String whatAnimalSay;
    private boolean animalExist;
    private int legsAndArms;
    private double percentPopulationInZoo;

    public boolean isAnimalExist() {
        return animalExist;
    }

    public int getLegsAndArms() {
        return legsAndArms;
    }

    public double getPercentPopulationInZoo() {
        return percentPopulationInZoo;
    }

    public String getWhatAnimalSay() {
        return whatAnimalSay;
    }

    public void setAnimalExist(boolean animalExist) {
        this.animalExist = animalExist;
    }

    public void setLegsAndArms(int legsAndArms) {
        this.legsAndArms = legsAndArms;
    }

    public void setPercentPopulationInZoo(double percentPopulationInZoo) {
        this.percentPopulationInZoo = percentPopulationInZoo;
    }

    public void setWhatAnimalSay(String whatAnimalSay) {
        this.whatAnimalSay = whatAnimalSay;
    }
}

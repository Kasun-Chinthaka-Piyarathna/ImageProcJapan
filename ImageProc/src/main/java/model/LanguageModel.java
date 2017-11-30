/*
 *   (C) Copyright 1996-2017 hSenid Software International (Pvt) Limited.
 *   All Rights Reserved.
 *
 *   These materials are unpublished, proprietary, confidential source code of
 *   hSenid Software International (Pvt) Limited and constitute a TRADE SECRET
 *   of hSenid Software International (Pvt) Limited.
 *
 *   hSenid Software International (Pvt) Limited retains all title to and intellectual
 *   property rights in these materials.
 *
 */
package model;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import sample.Main;

import java.util.*;

public class LanguageModel extends Observable {
    private ResourceBundle bundle;
    private Language lang;
    private Language2 lang2;
    private Language l1 = Language.JP;
    private Language2 l2 = Language2.EN;
    public static String choice;


    public LanguageModel() {


        Main.count_number++;

        System.out.println("count" + Main.count_number);

        if (Main.count_number == 2) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Language Changer");
            alert.setHeaderText("Japan | English.");
            alert.setContentText("Would you like to choose JAPANESE ");


            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                choice = "JAPAN";
                setBundle(l1);
            } else {
                choice = "ENGLISH";
                setBundle2(l2);
            }
        }
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(Language lang) {
        if (lang == null || lang.equals(this.bundle)) return;
        setLang(lang);
        bundle = ResourceBundle.getBundle("lang", new Locale(lang.getValue(), lang.toString()));
        setChanged();
        notifyObservers();
    }

    public void setBundle2(Language2 lang2) {
        if (lang2 == null || lang2.equals(this.bundle)) return;
        setLang(lang2);
        bundle = ResourceBundle.getBundle("lang", new Locale(lang2.getValue(), lang2.toString()));
        setChanged();
        notifyObservers();
    }


    public Language getLang() {
        return lang;
    }

    public Language2 getLang2() {
        return lang2;
    }

    public void setLang(Language lang) {

        this.lang = lang;
    }

    public void setLang(Language2 lang2) {

        this.lang2 = lang2;
    }


    public enum Language {


        JP("ja");

        private String value;

        Language(String s) {
            value = s;
        }

        public String getValue() {
            return value;
        }
    }

    public enum Language2 {


        EN("en");

        private String value;

        Language2(String s) {
            value = s;
        }

        public String getValue() {
            return value;
        }
    }

}

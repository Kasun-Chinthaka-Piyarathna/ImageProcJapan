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

import org.opencv.core.*;

import java.util.*;

public class MessageModel extends Observable {

    private String message;
    private Mat image;

    public MessageModel(){
        setMessage("Default Message...");
        setImage(new Mat());
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        if(message == null || message.equals(this.message)) return;
        this.message = message;
        setChanged();
        notifyObservers();
    }

    public Mat getImage() {
        return image;
    }

    public void setImage(Mat image) {
        if(image == null || image.equals(this.image)) return;
        this.image = image;
        setChanged();
        notifyObservers();
    }
}

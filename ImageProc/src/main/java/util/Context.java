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
package util;

import javafx.scene.image.*;

public class Context {
    private final static Context instance = new Context();

    public static Context getInstance() {
        return instance;
    }

    private Image resultImage ;

    public Image getResultImage() {
        return resultImage;
    }

    public void setResultImage(Image resultImage) {
        this.resultImage = resultImage;
    }
}

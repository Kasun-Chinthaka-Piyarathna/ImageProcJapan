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
package gui;

import config.*;

public abstract class Modal extends Presentation {

    protected ModalDialog dialog;

    public Modal(ScreensConfig config) {
        super(config);
    }

    public void setDialog(ModalDialog dialog) {
        this.dialog = dialog;
    }
}

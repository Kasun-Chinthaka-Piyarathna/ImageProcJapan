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
package config;

import model.*;
import org.springframework.context.annotation.*;
import sample.*;

@Configuration
@Import(ScreensConfig.class)
public class AppConfig {
    @Bean
    LanguageModel languageModel() {
        return new LanguageModel();
    }

    @Bean
    LanguageController languageController() {
        return new LanguageController(languageModel());
    }

    @Bean
    MessageModel messageModel() {
        return new MessageModel();
    }
}
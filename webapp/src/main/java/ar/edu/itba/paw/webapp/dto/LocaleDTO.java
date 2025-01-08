package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.validators.interfaces.MustBeAcceptedLanguage;

public class LocaleDTO {

    @MustBeAcceptedLanguage(message = "{Locale}")
    private String locale;

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public LocaleDTO() {
    }

}

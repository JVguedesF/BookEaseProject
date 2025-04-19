package com.bookease.model.enums;

import lombok.Getter;

@Getter
public enum SpecialityEnum {
    ODONTOLOGIA_GERAL("Odontologia Geral"),
    ORTODONTIA("Ortodontia"),
    ENDODONTIA("Tratamento de Canal"),
    PERIODONTIA("Periodontia"),
    PROTESE_DENTARIA("Prótese Dentária"),
    CIRURGIA_ORAL("Cirurgia Oral"),
    ODONTOPEDIATRIA("Odontopediatria"),
    ODONTOLOGIA_ESTETICA("Odontologia Estética"),
    IMPLANTOLOGIA("Implantodontia"),
    APARELHO_DENTAL("Aparelho Ortodôntico"),
    CLAREAMENTO_DENTAL("Clareamento Dental"),
    HARMONIZACAO_OROFACIAL("Harmonização Orofacial");

    private final String displayName;

    SpecialityEnum(String displayName) {
        this.displayName = displayName;
    }
}
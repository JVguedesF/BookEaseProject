package com.bookease.model.enums;

import lombok.Getter;

@Getter
public enum ProcedureEnum {
    LIMPEZA_DENTAL("Limpeza dental"),
    CLAREAMENTO_DENTAL("Clareamento dental"),
    TRATAMENTO_DE_CANAL("Tratamento de canal"),
    RESTAURACAO_DENTAL("Restauração dental"),
    EXTRACAO_DENTARIA("Extração dentária"),
    IMPLANTE_DENTARIO("Implante dentário"),
    APLICACAO_DE_FLUOR("Aplicação de flúor"),
    SELANTE_DENTAL("Selante dental"),
    TRATAMENTO_GENGIVAL("Tratamento gengival"),
    PROTESE_FIXA("Prótese fixa"),
    PROTESE_REMOVIVEL("Prótese removível"),
    CIRURGIA_ESTETICA_GENGIVAL("Cirurgia estética gengival"),
    CIRURGIA_BUCO_MAXILO_FACIAL("Cirurgia buco-maxilo-facial"),
    AVALIACAO_ORAL("Avaliação oral"),
    RADIOGRAFIA_ODONTOLOGICA("Radiografia odontológica"),
    TRATAMENTO_DE_HALITOSE("Tratamento de halitose"),
    APARELHO_FIXO("Aparelho fixo"),
    APARELHO_REMOVIVEL("Aparelho removível"),
    MANUTENCAO_ORTODONTICA("Manutenção ortodôntica"),
    CIRURGIA_DE_SISO("Cirurgia de siso"),
    BIOMETRIA_DENTAL("Biometria dental"),
    OBTURACAO("Obturação"),
    RECONTORNO_ESTETICO("Recontorno estético"),
    FACETAS_DE_PORCELANA("Facetas de porcelana"),
    LENTES_DE_CONTATO_DENTAIS("Lentes de contato dentais"),
    TRATAMENTO_PARA_BRUXISMO("Tratamento para bruxismo"),
    CIRURGIA_ORAL_MENOR("Cirurgia oral menor"),
    DRENAGEM_DE_ABSCESSO("Drenagem de abscesso"),
    REMOCAO_DE_TARTARO("Remoção de tártaro"),
    TRATAMENTO_OROFACIAL("Tratamento orofacial"),
    CONSERVACAO_DENTAL("Conservação dental"),
    DIAGNOSTICO_PRECOCE("Diagnóstico precoce");

    private final String displayName;

    ProcedureEnum(String displayName) {
        this.displayName = displayName;
    }
}

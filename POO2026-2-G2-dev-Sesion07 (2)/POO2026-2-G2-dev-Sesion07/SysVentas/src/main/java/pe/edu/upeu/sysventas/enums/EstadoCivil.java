package pe.edu.upeu.sysventas.enums;

import lombok.Getter;

@Getter
public enum EstadoCivil {
    SOLTERO("Soltero(a)"),
    CASADO("Casado(a)"),
    DIVORCIADO("Divorciado(a)"),
    VIUDO("Viudo(a)"),
    UNION_LIBRE("Unión libre");

    private final String descripcion;

    EstadoCivil(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}

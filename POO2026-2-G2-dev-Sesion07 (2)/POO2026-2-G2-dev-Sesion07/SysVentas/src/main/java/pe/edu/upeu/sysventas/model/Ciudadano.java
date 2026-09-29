package pe.edu.upeu.sysventas.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.sysventas.enums.EstadoCivil;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ciudadano {

    private Long idCiudadano;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    @NotBlank(message = "La CURP es obligatoria")
    @Pattern(regexp = "^[A-Z]{4}[0-9]{6}[HMX][A-Z]{5}[A-Z0-9][0-9]$",
            message = "La CURP debe tener 18 caracteres con formato válido (ej. GOPJ900515HDFMRN08)")
    private String curp;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El domicilio es obligatorio")
    private String domicilio;

    @NotNull(message = "El estado civil es obligatorio")
    private EstadoCivil estadoCivil;
}

package pe.edu.upeu.sysventas.service;

import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.model.Ciudadano;

import java.util.List;

public interface ICiudadanoService extends ICrudGenericoService<Ciudadano, Long> {
    List<ComboBoxOption> listarEstadoCivil();

    /** Consulta por nombre o CURP. Si el texto está vacío devuelve todos. */
    List<Ciudadano> buscar(String texto);
}

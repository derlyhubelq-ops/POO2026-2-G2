package pe.edu.upeu.sysventas.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.enums.EstadoCivil;
import pe.edu.upeu.sysventas.model.Ciudadano;
import pe.edu.upeu.sysventas.repository.CiudadanoRepository;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.service.ICiudadanoService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CiudadanoServiceImp extends CrudGenericoServiceImp<Ciudadano, Long>
        implements ICiudadanoService {

    private final CiudadanoRepository ciudadanoRepository;

    @Override
    protected ICrudGenericoRepository<Ciudadano, Long> getRepo() {
        return ciudadanoRepository;
    }

    @Override
    public Ciudadano save(Ciudadano ciudadano) {
        validarCurpUnica(ciudadano);
        return super.save(ciudadano);
    }

    @Override
    public Ciudadano update(Long id, Ciudadano ciudadano) {
        validarCurpUnica(ciudadano);
        return super.update(id, ciudadano);
    }

    @Override
    public List<ComboBoxOption> listarEstadoCivil() {
        List<ComboBoxOption> listar = new ArrayList<>();
        for (EstadoCivil ec : EstadoCivil.values()) {
            listar.add(new ComboBoxOption(ec.name(), ec.getDescripcion()));
        }
        return listar;
    }

    @Override
    public List<Ciudadano> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return findAll();
        }
        String t = texto.trim().toLowerCase();
        return findAll().stream()
                .filter(c -> c.getNombreCompleto().toLowerCase().contains(t)
                        || c.getCurp().toLowerCase().contains(t))
                .toList();
    }

    /** Regla de negocio: la CURP no puede repetirse entre ciudadanos distintos. */
    private void validarCurpUnica(Ciudadano ciudadano) {
        ciudadanoRepository.findByCurp(ciudadano.getCurp()).ifPresent(existente -> {
            if (!existente.getIdCiudadano().equals(ciudadano.getIdCiudadano())) {
                throw new IllegalStateException("Ya existe un ciudadano registrado con esa CURP");
            }
        });
    }
}

package pe.edu.upeu.sysventas.repository;

import pe.edu.upeu.sysventas.enums.EstadoCivil;
import pe.edu.upeu.sysventas.model.Ciudadano;

import java.time.LocalDate;
import java.util.Optional;

public class CiudadanoRepository extends AbstractJpaRepository<Ciudadano, Long> {
    private long sequence = 1;

    public CiudadanoRepository() {
        seedData();
    }

    @Override
    protected Long getId(Ciudadano entity) {
        return entity.getIdCiudadano();
    }

    @Override
    protected void setId(Ciudadano entity, Long id) {
        entity.setIdCiudadano(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }

    /** Busca un ciudadano por CURP (sin distinguir mayúsculas). */
    public Optional<Ciudadano> findByCurp(String curp) {
        if (curp == null) return Optional.empty();
        return data.stream()
                .filter(c -> c.getCurp() != null && c.getCurp().equalsIgnoreCase(curp.trim()))
                .findFirst();
    }

    /** Datos de ejemplo que se cargan una sola vez al iniciar la aplicación. */
    private void seedData() {
        save(new Ciudadano(null, "Juan Carlos Gómez Pérez", "GOPJ900515HDFMRN08",
                LocalDate.of(1990, 5, 15), "Av. Universidad 123, Juliaca", EstadoCivil.CASADO));
        save(new Ciudadano(null, "María Fernanda López Ruiz", "LORM951120MJCPZR03",
                LocalDate.of(1995, 11, 20), "Jr. Lima 456, Puno", EstadoCivil.SOLTERO));
        save(new Ciudadano(null, "Luis Ángel Ramírez Torres", "RATL880703HNLMRS05",
                LocalDate.of(1988, 7, 3), "Calle Los Pinos 78, Juliaca", EstadoCivil.DIVORCIADO));
    }
}

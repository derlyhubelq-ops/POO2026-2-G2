package pe.edu.upeu.sysventas.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.components.ColumnInfo;
import pe.edu.upeu.sysventas.components.TableViewHelper;
import pe.edu.upeu.sysventas.components.Toast;
import pe.edu.upeu.sysventas.components.ToltipCustom;
import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.enums.EstadoCivil;
import pe.edu.upeu.sysventas.model.Ciudadano;
import pe.edu.upeu.sysventas.service.ICiudadanoService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CiudadanoController {

    private final ICiudadanoService cs;

    @FXML TextField txtNombreCompleto, txtCurp, txtDomicilio, txtBuscar;
    @FXML DatePicker dpFechaNacimiento;
    @FXML ComboBox<ComboBoxOption> cbxEstadoCivil;
    @FXML Button btnAgregar, btnModificar, btnEliminar;
    @FXML TableView<Ciudadano> tableView;
    @FXML Label lbnMsg;
    @FXML AnchorPane miContenedor;

    private Validator validator;
    private final ToltipCustom ttc = new ToltipCustom();

    /** 0 = formulario en modo "nuevo"; > 0 = editando el ciudadano con ese id. */
    private Long idCiudadanoCE = 0L;

    // ------------------------------------------------------------------
    //  INICIALIZACIÓN
    // ------------------------------------------------------------------
    @FXML
    public void initialize() {
        cbxEstadoCivil.getItems().addAll(cs.listarEstadoCivil());
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        // La CURP siempre en mayúsculas y máximo 18 caracteres
        txtCurp.textProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo == null) return;
            String norm = nuevo.toUpperCase();
            if (norm.length() > 18) norm = (viejo == null ? "" : viejo);
            if (!norm.equals(nuevo)) txtCurp.setText(norm);
        });

        // Tabla (reutiliza TableViewHelper: iconos de editar / eliminar por fila)
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("idCiudadano", 50.0));
        columns.put("Nombre completo", new ColumnInfo("nombreCompleto", 220.0));
        columns.put("CURP", new ColumnInfo("curp", 170.0));
        columns.put("Fecha nac.", new ColumnInfo("fechaNacimiento", 100.0));
        columns.put("Domicilio", new ColumnInfo("domicilio", 230.0));
        columns.put("Estado civil", new ColumnInfo("estadoCivil", 110.0));

        new TableViewHelper<Ciudadano>().addColumnsInOrderWithSize(
                tableView, columns, this::editForm, this::confirmarEliminar);
        tableView.setTableMenuButtonVisible(true);

        // Al seleccionar una fila se carga en el formulario
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) editForm(nuevo);
        });

        actualizarBotones();
        listar();
    }

    // ------------------------------------------------------------------
    //  CRUD
    // ------------------------------------------------------------------

    /** ALTA (Create). */
    @FXML
    public void agregar() {
        Ciudadano c = leerFormulario();
        if (!validar(c)) return;
        try {
            cs.save(c);
            mostrarToast("Ciudadano registrado correctamente!!");
            mensaje("Ciudadano registrado", false);
            clearForm();
            listar();
        } catch (IllegalStateException ex) {
            errorEnCampo(txtCurp, ex.getMessage());
        }
    }

    /** CONSULTA (Read): por nombre o CURP. Con el cuadro vacío muestra todos. */
    @FXML
    public void consultar() {
        List<Ciudadano> resultado = cs.buscar(txtBuscar.getText());
        tableView.getItems().setAll(resultado);
        mensaje(resultado.size() + " ciudadano(s) encontrado(s)", false);
    }

    @FXML
    public void mostrarTodos() {
        txtBuscar.clear();
        listar();
        mensaje("", false);
    }

    /** MODIFICACIÓN (Update): actualiza el ciudadano cargado en el formulario. */
    @FXML
    public void modificar() {
        if (idCiudadanoCE <= 0L) {
            mensaje("Seleccione un ciudadano de la tabla para modificarlo", true);
            return;
        }
        Ciudadano c = leerFormulario();
        c.setIdCiudadano(idCiudadanoCE);
        if (!validar(c)) return;
        try {
            cs.update(idCiudadanoCE, c);
            mostrarToast("Ciudadano actualizado correctamente!!");
            mensaje("Ciudadano actualizado", false);
            clearForm();
            listar();
        } catch (IllegalStateException ex) {
            errorEnCampo(txtCurp, ex.getMessage());
        }
    }

    /** BAJA (Delete): botón de la barra de acciones. */
    @FXML
    public void eliminar() {
        if (idCiudadanoCE <= 0L) {
            mensaje("Seleccione un ciudadano de la tabla para eliminarlo", true);
            return;
        }
        confirmarEliminar(cs.findById(idCiudadanoCE));
    }

    /** También lo usa el icono de eliminar de cada fila. */
    private void confirmarEliminar(Ciudadano c) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Desea eliminar a " + c.getNombreCompleto() + " del padrón?",
                ButtonType.YES, ButtonType.NO);
        alerta.setTitle("Confirmar eliminación");
        alerta.setHeaderText(null);
        alerta.showAndWait()
                .filter(b -> b == ButtonType.YES)
                .ifPresent(b -> {
                    cs.delete(c.getIdCiudadano());
                    mostrarToast("Se eliminó correctamente!!");
                    clearForm();
                    listar();
                });
    }

    // ------------------------------------------------------------------
    //  FORMULARIO
    // ------------------------------------------------------------------
    private Ciudadano leerFormulario() {
        ComboBoxOption ec = cbxEstadoCivil.getSelectionModel().getSelectedItem();
        return Ciudadano.builder()
                .nombreCompleto(textoONull(txtNombreCompleto.getText()))
                .curp(textoONull(txtCurp.getText()) == null ? null : txtCurp.getText().trim().toUpperCase())
                .fechaNacimiento(dpFechaNacimiento.getValue())
                .domicilio(textoONull(txtDomicilio.getText()))
                .estadoCivil(ec == null ? null : EstadoCivil.valueOf(ec.getKey()))
                .build();
    }

    /** Devuelve null si el texto está vacío, así solo salta @NotBlank y no también @Pattern. */
    private String textoONull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    public void editForm(Ciudadano c) {
        txtNombreCompleto.setText(c.getNombreCompleto());
        txtCurp.setText(c.getCurp());
        dpFechaNacimiento.setValue(c.getFechaNacimiento());
        txtDomicilio.setText(c.getDomicilio());
        cbxEstadoCivil.getSelectionModel().select(
                cbxEstadoCivil.getItems().stream()
                        .filter(o -> o.getKey().equals(c.getEstadoCivil().name()))
                        .findFirst().orElse(null));
        idCiudadanoCE = c.getIdCiudadano();
        actualizarBotones();
        limpiarError();
        mensaje("Editando a " + c.getNombreCompleto(), false);
    }

    @FXML
    public void clearForm() {
        txtNombreCompleto.clear();
        txtCurp.clear();
        txtDomicilio.clear();
        dpFechaNacimiento.setValue(null);
        cbxEstadoCivil.getSelectionModel().clearSelection();
        tableView.getSelectionModel().clearSelection();
        idCiudadanoCE = 0L;
        actualizarBotones();
        limpiarError();
    }

    /** "Agregar" solo en modo nuevo; "Modificar" y "Eliminar" solo al editar. */
    private void actualizarBotones() {
        boolean editando = idCiudadanoCE > 0L;
        btnAgregar.setDisable(editando);
        btnModificar.setDisable(!editando);
        btnEliminar.setDisable(!editando);
    }

    private void listar() {
        tableView.getItems().setAll(cs.findAll());
    }

    // ------------------------------------------------------------------
    //  VALIDACIÓN Y MENSAJES
    // ------------------------------------------------------------------
    private boolean validar(Ciudadano c) {
        limpiarError();
        List<ConstraintViolation<Ciudadano>> violaciones = validator.validate(c).stream().toList();
        if (violaciones.isEmpty()) return true;

        Map<String, Control> campos = new LinkedHashMap<>();
        campos.put("nombreCompleto", txtNombreCompleto);
        campos.put("curp", txtCurp);
        campos.put("fechaNacimiento", dpFechaNacimiento);
        campos.put("domicilio", txtDomicilio);
        campos.put("estadoCivil", cbxEstadoCivil);

        Control primero = null;
        String primerMensaje = null;
        for (Map.Entry<String, Control> campo : campos.entrySet()) {
            for (ConstraintViolation<Ciudadano> v : violaciones) {
                if (v.getPropertyPath().toString().equals(campo.getKey())) {
                    ttc.marcarError(campo.getValue(), v.getMessage());
                    if (primero == null) {
                        primero = campo.getValue();
                        primerMensaje = v.getMessage();
                    }
                    break;   // un solo error por campo
                }
            }
        }
        if (primerMensaje != null) mensaje(primerMensaje, true);
        if (primero != null) Platform.runLater(primero::requestFocus);
        return false;
    }

    private void errorEnCampo(Control campo, String texto) {
        ttc.marcarError(campo, texto);
        mensaje(texto, true);
        Platform.runLater(campo::requestFocus);
    }

    private void limpiarError() {
        List.<Control>of(txtNombreCompleto, txtCurp, dpFechaNacimiento, txtDomicilio, cbxEstadoCivil)
                .forEach(ttc::limpiarCampo);
    }

    private void mensaje(String texto, boolean esError) {
        lbnMsg.setText(texto);
        lbnMsg.setStyle(esError
                ? "-fx-text-fill: red; -fx-font-size: 14px;"
                : "-fx-text-fill: green; -fx-font-size: 14px;");
    }

    private void mostrarToast(String texto) {
        Stage stage = (Stage) miContenedor.getScene().getWindow();
        Toast.showToast(stage, texto, 2000, stage.getWidth() / 1.5, stage.getHeight() / 2);
    }
}

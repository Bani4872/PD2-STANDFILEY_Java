package Calculadora;

import javax.swing.*;

public class CalcMVC {
    public static void main(String[] args) {
        // Creación única del modelo, vista y controlador
        CalcModel      model      = new CalcModel();
        CalcView       view       = new CalcView();
        CalcController controller = new CalcController(model, view);

        // Desplegar la interfaz
        view.setVisible(true);
    }
}

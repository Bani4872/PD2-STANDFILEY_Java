# Diagrama de Clases - Calculadora MVC

Este diagrama representa la estructura de la calculadora utilizando el patrón de diseño Modelo-Vista-Controlador (MVC) sin la implementación del patrón Command, basándose en un bloque de control de flujo para la selección de las operaciones.

```mermaid
classDiagram
    class CalcMVC {
        +main(args: String[]) void
    }

    class CalcModel {
        -INITIAL_VALUE : String
        -m_total : BigInteger
        +CalcModel()
        +reset() void
        +multiplyBy(operand: String) void
        +add(operand: String) void
        +subtract(operand: String) void
        +getValue() String
        +setValue(value: String) void
    }

    class CalcView {
        -INITIAL_VALUE : String
        -m_userInputTf : JTextField
        -m_totalTf : JTextField
        -m_calcBtn : JButton
        -m_clearBtn : JButton
        -m_operationCb : JComboBox~String~
        +CalcView()
        ~reset() void
        ~getUserInput() String
        ~getOperation() String
        ~setTotal(newTotal: String) void
        ~showError(errMessage: String) void
        ~addCalculateListener(mal: ActionListener) void
        ~addClearListener(cal: ActionListener) void
    }

    class CalcController {
        -m_model : CalcModel
        -m_view : CalcView
        +CalcController(model: CalcModel, view: CalcView)
    }

    class CalculateListener {
        <<Inner Class>>
        +actionPerformed(e: ActionEvent) void
    }

    class ClearListener {
        <<Inner Class>>
        +actionPerformed(e: ActionEvent) void
    }

    CalcMVC --> CalcModel : Instancia
    CalcMVC --> CalcView : Instancia
    CalcMVC --> CalcController : Instancia
    CalcController --> CalcModel : Modifica
    CalcController --> CalcView : Actualiza / Lee
    CalcController *-- CalculateListener : Contiene
    CalcController *-- ClearListener : Contiene
```
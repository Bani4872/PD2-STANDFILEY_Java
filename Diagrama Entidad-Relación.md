# Diagrama Entidad-Relación (ER) - Base de Datos FILEY

## Leyenda de Notación y Restricciones

### Restricciones de Columna
* **`PK` (Primary Key):** Llave primaria que identifica de manera única cada registro de la tabla.
* **`FK` (Foreign Key):** Llave foránea lógica que referencia a la llave primaria de otra tabla (`Editoriales` o `Stands`).
* **`UK` (Unique Key):** Restricción `UNIQUE` que impide que un mismo stand sea reservado o comprado más de una vez simultáneamente.

### Cardinalidades Utilizadas
* **`||--o{` (Uno a Muchos):** Una editorial puede realizar cero o múltiples reservas o compras.
* **`||--o|` (Uno a Cero o Uno):** Un stand puede estar asociado a lo sumo a una reserva activa, a una compra definitiva o a una asignación especial debido a su restricción `UNIQUE` / `PRIMARY KEY`.

---

## Diagrama Entidad-Relación (MySQL - Clever Cloud)

```mermaid
erDiagram
    EDITORIALES {
        int identificador PK "Hash del nombre de la editorial"
        varchar_100 nombre "Nombre comercial de la editorial"
    }

    STANDS {
        int Identificador PK "Número del stand (1 al 10)"
        int Estado "0: Disponible, 1: Reservado, 2: Comprado, 3: Bloqueado"
        int Alto "Medida de alto en metros"
        int Ancho "Medida de ancho en metros"
        double Costo "Alto * Ancho * CostoMetroCuadrado"
    }

    RESERVAS {
        int Id_Reserva PK "AUTO_INCREMENT"
        int Id_Editorial FK "Referencia a Editoriales.identificador"
        int Id_Stand FK, UK "Referencia única a Stands.Identificador"
        int Dia "Día del mes en que se realizó el apartado"
        int Mes "Número de mes (1-12) del apartado"
    }

    COMPRAS {
        int Id_Compra PK "AUTO_INCREMENT"
        int Id_Editorial FK "Referencia a Editoriales.identificador"
        int Id_Stand FK, UK "Referencia única a Stands.Identificador"
    }

    EDITORIALES_ESPECIALES {
        int Id_Editorial PK, FK "Hash de UADY, Planeta o Diario de Yucatán"
        int Id_Stand FK "Stand exclusivo pre-bloqueado (3, 4 o 5)"
    }

    EDITORIALES ||--o{ RESERVAS : "realiza"
    STANDS ||--o| RESERVAS : "es apartado en"

    EDITORIALES ||--o{ COMPRAS : "adquiere"
    STANDS ||--o| COMPRAS : "es vendido en"

    EDITORIALES ||--o| EDITORIALES_ESPECIALES : "tiene beneficio"
    STANDS ||--o| EDITORIALES_ESPECIALES : "se reserva exclusivamente para"
```

---

## Descripción de las Tablas y Relaciones

1. **`Editoriales` $\leftrightarrow$ `Reservas` y `Compras` (`1:N`):** Una misma editorial puede apartar o adquirir varios stands distintos durante la feria, registrando su `identificador` en las columnas `Id_Editorial`.
2. **`Stands` $\leftrightarrow$ `Reservas` y `Compras` (`1:0..1`):** Gracias a la restricción `UNIQUE` en `Id_Stand`, cada stand físico del croquis solo puede aparecer una vez en `Reservas` o una vez en `Compras`, evitando duplicidades o conflictos de asignación.
3. **`EditorialesEspeciales`:** Actúa como tabla de configuración de privilegios previos al evento, vinculando directamente el identificador de tres editoriales invitadas (`UADY`, `Planeta` y `Diario de Yucatán`) con su stand reservado (`5`, `3` y `4` respectivamente) para desbloquearlo automáticamente al iniciar sesión.
# Requisitos:

## Funcionales:

* **Multiplicar valor ingresado por el total acumulado:** El sistema debe permitir ingresar un número en el campo de texto y, al presionar el botón "Multiply", multiplicar dicho valor por el total acumulado actual y mostrar el resultado en pantalla.

* **Inicializacion del sistema:** Al iniciar la aplicación, el total acumulado debe inicializarse automáticamente en "1".

* **Reinicio de la calculadora:** Al presionar el botón "Clear", el sistema debe restablecer el total acumulado al valor inicial "1" y limpiar la caja de entrada del usuario.

* **Manejo de errores en la entrada de datos:** El sistema debe validar que el texto ingresado sea un número válido. Si el usuario ingresa caracteres no numéricos o una cadena vacía, debe capturar la excepción (NumberFormatException) y desplegar un mensaje emergente de error (showError) indicando el valor inválido evitando cerrar el programa.

* **RF05 - Bloqueo del campo de resultado:** El cuadro de texto correspondiente al "Total" debe ser de solo lectura para evitar que el usuario modifique manualmente el valor acumulado.

# No funcionales:

* **Presición:** El modelo utliza BigInteger en vez de primitivos, para poder realizar operaciones con numeros grandes sin ocasionar desbordamiento.

* **Usabilidad de interfaz:** Interfaz con componentes nativos de java swing con layout fluido.

* **Robustez y tolerancia a fallos:** Uso de bloques try-catch para evitar que el programa cierre abruptamente.

## Requisito PD3

* **Suma y resta** Incluir la funcionalidad de sumar y restar el valor ingresado al total acumulado, además de la multiplicación ya implementada.

### Instrucciones:

* Video que explique la estrategia para incrementar las operaciones de la calculadora (suma, resta, etc). Mantener el patrón MCV.  Indicar qué ventajas o desventajas específicas hay si se considera el patrón de diseño Command. 

* Si los argumentos indican que no es conveniente, indicar cuál sería la estrategia considerada para incrementar las operaciones de la calculadora, y las ventajas sobre el uso del patrón. Se podría considerar algún otro patrón?

* **LINK DEL VIDEO:** https://youtu.be/Yrd1tSY-bqo
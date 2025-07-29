package Utility;

import java.lang.reflect.Field;
import java.util.List;

public class ItemUtils {

    /**
     * Copia campos desde source hacia item solo si en item están vacíos,
     * excepto los campos en excluir. Los campos en proteger siempre se copian.
     *
     * @param item     Objeto destino
     * @param source   Objeto fuente
     * @param excluir  Lista de nombres de campos que no se deben copiar (puede ser null)
     * @param proteger Lista de nombres de campos que siempre se copian (puede ser null)
     */
    public static void copiarCamposSiVacios(Item item, Item source, List<String> excluir, List<String> proteger) {
        if (item == null || source == null) return;

        Field[] fields = Item.class.getDeclaredFields();

        for (Field field : fields) {
            String nombreCampo = field.getName();

            if (excluir != null && excluir.contains(nombreCampo)) {
                // Campo en excluir, saltar
                continue;
            }

            try {
                field.setAccessible(true);

                Object valorSource = field.get(source);
                Object valorItem = field.get(item);

                // Campo en proteger: copiar siempre desde source
                if (proteger != null && proteger.contains(nombreCampo)) {
                    field.set(item, valorSource);
                    continue;
                }

                // Verificar si el campo está "vacío"
                boolean estaVacio = false;

                if (valorItem == null) {
                    estaVacio = true;
                } else if (valorItem instanceof String) {
                    estaVacio = ((String) valorItem).isEmpty();
                } else if (valorItem instanceof List) {
                    estaVacio = ((List<?>) valorItem).isEmpty();
                } else if (valorItem.getClass().isArray()) {
                    estaVacio = (java.lang.reflect.Array.getLength(valorItem) == 0);
                } else {
                    // Para otros tipos, considerar no vacío (o implementar más reglas si deseas)
                    estaVacio = false;
                }

                if (estaVacio) {
                    field.set(item, valorSource);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}

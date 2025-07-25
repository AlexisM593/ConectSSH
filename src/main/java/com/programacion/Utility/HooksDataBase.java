package com.programacion.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.programacion.Protocol.Protocol;
import com.programacion.Protocol.SecureShell_DB;
import com.programacion.Protocol.SecureShell_metod;

public class HooksDataBase {

    public static SecureShell_DB beforeInsert(SecureShell_DB item, SecureShell_DB context, Protocol itemConect)
            throws Exception {


        int diasrestantes = calculateRemainingDays(itemConect.getVigencia());

        processIPs(item, itemConect, diasrestantes);

        return item;
    }

    private static int calculateRemainingDays(String fechaVencimientoStr) {
        Date hoy = new Date();
        Date fechaVencimiento = java.sql.Date.valueOf(fechaVencimientoStr);
        long tiempoRestante = fechaVencimiento.getTime() - hoy.getTime();
        int diasRestantes = (int) Math.ceil((double) tiempoRestante / (1000 * 60 * 60 * 24));
        return Math.max(diasRestantes, 1);
    }

    private static void processIPs(SecureShell_DB item, Protocol itemConect, int diasrestantes) throws Exception {
        List<String> ipArray = Arrays.asList(itemConect.getIp().split(","));

        List<Map<String, Object>> exists = consultUCSSH(item.gethwiDs(), ipArray);

        List<Map<String, Object>> existResult = new ArrayList<>();
        existResult.addAll(exists);

        List<Map<String, Object>> usersAlreadyExist = new ArrayList<>();
        for (Map<String, Object> obj : existResult) {
            if (Boolean.TRUE.equals(obj.get("result"))) {
                usersAlreadyExist.add(obj);
            }
        }

        if (!usersAlreadyExist.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Map<String, Object> obj : usersAlreadyExist) {
                sb.append(obj.toString()).append("\n");
            }
            throw new Exception("El usuario ya existe\n" + sb.toString());
        } else {
            List<SecureShell_metod.Result> overallResults = SecureShell_metod.crearCuenta(ipArray, item.gethwiDs(),
                    diasrestantes, 3);
            boolean todosTienenError = overallResults.stream()
                    .allMatch(obj -> obj.error != null);

            StringBuilder resultString = new StringBuilder();
            for (SecureShell_metod.Result obj : overallResults) {
                resultString.append(obj.toString());
            }

            if (todosTienenError) {
                System.err.println("Todos los intentos fallaron:\n" + resultString);
                throw new Exception("Todos los intentos de creación de usuarios SSH fallaron.");
            }

            System.out.println("Resultado exitoso: " + resultString);
        }
    }

    // Métodos simulados (debes implementarlos con tu lógica real)
    private static List<Map<String, Object>> consultUCSSH(List<String> arrayuser, List<String> ipArray) {
        return new ArrayList<>();
    }

}

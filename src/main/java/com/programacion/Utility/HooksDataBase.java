package com.programacion.Utility;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.programacion.Protocol.SecureShell_DB;
import static com.programacion.Protocol.SecureShell_DB.verificarHWIDsinDuplicadosPorIP;
import com.programacion.Protocol.SecureShell_metod;
import com.programacion.UxController.UtilityUX.Tool;
import com.programacion.UxController.UtilityUX.Tool.Result;

public class HooksDataBase {

    public static SecureShell_DB beforeInsertSSH(SecureShell_DB item)
            throws Exception {

        int diasrestantes = Tool.calculateRemainingDays(item.getVigencia());

        processIPs(item, diasrestantes);

        return item;
    }

    private static void processIPs(SecureShell_DB item, int diasrestantes) throws Exception {
        List<String> ipArray = Arrays.asList(item.getIp().split(","));

        System.out.println("PROCESS IP " + item.getVigencia());
        System.out.println("PROCESS IP " + ipArray.toString());

        List<Map<String, Object>> exists = verificarHWIDsinDuplicadosPorIP(item.gethwiDs(), ipArray);

        List<Map<String, Object>> usersAlreadyExist = new ArrayList<>();
        for (Map<String, Object> obj : exists) {
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
            System.err.println("Creando cuenta ssh:");
            List<Result> overallResults = SecureShell_metod.crearCuenta(ipArray, item.gethwiDs(),
                    diasrestantes, true);
            boolean todosTienenError = overallResults.stream()
                    .allMatch(obj -> obj.error != null);

            StringBuilder resultString = new StringBuilder();

            if (todosTienenError) {
                System.err.println("Todos los intentos fallaron:\n" + resultString);
                throw new Exception("Todos los intentos de creación de usuarios SSH fallaron.");
            }

            System.out.println("Resultado exitoso: " + resultString);
        }
    }

   public static CompletableFuture<Void> beforeUpdateSSH(SecureShell_DB item, SecureShell_DB previousItem) {

    return CompletableFuture.runAsync(() -> {
     
        List<String> itemIPList = parseIPs(item.getIp());
        List<String> previousIPList = parseIPs(previousItem.getIp());

        List<String> newIPs = itemIPList.stream()
                .filter(ip -> !previousIPList.contains(ip) && !ip.equals("0.0.0.0"))
                .collect(Collectors.toList());

        List<String> removedIPs = previousIPList.stream()
                .filter(ip -> !itemIPList.contains(ip) && !ip.equals("0.0.0.0"))
                .collect(Collectors.toList());

        int diasRestantes = Tool.calculateRemainingDays(item.getVigencia());

        List<String> array1 = previousItem.gethwiDs() != null ? previousItem.gethwiDs() : new ArrayList<>();
        List<String> array2 = item.gethwiDs() != null ? item.gethwiDs() : new ArrayList<>();

        List<CompletableFuture<Void>> promesas = new ArrayList<>();

        if (!removedIPs.isEmpty()) {
            List<String> arrayNew = previousItem.gethwiDs();
            promesas.add(SecureShell_metod.eliminarCuentaAsync(arrayNew, removedIPs));
        }

        Set<String> set2 = new HashSet<>(array2);
        List<String> difer = array1.stream()
                .filter(ip -> !set2.contains(ip))
                .collect(Collectors.toList());

        if (!difer.isEmpty()) {
            promesas.add(SecureShell_metod.eliminarCuentaAsync(difer, previousIPList));
        }

        if (!newIPs.isEmpty()) {
            List<String> arrayNew = item.gethwiDs();
            promesas.add(SecureShell_metod.crearCuentaAsync(newIPs, arrayNew, diasRestantes, item.isEstado()));
        }

        Set<String> set1 = new HashSet<>(array1);
        List<String> distintos2 = array2.stream()
                .filter(ip -> !set1.contains(ip))
                .collect(Collectors.toList());

        if (!distintos2.isEmpty()) {
            List<String> ipsfront = new ArrayList<>(Arrays.asList(previousItem.getIp().split(",")));

            if (!newIPs.isEmpty()) {
                Set<String> set3 = new HashSet<>(newIPs);
                ipsfront = ipsfront.stream()
                        .filter(ip -> !set3.contains(ip) && !"0.0.0.0".equals(ip))
                        .collect(Collectors.toList());
            }

            promesas.add(SecureShell_metod.crearCuentaAsync(ipsfront, distintos2, diasRestantes, item.isEstado()));
        }

        List<String> comunes = array2.stream()
                .filter(set1::contains)
                .collect(Collectors.toList());

        if (!verifyFechasIguales(item, previousItem) || !verifyVariacion(item, previousItem)) {
            List<String> ipsactual = new ArrayList<>(Arrays.asList(previousItem.getIp().split(",")));

            if (!removedIPs.isEmpty()) {
                Set<String> set3 = new HashSet<>(removedIPs);
                ipsactual = ipsactual.stream()
                        .filter(ip -> !set3.contains(ip) && !"0.0.0.0".equals(ip))
                        .collect(Collectors.toList());
            }

            System.out.println("MODIFICANDO CUENTA");

            promesas.add(SecureShell_metod.modificarCuentaAsync(ipsactual, comunes, diasRestantes, item.isEstado()));
        }

        // Esperar a que todas las promesas se completen (como Promise.all)
        CompletableFuture.allOf(promesas.toArray(new CompletableFuture[0])).join();
    });
}

    private static List<String> parseIPs(String ipString) {
        if (ipString == null || ipString.isEmpty())
            return new ArrayList<>();
        return Arrays.stream(ipString.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    private static boolean verifyFechasIguales(SecureShell_DB item, SecureShell_DB previousItem) {
        try {
            LocalDate d1 = LocalDate.parse(item.getVigencia());
            LocalDate d2 = LocalDate.parse(previousItem.getVigencia());
            return d1.equals(d2);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean verifyVariacion(SecureShell_DB item, SecureShell_DB previousItem) {
        return Objects.equals(item.isEstado(), previousItem.isEstado())
                && Objects.equals(item.gethwiDs(), previousItem.gethwiDs())
                && Objects.equals(parseIPs(item.getIp()), parseIPs(previousItem.getIp()));
    }

    public static void beforeRemoveSSH(SecureShell_DB item) throws Exception {
        List<String> hwiDs = (List<String>) item.gethwiDs();

        try {

            if (hwiDs != null && !hwiDs.isEmpty()) {
                processIPs(item, hwiDs);

            }
        } catch (Exception e) {
            if (!e.getMessage().toLowerCase().contains("not exist")) {
                System.err.println("❌ Error manejando el item: " + e.getMessage());
                throw new Exception(e.getMessage());
            }
        }

    }

    private static void processIPs(SecureShell_DB itemConect, List<String> userList) throws Exception {
        if (itemConect == null || itemConect.getIp() == null) {
            System.out.println("No hay IPs disponibles.");
            return;
        }

        String ipData = itemConect.getIp();
        List<String> ipArray = Arrays.stream(ipData.split(","))
                .map(String::trim)
                .filter(ip -> !ip.isEmpty() && !ip.equals("0.0.0.0"))
                .toList();

        System.out.println("🔍 Procesando IPs: " + ipArray + " para usuarios: " + userList);

        try {
            SecureShell_metod.eliminarCuenta(userList, ipArray);

        } catch (Exception e) {
            System.err.println("❌ Error procesando IPs: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

}

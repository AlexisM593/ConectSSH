package SamHinoCreateUserSSH;
import java.util.ArrayList;
import java.util.List;

import com.jcraft.jsch.Session;

public class SSHTaskProcessor {
    public static List<String> processTasks(List<String> ips, List<User> users, int vencimiento, int maxLogins) {
        List<String> results = new ArrayList<>();
        VPSServiceManager vpsManager = new VPSServiceManager();

        for (String ip : ips) {
            ip = ip.trim();
            if (ip.equals("0.0.0.0")) continue;

            Session session = null;

            try {
                String sshKey = vpsManager.getSSHKey(ip);
                session = SSHUtils.connectSSH(ip, "root", sshKey, 12000);

                for (User user : users) {
                    boolean exists = SSHUtils.checkUserExists(session, user.username, ip);

                    String createUserCmd = exists ? "" : "sudo useradd -m -s /bin/false " + user.username + " && ";
                    String setPassCmd = "echo \"" + user.username + ":" + user.password + "\" | sudo chpasswd";
                    String limitCmd = "echo \"" + user.username + " hard maxlogins " + maxLogins + "\" | sudo tee -a /etc/security/limits.conf";
                    String expireCmd = "sudo chage -E \"$(date -d '+" + vencimiento + " days' '+%Y-%m-%d')\" " + user.username;

                    String fullCommand = createUserCmd + setPassCmd + " && " + limitCmd + " && " + expireCmd;

                    try {
                        String output = SSHUtils.executeCommand(session, fullCommand, ip, 1, 1000, 4000);
                        results.add("✔ IP: " + ip + ", Usuario: " + user.username + "\n" + output);
                    } catch (Exception e) {
                        results.add("✖ IP: " + ip + ", Usuario: " + user.username + " - Error: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                results.add("✖ IP: " + ip + " - Error de conexión o clave: " + e.getMessage());
            } finally {
                if (session != null && session.isConnected()) {
                    session.disconnect();
                }
            }
        }
        return results;
    }
}

package Protocol;

import java.util.ArrayList;
import java.util.List;

import com.jcraft.jsch.*;

public class SecureShell {

//Transformar código de Javascript a Java e instalar la extensión Remote - SSH de microsoft

/*
Código de Creación del usuario
 
export async function sshCreateUser(ips, users, vencimiento, LogIn) {
    if (!vencimiento || vencimiento < 1) {
        vencimiento = 1;
    }


    let overallResults = [];

    const getSSHKey = async (ip) => {
        try {
            const result = await wixData.query("VPSservice").eq("ip", ip).find();
            if (result.items.length === 0) {
                throw new Error(`No se encontró la clave para la IP: ${ip}.`);
            }
            return result.items[0].clave;
        } catch (error) {
            throw new Error(`Error al obtener la clave SSH: ${error.message}`);
        }
    };

    const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

    const executeCommand = (conn, command, ip, retries = 1, delayTime = 1000, timeout = 4000) => {
        return new Promise((resolve, reject) => {
            const attempt = (retryCount) => {
                conn.exec(command, async (err, stream) => {
                    if (err) {
                        return reject(new Error(`Error al ejecutar el comando: ${err.message}`));
                    }

                    let stderr = '';
                    let stdout = '';
                    const timer = setTimeout(() => {
                        reject(new Error('El comando ha tardado demasiado en ejecutarse.'));
                        stream.close();
                    }, timeout);

                    stream.on('close', async (code) => {
                        clearTimeout(timer);
                        if (code === 0) {

                            resolve(stdout);

                        } else {
                            if (retryCount > 0) {
                                console.log(`Reintentando comando en ${delayTime / 1000} segundos...`);
                                await delay(delayTime);
                                attempt(retryCount - 1);
                            } else {
                                reject(new Error(`El comando falló con código de salida ${code}.`));
                            }
                        }

                    }).on('data', (data) => {
                        stdout += data;
                    }).stderr.on('data', (data) => {
                        stderr += data.toString();
                    }).on('end', async () => {
                        if (stderr) {
                            reject(new Error(`Error del comando: ${stderr}`));
                        }
                    });
                });
            };

            attempt(retries);
        });
    };

    const connectSSH = (conn, host, username, password, timeout = 12000) => {
        return new Promise((resolve, reject) => {

            const timeoutId = setTimeout(() => {
                reject(new Error('Error: Conexión SSH ha superado el tiempo de espera de 4 segundos.'));
                conn.end();
            }, timeout);

            conn.on('ready', () => {
                clearTimeout(timeoutId);
                resolve();
            }).on('error', (err) => {
                clearTimeout(timeoutId);
                reject(new Error(`Error de conexión SSH: ${err.message}`));
            }).connect({
                host: host,
                port: 22,
                username: username,
                password: password
            });
        });
    };

    async function checkUserExists(conn, user, ip) {
        const checkUserCommand = `id -u ${user}`;
        try {
            await executeCommand(conn, checkUserCommand, ip);
            return true;
        } catch (err) {
            const stderrMessage = err.message.toLowerCase();
            if (stderrMessage.includes('no such user') || stderrMessage.includes('no such file or directory') || stderrMessage.includes('id:') && stderrMessage.includes('no such')) {
                return false;
            }
            throw new Error(`Error al comprobar la existencia del usuario: ${err.message}`);
        }
    }
    const tasks = ips.map(async (ip) => {
        const conn = new Client();
        ip = ip.trim();
        if (ip !== "0.0.0.0") {
            try {
                const sshKey = await getSSHKey(ip);
                await connectSSH(conn, ip, 'root', sshKey);
              

                const userTasks = users.map(async (userObj) => {
                    const user = userObj.user;
                    const password = userObj.password;
                    const userExists = await checkUserExists(conn, user, ip);

                    let createUserCommand = `sudo useradd -m -s /bin/false ${user} && `;
                    if (userExists) {
                        createUserCommand = "";
                    }
                    if (!LogIn) { LogIn = 3; }

                    const setPasswordCommand = `echo "${user}:${password}" | sudo chpasswd`;
                    const setSessionsLimitCommand = `echo "${user} hard maxlogins ${LogIn}" | sudo tee -a /etc/security/limits.conf`;
                    const setExpirationDateCommand = `sudo chage -E "$(date -d '+${vencimiento} days' '+%Y-%m-%d')" ${user}`;
                    const combinedCommand = `${createUserCommand}${setPasswordCommand} && ${setSessionsLimitCommand} && ${setExpirationDateCommand}`;

                    try {
                        const result = await executeCommand(conn, combinedCommand, ip);
                        console.log(`Usuario ${user} creado con éxito:`, result);
                        overallResults.push({ ip, user, result: String(result), toString: function () { return `___________\nUbicación: ${this.ip}\nUsuario/HWID: ${this.user}\nResultado: ${this.result}\n` } });
                    } catch (error) {
                        console.error(`Error al crear el usuario ${user}: ${error.message}`);
                        overallResults.push({ ip, user, error: String(error.message || error), toString: function () { return `___________\nUbicación: ${this.ip}\nUsuario/HWID: ${this.user}\nError: ${this.error}\n` } });
                    }
                });

                await Promise.all(userTasks);
            } catch (error) {
                console.error(`Error en la conexión SSH o comando inicial: ${error.message}`);
                overallResults.push({ ip, error: String(error.message || error), toString: function () { return `___________\nUbicación: ${this.ip}\nError: ${this.error}\n` } });
            } finally {
                conn.end();
            }
        }
    });

    await Promise.all(tasks);

    return overallResults;
}

 */



// Segunda función actualización del usuario

/**
 * Actualizar usuario
 * 
 * @param ips Lista de direcciones IP de los servidores.
 * @param usuarios Lista de objetos Usuario que contiene nombre y contraseña.
 * @param vencimiento Número de días hasta que la cuenta expira.
 * @param inicioSesion Límite máximo de sesiones simultáneas permitidas.
 * @param habilitar Indica si se debe habilitar o deshabilitar el usuario.
 * @return Lista de resultados de la operación para cada IP.
 */

public List<String> gjActualizarUsuario(List<String> ips, List<Usuario> usuarios, Integer vencimiento, Integer inicioSesion, Boolean habilitar) {
        List<String> resultadosGlobales = new ArrayList<>();

        if (vencimiento == null || vencimiento < 1) {
            vencimiento = 1;
        }

        for (String ip : ips) {
            ip = ip.trim();
            if (!ip.equals("0.0.0.0")) {
                Session sesion = null;
                try {
                    String claveSSH = obtenerClaveSSH(ip);
                    sesion = conectarSSH(ip, "root", claveSSH);

                    for (Usuario usuario : usuarios) {
                        String nombreUsuario = usuario.getNombreUsuario();
                        String contrasena = usuario.getContrasena();

                        String comandoCrearUsuario = "";
                        if (!comprobarUsuarioExiste(sesion, nombreUsuario)) {
                            comandoCrearUsuario = "sudo useradd -m -s /bin/false " + nombreUsuario;
                        }

                        String comandoHabilitarUsuario = habilitar != null ? (habilitar ? "sudo passwd -u " + nombreUsuario : "sudo passwd -l " + nombreUsuario) : "";
                        String comandoEstablecerContrasena = "echo \"" + nombreUsuario + ":" + contrasena + "\" | sudo chpasswd";
                        String comandoLimitarSesiones = "echo \"" + nombreUsuario + " hard maxlogins " + inicioSesion + "\" | sudo tee -a /etc/security/limits.conf";
                        String comandoEstablecerExpiracion = "sudo chage -E \"$(date -d '" + vencimiento + " days' '+%Y-%m-%d')\" " + nombreUsuario;

                        String comandoCompleto = String.join(" && ", comandoCrearUsuario, comandoEstablecerContrasena, comandoLimitarSesiones, comandoEstablecerExpiracion, comandoHabilitarUsuario);

                        ejecutarComando(sesion, comandoCompleto);
                        resultadosGlobales.add("Usuario actualizado correctamente: " + nombreUsuario + " en " + ip);
                    }
                } catch (Exception e) {
                    resultadosGlobales.add("Error al procesar la IP: " + ip + " - " + e.getMessage());
                } finally {
                    if (sesion != null && sesion.isConnected()) {
                        sesion.disconnect();
                    }
                }
            }
        }
        return resultadosGlobales;
    }

    private String obtenerClaveSSH(String ip) throws Exception {

        // Implementar la lógica para obtener la clave SSH asociada a la IP.
       
        return "claveSSH"; // Reemplazar con la clave real obtenida
    }

    private Session conectarSSH(String ip, String usuario, String clave) throws JSchException {
        JSch jsch = new JSch();
        Session sesion = jsch.getSession(usuario, ip, 22);
        sesion.setPassword(clave);
        sesion.setConfig("StrictHostKeyChecking", "no");
        sesion.connect(8000); // Tiempo de espera: 8 segundos
        return sesion;
    }

    private boolean comprobarUsuarioExiste(Session sesion, String nombreUsuario) throws Exception {
        String comandoVerificar = "id -u " + nombreUsuario;
        try {
            ejecutarComando(sesion, comandoVerificar);
            return true;
        } catch (Exception e) {
            String mensajeError = e.getMessage().toLowerCase();
            return mensajeError.contains("usuario no encontrado") || mensajeError.contains("id:") && mensajeError.contains("usuario no existe");
        }
    }

    private void ejecutarComando(Session sesion, String comando) throws Exception {
        ChannelExec canal = (ChannelExec) sesion.openChannel("exec");
        canal.setCommand(comando);
        canal.setErrStream(System.err);
        canal.connect();

        try {
            canal.disconnect();
        } catch (Exception e) {
            throw new Exception("Error ejecutando comando: " + e.getMessage());
        }
    }

    public static class Usuario {
        private String nombreUsuario;
        private String contrasena;

        public Usuario(String nombreUsuario, String contrasena) {
            this.nombreUsuario = nombreUsuario;
            this.contrasena = contrasena;
        }

        public String getNombreUsuario() {
            return nombreUsuario;
        }

        public String getContrasena() {
            return contrasena;
        }
    }


  //tercera función eliminación de los usuarios


public boolean eliminarUsuario(String usuario, String ip, String claveSSH) {
    try {
        JSch jsch = new JSch();
        Session session = jsch.getSession("root", ip, 22);
        session.setPassword(claveSSH);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        System.out.println("Conectando al servidor SSH...");
        session.connect(5000);

        String comando = "sudo userdel -r " + usuario;
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(comando);
        channel.connect();

        System.out.println("Comando ejecutado: " + comando);

        channel.disconnect();
        session.disconnect();
        return true;
    } catch (Exception e) {
        System.out.println("Error al eliminar usuario: " + e.getMessage());
        return false;
    }
}

*/

    // Procesar todas las IPs simultáneamente
   //* */ const promises = ips.map(async (ip) => { const conn = new Client()
    //    ip = ip.trim();
        // if (ip !== "0.0.0.0") {
            //y {    const sshKey = await getSSHKey(ip);
       //       await connectSSH(conn, ip, 'root', sshKey);

               //onst userPromises = users.map(async (userObj) => {
   //               const user = userObj;
//
     //             try {
       //               const checkUserProcessesCommand = `pgrep -u ${user}`;
      //                const killUserProcessesCommand = `sudo timeout 3s pkill -u ${user}`;
//
            //          try {
      //                    // Verifica si hay procesos activos
     //                     const { stdout: processes, code } = await executeCommand(conn, checkUserProcessesCommand);

        //                 if (code === 0 && processes.trim().length > 0) {
       //  //                   console.log(`Procesos encontrados para el usuario ${user}. Ejecutando pkill.`);
         ///                    await executeCommand(conn, killUserProcessesCommand);
           / ///            } else if (code === 1 || processes.trim().length === 0) {
           ///                  console.log(`No se encontraron procesos activos para el usuario ${user}.`);
                        //
                       // catch (err) {
              ///   if (err.message.includes('código de salida 1')) {
              //      console.log(`No se encontraron procesos para el usuario ${user}.`);
                     //  } else {
                  //    console.error(`Error inesperado al ejecutar pkill para el usuario ${user}: ${err.message}`);
                  // }
                     //

                        // Eliminar usuario y su directorio home
                     // const deleteUserCommand = `sudo deluser --remove-home ${user}`;
                     // console.log(`Ejecutando comando: ${deleteUserCommand}`);
                   //  await executeCommand(conn, deleteUserCommand);

                        // Verificar si el usuario fue eliminado correctamente
                 //     const userStillExists = await checkUserExists(conn, user);
                //      let result = userStillExists ?
                 //         `Error: El usuario ${user} aún existe después de intentar eliminarlo.` :
                  //        `Usuario ${user} eliminado con éxito.`;
//
                  //    overallResults.push({ ip, user, result, toString: function () { return `___________\nUbicación: ${this.ip}, Usuario/HWID: ${this.user}, Resultado: ${this.result}\n` } });
//
                 // } catch (error) {
                 //     console.error(`Error al eliminar el usuario ${user} en ${ip}: ${error.message}`);
                 //     if (error.message.includes("does not exist")) {
                 //         error.message = "El usuario ya no existe";
                 //         overallResults.push({ ip, user, result: error.message, toString: function () { return `___________\nUbicación: ${this.ip}\nUsuario/HWID: ${this.user}\nResultado: ${this.result}\n` } });
////
                 //     } else {
                 //         overallResults.push({ ip, user, error: error.message, toString: function () { return `___________\nUbicación: ${this.ip}\nUsuario/HWID: ${this.user}\nError: ${this.error}\n` } });
                        }
           //       }
            //  });
///
           //   await Promise.all(userPromises); // Procesa todos los usuarios simultáneamente para cada IP
//
         // } catch (error) {
          //    console.error(`Error en la conexión SSH o comandos para ${ip}: ${error.message}`);
         //     overallResults.push({ ip, error: error.message, toString: function () { return `___________\nUbicación: ${this.ip}\nError: ${this.error}\n` } });
         // } finally {
        //      conn.end(); // Cerrar la conexión antes de pasar a la siguiente IP
   //       }
   // //}
  //});

    // Espera que todas las conexiones SSH para todas las IPs terminen
    //await Promise.all(promises);
  //await Promise.all(promises.map(p => p.catch(error => console.error(`Error capturado: ${error.message || error}`))));

 // return overallResults;}//
  ///


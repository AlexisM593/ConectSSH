package Utility;

public class GestorDataBase {

    /*
     
export async function Protocol_beforeInsert(item, context) {

    item.userEnable = true;
    item.record = [];

        item.owner = context.userId;
  
    const resultRS = await wixData.query("ResellerSSH").eq("reseller", context.userId).find();
    async function boolean() {
        const resultUser = await wixData.query("ServersGenerated").eq("_owner", context.userId).find();
        if (resultRS.items.length > 0) {
            if (resultRS.items[0].limite > resultUser.items.length) {
                return true;
            } else {
                return false;
            }
        } else {
            if (20 > resultUser.items.length) {
                return true;
            } else {
                return false;
            }
        }

    }

    if (!(await boolean())) {
        return Promise.reject(new Error('Limite alcanzado.'));
    }

    if (item.type.includes("Ridge")) {

        if (resultRS.items.length > 0) { // Asegurarse de que hay al menos un item en la respuesta
            const resellitem = resultRS.items[0];
            const optionresell = {
                _id: resellitem._id
            };
            if (resellitem.usoPrecio) {
                const costo = 0.003 + resellitem.usoPrecio;
                optionresell.usoPrecio = parseFloat(costo.toFixed(4));
            } else {
                optionresell.usoPrecio = 0.003
            }
            try {
                const ressupdate = await wixData.update("ResellerSSH", optionresell);
                console.log("Item guardado correctamente", ressupdate);
            } catch (err) {
                console.log("Ocurrió un error", err);
            }
        } else {
            console.log("No se encontraron resultados para el reseller con userId: ", context.userId);
        }

    }

    return item;
}
     */
}

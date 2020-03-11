package marraia.printer;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class marraiaprinter extends CordovaPlugin {

    private GertecPrinter gertecPrinter;
    private IGEDI iGedi = null;
    private IPRNTR iPrint = null;
    private GEDI_PRNTR_e_Status status;

    private ConfigPrint configPrint = new ConfigPrint();

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        gertecPrinter = new GertecPrinter(this.getApplicationContext());

        configPrint.setNegrito(true);

        // Configra o tamanho da fonte
        configPrint.setTamanho(18);

        // Aplica as novas configurações
        gertecPrinter.setConfigImpressao(configPrint);

        // Faz a impressão
        String sStatus = gertecPrinter.getStatusImpressora();
        if(gertecPrinter.isImpressoraOK()) {
            gertecPrinter.imprimeTexto("FERNANDO");
            // Usado apenas no exemplo, esse pratica não deve
            // ser repetida na impressão em produção
            gertecPrinter.avancaLinha(150);
            gertecPrinter.ImpressoraOutput();
        }else{
            ShowFalha(sStatus);
        }

        return true;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}

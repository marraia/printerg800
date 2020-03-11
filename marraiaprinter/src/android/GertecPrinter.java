package marraia.printer;

public class GertecPrinter {

    // Definições
    private final String IMPRESSORA_ERRO = "Impressora com erro.";

    // Statics
    private static boolean isPrintInit = false;

    // Vaviáveis iniciais
    private Activity activity;
    private Context context;

    // Classe de impressão
    private IGEDI iGedi = null;
    private IPRNTR iPrint = null;
    private GEDI_PRNTR_st_StringConfig stringConfig;
    private GEDI_PRNTR_st_PictureConfig pictureConfig;
    private GEDI_PRNTR_e_Status status;

    // Classe de configuração da impressão
    private ConfigPrint configPrint;
    private Typeface typeface;


    /**
     * Método construtor da classe
     * @param c = Context  atual que esta sendo inicializada a class
     * */
    public GertecPrinter(Context c) {
        this.context = c;
        startIGEDI();
    }

    private void startIGEDI() {
        new Thread(() -> {
            GEDI.init(this.context);
            this.iGedi = GEDI.getInstance(this.context);
            this.iPrint = this.iGedi.getPRNTR();
            try {
                new Thread().sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void setConfigImpressao(ConfigPrint config){

        this.configPrint = config;

        this.stringConfig = new GEDI_PRNTR_st_StringConfig(new Paint());
        this.stringConfig.paint.setTextSize(configPrint.getTamanho());
        this.stringConfig.paint.setTextAlign(Paint.Align.valueOf(configPrint.getAlinhamento()));
        this.stringConfig.offset = configPrint.getOffSet();
        this.stringConfig.lineSpace = configPrint.getLineSpace();

        switch (configPrint.getFonte()){
            case "NORMAL":
                this.typeface = Typeface.create(configPrint.getFonte(), Typeface.NORMAL );
                break;
            case "DEFAULT":
                this.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL );
                break;
            case "DEFAULT BOLD":
                this.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL );
                break;
            case "MONOSPACE":
                this.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL );
                break;
            case "SANS SERIF":
                this.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL );
                break;
            case "SERIF":
                this.typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL );
                break;
            default:
                this.typeface = Typeface.createFromAsset(this.context.getAssets(), configPrint.getFonte());
        }

        if (this.configPrint.isNegrito() && this.configPrint.isItalico()){
            typeface = Typeface.create(typeface, Typeface.BOLD_ITALIC);
        }else if(this.configPrint.isNegrito()){
            typeface = Typeface.create(typeface, Typeface.BOLD);
        }else if(this.configPrint.isItalico()){
            typeface = Typeface.create(typeface, Typeface.ITALIC);
        }

        if(this.configPrint.isSublinhado()){
            this.stringConfig.paint.setFlags(Paint.UNDERLINE_TEXT_FLAG);
        }

        this.stringConfig.paint.setTypeface(this.typeface);
    }

    /**
     * Método que retorna o atual estado da impressora
     *
     * @throws GediException = vai retorno o código do erro.
     *
     * @return String = traduzStatusImpressora()
     *
     * */
    public String getStatusImpressora() throws GediException {
        try {
            ImpressoraInit();
            this.status = this.iPrint.Status();
        } catch (GediException e) {
            throw new GediException(e.getErrorCode());
        }

        return traduzStatusImpressora(this.status);
    }

    /**
     * Método que recebe o atual texto a ser impresso
     * @param texto  = Texto que será impresso.
     *
     * @throws Exception = caso a impressora esteja com erro.
     *
     * */
    public void imprimeTexto(String texto) throws Exception {

        //this.getStatusImpressora();
        try{
            if (!isImpressoraOK()) {
                throw new Exception(IMPRESSORA_ERRO);
            }
            sPrintLine(texto);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Método que recebe o atual texto e o tamanho da fonte que deve ser usado na impressão.
     *
     * @param texto  = Texto que será impresso.
     * @param tamanho = Tamanho da fonte que será usada
     *
     * @throws Exception = caso a impressora esteja com erro.
     *
     * @apiNote = Esse mátodo só altera o tamanho do texto na impressão que for chamado
     * a classe {@link ConfigPrint} não será alterada para continuar sendo usado na impressão da
     * proxíma linha
     *
     * */
    public void imprimeTexto(String texto, int tamanho) throws Exception {

        int tamanhoOld;

        //this.getStatusImpressora();

        try{
            if (!isImpressoraOK()) {
                throw new Exception(IMPRESSORA_ERRO);
            }
            tamanhoOld = this.configPrint.getTamanho();
            this.configPrint.setTamanho(tamanho);
            sPrintLine(texto);
            this.configPrint.setTamanho(tamanhoOld);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Método que recebe o atual texto e ser o mesmo será impresso em negrito.
     *
     * @param texto  = Texto que será impresso.
     * @param negrito = Caso o texto deva ser impresso em negrito
     *
     * @throws Exception = caso a impressora esteja com erro.
     *
     * @apiNote = Esse mátodo só altera o tamanho do texto na impressão que for chamado
     *      * a classe {@link ConfigPrint} não será alterada para continuar sendo usado na impressão da
     *      * proxíma linha
     *
     * */
    public void imprimeTexto(String texto, boolean negrito) throws Exception {

        boolean negritoOld = false;

        //this.getStatusImpressora();

        try{
            if (!isImpressoraOK()) {
                throw new Exception(IMPRESSORA_ERRO);
            }
            negritoOld = this.configPrint.isNegrito();
            this.configPrint.setNegrito(negrito);

            sPrintLine(texto);

            this.configPrint.setNegrito(negritoOld);

        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Método que recebe o atual texto e ser o mesmo será impresso em negrito e/ou itálico.
     *
     * @param texto  = Texto que será impresso.
     * @param negrito = Caso o texto deva ser impresso em negrito
     * @param italico  = Caso o texto deva ser impresso em itálico
     *
     * @throws Exception = caso a impressora esteja com erro.
     *
     * @apiNote = Esse mátodo só altera o tamanho do texto na impressão que for chamado
     *      * a classe {@link ConfigPrint} não será alterada para continuar sendo usado na impressão da
     *      * proxíma linha
     *
     * */
    public void imprimeTexto(String texto, boolean negrito, boolean italico) throws Exception {

        boolean negritoOld = false;
        boolean italicoOld = false;

        //this.getStatusImpressora();

        try{
            if (!isImpressoraOK()) {
                throw new Exception(IMPRESSORA_ERRO);
            }
            negritoOld = this.configPrint.isNegrito();
            italicoOld = this.configPrint.isItalico();
            this.configPrint.setNegrito(negrito);
            this.configPrint.setItalico(italico);

            sPrintLine(texto);

            this.configPrint.setNegrito(negritoOld);
            this.configPrint.setItalico(italicoOld);

        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
    /**
     * Método que recebe o atual texto e ser o mesmo será impresso em negrito, itálico e/ou  sublinhado.
     *
     * @param texto  = Texto que será impresso.
     * @param negrito = Caso o texto deva ser impresso em negrito
     * @param italico  = Caso o texto deva ser impresso em itálico
     * @param sublinhado   = Caso o texto deva ser impresso em itálico.
     *
     * @throws Exception = caso a impressora esteja com erro.
     *
     * @apiNote = Esse mátodo só altera o tamanho do texto na impressão que for chamado
     *      * a classe {@link ConfigPrint} não será alterada para continuar sendo usado na impressão da
     *      * proxíma linha
     *
     * */
    public void imprimeTexto(String texto, boolean negrito, boolean italico, boolean sublinhado) throws Exception {

        boolean negritoOld = false;
        boolean italicoOld = false;
        boolean sublinhadoOld = false;

        //this.getStatusImpressora();

        try{
            if (!isImpressoraOK()) {
                throw new Exception(IMPRESSORA_ERRO);
            }
            negritoOld = this.configPrint.isNegrito();
            italicoOld = this.configPrint.isItalico();
            sublinhadoOld = this.configPrint.isSublinhado();

            this.configPrint.setNegrito(negrito);
            this.configPrint.setItalico(italico);
            this.configPrint.setSublinhado(sublinhado);

            sPrintLine(texto);

            this.configPrint.setNegrito(negritoOld);
            this.configPrint.setItalico(italicoOld);
            this.configPrint.setSublinhado(sublinhadoOld);

        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Método privado que faz a impressão do texto.
     *
     * @param texto = Texto que será impresso
     *
     * @throws GediException = retorna o código do erro
     *
     * */
    private boolean sPrintLine(String texto) throws Exception {
        //Print Data
        try {
            ImpressoraInit();
            this.iPrint.DrawStringExt(this.stringConfig, texto);
            this.avancaLinha(configPrint.getAvancaLinhas());
            //ImpressoraOutput();
            return true;
        } catch (GediException e) {
            throw new GediException(e.getErrorCode());
        }
    }

}
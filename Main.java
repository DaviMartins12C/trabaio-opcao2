import com.sun.jna.Library;
import com.sun.jna.Native;
import java.util.Scanner;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;

public class Main {

    // Interface que representa a DLL, usando JNA
    public interface ImpressoraDLL extends Library {

        // Caminho completo para a DLL
        ImpressoraDLL INSTANCE = (ImpressoraDLL) Native.load(
                "C:\\Users\\ronny_bezerra\\Downloads\\Java-Aluno EM\\Java-Aluno EM\\E1_Impressora01.dll",
                ImpressoraDLL.class
        );

        int AbreConexaoImpressora(int tipo, String modelo, String conexao, int param);

        int FechaConexaoImpressora();

        int ImpressaoTexto(String dados, int posicao, int estilo, int tamanho);

        int Corte(int avanco);

        int ImpressaoQRCode(String dados, int tamanho, int nivelCorrecao);

        int ImpressaoCodigoBarras(int tipo, String dados, int altura, int largura, int HRI);

        int AvancaPapel(int linhas);

        int StatusImpressora(int param);

        int AbreGavetaElgin();

        int AbreGaveta(int pino, int ti, int tf);

        int SinalSonoro(int qtd, int tempoInicio, int tempoFim);

        int ModoPagina();

        int LimpaBufferModoPagina();

        int ImprimeModoPagina();

        int ModoPadrao();

        int PosicaoImpressaoHorizontal(int posicao);

        int PosicaoImpressaoVertical(int posicao);

        int ImprimeXMLSAT(String dados, int param);

        int ImprimeXMLCancelamentoSAT(String dados, String assQRCode, int param);
    }

    private static boolean conexaoAberta = false;
    private static int tipo;
    private static String modelo;
    private static String conexao;
    private static int parametro;
    private static final Scanner scanner = new Scanner(System.in);

    private static String capturarEntrada(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    public static void configurarConexao() {
        if (!conexaoAberta) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Digite o tipo de conexão (ex: 1 para USB, 2 para serial, etc.): ");
            tipo = scanner.nextInt();
            scanner.nextLine(); // limpa buffer

            System.out.print("Digite o modelo da impressora: ");
            modelo = scanner.nextLine();

            System.out.print("Digite a conexão (ex: USB, COM3, etc.): ");
            conexao = scanner.nextLine();

            System.out.print("Digite o parâmetro (geralmente 0): ");
            parametro = scanner.nextInt();

            System.out.println("Configuração salva!");

        }
    }

    public static void abrirConexao () {

        //sempre que for chamar uma funçao da biblioteca, usar como abaixo (ImpressoraDLL.INSTANCE.AbreConexaoImpressora)

        if (!conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.AbreConexaoImpressora(tipo, modelo, conexao, parametro);
            if (retorno == 0) {
                conexaoAberta = true;
                System.out.println("Conexão aberta com sucesso.");
            } else {
                System.out.println("Erro ao abrir conexão. Código de erro: " + retorno);
            }
        } else {
            System.out.println("Conexão já está aberta.");
        }
    }

    public static void fecharConexao() {
        //Preparar o fechamento da conexão
        if (conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.FechaConexaoImpressora();
            //Não estiver o retorno
            if (retorno == 0) {
                conexaoAberta = false;
                System.out.println("Conexão fechada com sucesso.");
            } else {
                System.out.println("Falha ao fechar conexão. Código: " + retorno);
            }

        } else {
            System.out.println("Nenhuma conexão está aberta.");
        }
    } // Júlio

    public static void imprimirTexto() {
        // Verifica se a impressora está com a conexão aberta antes imprimir
        if (conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.ImpressaoTexto("Teste de impressao", 1, 4, 0);

            // A função retorna um valor inteiro indicando se a impressão foi bem-sucedida
            if (retorno == 0) {
                System.out.println("Texto impresso com sucesso.");

                // Após imprimir, avançar papel e cortar
                ImpressoraDLL.INSTANCE.AvancaPapel(2);
                ImpressoraDLL.INSTANCE.Corte(2);

            }
            else {
                System.out.println("Erro ao imprimir texto. Código de erro: " + retorno);
            }
        }
        else {
            // Se a conexão com a impressora ainda não foi aberta
            System.out.println("Nenhuma conexão aberta ;-; abra uma conexão antes de imprimir.");
        }
    } // Mell

    public static void sinalSonoro () {
        if (conexaoAberta) {

            // Chama a função da DLL: SinalSonoro(qtd, tempoInicio, tempoFim)
            int retorno = ImpressoraDLL.INSTANCE.SinalSonoro(4, 5, 5);

            // Resultado: retorno 0 = sucesso
            if (retorno == 0) {
                System.out.println("Sinal sonoro executado.");
            } else {
                System.out.println("Erro ao emitir sinal sonoro. Código de erro: " + retorno);
            }

        } else {
            System.out.println("Nenhuma conexão aberta -_- abra uma conexão antes de tentar executar o sinal sonoro.");
        }
    }// Mell

    public static void impressaoCodigoBarras() {
        if (conexaoAberta) {
            int retorno = ImpressoraDLL.INSTANCE.ImpressaoCodigoBarras(
                    8,                  // tipo
                    "{A012345678912",   // dados
                    100,                // altura
                    2,                  // largura
                    3                   // HRI
            );

            if (retorno == 0) {
                System.out.println("Código de barras impresso com sucesso.");
                // Corte e avanço automático
                ImpressoraDLL.INSTANCE.AvancaPapel(2);
                ImpressoraDLL.INSTANCE.Corte(2);

            } else {
                System.out.println("Erro ao imprimir código de barras. Código de erro: " + retorno);
            }

        } else {
            System.out.println("Nenhuma conexão aberta. Abra uma conexão antes de imprimir o código de barras.");
        }
    }

    public static void impressaoQRCode() {
        if (conexaoAberta) {

            int retorno = ImpressoraDLL.INSTANCE.ImpressaoQRCode(
                    "Teste de impressao", // dados
                    6,                    // tamanho
                    4                     // nível de correção
            );

            if (retorno == 0) {
                System.out.println("QR Code impresso com sucesso.");
                // Corte e avanço automático
                ImpressoraDLL.INSTANCE.AvancaPapel(2);
                ImpressoraDLL.INSTANCE.Corte(2);

            } else {
                System.out.println("Erro ao imprimir QR Code. Código de erro: " + retorno);
            }

        } else {
            System.out.println("Nenhuma conexão aberta. Abra uma conexão antes de imprimir o QR Code.");
        }
    }

    public static void abreGavetaElgin() {
        if (conexaoAberta) {

            //- `AbreGavetaElgin()`            (1, 50, 50)
            int retorno = ImpressoraDLL.INSTANCE.AbreGaveta(
                    1,   // pino
                    50,  // tempo de início
                    50   // tempo de fim
            );

            if (retorno == 0) {
                System.out.println("Gaveta acionada com sucesso.");
            } else {
                System.out.println("Erro ao abrir gaveta. Código de erro: " + retorno);
            }

        } else {
            System.out.println("Nenhuma conexão aberta. Abra uma conexão antes de tentar abrir a gaveta.");
        }
    }

    public static void abreGaveta() {
        if (conexaoAberta) {
            // Usa a função da DLL AbreGaveta(pino, ti, tf) com os parâmetros (1,5,10)
            int retorno = ImpressoraDLL.INSTANCE.AbreGaveta(
                    1,  // pino
                    5,  // tempo início
                    10  // tempo fim
            );

            if (retorno == 0) {
                System.out.println("Gaveta aberta com sucesso.");
            } else {
                System.out.println("Erro ao abrir gaveta. Código de erro: " + retorno);
            }
        } else {
            System.out.println("Nenhuma conexão aberta. Abra a conexão antes de tentar abrir a gaveta.");
        }
    }

    public static void corte() {
        if (conexaoAberta) {

            int retorno = ImpressoraDLL.INSTANCE.Corte(2);

            if (retorno == 0) {
                System.out.println("Corte realizado com sucesso.");
            } else {
                System.out.println("Erro ao realizar o corte. Código de retorno: " + retorno);
            }

        } else {
            System.out.println("Nenhuma conexão aberta. Abra a conexão antes de tentar cortar o papel.");
        }
    }

    public static void avancaPapel() {
        // Antes de avançar o papel, é obrigatório verificar se a conexão está aberta
        if (conexaoAberta) {

            // O valor 2 indica que o papel deve avançar 2 linhas
            int retorno = ImpressoraDLL.INSTANCE.AvancaPapel(2);

            // Se retorno for 0, significa que o comando executou corretamente
            if (retorno == 0) {
                System.out.println("Papel avançado com sucesso.");
            } else {
                System.out.println("Erro ao avançar papel. Código de erro: " + retorno);
            }

        } else {
            // Caso o usuário tente avançar papel sem abrir conexão antes
            System.out.println("Nenhuma conexão aberta. Abra a conexão antes de tentar avançar o papel.");
        }
    }

    public static void imprimeXMLSAT() {

        if (conexaoAberta) {

            // Inserido o XML completo em formato de String... teste
            String xml = "<xml>Teste de XML do SAT</xml>";

            // O segundo parâmetro (param) geralmente é 0
            int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLSAT(xml, 0);

            // Verifica o retorno: 0 = OK, diferente de 0 = erro
            if (retorno == 0) {
                System.out.println("XML SAT impresso com sucesso.");
            } else {
                System.out.println("Erro ao imprimir XML SAT. Código de erro: " + retorno);
            }

        } else {
            System.out.println("Nenhuma conexão aberta. Abra a conexão antes de tentar imprimir o XML SAT.");
        }
    }

    public static void imprimeXMLCancelamentoSAT() {

        if (conexaoAberta) {

            // XML de cancelamento (exemplo simples — substitua depois se quiser)
            String xmlCancelamento = "<xml>Exemplo de XML de cancelamento</xml>";

            // Assinatura QRCode fornecida
            String assQRCode = "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQsE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+ZfO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";

            // param = 0 (padrão)
            int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLCancelamentoSAT(
                    xmlCancelamento,
                    assQRCode,
                    0
            );

            if (retorno == 0) {
                System.out.println("XML de cancelamento impresso com sucesso.");
            } else {
                System.out.println("Erro ao imprimir XML de cancelamento. Código de erro: " + retorno);
            }

        } else {
            System.out.println("Nenhuma conexão aberta. Abra a conexão antes de tentar imprimir o XML de cancelamento.");
        }
    }

    private static void Corte() {
        if (!conexaoAberta) {
            System.out.println("Não há conexão aberta.");
            return;
        }

        int retorno = ImpressoraDLL.INSTANCE.Corte(2);
        if (retorno == 0) System.out.println("Corte realizado com sucesso.");
        else System.out.println("Erro ao realizar corte. Código: " + retorno);
    }

    public static void ImpressaoQRCode() {
        if (!conexaoAberta) {
            System.out.println("Não há conexão aberta.");
            return;
        }

        System.out.println("Digite as informações que irão compor o QRCode:");
        String dados = scanner.nextLine();

        System.out.println("Tamanho do QRCode (1 a 6):");
        int tamanho = Integer.parseInt(scanner.nextLine());

        System.out.println("Nível de correção (1 = 7%, 2 = 15%, 3 = 25%, 4 = 30%):");
        int nivel = Integer.parseInt(scanner.nextLine());

        int retorno = ImpressoraDLL.INSTANCE.ImpressaoQRCode(dados, tamanho, nivel);
        if (retorno >= 0) System.out.println("QRCode enviado com sucesso.");
        else System.out.println("Erro ao enviar QRCode. Código: " + retorno);
    }


    public static void AvancaPapel() {
        if (!conexaoAberta) {
            System.out.println("Não há conexão aberta.");
            return;
        }

        System.out.print("Digite quantas linhas devem pular: ");
        int linhas = Integer.parseInt(scanner.nextLine());

        int retorno = ImpressoraDLL.INSTANCE.AvancaPapel(linhas);
        if (retorno >= 0) System.out.println("Avanço do papel realizado.");
        else System.out.println("Erro ao avançar papel. Código: " + retorno);
    }


	/* - `ImpressaoTexto()`          ("Teste de impressao", 1, 4, 0); -- Mell
	- `Corte()`						(2)  usar sempre após a impressao de algum documento -- Lipipe
	- `ImpressaoQRCode()`            ("Teste de impressao", 6, 4) -- João
	- `ImpressaoCodigoBarras()`    (8, "{A012345678912", 100, 2, 3) -- João
	- `AvancaPapel()`                 (2)  usar sempre após a impressao de algum documento -- Lipipe
	- `AbreGavetaElgin()`            (1, 50, 50) -- Cezar
	- `AbreGaveta()`                  (1, 5, 10) -- Cezar
	- `SinalSonoro()`				 (4,5,5) -- Mell
	- `ImprimeXMLSAT()`
	- `ImprimeXMLCancelamentoSAT()`    (assQRCode = "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQsE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+ZfO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";)
	*/


    public static void main (String[]args){
        while (true) {
            System.out.println("\n*************************************************");
            System.out.println("**************** MENU IMPRESSORA *******************");
            System.out.println("*************************************************\n");

            System.out.println("1  - Configurar Conexao");
            System.out.println("2  - Abrir Conexao");
            System.out.println("3  - Imprimir Texto");
            System.out.println("4  - Impressao QRCode");
            System.out.println("5  - Impressao Codigo Barras");
            System.out.println("6  - Imprime XML SAT");
            System.out.println("7  - Imprime XML Canc SAT");
            System.out.println("8  - Abre Gaveta Elgin");
            System.out.println("9  - Abre Gaveta");
            System.out.println("10  - Sinal Sonoro");
            System.out.println("11  - corte");
            System.out.println("12  - avançar papel");
            System.out.println("0  - Fechar Conexao e Sair");


                /*Exemplo de Menu
                1 - Configurar Conexao
                2 - Abrir Conexao
                3 - Impressao Texto
                4 - Impressao QRCode
                5 - Impressao Cod Barras
                6 - Impressao XML SAT
                7 - Impressao XML Canc SAT
                8 - Abrir Gaveta Elgin
                9 - Abrir Gaveta
                10- Sinal Sonoro
                11-
                0 - Fechar Conexao e Sair*/


            String escolha = capturarEntrada("\nDigite a opção desejada: ");

            if (escolha.equals("0")) {
                fecharConexao();
                System.out.println("Programa encerrado.");
                break;
            }

            switch (escolha) {
                case "1":
                    configurarConexao();
                    break;

                case "2":
                    abrirConexao();
                    break;

                case "3":
                    if (conexaoAberta) {
                        String txt = capturarEntrada("Digite o texto que quer imprimir: ");
                        int r = ImpressoraDLL.INSTANCE.ImpressaoTexto( txt , 1, 4, 0);

                        if (r == 0) {
                            System.out.println("Texto enviado pra impressora.");
                        } else {
                            System.out.println("Não deu pra imprimir. Código: " + r);
                        }
                    } else {
                        System.out.println("Primeiro abra a conexão.");
                    }
                    break;

                case "4":
                    impressaoQRCode();
                    break;

                case "5":
                    if (conexaoAberta) {
                        String cod = capturarEntrada("Código para imprimir (ex: 123456789012): ");
                        int rBar = ImpressoraDLL.INSTANCE.ImpressaoCodigoBarras(8, "{A012345678912", 100, 2, 3);

                        if (rBar == 0) {
                            System.out.println("Código de barras impresso.");
                        } else {
                            System.out.println("Erro ao imprimir o código. Retorno: " + rBar);
                        }
                    } else {
                        System.out.println("Sem conexão aberta.");
                    }
                    break;

                case "6":
                    if (conexaoAberta) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setCurrentDirectory(new File(".")); // Diretório atual do programa
                        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos XML", "xml"));

                        int result = fileChooser.showOpenDialog(null);

                        if (result == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = fileChooser.getSelectedFile();
                            String path = selectedFile.getAbsolutePath();

                            try {
                                String conteudoXML = lerArquivoComoString(path);
                                int retImpXMLSAT = ImpressoraDLL.INSTANCE.ImprimeXMLSAT(conteudoXML, 0);
                                ImpressoraDLL.INSTANCE.Corte(5);
                                System.out.println(retImpXMLSAT == 0 ? "Impressão de XML realizada" : "Erro ao realizar a impressão do XML SAT! Retorno: " + retImpXMLSAT);
                            } catch (IOException e) {
                                System.out.println("Erro ao ler o arquivo XML: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Nenhum arquivo selecionado.");
                        }
                    } else {
                        System.out.println("Erro: Conexão não está aberta.");
                    }
                    break;

                case "7":
                    if (conexaoAberta) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setCurrentDirectory(new File(".")); // Diretório atual do programa
                        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos XML", "xml"));
                        String assQRCode = "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQsE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+ZfO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";

                        int result = fileChooser.showOpenDialog(null);

                        if (result == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = fileChooser.getSelectedFile();
                            String path = selectedFile.getAbsolutePath();

                            try {
                                String conteudoXML = lerArquivoComoString(path);
                                int retImpCanXMLSAT = ImpressoraDLL.INSTANCE.ImprimeXMLCancelamentoSAT(conteudoXML, assQRCode, 0);
                                ImpressoraDLL.INSTANCE.Corte(5);
                                System.out.println(retImpCanXMLSAT == 0 ? "Impressão de XML de Cancelamento realizada" : "Erro ao realizar a impressão do XML de Cancelamento SAT! Retorno: " + retImpCanXMLSAT);
                            } catch (IOException e) {
                                System.out.println("Erro ao ler o arquivo XML: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Nenhum arquivo selecionado.");
                        }
                    } else {
                        System.out.println("Erro: Conexão não está aberta.");
                    }
                    break;

                case "8":
                    abreGavetaElgin();
                    break;

                case "9":
                    abreGaveta();
                    break;

                case "10":
                    sinalSonoro(); // Chama a função que SinalSonoro
                    break;

                case "11":
                    corte();
                    break;
                    
                case "12":
                    avancaPapel();
                    break;


                case "0":
                    fecharConexao();
                    System.out.println("Saindo...");
                    return;

                default:
                    System.out.println("OPÇÃO INVÁLIDA");
            }
        }

        scanner.close();
    }

    private static String lerArquivoComoString (String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        byte[] data = fis.readAllBytes();
        fis.close();
        return new String(data, StandardCharsets.UTF_8);
    }
}
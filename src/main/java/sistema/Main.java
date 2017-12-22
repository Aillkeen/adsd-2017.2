package sistema;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import simulacao.Simulador;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;


public class Main {
    private static final String DISTRIBUICAO_NORMAL = "normal";
    private static final String DISTRIBUICAO_EXPONENCIAL = "exponencial";
    private static final String DISTRIBUICAO_UNIFORME = "uniforme";

    private static String distribuicao = "";
    private static List<Double> parametros = new ArrayList<>();
    private static int valorTempoMedioServico = 1000; //será transformado para Milissegundo
    private static int duracaoSimulacao =  1000; //será transformado para Milissegundo
    private static int repeticoes = 0;

    public static void main(String[] args) {

        imprimeCabecalho();
        escolheDistribuicao();

    }

    private static void escolheDistribuicao() {
        System.out.println("\n Escolha a distribuição \n" +
                "1 - UNIFORME \n" +
                "2 - EXPONENCIAL \n" +
                "3 - NORMAL \n");
        Scanner reader = new Scanner(System.in);

        try {
            int tipoDistribuicao = reader.nextInt();

            switch (tipoDistribuicao) {
                case 1:
                    distribuicao = DISTRIBUICAO_UNIFORME;
                    informaParamDistUniforme();
                    return;
                case 2:
                    distribuicao = DISTRIBUICAO_EXPONENCIAL;
                    informaParamDistExponencial();
                    return;
                case 3:
                    distribuicao = DISTRIBUICAO_NORMAL;
                    informaParamDistNormal();
                    return;
                default:
                    System.out.println("Entrada invalida. Tente novamente.");
                    escolheDistribuicao();

            }
        }catch (InputMismatchException e){
            System.out.println("Escolha um numero: 1, 2 ou 3");
            escolheDistribuicao();
        }
        reader.close();

    }

    private static void informaParamDistNormal() {
        System.out.println("Informe os parametros para a distribuicao normal \n" + "Digite a media : ");
        Scanner scanner = new Scanner(System.in);
        try{
            Double media = scanner.nextDouble();
            if (media < 0){
                System.out.println("Informe um valor positivo.");
                informaParamDistNormal();
            }else{
                parametros.add(0,media);
                informaDesvioPadrao();
            }
        }catch (InputMismatchException e){
            System.out.println("Digite apenas numeros.");
            informaParamDistNormal();
        }
    }

    private static void informaDesvioPadrao() {
        System.out.println("Informe o desvio padrao : ");
        Scanner scanner = new Scanner(System.in);
        try{
            Double desvio = scanner.nextDouble();
            if (desvio < 0){
                System.out.println("Informe um valor positivo.");
                informaDesvioPadrao();
            }else{
                parametros.add(1,desvio);
                informaTempoMedio();
            }
        }catch (InputMismatchException e){
            System.out.println("Digite apenas numeros.");
            informaDesvioPadrao();
        }
    }

    private static void informaParamDistExponencial() {
        System.out.println("Informe o intervalo para a distribuicao exponencial \n" + "Digite a média: ");
        Scanner scanner = new Scanner(System.in);
        try{
            Double media = scanner.nextDouble();
            if (media < 0){
                System.out.println("Informe um valor positivo.");
                informaParamDistExponencial();
            }else{
                parametros.add(0,media);
                informaTempoMedio();
            }
        }catch (InputMismatchException e){
            System.out.println("Digite apenas numeros.");
            informaParamDistExponencial();
        }
    }

    private static void informaParamDistUniforme() {
        System.out.println("Informe o intervalo para a distribuicao uniforme \n" + "Valor min: ");
        Scanner scanner = new Scanner(System.in);
        try{
            Double a = scanner.nextDouble();
            if (a < 0){
                System.out.println("Informe um valor maior ou igual a zero.");

                informaParamDistUniforme();
            }else{
                parametros.add(0,a);
                informSegundoIntevalo(scanner);
            }
        }catch (InputMismatchException e){
            System.out.println("Digite apenas numeros.");
            informaParamDistUniforme();
        }

    }

    private static void informSegundoIntevalo(Scanner scanner) {
        System.out.println("Informe o intervalo para a distribuicao uniforme \n" + "Valor max: ");

        try{
            Double b = scanner.nextDouble();
            if (b < 0){
                System.out.println("Informe um valor maior ou igual a zero.");
                informSegundoIntevalo(scanner);
            }else{
                parametros.add(1,b);
                informaTempoMedio();
            }
        }catch (InputMismatchException e){
            System.out.println("Digite apenas numeros.");
            informaParamDistUniforme();
        }
    }

    private static void informaTempoMedio() {
        System.out.println("Digite o valor medio do tempo de servico em segundos: ");
        Scanner scanner = new Scanner(System.in);
        try{
            int tempoServico = scanner.nextInt();
            if(tempoServico < 0){
                System.out.println("Digite apenas numeros positivos.");
                informaTempoMedio();
            }else{

                valorTempoMedioServico *= tempoServico;
                informaDuracaoSimulacao();
            }
        }catch (InputMismatchException e){
            System.out.println("Digite apenas numeros");
            informaTempoMedio();
        }

    }

    private static void informaDuracaoSimulacao() {
        System.out.println("Digite o tempo da simulacao em segundos: ");
        Scanner scanner = new Scanner(System.in);
        try{

            int tempoSimulacao = scanner.nextInt();
            if (tempoSimulacao < 0){
                System.out.println("Digite apenas numeros positivos.");
                informaDuracaoSimulacao();
            }else{

                duracaoSimulacao *= tempoSimulacao;
                informaQtdRepeticao();
            }
        }catch (InputMismatchException e){
            System.out.println("Digite apenas numeros");
            informaDuracaoSimulacao();
        }
    }

    private static void informaQtdRepeticao() {
        System.out.println("Digite a quantidade de repeticoes: ");
        Scanner scanner = new Scanner(System.in);
        try{

            repeticoes = scanner.nextInt();
            if (repeticoes < 0){
                System.out.println("Digite apenas numeros maiores que zero");
                informaQtdRepeticao();
            }else{

                executaSimulacao();

            }

        }catch (InputMismatchException e){
            System.out.println("Digite apenas numeros maiores que zero");
            informaQtdRepeticao();
        }
    }

    private static void executaSimulacao() {
        AbstractRealDistribution distribuicao = retornaDistribuicaoEscolhida();
        Simulador simulador = new Simulador(distribuicao, valorTempoMedioServico, duracaoSimulacao, repeticoes);
        simulador.executaSimulacao();
    }

    private static void imprimeCabecalho() {
        System.out.println("\033[35m################################################# \n" +
                "#                   ADSD 2017.2                 #\n" +
                "# Alunos: Aillkeen Oliveira e Izabella Antonino #\n" +
                "#################################################");
    }

    private static AbstractRealDistribution retornaDistribuicaoEscolhida() {
        switch (distribuicao) {
            case DISTRIBUICAO_NORMAL:
                Double media = parametros.get(0);
                Double desvioPadrao = parametros.get(1);
                return new NormalDistribution(media, desvioPadrao);
            case DISTRIBUICAO_EXPONENCIAL:
                Double mediaExp = parametros.get(0);
                return new ExponentialDistribution(mediaExp);
            case DISTRIBUICAO_UNIFORME:
                return new UniformRealDistribution(parametros.get(0), parametros.get(1));
        }
        throw new RuntimeException("Os parâmetros da distribuição está incorreto.");
    }
}

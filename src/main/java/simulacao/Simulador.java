package simulacao;

import model.Fregues;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import util.Util;
import java.util.Date;
import java.util.Locale;


public class Simulador {

    private final AbstractRealDistribution distribuicao;
    private final int duracaoMaxSimulacao;
    private final int repeticoes;
    private Escalonador escalonador;
    private Long inicioSimulacao;
    private int valorMedioServico;

    public Simulador(AbstractRealDistribution distribuicao, int valorMedioServico, int duracaoMaxSimulacao, int repeticoes) {
        this.distribuicao = distribuicao;
        this.duracaoMaxSimulacao = duracaoMaxSimulacao;
        this.repeticoes = repeticoes;
        this.escalonador = new Escalonador(valorMedioServico);
        this.valorMedioServico = valorMedioServico;
    }

    private boolean finalAtendimento(){
        Date data = new Date();
        return data.getTime() - inicioSimulacao > duracaoMaxSimulacao;

    }

    private String executa(){
        int totalRequisicao = 0;
        inicioSimulacao = new Date().getTime();
        String resposta = "";

        while (!finalAtendimento()){
            String rotuloFregues = "Fregues" + totalRequisicao++;
            escalonador.addFregues(new Fregues(rotuloFregues),this::finalAtendimento);
            int proximoAtendimento = (int) Math.abs(distribuicao.sample());

            esperaProxAtendimento(proximoAtendimento);

        }

        resposta = geraResposta(resposta);

        return resposta;
    }

    private String geraResposta(String resposta) {
        int totalRequisicoesAtendidas = escalonador.getTotalRequisicoesAtendidas();
        double mediaAtendimento = escalonador.getMediaAtendimento();
        int totalRequisicoesRecebidas = escalonador.getTotalRequisicoesRecebidas();
        double mediaElementosEspera = escalonador.getMediaElementosEspera();
        Long finalSimulacao = new Date().getTime();
        Long duracaoSimulacao = finalSimulacao - inicioSimulacao;

        resposta += retornaTipoDistribuicao(resposta);
        resposta += "Valor médio serviço passado: " + (valorMedioServico/1000) + "\n";
        resposta += "Duração da simulação: " + (duracaoSimulacao/1000) + " segundos \n";
        resposta += "Quantidade de requisições recebidas: " + totalRequisicoesRecebidas + "\n";
        resposta += "Quantidade de requisições atendidas: " + totalRequisicoesAtendidas + "\n";
        resposta += String.format(Locale.getDefault(),"Quantidade média de elementos em espera: %.2f%s ", mediaElementosEspera,System.lineSeparator());
        resposta += String.format(Locale.getDefault(),"Tempo médio de atendimento: %.2f%s",(mediaAtendimento/1000), " segundos \n");
        return resposta;
    }

    private String retornaTipoDistribuicao(String resposta) {
        if (distribuicao instanceof NormalDistribution) {
            resposta += "Distribuição Normal - Media: ";
            resposta += String.format(Locale.getDefault(),"%.2f",((NormalDistribution) distribuicao).getMean());
            resposta += String.format(Locale.getDefault()," / Desvio padrão: %.2f%s",((NormalDistribution) distribuicao).getStandardDeviation(),System.lineSeparator());


            return resposta;
        }else if (distribuicao instanceof  ExponentialDistribution){
            resposta += String.format(Locale.getDefault(),"Distribuição Exponencial - Media: %.2f%s",((ExponentialDistribution) distribuicao).getMean(), System.lineSeparator());
            return resposta;
        }else{
            resposta += "Distribuição Uniforme - intervalo: [" + distribuicao.getSupportLowerBound() + "," + distribuicao.getSupportUpperBound() + "] \n";
            return resposta;
        }
    }

    public void executaSimulacao(){
        String resposta = "";
        String nomeArquivo;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < repeticoes+1; i++) {
            System.out.println();
            System.out.println("\033[34m ##### Rodando " + i + "ª repetição #####");
            stringBuilder.append("##### Rodando ").append(i).append("ª repetição #####");
            stringBuilder.append(System.getProperty("line.separator"));
            stringBuilder.append(System.getProperty("line.separator"));
            System.out.println();
            resposta = executa();

            stringBuilder.append(resposta);

            System.out.println(resposta);
            System.out.println("\033[34m <--- Final da " + i + "º repetição --->");
            stringBuilder.append("<--- Final da ").append(i).append("º repetição --->");
            stringBuilder.append(System.getProperty("line.separator"));
            stringBuilder.append(System.getProperty("line.separator"));

        }

        escalonador.shutDown();
        System.out.println();
        System.out.println("\033[31m $$$ Fim da simulação $$$");
        stringBuilder.append("$$$ Fim da simulação $$$");
        stringBuilder.append(System.getProperty("line.separator"));

        if (distribuicao instanceof NormalDistribution) {
           nomeArquivo = "Normal";
        }else if (distribuicao instanceof  ExponentialDistribution){

            nomeArquivo = "Exponencial";
        }else{

            nomeArquivo = "Uniforme";

        }
        System.out.println("Grando resultados na pasta artefatos...");
        Util.exportaResutado(nomeArquivo,stringBuilder.toString());
    }

    private void esperaProxAtendimento(int proximoAtendimento) {
        try{
            Thread.sleep(proximoAtendimento);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

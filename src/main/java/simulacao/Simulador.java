package simulacao;

import model.Fregues;
import org.apache.commons.math3.distribution.AbstractRealDistribution;

import java.util.Date;
import java.util.Locale;


public class Simulador {

    private final AbstractRealDistribution distribuicao;
    private final int duracaoMaxSimulacao;
    private final int repeticoes;
    private Escalonador escalonador;
    private Long inicioSimulacao;

    public Simulador(AbstractRealDistribution distribuicao, int valorTempoMedio, int duracaoMaxSimulacao, int repeticoes) {
        this.distribuicao = distribuicao;
        this.duracaoMaxSimulacao = duracaoMaxSimulacao;
        this.repeticoes = repeticoes;
        this.escalonador = new Escalonador(valorTempoMedio);
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
            escalonador.addFregues(new Fregues(rotuloFregues),finalAtendimento());
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
        resposta += "Duração da simulação: " + (duracaoSimulacao/1000) + " segundos \n";
        resposta += "Quantidade de requisições recebidas: " + totalRequisicoesRecebidas + "\n";
        resposta += "Quantidade de requisições atendidas: " + totalRequisicoesAtendidas + "\n";
        resposta += String.format(Locale.getDefault(),"Quantidade média de elementos em espera: %.2f%s ", mediaElementosEspera,System.lineSeparator());
        resposta += String.format(Locale.getDefault(),"Tempo médio de atendimento: %.2f%s",(mediaAtendimento/1000), " segundos \n");
        return resposta;
    }

    public void executaSimulacao(){
        for (int i = 1; i < repeticoes+1; i++) {
            System.out.println();
            System.out.println("\033[34m ##### Rodando " + i + "º repetição #####");
            String saida = executa();
            System.out.println(saida);
            System.out.println("\033[34m <--- Final da " + i + "º repetição --->");
        }

        escalonador.shutDown();
        System.out.println();
        System.out.println("\033[31m $$$ Fim da simulação $$$");
    }

    private void esperaProxAtendimento(int proximoAtendimento) {
        try{
            Thread.sleep(proximoAtendimento);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

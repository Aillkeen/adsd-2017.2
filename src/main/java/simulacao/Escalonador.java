package simulacao;

import com.google.common.base.Supplier;
import model.Fregues;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Escalonador {

    private final Queue<Fregues> fila;
    private AbstractRealDistribution distribuicao;
    private int totalRequisicoesAtendidas;
    private int mediaAtendimento;
    private int totalRequisicoesRecebidas;
    private int mediaElementosEspera;
    private ExecutorService executorService;


    public Escalonador(int media) {
        this.fila = new ConcurrentLinkedDeque<>();
        this.distribuicao = new ExponentialDistribution(media);
        this.totalRequisicoesAtendidas = 0;
        this.mediaAtendimento = 0;
        this.totalRequisicoesRecebidas = 0;
        this.mediaElementosEspera = 0;
        this.executorService = Executors.newSingleThreadExecutor();

    }

    private void executaServico(Supplier<Boolean> finalAtendimento){

        executorService.submit(new Runnable() {
            public void run() {
                synchronized (fila){
                    totalRequisicoesRecebidas++;
                    fila.poll();
                    int tempoProcessado = (int) Math.abs(distribuicao.sample());
                    calculaMediaElementosEspera();
                    if (finalAtendimento.get()) {
                        return;
                    }

                    try{
                        Thread.sleep(tempoProcessado);
                    }catch (InterruptedException e){
                        throw new RuntimeException(e.getMessage());
                    }
                    if (finalAtendimento.get())
                        return;
                    totalRequisicoesAtendidas++;
                    calculaTempoAtendimento(tempoProcessado);

                }
            }
        });
    }

    private void calculaTempoAtendimento(int tempoProcessado) {
        mediaAtendimento = (mediaAtendimento * (totalRequisicoesAtendidas -1) + tempoProcessado) / totalRequisicoesAtendidas;
    }

    private void calculaMediaElementosEspera() {
        mediaElementosEspera = (mediaElementosEspera * (totalRequisicoesRecebidas -1) + fila.size())/ totalRequisicoesRecebidas;
    }

    public void addFregues(Fregues fregues, Supplier<Boolean> encerraAtendimento){
        this.fila.offer(fregues);
        executaServico(encerraAtendimento);
    }

    public int getTotalRequisicoesAtendidas() {
        return totalRequisicoesAtendidas;
    }


    public int getMediaAtendimento() {
        return mediaAtendimento;
    }


    public int getTotalRequisicoesRecebidas() {
        return totalRequisicoesRecebidas;
    }


    public int getMediaElementosEspera() {
        return mediaElementosEspera;
    }


    public void shutDown() {
        executorService.shutdown();
    }
}

package Modelo;

import com.sun.source.tree.BreakTree;

public class Medico {
    private String nome;
    private String especialidade;
    private int horaEntrada;
    private int horaSaida;
    private int salario;
    private boolean disponivel;
    private int horasSeguidasTrabalhadas;
    private int tempoOcupadoRestante = 0;

    public Medico(String nome, String especialidade, int horaEntrada, int horaSaida, int salario, boolean disponivel, int horasSeguidasTrabalhadas) {
        this.nome = nome;
        this.especialidade = especialidade;
        this.horaEntrada = horaEntrada;
        this.horaSaida = horaSaida;
        this.salario = salario;
        this.disponivel = false;
        this.horasSeguidasTrabalhadas = horasSeguidasTrabalhadas;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public int getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(int horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public int getHoraSaida() {
        return horaSaida;
    }

    public void setHoraSaida(int horaSaida) {
        this.horaSaida = horaSaida;
    }

    public int getSalario() {
        return salario;
    }

    public void setSalario(int salario) {
        this.salario = salario;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public int getHorasSeguidasTrabalhadas() {
        return horasSeguidasTrabalhadas;
    }

    public void setHorasSeguidasTrabalhadas(int horasSeguidasTrabalhadas) {
        this.horasSeguidasTrabalhadas = horasSeguidasTrabalhadas;
    }

    public int getTempoOcupadoRestante() { return tempoOcupadoRestante; }

    public void setTempoOcupadoRestante(int tempo) { this.tempoOcupadoRestante = tempo; }

    public void decrementarTempoOcupado() {
        if (this.tempoOcupadoRestante > 0) {
            this.tempoOcupadoRestante--;
            if (this.tempoOcupadoRestante == 0) {
                this.setDisponivel(true); // Fica livre quando o tempo acaba
            }
        }
    }

    @Override
    public String toString(){
       return "Nome: " + nome + " | Especialidade: " + especialidade + " | Horário: " + horaEntrada + " até " + horaSaida;
    }

}

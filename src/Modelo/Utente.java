package Modelo;

public class Utente {
    private int numero;
    public String nome;
    public int idade;
    private String sintoma;
    private String NivelUrgencia;
    private int tempoEsperaNivel;

//    public Utente(int numero, String nome, int idade, String sintoma, String nivelUrgencia) {
//        this.numero = numero;
//        this.nome = nome;
//        this.idade = idade;
//        this.sintoma = sintoma;
//        NivelUrgencia = nivelUrgencia;
//        this.tempoEsperaNivel = 0;
//    }

    // Novo construtor para Admissão (antes da triagem)
    public Utente(int numero, String nome, int idade) {
        this.numero = numero;
        this.nome = nome;
        this.idade = idade;
        this.sintoma = "Pendente";        // Valor padrão
        this.NivelUrgencia = "A aguardar"; // Valor padrão
        this.tempoEsperaNivel = 0;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getSintoma() {
        return sintoma;
    }

    public void setSintoma(String sintoma) {
        this.sintoma = sintoma;
    }

    public String getNivelUrgencia() {
        return NivelUrgencia;
    }

    public void setNivelUrgencia(String nivelUrgencia) {
        NivelUrgencia = nivelUrgencia;
    }

    public int getTempoEsperaNivel() {
        return tempoEsperaNivel;
    }

    public void incrementarTempoEspera() {
        this.tempoEsperaNivel++;
    }

    public void resetarTempoEspera() {
        this.tempoEsperaNivel = 0;
    }

    @Override
    public String toString(){
        return  " Número:" + numero + " | Nome: " + nome + " | Idade:  " + idade +  " | Sintoma " + sintoma + " | Nível da Urgência " + NivelUrgencia + "| Tempo de Espera : " + tempoEsperaNivel;
    }
}

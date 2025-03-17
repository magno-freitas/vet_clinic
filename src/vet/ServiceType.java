package vet;

public enum ServiceType {
    BANHO("Banho"),
    TOSA("Tosa"),
    VACINA("Vacina"),
    CONSULTA("Consulta");
    
    private String descricao;
    
    ServiceType(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}
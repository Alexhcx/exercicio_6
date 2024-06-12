package com.example.exercicio_6;

public class Sighting {
    private int id;
    private int entityId;
    private String data;
    private String horario;
    private String local;
    public Sighting() {
    }
    public Sighting(int id, int entityId, String data, String horario, String local) {
        this.id = id;
        this.entityId = entityId;
        this.data = data;
        this.horario = horario;
        this.local = local;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getEntityId() {
        return entityId;
    }
    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }
    public String getData() { // Getter para a data
        return data;
    }
    public void setData(String data) { // Setter para a data
        this.data = data;
    }
    public String getHorario() { // Getter para o horário
        return horario;
    }
    public void setHorario(String horario) { // Setter para o horário
        this.horario = horario;
    }
    public String getLocal() { // Getter para o local
        return local;
    }
    public void setLocal(String local) { // Setter para o local
        this.local = local;
    }
}
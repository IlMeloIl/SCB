package com.example.demo.simulator;

public class TrancaSimulator {
    private int numero;
    private boolean travada;
    private boolean temBicicleta;
    private String statusDisplay;
    private Long bicicletaId;

    public TrancaSimulator(int numero) {
        this.numero = numero;
        this.travada = true;
        this.temBicicleta = false;
        this.statusDisplay = "Tranca disponível";
    }
  
    public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public boolean isTemBicicleta() {
		return temBicicleta;
	}

	public void setTemBicicleta(boolean temBicicleta) {
		this.temBicicleta = temBicicleta;
	}

	public String getStatusDisplay() {
		return statusDisplay;
	}

	public void setStatusDisplay(String statusDisplay) {
		this.statusDisplay = statusDisplay;
	}

	public Long getBicicletaId() {
		return bicicletaId;
	}

	public void setBicicletaId(Long bicicletaId) {
		this.bicicletaId = bicicletaId;
	}

	public boolean isTravada() {
		return travada;
	}

	// Remove setTravada público para evitar inconsistências
    private void setTravada(boolean travada) {
        this.travada = travada;
    }

    public void liberar() {
        System.out.println("Tentando liberar tranca " + numero + 
            " (travada=" + travada + ", temBicicleta=" + temBicicleta + ")");
            
        if (!temBicicleta) {
            throw new IllegalStateException("Tranca " + numero + " não tem bicicleta para liberar");
        }
        if (!travada) {
            throw new IllegalStateException("Tranca " + numero + " já está destravada");
        }

        setTravada(false);
        this.temBicicleta = false;
        this.statusDisplay = "Bicicleta liberada! ✅";
        System.out.println("🔓 Tranca " + numero + " liberada - Bicicleta " + bicicletaId + " removida");
        this.bicicletaId = null;
    }

    public void travar(Long bicicletaId) {
        System.out.println("Tentando travar tranca " + numero + 
            " (travada=" + travada + ", temBicicleta=" + temBicicleta + ")");
            
        if (temBicicleta) {
            throw new IllegalStateException("Tranca " + numero + " já tem uma bicicleta");
        }
        if (travada) {
            throw new IllegalStateException("Tranca " + numero + " já está travada");
        }

        setTravada(true);
        this.temBicicleta = true;
        this.bicicletaId = bicicletaId;
        this.statusDisplay = "Bicicleta " + bicicletaId + " travada! ✅";
        System.out.println("🔒 Tranca " + numero + " travada - Bicicleta " + bicicletaId + " detectada");
    }

    // Método público para preparar tranca para receber bicicleta
    public void prepararParaBicicleta() {
        if (!travada || temBicicleta) {
            throw new IllegalStateException("Tranca " + numero + " não pode ser preparada");
        }
        setTravada(false);
        this.statusDisplay = "Tranca pronta para receber bicicleta";
        System.out.println("🔓 Tranca " + numero + " preparada para receber bicicleta");
    }

    public String getStatus() {
        if (travada && temBicicleta) return "🔒 🚲 (ID:" + bicicletaId + ")";
        if (travada && !temBicicleta) return "🔒 ❌";
        return "🔓";
    }
}

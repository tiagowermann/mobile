package egtec.com.mylocation.Model;

public class Alerta {

    public int id;
    public int id_categoria;
    public int id_passeio;
    public String foto;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }

    public int getId_passeio() {
        return id_passeio;
    }

    public void setId_passeio(int id_passeio) {
        this.id_passeio = id_passeio;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}

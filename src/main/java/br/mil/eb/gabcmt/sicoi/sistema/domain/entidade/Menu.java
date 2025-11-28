package br.mil.eb.gabcmt.sicoi.sistema.domain.entidade;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MenuEntity")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private Long parentId;
    private String url;
    private String icone;
    private Integer ordem;

    @Column(name = "favorito")
    private Boolean favorito = false;

    // Getter personalizado para garantir que nunca retorne null
    public Boolean getFavorito() {
        return favorito != null ? favorito : false;
    }

    // Método auxiliar para verificar se é favorito (retorna primitive boolean)
    public boolean isFavorito() {
        return favorito != null && favorito;
    }

}

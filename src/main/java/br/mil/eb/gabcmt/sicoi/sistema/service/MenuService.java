package br.mil.eb.gabcmt.sicoi.sistema.service;

import br.mil.eb.gabcmt.sicoi.sistema.domain.entidade.Menu;
import br.mil.eb.gabcmt.sicoi.sistema.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository repository;

    public List<Menu> listaTodos() {
        return repository.findAll();
    }

    public Menu salvar(Menu menuEntity) {
        return repository.save(menuEntity);
    }
}

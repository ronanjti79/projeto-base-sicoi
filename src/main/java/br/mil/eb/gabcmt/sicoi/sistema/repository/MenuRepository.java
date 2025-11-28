package br.mil.eb.gabcmt.sicoi.sistema.repository;

import br.mil.eb.gabcmt.sicoi.sistema.domain.entidade.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {}

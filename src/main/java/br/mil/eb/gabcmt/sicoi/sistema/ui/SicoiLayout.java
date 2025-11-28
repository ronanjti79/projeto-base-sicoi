package br.mil.eb.gabcmt.sicoi.sistema.ui;

import br.mil.eb.gabcmt.sicoi.sistema.domain.entidade.Menu;
import br.mil.eb.gabcmt.sicoi.sistema.service.MenuService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.stream.Collectors;

@CssImport("themes/sicoi/styles.css")
public class SicoiLayout extends AppLayout implements RouterLayout {

    @Value("${app.version}")
    private String VERSAO_PROJETO = "";

    // Services
    private final MenuService MenuService;

    // Estruturas de dados para o menu
    private Map<Long, List<Menu>> subMenusPorParentId;
    private List<Menu> menusNivelSuperior;
    private Long idPaiAtualmenteSelecionado = null;

    // Cache de componentes do menu para atualização de estilo
    private final Map<Long, SideNavItem> itensNavPrimarioPorId = new HashMap<>();
    private final Map<Long, Button> botoesIconePorId = new HashMap<>();

    // Componentes da UI que precisam ser acessados em múltiplos métodos
    private Div areaConteudo;
    private SideNav navegacaoSecundaria;
    private VerticalLayout layoutSubmenu;
    private Span tituloSubmenu;
    private Span spanNomeSubmenu;
    private Span breadcrumbSubmenu;
    private VerticalLayout menuPrimarioApenasIcone;
    private DrawerToggle alternarDrawer;

    private TextField campoPesquisaSubmenu;

    // Seção de favoritos
    private VerticalLayout secaoFavoritos;

    // Constantes para as classes CSS de seleção
    private static final String CLASSE_ITEM_PRIMARIO_SELECIONADO = "manually-selected-primary-item";
    private static final String CLASSE_ITEM_ICONE_SELECIONADO = "manually-selected-icon-only-item";

    public SicoiLayout(@Value("${app.version}") String versaoProjeto, MenuService MenuService) {
        VERSAO_PROJETO = versaoProjeto;
        this.MenuService = MenuService;

        prepararDadosMenu();
        iniciarComponentes();
        montarLayout();
        configurarListenersEventos();
        aplicarEstadoInicial();
    }

    private void prepararDadosMenu() {
        List<Menu> todosMenus = MenuService.listaTodos();

        menusNivelSuperior = todosMenus.stream()
                .filter(item -> item.getParentId() == null)
                .sorted(Comparator.comparing(Menu::getOrdem))
                .collect(Collectors.toList());

        subMenusPorParentId = todosMenus.stream()
                .filter(item -> item.getParentId() != null)
                .sorted(Comparator.comparing(Menu::getOrdem))
                .collect(Collectors.groupingBy(
                        Menu::getParentId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    private void iniciarComponentes() {
        setPrimarySection(Section.DRAWER);
        alternarDrawer = new DrawerToggle();
        alternarDrawer.getElement().setAttribute("aria-label", "Alternar Menu Principal");

        spanNomeSubmenu = new Span();
        spanNomeSubmenu.getStyle().set("font-weight", "bold");

        breadcrumbSubmenu = new Span();
        areaConteudo = new Div();
        areaConteudo.getStyle().set("overflow", "auto");
        areaConteudo.setSizeFull();

        navegacaoSecundaria = new SideNav();
        navegacaoSecundaria.setWidth("250px");
        navegacaoSecundaria.setHeightFull();
        navegacaoSecundaria.setCollapsible(false);

        tituloSubmenu = new Span();
        tituloSubmenu.getStyle().set("font-weight", "bold");

        campoPesquisaSubmenu = new TextField();
        campoPesquisaSubmenu.setSuffixComponent(VaadinIcon.SEARCH.create());
        campoPesquisaSubmenu.setClearButtonVisible(true);
        campoPesquisaSubmenu.setWidth("90%");
    }

    private void montarLayout() {
        this.menuPrimarioApenasIcone = criarMenuPrimarioApenasIcone();
        addToDrawer(criarContainerMenuPrimario());

        Component barraTop = Topo.criarTopo();
        Button botaoAlternarSubmenu = criarBotaoAlternarSubmenu();
        this.layoutSubmenu = criarLayoutSubmenu();
        VerticalLayout areaConteudoPrincipal = criarAreaConteudoPrincipal(botaoAlternarSubmenu);
        Component rodape = Fundo.createFooter();

        HorizontalLayout corpoOuPagina = new HorizontalLayout(layoutSubmenu, areaConteudoPrincipal);
        corpoOuPagina.setSizeFull();
        corpoOuPagina.setSpacing(false);
        corpoOuPagina.setFlexGrow(0, layoutSubmenu);
        corpoOuPagina.setFlexGrow(1, areaConteudoPrincipal);

        VerticalLayout layoutPaginaPrincipal = new VerticalLayout();
        layoutPaginaPrincipal.getStyle()
                .set("background", "linear-gradient(to left, var(--lumo-primary-color-5pct), transparent 80%)")
                .set("height", "100vh")
                .set("overflow", "hidden");

        layoutPaginaPrincipal.setWidthFull();
        layoutPaginaPrincipal.setPadding(false);
        layoutPaginaPrincipal.setSpacing(false);

        // --- topo fixo ---
        barraTop.getStyle().set("flex-shrink", "0");

        // --- conteúdo central com scroll ---
        Div conteudoRolavel = new Div(corpoOuPagina);
        conteudoRolavel.setSizeFull();
        conteudoRolavel.getStyle()
                .set("overflow", "auto")
                .set("flex-grow", "1");

        // --- rodapé fixo ---
        rodape.getStyle().set("flex-shrink", "0");

        // Monta o layout fixo
        layoutPaginaPrincipal.add(barraTop, conteudoRolavel, rodape);
        layoutPaginaPrincipal.setFlexGrow(1, conteudoRolavel);

        HorizontalLayout wrapperConteudoRaiz = new HorizontalLayout(menuPrimarioApenasIcone, layoutPaginaPrincipal);
        wrapperConteudoRaiz.setSizeFull();
        wrapperConteudoRaiz.setSpacing(false);
        wrapperConteudoRaiz.setFlexGrow(1, layoutPaginaPrincipal);
        setContent(wrapperConteudoRaiz);
    }

    private void configurarListenersEventos() {
        getElement().addPropertyChangeListener("drawerOpened", event -> atualizarVisibilidadeMenuIcone());
        campoPesquisaSubmenu.addValueChangeListener(e -> filtrarMenuSecundario(e.getValue()));
    }

    private void aplicarEstadoInicial() {
        UI.getCurrent().getPage().fetchCurrentURL(url -> {
            String caminhoAtual = url.getPath().replaceFirst("^/", "");

            if (caminhoAtual.isBlank()) {
                // Está na página inicial (Dashboard)
                layoutSubmenu.setVisible(false);
                secaoFavoritos.setVisible(false);
                spanNomeSubmenu.setText("Dashboard");
                idPaiAtualmenteSelecionado = null;
            } else {
                // Tenta encontrar qual menu principal contém esta URL
                Long idPaiEncontrado = encontrarIdPaiPorUrl(caminhoAtual);

                if (idPaiEncontrado != null) {
                    idPaiAtualmenteSelecionado = idPaiEncontrado;
                    layoutSubmenu.setVisible(true);
                    atualizarMenuSecundario(idPaiEncontrado);

                    // Atualiza o breadcrumb com base na URL atual
                    atualizarBreadcrumbPorUrl(caminhoAtual, idPaiEncontrado);
                } else {
                    // URL não encontrada nos menus
                    layoutSubmenu.setVisible(false);
                    secaoFavoritos.setVisible(false);
                    idPaiAtualmenteSelecionado = null;
                }
            }

            atualizarVisibilidadeMenuIcone();
            atualizarEstiloSelecaoMenuPrimario();
        });
    }

    private VerticalLayout criarMenuPrimarioApenasIcone() {
        VerticalLayout menu = new VerticalLayout();
        menu.setWidth("100px");
        menu.setHeightFull();
        menu.setSpacing(false);
        menu.addClassName(LumoUtility.Padding.Horizontal.MEDIUM);
        menu.getStyle().set("background", "linear-gradient(to top, var(--lumo-primary-color-10pct), transparent 80%)");
        menu.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        Image icone = new Image("icons/SiCoI-S-Logo_v0.0.14 1.png", "Logo Sicoi");
        icone.setWidth("4.3rem");
        icone.setHeight("3.5rem");
        menu.add(icone);

        botoesIconePorId.clear();
        for (Menu item : menusNivelSuperior) {
            if (subMenusPorParentId.containsKey(item.getId())) {
                Icon iconeBotao = criarIcone(item.getIcone(), "38px");
                iconeBotao.getStyle().set("color", "var(--lumo-primary-color)");

                Button botaoIcone = new Button(iconeBotao);
                botaoIcone.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
                botaoIcone.setWidth("75px");
                botaoIcone.setHeight("75px");
                botaoIcone.getStyle().set("margin", "8px 0");

                botaoIcone.addClickListener(e -> {
                    idPaiAtualmenteSelecionado = item.getId();
                    atualizarEstiloSelecaoMenuPrimario();
                    atualizarMenuSecundario(item.getId());
                    alternarDrawer.setVisible(true);
                });

                Tooltip.forComponent(botaoIcone)
                        .withText(item.getNome())
                        .withPosition(Tooltip.TooltipPosition.BOTTOM);

                menu.add(botaoIcone);
                botoesIconePorId.put(item.getId(), botaoIcone);
            }
        }
        return menu;
    }

    private VerticalLayout criarContainerMenuPrimario() {
        VerticalLayout containerNav = new VerticalLayout();
        containerNav.setClassName("nav-container");
        containerNav.setSpacing(false);
        containerNav.setPadding(true);
        containerNav.setHeightFull();
        containerNav.getStyle().set("overflow-y", "auto");

        Image icone = new Image("icons/SiCoI.png", "Logo Sicoi");
        icone.setWidth("180px");
        icone.setHeight("37px");

        Span nomeApp = new Span();
        nomeApp.setText(VERSAO_PROJETO);
        nomeApp.setClassName("app-name-span");
        nomeApp.getStyle()
                .set("font-weight", "bold")
                .set("font-size", "0.8em")
                .set("margin-top", "4px");

        VerticalLayout layoutLogo = new VerticalLayout(icone, nomeApp);
        layoutLogo.setAlignItems(FlexComponent.Alignment.CENTER);
        layoutLogo.setPadding(false);
        layoutLogo.setSpacing(false);
        containerNav.add(layoutLogo);

        SideNav menuAtual = new SideNav();
        menuAtual.addClassName("primary-nav");
        menuAtual.setWidthFull();

        itensNavPrimarioPorId.clear();
        for (Menu item : menusNivelSuperior) {
            if (subMenusPorParentId.containsKey(item.getId())) {
                SideNavItem itemGatilhoPai = new SideNavItem(
                        item.getNome(),
                        (String) null,
                        criarIcone(item.getIcone())
                );
                itensNavPrimarioPorId.put(item.getId(), itemGatilhoPai);

                itemGatilhoPai.getElement().addEventListener("click", event -> {
                    UI.getCurrent().access(() -> {
                        idPaiAtualmenteSelecionado = item.getId();
                        atualizarEstiloSelecaoMenuPrimario();
                        atualizarMenuSecundario(item.getId());
                    });
                });

                menuAtual.addItem(itemGatilhoPai);
            } else {
                SideNavItem itemNavOuEstatico = new SideNavItem(
                        item.getNome(),
                        item.getUrl(),
                        criarIcone(item.getIcone())
                );

                itemNavOuEstatico.getElement().addEventListener("click", event -> {
                    idPaiAtualmenteSelecionado = null;
                    atualizarEstiloSelecaoMenuPrimario();
                });

                menuAtual.addItem(itemNavOuEstatico);
            }
        }

        containerNav.add(menuAtual);
        return containerNav;
    }

    private Button criarBotaoAlternarSubmenu() {
        Button botaoAbrirFecharSubmenu = new Button(VaadinIcon.MENU.create(), e -> {
            boolean estaCompletamenteAberto = isDrawerOpened() && navegacaoSecundaria.isVisible();
            if (estaCompletamenteAberto) {
                navegacaoSecundaria.setVisible(false);
                tituloSubmenu.setVisible(false);
                campoPesquisaSubmenu.setVisible(false);
                secaoFavoritos.setVisible(false);
                setDrawerOpened(false);
                alternarDrawer.setVisible(false);
            } else {
                alternarDrawer.setVisible(true);
                campoPesquisaSubmenu.setVisible(true);
                setDrawerOpened(true);
                if (idPaiAtualmenteSelecionado != null) {
                    atualizarMenuSecundario(idPaiAtualmenteSelecionado);
                    secaoFavoritos.setVisible(true);
                }
            }
        });

        botaoAbrirFecharSubmenu.getElement().setAttribute("aria-label", "Expandir/Recolher Menus");
        botaoAbrirFecharSubmenu.getStyle()
                .set("background", "none")
                .set("background-color", "transparent");
        return botaoAbrirFecharSubmenu;
    }

    /**
     * Cria o layout para o menu secundário, incluindo o campo de pesquisa.
     */
    private VerticalLayout criarLayoutSubmenu() {
        HorizontalLayout cabecalhoSubmenu = new HorizontalLayout(alternarDrawer, tituloSubmenu);
        cabecalhoSubmenu.setWidthFull();
        cabecalhoSubmenu.setAlignItems(FlexComponent.Alignment.CENTER);
        cabecalhoSubmenu.setPadding(false);
        cabecalhoSubmenu.getStyle().set("padding-bottom", "0");

        // Cria a seção de favoritos
        secaoFavoritos = criarSecaoFavoritos();

        // Adiciona os componentes ao layout
        VerticalLayout layout = new VerticalLayout(
                cabecalhoSubmenu,
                campoPesquisaSubmenu,
                secaoFavoritos,
                navegacaoSecundaria
        );

        layout.getStyle()
                .set("border-right", "1px solid var(--lumo-contrast-5pct)")
                .set("background", "linear-gradient(to bottom, var(--lumo-primary-color-10pct), transparent 80%)");
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setWidth(null);
        layout.setHeightFull();
        navegacaoSecundaria.getStyle().set("overflow-y", "auto");

        return layout;
    }

    /**
     * Cria a seção de favoritos
     */
    private VerticalLayout criarSecaoFavoritos() {
        VerticalLayout secaoFav = new VerticalLayout();
        secaoFav.setWidth("100%");
        secaoFav.setPadding(true);
        secaoFav.setSpacing(false);
        secaoFav.getStyle()
                .set("margin-bottom", "8px")
                .set("padding-bottom", "8px")
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)");

        // Header "Favoritos"
        HorizontalLayout cabecalhoFav = new HorizontalLayout();
        cabecalhoFav.setWidthFull();
        cabecalhoFav.setAlignItems(FlexComponent.Alignment.CENTER);
        cabecalhoFav.getStyle()
                .set("cursor", "pointer")
                //.set("padding", "4px 12px")      // padding similar ao SideNavItem
                .set("border-radius", "4px");

        // Efeito hover
        cabecalhoFav.getElement().addEventListener("mouseenter", e ->
                cabecalhoFav.getStyle().set("background-color", "var(--lumo-primary-color-5pct)")
        );
        cabecalhoFav.getElement().addEventListener("mouseleave", e ->
                cabecalhoFav.getStyle().set("background-color", "transparent")
        );

        // Ícone estrela à esquerda
        Icon iconeEstrela = VaadinIcon.STAR.create();
        iconeEstrela.setSize("1em");
        iconeEstrela.getStyle().set("color", "#FFD700");

        // Texto "Favoritos"
        Span rotuloFav = new Span("Favoritos");
        rotuloFav.getStyle()
                .set("font-size", "1em")
                .set("color", "var(--lumo-contrast-70pct)")
                .set("font-weight", "500");

        // Ícone de expand/collapse à direita, cinza
        Icon iconeExpandir = VaadinIcon.CHEVRON_DOWN.create();
        iconeExpandir.setSize("0.8em");
        iconeExpandir.getStyle()
                .set("color", "var(--lumo-contrast-50pct)")   // cinzinha
                .set("transition", "transform 0.2s");

        // Ordem: estrela, texto, flex-gap, chevron à direita
        cabecalhoFav.add(iconeEstrela, rotuloFav);
        cabecalhoFav.setFlexGrow(1, rotuloFav);               // empurra o chevron para a direita
        cabecalhoFav.add(iconeExpandir);
        cabecalhoFav.setSpacing(true);

        // SideNav para os favoritos
        SideNav navegacaoFavoritos = new SideNav();
        navegacaoFavoritos.setWidthFull();
        navegacaoFavoritos.setCollapsible(false);
        navegacaoFavoritos.getStyle()
                .set("margin-left", "0")
                .set("padding-left", "0");

        // Controle de expansão/colapso
        cabecalhoFav.addClickListener(e -> {
            boolean estaExpandido = navegacaoFavoritos.isVisible();
            navegacaoFavoritos.setVisible(!estaExpandido);

            // Rotaciona o ícone (aponta para a direita quando fechado)
            if (estaExpandido) {
                iconeExpandir.getElement().getStyle().set("transform", "rotate(-90deg)");
            } else {
                iconeExpandir.getElement().getStyle().set("transform", "rotate(0deg)");
            }
        });

        // Inicialmente expandido
        navegacaoFavoritos.setVisible(true);

        secaoFav.add(cabecalhoFav, navegacaoFavoritos);
        secaoFav.setVisible(false); // Inicialmente oculto

        return secaoFav;
    }

    private VerticalLayout criarAreaConteudoPrincipal(Button botaoAbrirFecharSubmenu) {
        var linhaHorizontal = new HorizontalLayout(botaoAbrirFecharSubmenu, spanNomeSubmenu);
        linhaHorizontal.getStyle().set("background", "linear-gradient(to right, var(--lumo-contrast-5pct), transparent 80%)");
        linhaHorizontal.setWidthFull();
        linhaHorizontal.setAlignItems(FlexComponent.Alignment.CENTER);

        var linhaHorizontal2 = new HorizontalLayout(breadcrumbSubmenu);
        linhaHorizontal2.getStyle()
                .set("margin", "0")
                .set("padding", "0 8px")
                .set("min-height", "unset")
                .set("height", "auto");
        linhaHorizontal2.setWidthFull();

        var conteudoTopo = new VerticalLayout(linhaHorizontal, linhaHorizontal2, areaConteudo);
        conteudoTopo.setPadding(false);
        conteudoTopo.setSpacing(false);
        conteudoTopo.setSizeFull();
        conteudoTopo.setFlexGrow(1, areaConteudo);

        return conteudoTopo;
    }

    /**
     * Atualiza o menu secundário, limpando o campo de pesquisa.
     */
    private void atualizarMenuSecundario(Long idPai) {
        // Limpa o campo de pesquisa ao trocar de menu principal
        if (!campoPesquisaSubmenu.getValue().isEmpty()) {
            campoPesquisaSubmenu.clear();
        }

        Menu pai = menusNivelSuperior.stream()
                .filter(item -> item.getId().equals(idPai))
                .findFirst().orElse(null);

        if (pai == null) {
            layoutSubmenu.setVisible(false);
            return;
        }

        layoutSubmenu.setVisible(true);
        tituloSubmenu.setText(pai.getNome());
        tituloSubmenu.setVisible(true);
        navegacaoSecundaria.removeAll();

        List<Menu> filhos = subMenusPorParentId.get(idPai);
        if (filhos != null && !filhos.isEmpty()) {
            // Atualiza a seção de favoritos
            atualizarSecaoFavoritos(filhos);

            for (Menu filho : filhos) {
                navegacaoSecundaria.addItem(criarItemNavComFavorito(filho, pai.getNome()));
            }
            navegacaoSecundaria.setVisible(true);
        } else {
            navegacaoSecundaria.setVisible(false);
            secaoFavoritos.setVisible(false);
        }
    }

    /**
     * Atualiza a seção de favoritos com os itens marcados como favoritos
     */
    private void atualizarSecaoFavoritos(List<Menu> itensMenu) {
        // Pega o SideNav de favoritos (segundo componente, após o header)
        if (secaoFavoritos.getComponentCount() < 2) {
            return;
        }

        SideNav navegacaoFav = (SideNav) secaoFavoritos.getComponentAt(1);
        navegacaoFav.removeAll();

        // Lista todos os favoritos (incluindo sub-níveis)
        List<Menu> favoritos = obterTodosFavoritos(itensMenu);

        if (!favoritos.isEmpty()) {
            // Agrupa favoritos por parent para criar hierarquia
            Map<Long, List<Menu>> favoritosPorPai = favoritos.stream()
                    .collect(Collectors.groupingBy(
                            fav -> fav.getParentId() != null ? fav.getParentId() : 0L,
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));

            // Adiciona os favoritos de nível superior
            for (Menu fav : favoritos) {
                if (deveMostrarComoNivelSuperior(fav, favoritos)) {
                    SideNavItem itemFav = criarItemNavFavorito(fav, favoritosPorPai);
                    navegacaoFav.addItem(itemFav);
                }
            }

            // Só mostra favoritos se a navegação secundária também estiver visível
            secaoFavoritos.setVisible(navegacaoSecundaria.isVisible());
        } else {
            secaoFavoritos.setVisible(false);
        }
    }

    /**
     * Verifica se um item deve ser mostrado como nível superior na lista de favoritos
     */
    private boolean deveMostrarComoNivelSuperior(Menu item, List<Menu> todosFavoritos) {
        // Se não tem parent, é top level
        if (item.getParentId() == null) {
            return true;
        }

        // Se tem parent, verifica se o parent também é favorito
        boolean paiEFavorito = todosFavoritos.stream()
                .anyMatch(f -> f.getId().equals(item.getParentId()));

        // Só mostra como top level se o parent NÃO for favorito
        return !paiEFavorito;
    }

    /**
     * Cria um SideNavItem para favoritos com hierarquia
     */
    private SideNavItem criarItemNavFavorito(Menu Menu, Map<Long, List<Menu>> favoritosPorPai) {
        Icon iconeEstrela = VaadinIcon.STAR.create();
        iconeEstrela.setSize("0.9em");
        iconeEstrela.getStyle().set("color", "#FFD700");

        SideNavItem itemFav = new SideNavItem(Menu.getNome(), Menu.getUrl());

        // Adiciona filhos favoritos se existirem
        List<Menu> filhos = favoritosPorPai.get(Menu.getId());
        if (filhos != null && !filhos.isEmpty()) {
            for (Menu filho : filhos) {
                SideNavItem itemFilho = criarItemNavFavorito(filho, favoritosPorPai);
                itemFav.addItem(itemFilho);
            }
            // Inicia expandido para facilitar a visualização
            itemFav.setExpanded(true);
        }

        // Adiciona listener para navegação
        itemFav.getElement().addEventListener("click", e -> {
            if (Menu.getUrl() != null && !Menu.getUrl().isBlank()) {
                UI.getCurrent().access(() -> {
                    UI.getCurrent().navigate(Menu.getUrl());
                });
            }
        });

        return itemFav;
    }

    /**
     * Cria um SideNavItem com botão de favorito
     */
    private SideNavItem criarItemNavComFavorito(Menu Menu, String nomePai) {
        SideNavItem itemNav;

        // Define o ícone baseado no estado de favorito e ícone do menu
        Icon iconeMenu = criarIconeMenuComFavorito(Menu);

        if (iconeMenu != null) {
            itemNav = new SideNavItem(Menu.getNome(), Menu.getUrl(), iconeMenu);
        } else {
            itemNav = new SideNavItem(Menu.getNome(), Menu.getUrl());
        }

        // Torna o ícone clicável para favoritar/desfavoritar
        if (iconeMenu != null) {
            iconeMenu.getElement().getStyle()
                    .set("cursor", "pointer")
                    .set("z-index", "10");

            iconeMenu.getElement().addEventListener("click", e -> {
                // Para a propagação do evento para não navegar
                UI.getCurrent().access(() -> {
                    // Alterna o estado de favorito
                    Menu.setFavorito(!Menu.isFavorito());
                    MenuService.salvar(Menu);

                    // Atualiza o ícone do item
                    Icon novoIcone = criarIconeMenuComFavorito(Menu);
                    if (novoIcone != null) {
                        itemNav.setPrefixComponent(novoIcone);
                        // Recria o listener no novo ícone
                        configurarListenerCliqueIcone(novoIcone, Menu, itemNav);
                    }

                    // Atualiza a seção de favoritos
                    if (idPaiAtualmenteSelecionado != null) {
                        List<Menu> filhos = subMenusPorParentId.get(idPaiAtualmenteSelecionado);
                        if (filhos != null) {
                            atualizarSecaoFavoritos(filhos);
                        }
                    }
                });
            }).addEventData("event.stopPropagation()");
        }

        // Adiciona filhos recursivamente
        List<Menu> filhos = subMenusPorParentId.get(Menu.getId());
        if (filhos != null) {
            filhos.forEach(filho -> itemNav.addItem(criarItemNavComFavorito(filho, nomePai)));
            itemNav.setExpanded(false);
        }

        itemNav.getElement().addEventListener("click", event -> {
            String textoClicado = event.getEventData().getString("event.target.innerText");
            if (textoClicado != null && !textoClicado.trim().isEmpty()) {
                String nomeClicado = textoClicado.split("\n")[0].trim();
                atualizarBreadcrumb(itemNav, nomePai, nomeClicado);
            }
        }).addEventData("event.target.innerText");

        return itemNav;
    }

    /**
     * Configura o listener de clique no ícone para favoritar/desfavoritar
     */
    private void configurarListenerCliqueIcone(Icon icone, Menu Menu, SideNavItem itemNav) {
        icone.getElement().getStyle()
                .set("cursor", "pointer")
                .set("z-index", "10");

        icone.getElement().addEventListener("click", e -> {
            UI.getCurrent().access(() -> {
                // Alterna o estado de favorito
                Menu.setFavorito(!Menu.isFavorito());
                MenuService.salvar(Menu);

                // Atualiza o ícone do item
                Icon novoIcone = criarIconeMenuComFavorito(Menu);
                if (novoIcone != null) {
                    itemNav.setPrefixComponent(novoIcone);
                    // Recria o listener no novo ícone
                    configurarListenerCliqueIcone(novoIcone, Menu, itemNav);
                }

                // Atualiza a seção de favoritos
                if (idPaiAtualmenteSelecionado != null) {
                    List<Menu> filhos = subMenusPorParentId.get(idPaiAtualmenteSelecionado);
                    if (filhos != null) {
                        atualizarSecaoFavoritos(filhos);
                    }
                }
            });
        }).addEventData("event.stopPropagation()");
    }

    /**
     * Cria o ícone do menu considerando se é favorito ou não
     */
    private Icon criarIconeMenuComFavorito(Menu Menu) {
        Icon icone;

        if (Menu.isFavorito()) {
            // Se for favorito, usa estrela amarela
            icone = VaadinIcon.STAR.create();
            icone.getStyle().set("color", "#FFD700");
        } else {
            // Se não for favorito, usa o ícone normal do menu
            if (Menu.getIcone() != null && !Menu.getIcone().isBlank()) {
                icone = criarIcone(Menu.getIcone(), "1.0em");
            } else {
                // Se não tem ícone definido, usa estrela vazia
                icone = VaadinIcon.STAR_O.create();
                icone.getStyle().set("color", "var(--lumo-contrast-30pct)");
            }
        }

        if (icone != null) {
            icone.setSize("1.0em");
        }

        return icone;
    }

    /**
     * Coleta recursivamente todos os itens marcados como favoritos
     */
    private List<Menu> obterTodosFavoritos(List<Menu> itensMenu) {
        List<Menu> favoritos = new ArrayList<>();
        for (Menu item : itensMenu) {
            if (item.isFavorito()) {
                favoritos.add(item);
            }
            // Verifica sub-itens
            List<Menu> filhos = subMenusPorParentId.get(item.getId());
            if (filhos != null) {
                favoritos.addAll(obterTodosFavoritos(filhos));
            }
        }
        return favoritos;
    }

    private void atualizarBreadcrumb(SideNavItem itemClicado, String nomePai, String nomeClicado) {
        List<String> caminho = new ArrayList<>();
        SideNavItem atual = itemClicado;
        while (atual != null) {
            caminho.add(0, atual.getLabel());
            atual = atual.getParent()
                    .filter(SideNavItem.class::isInstance)
                    .map(SideNavItem.class::cast)
                    .orElse(null);
        }
        String textoBreadcrumb = String.join(" > ", caminho);
        spanNomeSubmenu.setText(nomeClicado);
        breadcrumbSubmenu.setText(nomePai + " > " + textoBreadcrumb);
    }

    /**
     * Encontra o ID do menu principal (parent) baseado na URL fornecida
     */
    private Long encontrarIdPaiPorUrl(String url) {
        // Percorre todos os submenus para encontrar a URL
        for (Map.Entry<Long, List<Menu>> entrada : subMenusPorParentId.entrySet()) {
            if (urlExisteNaListaMenu(url, entrada.getValue())) {
                return entrada.getKey();
            }
        }
        return null;
    }

    /**
     * Verifica recursivamente se a URL existe na lista de menus
     */
    private boolean urlExisteNaListaMenu(String url, List<Menu> listaMenu) {
        for (Menu menu : listaMenu) {
            if (menu.getUrl() != null && menu.getUrl().equals(url)) {
                return true;
            }
            // Verifica nos filhos
            List<Menu> filhos = subMenusPorParentId.get(menu.getId());
            if (filhos != null && urlExisteNaListaMenu(url, filhos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Atualiza o breadcrumb baseado na URL atual
     */
    private void atualizarBreadcrumbPorUrl(String url, Long idPai) {
        Menu pai = menusNivelSuperior.stream()
                .filter(item -> item.getId().equals(idPai))
                .findFirst()
                .orElse(null);

        if (pai == null) {
            return;
        }

        // Procura o menu item com esta URL
        List<Menu> filhos = subMenusPorParentId.get(idPai);
        if (filhos != null) {
            Menu itemEncontrado = encontrarMenuPorUrl(url, filhos);
            if (itemEncontrado != null) {
                spanNomeSubmenu.setText(itemEncontrado.getNome());

                // Constrói o breadcrumb completo
                List<String> caminho = construirCaminhoParaMenu(itemEncontrado.getId());
                String textoBreadcrumb = String.join(" > ", caminho);
                breadcrumbSubmenu.setText(pai.getNome() + " > " + textoBreadcrumb);
            }
        }
    }

    /**
     * Encontra um Menu pela URL recursivamente
     */
    private Menu encontrarMenuPorUrl(String url, List<Menu> listaMenu) {
        for (Menu menu : listaMenu) {
            if (menu.getUrl() != null && menu.getUrl().equals(url)) {
                return menu;
            }
            // Verifica nos filhos
            List<Menu> filhos = subMenusPorParentId.get(menu.getId());
            if (filhos != null) {
                Menu encontrado = encontrarMenuPorUrl(url, filhos);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }

    /**
     * Constrói o caminho completo até um menu específico
     */
    private List<String> construirCaminhoParaMenu(Long idMenu) {
        List<String> caminho = new ArrayList<>();
        Menu atual = encontrarMenuPorId(idMenu);

        while (atual != null) {
            caminho.add(0, atual.getNome());
            Menu finalAtual = atual;
            if (atual.getParentId() != null &&
                    !menusNivelSuperior.stream().anyMatch(m -> m.getId().equals(finalAtual.getParentId()))) {
                atual = encontrarMenuPorId(atual.getParentId());
            } else {
                break;
            }
        }

        return caminho;
    }

    /**
     * Encontra um Menu pelo ID
     */
    private Menu encontrarMenuPorId(Long id) {
        // Procura nos top level
        Optional<Menu> nivelSuperior = menusNivelSuperior.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst();
        if (nivelSuperior.isPresent()) {
            return nivelSuperior.get();
        }

        // Procura nos submenus
        for (List<Menu> listaMenu : subMenusPorParentId.values()) {
            Menu encontrado = encontrarMenuPorIdNaLista(id, listaMenu);
            if (encontrado != null) {
                return encontrado;
            }
        }

        return null;
    }

    /**
     * Encontra um Menu pelo ID em uma lista recursivamente
     */
    private Menu encontrarMenuPorIdNaLista(Long id, List<Menu> listaMenu) {
        for (Menu menu : listaMenu) {
            if (menu.getId().equals(id)) {
                return menu;
            }
            List<Menu> filhos = subMenusPorParentId.get(menu.getId());
            if (filhos != null) {
                Menu encontrado = encontrarMenuPorIdNaLista(id, filhos);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }

    private void atualizarEstiloSelecaoMenuPrimario() {
        itensNavPrimarioPorId.forEach((id, itemNav) ->
                itemNav.getElement().getClassList().set(
                        CLASSE_ITEM_PRIMARIO_SELECIONADO,
                        id.equals(idPaiAtualmenteSelecionado)
                )
        );
        botoesIconePorId.forEach((id, botao) ->
                botao.getElement().getClassList().set(
                        CLASSE_ITEM_ICONE_SELECIONADO,
                        id.equals(idPaiAtualmenteSelecionado)
                )
        );
    }

    /**
     * Filtra os itens no menu secundário com base no termo de busca.
     */
    private void filtrarMenuSecundario(String termoBusca) {
        String buscaNormalizada = termoBusca.toLowerCase().trim();
        // Itera sobre os itens de nível superior e aplica o filtro recursivamente
        navegacaoSecundaria.getItems().forEach(item ->
                filtrarItemNav((SideNavItem) item, buscaNormalizada));
    }

    /**
     * Função recursiva que verifica se um item ou seus filhos correspondem à busca.
     */
    private boolean filtrarItemNav(SideNavItem item, String buscaNormalizada) {
        // 1. Verifica se o próprio item corresponde
        boolean correspondeProprio = item.getLabel().toLowerCase().contains(buscaNormalizada);

        // 2. Verifica recursivamente se algum filho corresponde
        boolean correspondeFilho = false;
        for (Component filho : item.getItems()) {
            if (filho instanceof SideNavItem) {
                if (filtrarItemNav((SideNavItem) filho, buscaNormalizada)) {
                    correspondeFilho = true;
                }
            }
        }

        // 3. Decide a visibilidade e o estado de expansão
        boolean estaVisivel = correspondeProprio || correspondeFilho;
        item.setVisible(estaVisivel);

        // Expande o item se um filho correspondeu
        // Se o campo de busca estiver vazio, volta ao estado colapsado padrão
        if (buscaNormalizada.isEmpty()) {
            item.setExpanded(false);
        } else if (correspondeFilho) {
            item.setExpanded(true);
        }

        return estaVisivel;
    }

    private void atualizarVisibilidadeMenuIcone() {
        menuPrimarioApenasIcone.setVisible(!isDrawerOpened());
    }

    @Override
    public void showRouterLayoutContent(HasElement conteudo) {
        areaConteudo.removeAll();
        if (conteudo instanceof Component) {
            areaConteudo.add((Component) conteudo);
        } else if (conteudo != null) {
            areaConteudo.getElement().appendChild(conteudo.getElement());
        }
    }

    private Icon criarIcone(String nomeIcone) {
        return criarIcone(nomeIcone, "2em");
    }

    private Icon criarIcone(String nomeIcone, String tamanho) {
        Icon icone;
        if (nomeIcone == null || nomeIcone.isBlank()) {
            icone = VaadinIcon.CIRCLE_THIN.create();
        } else {
            try {
                icone = VaadinIcon.valueOf(nomeIcone.toUpperCase().replace("-", "_")).create();
            } catch (IllegalArgumentException e) {
                icone = VaadinIcon.QUESTION_CIRCLE.create();
            }
        }
        if (tamanho != null) {
            icone.setSize(tamanho);
        }
        return icone;
    }
}

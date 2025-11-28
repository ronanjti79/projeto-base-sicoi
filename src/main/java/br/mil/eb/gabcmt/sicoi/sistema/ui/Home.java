package br.mil.eb.gabcmt.sicoi.sistema.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Dashboard | SiCoI")
@Route(value = "", layout = SicoiLayout.class)
public class Home extends VerticalLayout {

    public Home() {
        addClassName("dashboard-view");
        setPadding(true);
        setSpacing(true);
        setSizeFull();

        // Configuração do container principal para scroll se necessário
        getStyle().set("overflow", "auto");

        // 1. Cabeçalho com Abas
        add(criarCabecalhoAbas());

        // 2. Barra de Ferramentas (Botões de Ação)
        add(criarBarraFerramentas());

        // 3. Cards de Métricas (KPIs)
        add(criarCardsMetricas());

        // 4. Gráficos da Linha do Meio (Processos + Tipos)
        add(criarLinhaMeio());

        // 5. Gráfico Grande Inferior (Overview Compras)
        add(criarGraficoOverview());
    }

    private Component criarCabecalhoAbas() {
        Tabs tabs = new Tabs();
        tabs.add(new Tab("Fase Interna"));
        tabs.add(new Tab("Fase Externa"));
        tabs.add(new Tab("Programa FMS"));

        // Estilização simples para parecer com a imagem (azul no selecionado)
        tabs.addSelectedChangeListener(event -> {
            // Lógica de troca de aba (apenas visual por enquanto)
        });

        return tabs;
    }

    private Component criarBarraFerramentas() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.CENTER);

        Button btnEditar = new Button("Editar");
        btnEditar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnSalvar = new Button("Salvar");
        btnSalvar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button btnCarregar = new Button("Carregar");
        btnCarregar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button btnAdicionar = new Button("Adicionar widget", VaadinIcon.CHEVRON_DOWN.create());
        btnAdicionar.setIconAfterText(true);
        btnAdicionar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button btnRestaurar = new Button("Restaurar padrão");
        btnRestaurar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnRestaurar.getStyle().set("color", "var(--lumo-error-text-color)");
        btnRestaurar.getStyle().set("margin-left", "auto"); // Empurra para a direita se precisar, ou deixe junto

        toolbar.add(btnEditar, btnSalvar, btnCarregar, btnAdicionar, btnRestaurar);
        return toolbar;
    }

    private Component criarCardsMetricas() {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);

        // Card 1: Compras (mensais)
        row.add(criarCardKpi("Compras (mensais)", "15 / mês", "+ 20.82% do último mês", true));

        // Card 2: Compras (anuais)
        row.add(criarCardKpi("Compras (anuais)", "275 / ano", "+ 9.05% do último ano", true));

        // Card 3: Processos concluídos (com Mini Chart)
        row.add(criarCardProcessosConcluidos());

        return row;
    }

    private Component criarCardKpi(String titulo, String valor, String subtexto, boolean isPositive) {
        VerticalLayout card = new VerticalLayout();
        estilizarCard(card);

        Span titleSpan = new Span(titulo);
        titleSpan.addClassName(LumoUtility.FontWeight.BOLD);
        titleSpan.addClassName(LumoUtility.FontSize.SMALL);
        titleSpan.getStyle().set("color", "var(--lumo-contrast-60pct)");

        Span valueSpan = new Span(valor);
        valueSpan.addClassName(LumoUtility.FontSize.XXXLARGE);
        valueSpan.addClassName(LumoUtility.FontWeight.BOLD);

        Span diffSpan = new Span(subtexto);
        diffSpan.addClassName(LumoUtility.FontSize.SMALL);
        diffSpan.getStyle().set("color", isPositive ? "var(--lumo-success-text-color)" : "var(--lumo-error-text-color)");

        // Ícone da seta
        if (isPositive) {
            diffSpan.addComponentAsFirst(VaadinIcon.ARROW_UP.create());
        }

        card.add(titleSpan, valueSpan, diffSpan);
        return card;
    }

    private Component criarCardProcessosConcluidos() {
        VerticalLayout card = new VerticalLayout();
        estilizarCard(card);

        Span titleSpan = new Span("Processos concluídos");
        titleSpan.addClassName(LumoUtility.FontWeight.BOLD);
        titleSpan.addClassName(LumoUtility.FontSize.SMALL);
        titleSpan.getStyle().set("color", "var(--lumo-contrast-60pct)");

        HorizontalLayout content = new HorizontalLayout();
        content.setWidthFull();
        content.setAlignItems(Alignment.END);

        Span valueSpan = new Span("35 %");
        valueSpan.addClassName(LumoUtility.FontSize.XXXLARGE);
        valueSpan.addClassName(LumoUtility.FontWeight.BOLD);

        // Mini Sparkline chart simulado
        Chart chart = new Chart(ChartType.AREA);
        chart.setHeight("50px");
        chart.setWidth("100px");

        Configuration conf = chart.getConfiguration();
        conf.setTitle("");
        conf.getLegend().setEnabled(false);
        conf.getTooltip().setEnabled(false);
        conf.getxAxis().setVisible(false);
        conf.getyAxis().setVisible(false);
        conf.getChart().setBackgroundColor(new SolidColor(255, 255, 255, 0));
        conf.getChart().setMargin(0, 0, 0, 0);

        ListSeries series = new ListSeries(10, 15, 12, 20, 18, 35);
        PlotOptionsArea plotOptions = new PlotOptionsArea();
        plotOptions.setEnableMouseTracking(false);
        plotOptions.setColor(new SolidColor("#007bff")); // Azul
        plotOptions.setFillColor(new SolidColor(0, 123, 255, 0.5)); // CORRIGIDO: Opacidade deve ser entre 0.0 e 1.0 (0.5 = 50%)
        plotOptions.setMarker(new Marker(false));
        series.setPlotOptions(plotOptions);

        conf.addSeries(series);

        content.add(valueSpan, chart);

        card.add(titleSpan, content);
        return card;
    }

    private Component criarLinhaMeio() {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);

        // Card: Processos em andamento (Número grande)
        VerticalLayout card1 = new VerticalLayout();
        estilizarCard(card1);
        card1.setWidth("40%");

        Span title1 = new Span("Processos em andamento");
        title1.addClassName(LumoUtility.FontWeight.BOLD);
        title1.getStyle().set("color", "var(--lumo-contrast-60pct)");

        Span value1 = new Span("18");
        value1.getStyle().set("font-size", "4rem");
        value1.addClassName(LumoUtility.FontWeight.BOLD);

        card1.add(title1, value1);

        // Card: Tipos de Processos (Pie Chart)
        VerticalLayout card2 = new VerticalLayout();
        estilizarCard(card2);
        card2.setWidth("60%");

        Span title2 = new Span("Tipos de Processos");
        title2.addClassName(LumoUtility.FontWeight.BOLD);
        title2.getStyle().set("color", "var(--lumo-contrast-60pct)");

        Chart chart = new Chart(ChartType.PIE);
        Configuration conf = chart.getConfiguration();
        conf.setTitle("");
        conf.getChart().setBackgroundColor(new SolidColor(255, 255, 255, 0));
        conf.getChart().setHeight(String.valueOf(200)); // Altura controlada

        DataSeries series = new DataSeries();
        series.add(new DataSeriesItem("PCI", 40));
        series.add(new DataSeriesItem("RFI", 25));
        series.add(new DataSeriesItem("QI", 20));
        series.add(new DataSeriesItem("RFQ", 15));

        PlotOptionsPie plotOptionsPie = new PlotOptionsPie();
        plotOptionsPie.setInnerSize("0"); // Pizza cheia
        plotOptionsPie.setSize("80%");
        plotOptionsPie.getDataLabels().setEnabled(false);
        series.setPlotOptions(plotOptionsPie);

        conf.addSeries(series);

        // Layout horizontal para colocar gráfico e legenda lado a lado
        HorizontalLayout chartLayout = new HorizontalLayout(chart);
        chartLayout.setWidthFull();
        chartLayout.setAlignItems(Alignment.CENTER);

        // Legenda manual customizada à direita
        VerticalLayout legend = new VerticalLayout();
        legend.setSpacing(false);
        legend.setPadding(false);
        legend.setWidth("auto");
        legend.add(criarItemLegenda("PCI", "#2196F3"));
        legend.add(criarItemLegenda("RFI", "#E91E63"));
        legend.add(criarItemLegenda("QI", "#9C27B0"));
        legend.add(criarItemLegenda("RFQ", "#F44336"));

        chartLayout.add(chart, legend);
        chartLayout.setFlexGrow(1, chart);

        card2.add(title2, chartLayout);

        row.add(card1, card2);
        return row;
    }

    private Component criarItemLegenda(String label, String colorHex) {
        HorizontalLayout item = new HorizontalLayout();
        item.setAlignItems(Alignment.CENTER);
        item.setSpacing(true);

        Div dot = new Div();
        dot.setWidth("10px");
        dot.setHeight("10px");
        dot.getStyle().set("background-color", colorHex);
        dot.getStyle().set("border-radius", "50%");

        Span text = new Span(label);
        text.getStyle().set("font-size", "var(--lumo-font-size-s)");

        item.add(dot, text);
        return item;
    }

    private Component criarGraficoOverview() {
        VerticalLayout card = new VerticalLayout();
        estilizarCard(card);

        Span title = new Span("Overview Compras");
        title.addClassName(LumoUtility.FontWeight.BOLD);
        title.getStyle().set("color", "var(--lumo-contrast-60pct)");

        Chart chart = new Chart(ChartType.COLUMN);
        chart.setHeight("300px"); // Altura fixa

        Configuration conf = chart.getConfiguration();
        conf.setTitle("");
        conf.getChart().setBackgroundColor(new SolidColor(255, 255, 255, 0));
        conf.getLegend().setEnabled(false);

        XAxis xAxis = conf.getxAxis();
        xAxis.setCategories("Jan", "Feb", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dec");

        YAxis yAxis = conf.getyAxis();
        yAxis.setTitle("");
        yAxis.setGridLineDashStyle(DashStyle.DOT); // Linhas pontilhadas no fundo

        ListSeries series = new ListSeries("Compras", 49, 35, 20, 25, 30, 20, 50, 38, 20, 25, 30, 20);

        PlotOptionsColumn plotOptions = new PlotOptionsColumn();
        plotOptions.setColor(new SolidColor("#2196F3")); // Azul principal
        plotOptions.setBorderRadius(2); // Cantos levemente arredondados nas colunas
        series.setPlotOptions(plotOptions);

        // Para deixar algumas colunas mais claras como na imagem, teríamos que usar DataSeriesItem com cores individuais
        // ou simplificar como fiz acima (todas azuis) e alterar opacidade via CSS ou DataSeries.
        // Vou criar uma variação simples:
        DataSeries dataSeries = new DataSeries();
        int[] values = {49, 35, 20, 25, 30, 20, 50, 38, 20, 25, 30, 20};
        for (int val : values) {
            DataSeriesItem item = new DataSeriesItem();
            item.setY(val);
            // Exemplo: Valores menores que 30 ficam mais claros
            if (val < 30) {
                item.setColor(new SolidColor("#90CAF9")); // Azul claro
            } else {
                item.setColor(new SolidColor("#1976D2")); // Azul escuro
            }
            dataSeries.add(item);
        }

        conf.addSeries(dataSeries);
        card.add(title, chart);

        return card;
    }

    private void estilizarCard(VerticalLayout card) {
        card.getStyle()
                .set("background-color", "#ffffff")
                .set("border-radius", "8px")
                .set("box-shadow", "0 1px 3px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.24)")
                .set("padding", "var(--lumo-space-m)");
        card.setSpacing(false);
    }
}

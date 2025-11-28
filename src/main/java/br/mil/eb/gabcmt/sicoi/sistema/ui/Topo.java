package br.mil.eb.gabcmt.sicoi.sistema.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;

public class Topo {

    /**
     * Cria o componente de topo utilizado  na aplicação.
     * @return O componente do topo.
     */
    public static Component criarTopo() {
        var line = new HorizontalLayout();
        line.getStyle().set("background", "linear-gradient(to left, var(--lumo-primary-color-10pct), transparent 80%)");
        line.setPadding(true);
        line.setSpacing(true);
        line.setWidthFull();
        line.setMaxHeight("5rem");
        line.setAlignItems(FlexComponent.Alignment.CENTER);
        line.setJustifyContentMode(FlexComponent.JustifyContentMode.AROUND);

        var home = new HorizontalLayout();
        var homeIcon = VaadinIcon.HOME_O.create();
        homeIcon.setSize("2rem");
        homeIcon.addClickListener(event -> {
            UI.getCurrent().navigate("");
        });
        home.add(homeIcon);

        // === Layout Órgão ===
        HorizontalLayout orgaoLayout = new HorizontalLayout();
        orgaoLayout.addClassName("layout-gap");

        Span orgaoLabel = new Span("Órgão:");
        orgaoLabel.addClassName("label-bold");

        Span orgaoValue = new Span("Exército Brasileiro");

        orgaoLayout.add(orgaoLabel, orgaoValue);

        // === Layout Perfil Atual ===
        HorizontalLayout perfilAtualLayout = new HorizontalLayout();
        perfilAtualLayout.addClassName("layout-gap");

        Span perfilAtualLabel = new Span("Perfil Atual:");
        perfilAtualLabel.addClassName("label-bold");

        Span perfilAtualValue = new Span("Sem Perfil");

        perfilAtualLayout.add(perfilAtualLabel, perfilAtualValue);

        // === Layout Perfis Disponíveis ===
        HorizontalLayout perfilDisponivelLayout = new HorizontalLayout();
        perfilDisponivelLayout.addClassName("layout-gap");

        Span perfilDisponivelLabel = new Span("Perfis Disponíveis:");
        perfilDisponivelLabel.addClassName("label-bold");

        ComboBox<String> perfilCombo = new ComboBox<>();
        perfilCombo.setItems("Administrador", "Usuário", "Convidado");
        perfilCombo.setPlaceholder("Selecione um Perfil");

        perfilCombo.addValueChangeListener(e -> {
            String selectedPerfilName = e.getValue();
            Object selectedPerfil = null;

            if (selectedPerfil != null) {
                VaadinSession.getCurrent().setAttribute("perfilAtivo", selectedPerfil);
                UI.getCurrent().getPage().reload();
            } else {
                Notification.show("Perfil não encontrado: " + selectedPerfilName, 3000, Notification.Position.MIDDLE);
            }
        });

        perfilDisponivelLayout.add(perfilDisponivelLabel, perfilCombo);

        var b1 = new Button(VaadinIcon.BELL_O.create());

        //Botão de Menssagens de e-mail
        var b2 = new Button(VaadinIcon.ENVELOPE_O.create());
        Span badge = new Span("0");
        badge.getElement().getThemeList().add("badge primary small pill");
        badge.addClassName("badge-notification");
        b2.getElement().appendChild(badge.getElement());
        b2.getStyle().setCursor("pointer");
        badge.setVisible(false);

        //Botão do Chat
        var b3 = new Button(VaadinIcon.COMMENT_ELLIPSIS_O.create());
        Span badgeChat = new Span("0");
        badgeChat.getElement().getThemeList().add("badge primary small pill");
        badgeChat.addClassName("badge-notification");
        b3.getElement().appendChild(badgeChat.getElement());
        b3.getStyle().setCursor("pointer");
        badgeChat.setVisible(false);


        b1.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        b2.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        b3.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);

        b1.getStyle().set("background", "var(--lumo-contrast-10pct)").set("color", "var(--lumo-secondary-text-color)");
        b2.getStyle().set("background", "var(--lumo-contrast-10pct)").set("color", "var(--lumo-secondary-text-color)");
        b3.getStyle().set("background", "var(--lumo-contrast-10pct)").set("color", "var(--lumo-secondary-text-color)");
        var butons = new HorizontalLayout();
        butons.add(b1, b2, b3);

        MenuUsuarioCustomizado avatarMenu = new MenuUsuarioCustomizado();


        line.add(home, orgaoLayout, perfilAtualLayout, perfilDisponivelLayout, butons, avatarMenu);

        return line;
    }

}

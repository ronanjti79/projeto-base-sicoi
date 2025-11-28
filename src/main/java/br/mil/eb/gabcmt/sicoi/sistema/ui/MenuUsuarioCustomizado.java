package br.mil.eb.gabcmt.sicoi.sistema.ui;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;

public class MenuUsuarioCustomizado extends HorizontalLayout {

    public MenuUsuarioCustomizado() {
        init();
    }

    private void init() {

        Avatar avatar = new Avatar();

        avatar.getStyle().set("display", "block");
        avatar.getStyle().set("cursor", "pointer");

        VerticalLayout layoutAvatar = new VerticalLayout();
        layoutAvatar.setPadding(false);
        layoutAvatar.setSpacing(false);
        layoutAvatar.setAlignItems(Alignment.CENTER);
        layoutAvatar.setWidth("8rem");

        Button button = new Button(avatar);
        button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);

        VerticalLayout textLayout = new VerticalLayout();
        textLayout.setPadding(false);
        textLayout.setSpacing(false);
        textLayout.setAlignItems(Alignment.CENTER);
        textLayout.getStyle().set("line-height", "1.1");

        Span postoGraduacaoSpan = new Span("postoGraduacao");
        Span nomeSpan = new Span("nome");
        nomeSpan.getStyle().set("overflow", "hidden");
        nomeSpan.getStyle().set("white-space", "nowrap");
        nomeSpan.getStyle().set("text-overflow", "ellipsis");
        nomeSpan.getStyle().set("max-width", "100%");

        textLayout.add(postoGraduacaoSpan, nomeSpan);

        layoutAvatar.add(button, textLayout);

        Popover popover = new Popover();
        popover.setModal(true);
        popover.setOverlayRole("menu");
        popover.setAriaLabel("User menu");
        popover.setTarget(button);
        popover.setPosition(PopoverPosition.TOP_END);
        popover.addThemeVariants(PopoverVariant.ARROW,
                PopoverVariant.LUMO_NO_PADDING);
        popover.addThemeVariants(PopoverVariant.LUMO_NO_PADDING);

        VerticalLayout linksLayout = new VerticalLayout();
        linksLayout.setSpacing(false);
        linksLayout.setPadding(true);
        linksLayout.addClassName("userMenuLinks");

        Anchor profile = new Anchor("perfil");
        Span profileLabel = new Span("Perfil");
        profileLabel.addClassName("menu-label");
        Icon userIcon = VaadinIcon.USER.create();
        userIcon.addClassName("menu-icon");
        profile.addClassName("menu-item");
        profile.add(userIcon, profileLabel);

        Anchor preferences = new Anchor("#");
        Span preferencesLabel = new Span("Preferências");
        preferencesLabel.addClassName("menu-label");
        Icon preferencesIcon = VaadinIcon.SLIDERS.create();
        preferencesIcon.addClassName("menu-icon");
        preferences.addClassName("menu-item");
        preferences.add(preferencesIcon, preferencesLabel);

        Anchor tema = new Anchor("#");
        Span temaLabel = new Span("Tema");
        temaLabel.addClassName("menu-label");
        Icon temaIcon = VaadinIcon.ADJUST.create();
        Icon temaArrow = VaadinIcon.CHEVRON_RIGHT_SMALL.create();
        temaArrow.addClassName("chevron-icon");
        temaIcon.addClassName("menu-icon");
        tema.addClassName("menu-item");
        tema.add(temaIcon, temaLabel, temaArrow);

        Anchor ajuda = new Anchor("#");
        Span ajudaLabel = new Span("Ajuda");
        ajudaLabel.addClassName("menu-label");
        Icon ajudaIcon = VaadinIcon.QUESTION_CIRCLE.create();
        Icon ajudaArrow = VaadinIcon.CHEVRON_RIGHT_SMALL.create();
        ajudaArrow.addClassName("chevron-icon");
        ajudaIcon.addClassName("menu-icon");
        ajuda.addClassName("menu-item");
        ajuda.add(ajudaIcon, ajudaLabel, ajudaArrow);

        Anchor signOut = new Anchor("#");
        Span signOutLabel = new Span("Logout");
        signOutLabel.addClassName("menu-label");
        Icon signOutIcon = VaadinIcon.SIGN_OUT.create();
        signOutIcon.addClassName("menu-icon");
        signOut.getElement().addEventListener("click", e -> {

        });
        signOut.addClassName("menu-item");
        signOut.add(signOutIcon, signOutLabel);

        Popover temaPopover = new Popover();
        temaPopover.setTarget(tema);
        temaPopover.setPosition(PopoverPosition.END_TOP);
        temaPopover.addThemeVariants(PopoverVariant.LUMO_NO_PADDING);
        temaPopover.setOpenOnHover(true);

        VerticalLayout temaSubmenuLayout = new VerticalLayout();
        temaSubmenuLayout.setPadding(true);
        temaSubmenuLayout.setSpacing(false);

        Anchor systemTheme = new Anchor("#");
        Span systemLabel = new Span();
        systemLabel.add(
                new Text("Tema do"),
                new Html("<br>"),
                new Text("Sistema")
        );
        systemLabel.addClassName("menu-label");
        Icon systemIcon = VaadinIcon.DESKTOP.create();
        systemIcon.addClassName("menu-icon");
        systemTheme.addClassName("menu-item");
        systemTheme.getElement().addEventListener("click", e -> {
            UI.getCurrent().getPage().executeJs(
                    "const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;"
                            + "document.documentElement.setAttribute('theme', prefersDark ? 'dark' : 'light');"
            );
        });
        systemTheme.add(systemIcon, systemLabel);

        Anchor lightTheme = new Anchor("#");
        Span lightLabel = new Span("Claro");
        lightLabel.addClassName("menu-label");
        Icon lightIcon = VaadinIcon.SUN_O.create();
        lightIcon.addClassName("menu-icon");
        lightTheme.addClassName("menu-item");
        lightTheme.getElement().addEventListener("click", e -> {
//            UI.getCurrent().getPage().executeJs("document.documentElement.setAttribute('theme', 'light')");

        });
        lightTheme.add(lightIcon, lightLabel);

        Anchor darkTheme = new Anchor("#");
        Span darkLabel = new Span("Escuro");
        darkLabel.addClassName("menu-label");
        Icon darkIcon = VaadinIcon.MOON_O.create();
        darkIcon.addClassName("menu-icon");
        darkTheme.addClassName("menu-item");
        darkTheme.getElement().addEventListener("click", e -> {
//            UI.getCurrent().getPage().executeJs("document.documentElement.setAttribute('theme', 'dark')");

        });
        darkTheme.add(darkIcon, darkLabel);

        temaSubmenuLayout.add(systemTheme, lightTheme, darkTheme);
        temaPopover.add(temaSubmenuLayout);

        Popover ajudaPopover = new Popover();
        ajudaPopover.setTarget(ajuda);
        ajudaPopover.setPosition(PopoverPosition.END_TOP);
        ajudaPopover.addThemeVariants(PopoverVariant.LUMO_NO_PADDING);
        ajudaPopover.setOpenOnHover(true);

        VerticalLayout ajudaSubmenuLayout = new VerticalLayout();
        ajudaSubmenuLayout.setPadding(true);
        ajudaSubmenuLayout.setSpacing(false);

        Anchor faq = new Anchor("#");
        Span faqLabel = new Span("FAQ");
        faqLabel.addClassName("menu-label");
        Icon faqIcon = VaadinIcon.BOOK.create();
        faqIcon.addClassName("menu-icon");
        faq.addClassName("menu-item");
        faq.add(faqIcon, faqLabel);

        Anchor contact = new Anchor("#");
        Span contactLabel = new Span("Contato");
        contactLabel.addClassName("menu-label");
        Icon contactIcon = VaadinIcon.ENVELOPE.create();
        contactIcon.addClassName("menu-icon");
        contact.addClassName("menu-item");
        contact.add(contactIcon, contactLabel);

        ajudaSubmenuLayout.add(faq, contact);
        ajudaPopover.add(ajudaSubmenuLayout);

        linksLayout.add(profile);
        linksLayout.add(new Hr());
        linksLayout.add(preferences);
        linksLayout.add(new Hr());
        linksLayout.add(tema);
        linksLayout.add(new Hr());
        linksLayout.add(ajuda);
        linksLayout.add(new Hr());
        linksLayout.add(signOut);

        popover.add(linksLayout);

        add(layoutAvatar, popover, temaPopover, ajudaPopover);


    }


}

package br.mil.eb.gabcmt.sicoi.sistema.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;

public class Fundo {

    /**
     * Cria o rodapé da aplicação com o logo do Vaadin e um texto estilizado.
     * @return O componente do rodapé.
     */
    public static Component createFooter() {
        Span developedWith = new Span("Developed with:");
        developedWith.getStyle().set("font-style", "italic").set("font-size", "0.7em").set("margin-bottom", "0.2em");
        Image foot = new Image("icons/VaadinLogo.png", "Vaadin Logo");
        foot.setMaxHeight("2em");
        Div footer = new Div(developedWith, foot);
        footer.setWidthFull();
        footer.setHeight("60px");
        footer.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("background", "linear-gradient(to right, var(--lumo-primary-color-10pct), transparent 80%)");
        return footer;
    }

}

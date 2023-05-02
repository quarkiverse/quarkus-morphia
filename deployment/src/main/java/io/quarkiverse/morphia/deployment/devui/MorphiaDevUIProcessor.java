package io.quarkiverse.morphia.deployment.devui;

import static java.lang.String.format;

import dev.morphia.Datastore;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.Page;

/**
 * Dev UI card for displaying important details such as the library version.
 */
public class MorphiaDevUIProcessor {

    @BuildStep(onlyIf = IsDevelopment.class)
    void createVersion(BuildProducer<CardPageBuildItem> cardPageBuildItemBuildProducer) {
        final CardPageBuildItem card = new CardPageBuildItem();

        Package pkg = Datastore.class.getPackage();
        card.addPage(Page.externalPageBuilder("Home Page")
                .icon("font-awesome-solid:code")
                .url(format("https://morphia.dev/morphia/%s/quicktour.html", pkg.getSpecificationVersion()))
                .doNotEmbed()
                .staticLabel(pkg.getImplementationVersion()));
        card.addPage(Page.externalPageBuilder("Critter Home Page")
                .icon("font-awesome-solid:code")
                .url(format("https://morphia.dev/critter/4.4/index.html"))
                .doNotEmbed());
        card.addPage(Page.externalPageBuilder("MongoDB Tutorial")
                .icon("font-awesome-solid:code")
                .url("https://www.mongodb.com/languages/morphia")
                .doNotEmbed());

        card.setCustomCard("qwc-morphia-card.js");

        cardPageBuildItemBuildProducer.produce(card);
    }
}

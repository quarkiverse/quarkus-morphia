package io.quarkiverse.morphia.deployment.devui;

import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.Page;
import io.quarkus.devui.spi.page.PageBuilder;

/**
 * Dev UI card for displaying important details such as the library version.
 */
public class MorphiaDevUIProcessor {

    @BuildStep(onlyIf = IsDevelopment.class)
    void createVersion(BuildProducer<CardPageBuildItem> cardPageBuildItemBuildProducer) {
        final CardPageBuildItem card = new CardPageBuildItem();

        final PageBuilder versionPage = Page.externalPageBuilder("Morphia Version")
                .icon("font-awesome-solid:code")
                .url("https://www.mongodb.com/languages/morphia")
                .doNotEmbed()
                .staticLabel(dev.morphia.Datastore.class.getPackage().getImplementationVersion());
        card.addPage(versionPage);

        card.setCustomCard("qwc-morphia-card.js");

        cardPageBuildItemBuildProducer.produce(card);
    }
}

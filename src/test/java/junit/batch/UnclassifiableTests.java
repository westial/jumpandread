package junit.batch;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;

public class UnclassifiableTests
{
    @Test
    public void cleanWithEscapeTest()
    {
        String input = "1. Peras al vino tinto con canela. Receta tradicional.<break time=\"1500ms\"/>2. Receta de Peras pochadas en vino tinto - Recetas de Allrecipes.<break time=\"1500ms\"/>3. De temporada: Seis recetas con peras que querrás devorar.<break time=\"1500ms\"/>4. Doce recetas con peras | Gastronomía & Cía.<break time=\"1500ms\"/>5. Peras al vino tinto receta tradicional - Receta de Los Tragaldabas.<break time=\"1500ms\"/>6. Receta de peras en almíbar con aroma de vainilla.<break time=\"1500ms\"/>7. Receta de Peras Rellenas en Almíbar - elgourmet.<break time=\"1500ms\"/>8. Peras asadas con miel y canela | Receta en TELVA.com.<break time=\"1500ms\"/>9. Peras Bella Helena (Poires Belle Helene) - Anna Olson - Receta ....<break time=\"1500ms\"/>10. Receta de Peras al moscatel - Gallina Blanca.<break time=\"1500ms\"/>";
        String output = StringEscapeUtils.escapeXml11(input);
        System.out.println(input);
        System.out.println(output);
    }
}

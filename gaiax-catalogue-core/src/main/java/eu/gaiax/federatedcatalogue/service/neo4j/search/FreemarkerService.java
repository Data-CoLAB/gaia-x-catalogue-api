package eu.gaiax.federatedcatalogue.service.neo4j.search;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import freemarker.template.Template;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

@Service
public class FreemarkerService {

    private final Configuration freemarkerConfiguration;

    public FreemarkerService(Configuration freemarkerConfiguration) {
        this.freemarkerConfiguration = freemarkerConfiguration;
    }

    /**
     * @param templateName name of the template
     * @param model        model data
     * @return text after parsing the template
     * @throws IOException       in case no template found with given name in
     *                           classpath:/templates/ folder
     * @throws TemplateException in case any error while parsing template
     */
    public String parseTemplate(String templateName, Object model) throws IOException, TemplateException {
        return FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(templateName, StandardCharsets.UTF_8.name()), model);
    }
}

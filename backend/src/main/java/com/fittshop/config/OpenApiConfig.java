package com.fittshop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fittShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FittShop E-Commerce API")
                        .description("Plataforma Omnichannel de E-Commerce para Atletas e Praticantes de Academia. " +
                                "Inclui motor de recomendação nutricional NutriFit, reservas atômicas de estoque, checkout multi-forma e gestão integrada de operações.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FittShop DevSecOps Team")
                                .email("dev@fittshop.com.br")
                                .url("https://fittshop.com.br"))
                        .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT")));
    }
}

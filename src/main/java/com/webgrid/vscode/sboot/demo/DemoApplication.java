package com.webgrid.vscode.sboot.demo;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    OpenAPI openAPI() {
        final Server server = new Server().url("http://localhost:8080");
        return new OpenAPI().addServersItem(server);
    }

    @Bean
    RouterFunction<ServerResponse> test() {
        return route()
                .GET("/test",
                     request -> ServerResponse.ok()
                                              .bodyValue("Hello"),
                     ops -> ops.operationId("testOp"))
                .build();

    }

    @Bean
    OpenApiCustomiser openApiCustomiser(@Value("${spring.webflux.base-path}") String base) {
        return openApi -> openApi.setPaths(openApi.getPaths()
                                                  .entrySet()
                                                  .stream()
                                                  .reduce(new Paths(),
                                                          (paths, item) -> paths.addPathItem(
                                                                  java.nio.file.Paths.get(base, item.getKey())
                                                                                     .toString(),
                                                                  item.getValue()),
                                                          (paths, paths2) -> paths));
    }
}

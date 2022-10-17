// camel-k: name=route-logic
// camel-k: dependency=mvn:camel-quarkus-direct dependency=mvn:io.quarkus:quarkus-resteasy-jackson

import org.apache.camel.BeanInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import io.quarkus.arc.DefaultBean;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
public class RouteLogic extends RouteBuilder {

    @Produces
    @Named("foo")
    @DefaultBean
    @ApplicationScoped
    public Pojo foo() {
        return new Pojo("camel from bean");
    }

    void addGreeting(Pojo pojo) {
        pojo.hello = pojo.hello + " Rocks";
    }

    @Override
    public void configure() {
        rest().get("orders")
                .produces(MediaType.APPLICATION_JSON)
                .to("direct://getOrders");

        from("direct://getOrders").routeId("routeHello").log("Hello world")
                .setBody(ex -> {
                    return new Pojo("Camel");
                })
                .bean(this, "addGreeting")
                .marshal().json(JsonLibrary.Jackson)
                .end();

    }

    class Pojo {
        public String hello;

        public Pojo(String message) {
            this.hello = message;
        }
    }

}

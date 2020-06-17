package com.personal.web.nullvalue.vertex;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {
  private final Map<String, Product> values = new HashMap<>();

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {
    final Router router = Router.router(vertx);
    router.route("/product*").handler(BodyHandler.create());

    router.post("/product").handler(context -> {
      String body = context.getBodyAsString();

      Product product = Json.decodeValue(body, Product.class);
      values.put(product.getId(), product);
      context.response().setStatusCode(201).end();
    });

    router.patch("/product").handler(this::merge);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8080, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          System.out.println("HTTP server started on port 8888");
        } else {
          startPromise.fail(http.cause());
        }
      });
  }

  private void merge(final RoutingContext json) {
    try {
      final JsonNode target = JsonLoader.fromString(json.getBodyAsString());
      final JsonNode source = JsonLoader.fromString(Json.encode(values.get(json.getBodyAsJson().getString("id"))));

      final JsonMergePatch patch = JsonMergePatch.fromJson(target);
      JsonNode result = patch.apply(source);

      System.out.println("Result is: " + result.asText());

      json.response()
          .setStatusCode(200)
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(result.toPrettyString());
    } catch (JsonPatchException | IOException e) {
      e.printStackTrace();
    }
  }
}

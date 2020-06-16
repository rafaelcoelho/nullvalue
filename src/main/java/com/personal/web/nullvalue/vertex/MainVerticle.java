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

public class MainVerticle extends AbstractVerticle {

  private Map<String, String> values = new HashMap<>();

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.createHttpServer().requestHandler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");
    }).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  private void merge(final String json) throws IOException {
    try {
      JsonNode target = JsonLoader.fromString(json);
      JsonNode source = JsonLoader.fromString(values.get("key"));

      JsonMergePatch patch = JsonMergePatch.fromJson(source);
      patch.apply(target);
    } catch (JsonPatchException e) {
      e.printStackTrace();
    }
  }
}

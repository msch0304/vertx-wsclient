package de.michaschmidt.vertx.wsclient

import io.vertx.core.AbstractVerticle
import io.vertx.core.impl.VertxInternal
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory

class MessageHandler extends AbstractVerticle {

    def ownname = System.getProperty("ownname", "client1")

    def LOGGER = LoggerFactory.getLogger("MessageHandler")

    @Override
    void start(){
        def clusterNode = ((VertxInternal)vertx).getClusterManager()

        LOGGER.info(clusterNode)
        def eb = vertx.eventBus()

        eb.consumer("received", {h ->
            def b = h.body()
            def jso = new JsonObject(b)
            jso.put("handled", ownname)
            LOGGER.info(jso.encodePrettily())

        })
    }
}

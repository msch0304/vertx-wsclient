package de.michaschmidt.vertx.wsclient

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.http.RequestOptions
import io.vertx.core.impl.VertxInternal
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.spi.VertxFactory


class WSClient extends AbstractVerticle {

    static Logger LOGGER = LoggerFactory.getLogger("WSClient")

    static void main(String[] args){

        VertxOptions voptions = new VertxOptions()
        Vertx.clusteredVertx(voptions, {vhandler ->
            if (vhandler.succeeded()){
                def vertx = vhandler.result();

                WSClient wsClient = new WSClient()
                MessageHandler msgHandler = new MessageHandler()
                vertx.deployVerticle(msgHandler)
                vertx.deployVerticle(wsClient)

                LOGGER.info(voptions.clusterHost)

            }
        })

    }

    @Override
    void start(){
        def eventBus = vertx.eventBus()
        def serverhost = System.getProperty("websockethost", "localhost")
        def serverport = Integer.parseInt(System.getProperty("websocketport", "8080"))
        def ownname = System.getProperty("ownname", "client1")
        def withSsl = System.getProperty("wsSsl", "false") == "true"
        def path = System.getProperty("path", "/demo")
        def options = new HttpClientOptions()
        options.setDefaultHost("localhost")
        options.setDefaultPort(8080)
        def httpClient = vertx.createHttpClient(options)
        def wsoptions = new RequestOptions().setHost(serverhost).setPort(serverport).setSsl(withSsl).setURI(path)
        httpClient.websocket(wsoptions, { wshandler ->
            LOGGER.info("Client" + ownname + " - websocket established")
            wshandler.handler({buffer ->
                def jso = buffer.toJsonObject()
                jso.put("receiver", ownname);
                eventBus.send("received", jso)
            })
        })
    }
    
}
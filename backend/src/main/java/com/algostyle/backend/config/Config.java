package com.algostyle.backend.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration  //Cette annotation indique que c'est une classe de configuration Spring
@EnableWebSocketMessageBroker   //Activer la fonctionnalité de messgerie WbSocket dans Spring
public class Config implements WebSocketMessageBrokerConfigurer {

    /**
     * Méthode pour enregistrer les points de terminaison (endpoints) WebSocket
     * @param registry  le registre des endpoints STOMP
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        // Définir l'endpoint "/ws" pour les connexions WebSocket
        registry.addEndpoint("/ws")
                // Autoriser les requêtes uniquement depuis localhost:4200 (Sécurité CORS)
                .setAllowedOrigins("http://localhost:4200")
                .withSockJS(); //Activer le fallback SockJS pour les clients qui ne
                                // supportent pas WebSocket nativement
    }

    /**
     * *** à noter ***
     * --> /ws est simplement une convention, on peut mettre n'importe quel chemin
     *     ( c'est l'URL que les clients utiliseront pour établir la connexion WebSocket )
     *     ( ex: un client Angular se connecte avec : 'http://localhost:8080/ws' )
     * /websocket , /stomp, /connection
     */


    /**
     * Configurer le broker de messages
     * Cette méthode configure comment les messages sont routés entre les clients et le serveur.
     * @param registry le registre de configuration du broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        // Préfixe pour les messages qui sont routés vers les méthodes annotées @MessageMapping
        registry.setApplicationDestinationPrefixes("/app"); // Tous les messages envoyés par le client vers le serveur doivent commencer par /app
                                                             // Le serveur ensuite les redirige vers la méthode appropriée (annotée avec @MessageMapping)
        // Active un simple broker de messages en mémoire pour les destinations préfixées par "/topic"
        registry.enableSimpleBroker("/topic"); // Activer un broker de messages en mémoire qui permet au serveur d'envoyer des messages à tous les clients abonnés à un topic
    }
    /**
     * /app --> Préfixe pour les requêtes envoyées par le client au serveur (ex : envoyer un message)
     * Exemple : @MessageMapping("/chat.send")
     * --> Le client envoie un message vers "/app/chat.send" (Spring enlève /app et route vers @MessageMapping("/chat.send")
     * /topic --> Préfixe pour les messages diffusés par le serveur aux clients (ex: recevoir une notification)
     */

    /*************  Client --> Serveur (/app)  **************************************************
     * - Le client envoie un message à "/app/chat.send"
     * - Le serveur le reçoit via @MessageMapping("/chat.send")
     *
     * *************  Serveur --> Clint (/topic)  **************************************************
     * - Le serveur diffuse le message à "/topic/public"
     * - Tous les clients abonnés à "/topic/public" le reçoivent
     */
}

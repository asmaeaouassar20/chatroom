package com.algostyle.backend.config;


import com.algostyle.backend.controller.WsChatMessage;
import com.algostyle.backend.controller.WsChatMessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component  //Marquer cette classe comme un composant Spring (géré par le conteneur IoC)
//@RequiredArgsConstructor    //Générer automatiquement un constructeur avec les dépendances requises (final)
@Slf4j  //Fournir un logger SLF4J via la variable 'log'
public class WsEventListener {

    // Injection de dépendance pour l'envoi de messages WebSocket
    private final SimpMessageSendingOperations messageSendingOperations;

    public WsEventListener(SimpMessageSendingOperations messageSendingOperations) {
        this.messageSendingOperations = messageSendingOperations;
    }

    /**
     * Ecouteur d'événements de déconnexion WebSocket
     * @param event L'événement de déconnexion de session
     */
    @EventListener
    public void handleWsDisconnectListener(SessionDisconnectEvent event){
        // Encapsuler le message STOMP pour accéder aux en-têtes et attributs
        // To listen to another event, create the another method with NewEvent as argument
        StompHeaderAccessor headerAccessor=StompHeaderAccessor.wrap(event.getMessage());

        // Récupérer le 'username' stocké dans les attributs de session
        String username=(String) headerAccessor.getSessionAttributes().get("username");
        if(username!=null){

            // Créer un message de type "LEAVE"(Départ)
            var message= new WsChatMessage();
            message.setType(WsChatMessageType.LEAVE);
            message.setSender(username);

            // Envoyer le message à tous les clients abonnés au topic "/topic/public"
            // pass the message to the broker specific topic : public
            messageSendingOperations.convertAndSend("/topic/public",message);
        }
    }
}

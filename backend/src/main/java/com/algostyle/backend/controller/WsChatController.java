package com.algostyle.backend.controller;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller  //Indique que cette classe est un contrôleur Spring pouvant gérer des messages WebSocket
public class WsChatController {


    /**
     * Gérer l'envoi de messages dans le chat
     * @param msg Le message reçu avec le contenu et l'expéditeur
     * @return Le même message qui sera broadcasté à tous les clients
     */
    @MessageMapping("chat.sendMessage") //Traite les messages envoyés à "/app/chat.sendMessage"
    @SendTo("/topic/public") //Specifies that the return message will be sent to "/topic/public"
                            // Envoie le retour à tous les abonnés de "/topic/public"
    public WsChatMessage sendMessage(@Payload WsChatMessage msg){
        System.out.println("Message received from : "+msg.getSender()+" : "+msg.getContent());

        // Broadcast the message to all subscribers on the "/topic/public" topic
        // >> retourne le message pour qu'il soit broadcasté
        return msg;
    }


    /**
     * Gérer l'ajout d'un nouvel utilisateur au chat
     * @param msg Le message contenant le nom d'utilisateur
     * @param headerAccessor Permet d'accéder aux en-têtes et attributs de session
     * @return Le message de bienvenue qui sera broadcasté
     */
    @MessageMapping("chat.addUser") //Maps messages sent to "chat.addUser" WebSocket destination
                                    // Traite les messsages envoyés à "app/chat.addUser"
    @SendTo("/topic/chat")  //Envoie le retour à tous les abonnés de "/topic/chat"
                            // Specifies hat the return message will be sent to "/topic/chat"
    public WsChatMessage addUser(@Payload WsChatMessage msg, SimpMessageHeaderAccessor headerAccessor){

        // Stocker le nom d'utilisateur dans les attributs de session WebSocket
        headerAccessor.getSessionAttributes().put("username",msg.getSender());
        System.out.println("user joined : "+msg.getSender() + " - type: "+msg.getType());

        //Retourner le message pour notifier tous les clients
        // Broadcast the user join event to all subscribers on the "/topic/chat" topic
        return msg;
    }


    /***** pour les endpoints utilisés ici : "chat.sendMessage" , "chat.addUser" ****
     * - ils sont configurable librement (on peut changer ces noms, il suffit de garder une logique cohérente dans l'app)
     * - On peut utiliser d'autres écritures, par ex:
     *      >> "messages/envoyer"  : Style path REST-like
     *      >> "user-join"  :  Style minimaliste
     *      >> "salon/entree"  :  en français
     * - Bonnes pratiques (conventions courantes) :
     *      * Préfixe chat. pour les fonctionnalités de chat
     * - Le préfixe /app est automatiquement ajouté à ce que vous mettez dans @MessageMapping (défini dans WebSocketConfig)
     */
}

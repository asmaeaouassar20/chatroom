package com.algostyle.backend.config;
import com.algostyle.backend.controller.WsChatMessage;
import com.algostyle.backend.controller.WsChatMessageType;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WsEventListener {


    private final SimpMessageSendingOperations messageSendingOperations;

    public WsEventListener(SimpMessageSendingOperations messageSendingOperations) {
        this.messageSendingOperations = messageSendingOperations;
    }

    @EventListener
    public void handleWsDisconnectListener( SessionDisconnectEvent event){
        //To listen to another even, create the another method with NewEvent as argument.
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if(username !=null){
            var message = new WsChatMessage();
            message.setSender(username);
            message.setType(WsChatMessageType.LEAVE);
            //pass the message to the broker specific topic : public
            messageSendingOperations.convertAndSend("/topic/public",message);
        }
    }

}

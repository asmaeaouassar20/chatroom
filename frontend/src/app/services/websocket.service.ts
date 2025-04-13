import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import { BehaviorSubject } from 'rxjs';
import SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  // Instance du client STOMP pour gérer la connexion WebSocket
  // Peut être null si la connexion n'est pas établie
  stompClient: Client | null = null;  // STOMP client instance to handle WebSocket connection

  // Subject pour gérer l flux des messages entrants
  // Utiliser BehaviorSubject pou conserver la dernière valeur 
  // et la fournir  aux nouveaux subscribers
  // Subject to manage the stream of incoming messages
  private messageSubject = new BehaviorSubject<any>(null);

  // Observable public pour que les composants puissnet s'abonner aux messages
  // masquer l'implémentation du Subject pour respecter le principe d'encapsulation
  public messages$ = this.messageSubject.asObservable();  // Observable for components to subscribe to messages


  // Subject pour suivre l'état de la connexion (connecté/déconnecté)
  // initialiser à false car la connexion n'est pas établie au démarrage
  // Subject to track the connection status (connected/disconnected)
  private connectionSubject = new BehaviorSubject<boolean>(false);

  // Observable public pour que les composants puissent suivre l'état de la connexion
  public connectionStatus$ = this.connectionSubject.asObservable();  // Observable for components to track connection status




  /**
   * Etablie une connexion WebSocket avec le serveur
   * @param username - Le nom de l'utilisateur pour s'identifier auprès du serveur
   */
  connect (username:string){
    // Initialise la connexion SockJS vers le endpoint serveur
    // L'URL devrait être configurable via environment
    const socket = new SockJS('http://localhost:8080/ws');  // Initialize the SockJS WebSocket connection to the server

    // Configure the STOMP client with connection details
    this.stompClient = new Client({
      webSocketFactory: () => socket,  // Use SockJS as the WebSocket factory
      reconnectDelay: 5000,  // Reconnect delay if connection is lost
      debug: (str) => console.log(str)  // Log STOMP debug messages for troubleshooting
    });


    // On successful connection
    // Callback appelé lorsque la connexion STOMP est établie
    this.stompClient.onConnect = (frame) => {
      console.log('Connected to WebSocket server');

      // Mettre à jour l'état de connexion
      this.connectionSubject.next(true);  // Notify that the connection is successful

      // Subscribe to the '/topic/public' topic to receive public messages
      // S'abonner au topic public pour recevoir les messages
      this.stompClient?.subscribe('/topic/public', (message: Message) => {
        // Parse le corps du message JSON et le publier aux subscribers
        this.messageSubject.next(JSON.parse(message.body));  // Pass the message to subscribers
      });

      // Send a "JOIN" message to notify the server that a user has joined
      // Envoyer un message JOIN pour notifier le serveur de la connexion
      this.stompClient?.publish({
        // Endpoint serveur pour ajouter un utilisateur
        destination: '/app/chat.addUser',  // Server endpoint for adding users
        body: JSON.stringify({ 
          sender: username,
           type: 'JOIN'  // Type de message pour identifier une connexion
          })  // Send username and join event
      });
    };

    // Handle errors reported by the STOMP broker
    this.stompClient.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);  // Log the error message
      console.error('Additional details: ' + frame.body);  // Log additional error details
    };
    

    // Activier la connexion STOMP
    this.stompClient?.activate();
  }





/**
 * Envoyer un message via la connexion WebSocket
 * @param username  - L'expéditeur du message
 * @param content  - Le contenu du message
 */
  sendMessage(username:string, content:string){
    if (this.stompClient && this.stompClient.connected) {
      // Create a chat message object 
      // Structurer le message selon le format attendu par le serveur
      const chatMessage = { 
        sender: username, 
        content: content, 
        type: 'CHAT'  // Type de message pour un message standar
      };

      // Log the message being sent and the sender
      console.log(`Message sent by ${username}: ${content}`);

      // Publish (send) the message to the '/app/chat.sendMessage' destination
      this.stompClient.publish({
        destination: '/app/chat.sendMessage', //Endpoint serveur pour envoyer des messages
        body: JSON.stringify(chatMessage)  // Convert the message to JSON and send
      });
    } else {
      // Log an error if the WebSocket connection is not active
      console.error('WebSocket is not connected. Unable to send message.');
    }

  }





  /**
   * Déconnecte proprement le client WebSocket (nettoyer les ressources et notifier les subscribers de la déconnexion)
   */
  disconnect(){
    if (this.stompClient) {
      this.stompClient.deactivate();  // Deactivate the WebSocket connection
      this.connectionSubject.next(false); // Met à jour l'état de la connexion
    }
  }
}

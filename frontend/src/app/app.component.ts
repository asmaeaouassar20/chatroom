import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { WebsocketService } from './services/websocket.service';
import { CommonModule, NgClass, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-root',
  imports: [FormsModule,NgIf,NgClass,NgFor],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{
 
  username:string =''; //Stocker le 'username' entré par l'utilisateur
  message:string=''; //Stocker le message saisi par l'utilisateur
  messages : any[]=[]; //Stocker tous les messages du chat
  isConnected=false; // Trackes whther the user is connected to the Web

  connectingMessage='Connecting ...'; //Message to chow while connecting

  constructor(private websocketService:WebsocketService){
    console.log('AppComponnet constructor called');
  }


  ngOnInit(): void {
      console.log('AppComponent ngOnInit called');

      // S'abonner à l'observable messages$ pour recevoir les messages du service WebSocket
      // L'abonnement reste actif jusqu'à la destruction du composant (cela nécessite un unsubscribe)
      // Subscribe to messages observable to receive messages from the WebSocket service
      this.websocketService.messages$.subscribe(message => {
        if(message){
          // Log and add the received message to the array of messages
          console.log(`Message received from ${message.sender} : ${message.content}`);

          // Ajouter le message au tableau messages
          this.messages.push(message);
        }
      });


      
      
      // Subscribe  to connection status observable to monitor connection status
      // S'abonner à l'observable connectionStatus$ pour surveiller l'état de connexion
      this.websocketService.connectionStatus$.subscribe(connected=>{
        // Mettre à jour la variable d'état locale avec le statut de connexion
        this.isConnected=connected; // update the connection status

        if(connected){
          this.connectingMessage=''; // Clear the connecting message once connected
          console.log('WebSocket connection established');
        }
      });

  }





  connect(){
    console.log('Attempting to connect to WebSocket at http://localhost:8080/ws with username:',this.username);
    this.websocketService.connect(this.username);
  }



  sendMessage(){
    if(this.message){
      this.websocketService.sendMessage(this.username,this.message); // Send the message via WebSocket service
      this.message=''; // Clear the message input after sending
    }
  }


  /**
   * Dans une application de chat, la fonction suivante permet d'avoir :
   *  -> Une couleur persistante pour chaque utilisateur
   *  -> Sans avoir à stocker cette information en base de données
   *  -> Tout en évitant les couleurs aléatoires qui changeraient à chaque affichage
   */

  getAvatar(sender:string):string{
    // Array of colors to choose from
    const colors = ['#2196F3', '#32c787', '#00BCD4', '#ff5652', '#ffc107', '#ff85af', '#FF9800', '#39bbb0'];

    let hash=0;
    for(let i=0;i<sender.length;i++){
      // generate a hash from the sender's name
      hash=31*hash+sender.charCodeAt(i); //Create a hash based on the userbame
    }
    // Return a color from the array based on the hash value
    return colors[Math.abs(hash%colors.length)];
  }



}

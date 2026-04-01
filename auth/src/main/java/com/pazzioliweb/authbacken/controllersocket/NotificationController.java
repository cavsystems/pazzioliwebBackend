package com.pazzioliweb.authbacken.controllersocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {
	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public  greeting greeting(HelloMessage msg) {
	  // proceso...
	  return new greeting("Hola " + msg.getName());
	}
}

 class greeting {
	 private String content;
	 private String con;
	 
	 public String getCon() {
		return con;
	}

	 public void setCon(String con) {
		 this.con = con;
	 }

	 public greeting(String content) {
	        this.content = content;
	    }
	 
	 public String getContent() {
	        return content;
	    }

	    public void setContent(String content) {
	        this.content = content;
	        this.con="comosea";
	    }
 }
 
 
 
 
 class HelloMessage {
	  
	  private String name;

	  public HelloMessage() { }

	  public HelloMessage(String name) {
	    this.name = name;
	  }

	  public String getName() {
	    return name;
	  }

	  public void setName(String name) {
	    this.name = name;
	  }

	}
package org.akka;

import org.akka.actors.ClusterClient;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;

// mvn exec:java -Dexec.mainClass="org.akka.ClientApp" -Dexec.args="2551"
public class ClientApp {

	public static void main(String[] args) {
		
		ActorSystem system = ActorSystem.create("ClusterSystem",
			ConfigFactory.load("file"));
		system.actorOf(Props.create(ClusterClient.class, "/user/fileService"),
			"client");
	}
}

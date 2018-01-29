package org.akka;

import org.akka.actors.ClusterClient;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;

// mvn exec:java -Dexec.mainClass="org.akka.ClientApp" -Dexec.args="2551"
public class ClientApp {

	public static void main(String[] args) {
		
		if (args.length == 0) {
			startup(new String[] { "2550" });
		}
		else {
			startup(args);
		}
	}
	
	public static void startup(String[] ports) {
		for (String port : ports) {
			Config config = ConfigFactory.parseString(
					"akka.remote.netty.tcp.port=" + port + "\n")
				.withFallback(
					ConfigFactory.parseString("akka.cluster.roles = [compute]"))
				.withFallback(ConfigFactory.load("file"));
			
			ActorSystem system = ActorSystem.create("ClusterSystem", config);
			system.actorOf(Props.create(ClusterClient.class, "/user/fileServiceProxy"),
				"client");
		}
	}
}

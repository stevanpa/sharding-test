package org.akka;

import org.akka.actors.ClusterListener;
import org.akka.actors.FileParser;
import org.akka.actors.FileService;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// mvn exec:java -Dexec.mainClass="org.akka.ClusterApp" -Dexec.args="2551"
public class ClusterApp {

	public static void main(String[] args) {
		if (args.length == 0)
			startup(new String[] { "2551", "2552", "0" });
		else
			startup(args);
	}

	public static void startup(String[] ports) {
		for (String port : ports) {
			// Override the configuration of the port
			Config config = ConfigFactory.parseString(
				"akka.remote.netty.tcp.port=" + port + "\n" +
				"akka.remote.artery.canonical.port=" + port)
			.withFallback(
				ConfigFactory.parseString("akka.cluster.roles = [compute]"))
			.withFallback(ConfigFactory.load("file"));

			// Create an Akka system
			ActorSystem system = ActorSystem.create("ClusterSystem", config);
	
			// Create an actor that handles cluster domain events
			//system.actorOf(Props.create(ClusterListener.class),
			//		"clusterListener");
			
			ActorRef fileParser = system.actorOf(Props.create(FileParser.class),
					"fileParser");
			ActorRef fileService = system.actorOf(Props.create(FileService.class),
					"fileService");
			
			system.log().info("fileParser Actor Reference: {}", fileParser);
			system.log().info("fileService Actor Reference: {}", fileService);
		}
	}
}

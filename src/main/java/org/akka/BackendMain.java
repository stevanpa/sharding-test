package org.akka;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;

// mvn exec:java -Dexec.mainClass="org.akka.BackendMain" // -Dexec.args="arg0 arg1"
public class BackendMain {

	public static void main(String[] args) {
		
		// Override the configuration of the port when specified as program argument
		final String port = args.length > 0 ? args[0] : "0";
		final Config config = 
				ConfigFactory.parseString(
						"akka.remote.netty.tcp.port=" + port + "\n" +
						"akka.remote.artery.canonical.port=" + port)
				.withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"))
				.withFallback(ConfigFactory.load("application"));
		
		ActorSystem system = ActorSystem.create("ClusterSystem", config);
		system.actorOf(Props.create(Backend.class), "backend");
		system.actorOf(Props.create(MetricsListener.class), "metricsListener");
		
	}
}

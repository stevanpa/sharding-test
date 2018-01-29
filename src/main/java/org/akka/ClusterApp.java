package org.akka;

import org.akka.actors.FileService;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;

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
			
			ClusterSingletonManagerSettings settings = ClusterSingletonManagerSettings.create(system)
					.withRole("compute");
			system.actorOf(ClusterSingletonManager.props(
					Props.create(FileService.class), PoisonPill.getInstance(), settings),
					"fileService");
			
			ClusterSingletonProxySettings proxySettings =
					ClusterSingletonProxySettings.create(system).withRole("compute");
			system.actorOf(ClusterSingletonProxy.props("/user/fileService", 
					proxySettings), "fileServiceProxy");
		}
	}
}

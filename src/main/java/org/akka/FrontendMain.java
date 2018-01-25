package org.akka;

import java.util.concurrent.TimeUnit;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

// mvn exec:java -Dexec.mainClass="org.akka.FrontendMain" // -Dexec.args="arg0 arg1"
public class FrontendMain {

	public static void main(String[] args) {
		
		final Config config = 
				ConfigFactory.parseString("akka.cluster.roles = [frontend]")
				.withFallback(ConfigFactory.load("application"));
		final ActorSystem system = ActorSystem.create("ClusterSystem", config);
		system.log().info("Processing can start when 2 backend members in the cluster");
		
		Cluster.get(system).registerOnMemberUp(new Runnable() {
			@Override
			public void run() {
				system.actorOf(Props.create(Frontend.class, args[0]), "frontend");
			}
		});
		
		Cluster.get(system).registerOnMemberRemoved(new Runnable() {
			@Override
			public void run() {
				// exit JVM when ActorSystem has been terminated
				final Runnable exit = new Runnable() {
					 @Override 
					 public void run() {
						 System.exit(0);
					 }
				};
				system.registerOnTermination(exit);
				
				// shut down ActorSystem
				system.terminate();
				
				// In case ActorSystem shutdown takes longer than 10 seconds, exit the JVM forcefully anyway.
				// We must spawn a separate thread to not block current thread, since that would have blocked 
				// the shutdown of the ActorSystem.
				new Thread() {
					@Override 
					public void run() {
						try {
							Await.ready(system.whenTerminated(), Duration.create(10, TimeUnit.SECONDS));
						} catch (Exception e) {
							System.exit(-1);
						}
					}
				}.start();
			}
		});
	}
}
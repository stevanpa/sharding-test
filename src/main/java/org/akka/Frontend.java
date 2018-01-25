package org.akka;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ReceiveTimeout;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;
import scala.concurrent.duration.Duration;

public class Frontend extends AbstractActor {
	
	final Path rootFolder;
	
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	ActorRef backend = getContext().actorOf(FromConfig.getInstance().props(), "backendRouter");
	
	public Frontend(String rootFolder) {
		this.rootFolder = Paths.get(rootFolder);
	}
	
	@Override
	public void preStart() {
		sendJobs();
		getContext().setReceiveTimeout(Duration.create(10, TimeUnit.SECONDS));
	}

	@Override
	public Receive createReceive() {
		
		return receiveBuilder()
				.match(TrackResult.class, result -> {
					if(result.getPointList().size() > 0) {
						log.debug("{} points parsed {}", result.getPointList().size(), result.getPointList().toString());
						//getContext().stop(self());
					}
				})
				.match(ReceiveTimeout.class, message -> {
					log.info("Timeout");
				})
				.build();
	}

	private void sendJobs() {
		log.info("Start batch of file import from [{}]", rootFolder.toString());
		File[] files = new File(this.rootFolder.toString()).listFiles();
		for (File file : files) {
			if (file.isFile()) {
				log.info("File [{}]", file.getName());
				FileMessage fileMessage = new FileMessage(file);
				backend.tell(fileMessage, self());
		    }
		}
	}
}

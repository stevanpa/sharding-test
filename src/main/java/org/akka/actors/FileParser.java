package org.akka.actors;

import java.io.File;
import java.nio.file.Paths;

import org.akka.messages.FileMessage.FileJob;
import org.akka.messages.FileMessage.FileJobFailed;
import org.akka.messages.FileMessage.FileJobResult;

import akka.actor.UntypedAbstractActor;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope;

public class FileParser extends UntypedAbstractActor {
	
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	Cluster cluster = Cluster.get(getContext().system());

	@Override
	public void preStart() {
		log.info("FileParser prestart: {}", getSelf());
	}
	
	@Override
	public void postStop() {
		
	}
	
	@Override
	public void onReceive(Object message) throws Throwable {
		
		if (message instanceof FileJob) {
			FileJob job = (FileJob) message;
			File file = Paths.get(job.getFile()).toFile();
			String fileName = file.getName();
			log.info("New FileJob Message: {}", file.getName());
			
			if(fileName == null) {
				FileJobFailed failed = new FileJobFailed("fileName is NULL");
				getSender().tell(failed, getSelf());
			}
			
			FileJobResult result = new FileJobResult(fileName);
			getSender().tell(new ConsistentHashableEnvelope(result, fileName), getSelf());
		}
		else {
			log.info("FileParser has unknown Message: " + message.toString());
			unhandled(message);
		}
	}

}

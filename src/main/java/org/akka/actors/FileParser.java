package org.akka.actors;

import java.io.File;
import java.nio.file.Paths;

import org.akka.messages.FileMessage.FileJob;
import org.akka.messages.FileMessage.FileJobFailed;
import org.akka.messages.FileMessage.FileJobResult;

import akka.actor.UntypedAbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.MemberUp;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope;

public class FileParser extends UntypedAbstractActor {
	
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	Cluster cluster = Cluster.get(getContext().system());

	@Override
	public void preStart() {
		log.info("FileParser - preStart: {}", getSelf());
		cluster.subscribe(context().sender(), MemberUp.class);
	}
	
	@Override
	public void postStop() {
		cluster.unsubscribe(getSelf());
	}
	
	@Override
	public void onReceive(Object message) throws Throwable {
		
		if (message instanceof ConsistentHashableEnvelope) {
			log.info("FileParser - INSTANCEOF ConsistentHashableEnvelope");
			ConsistentHashableEnvelope envelope = (ConsistentHashableEnvelope) message;
			if (envelope.message() instanceof FileJob) {
				handleFileJobMessage(envelope.message());
			}
		}
		else if (message instanceof FileJob) {
			log.info("FileParser - INSTANCEOF FileJob");
			handleFileJobMessage(message);
		}
		else {
			log.info("FileParser - received unknown Message: " + message.toString());
			unhandled(message);
		}
	}
	
	private void handleFileJobMessage(Object message) {
		FileJob job = (FileJob) message;
		File file = Paths.get(job.getFile()).toFile();
		String fileName = file.getName();
		log.info("FileParser - New FileJob Message: {}", file.getName());
		
		if(fileName == null) {
			FileJobFailed failed = new FileJobFailed("fileName is NULL");
			getSender().tell(failed, getSelf());
		}
		
		FileJobResult result = new FileJobResult(fileName);
		log.info("FileParser - Reply to Actor: {}", sender());
		sender().tell(new ConsistentHashableEnvelope(result, fileName), getSelf());
	}

}

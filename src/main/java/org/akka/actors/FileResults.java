package org.akka.actors;

import java.util.ArrayList;
import java.util.List;

import org.akka.messages.FileMessage.FileJobResult;
import org.akka.messages.FileMessage.FolderJobResult;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope;

public class FileResults extends UntypedAbstractActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private final int expectedFiles;
	private final List<String> results = new ArrayList<>();
	private final ActorRef replyTo;
	
	public FileResults(int expectedFiles, ActorRef replyTo) {
		this.expectedFiles = expectedFiles;
		this.replyTo = replyTo;
	}
	
	@Override
	public void preStart() {
		
	}
	
	@Override
	public void postStop() {
		
	}
	
	@Override
	public void onReceive(Object message) throws Throwable {
		
		if (message instanceof ConsistentHashableEnvelope) {
			log.info("FileResults - INSTANCEOF ConsistentHashableEnvelope");
			ConsistentHashableEnvelope envelope = (ConsistentHashableEnvelope) message;
			if (envelope.message() instanceof FileJobResult) {
				handleFilJobResultMessage(envelope.message());
			}
			else {
				log.info("FileResults - received unknown Message: " + message.toString());
			}
		}
		else if (message instanceof FileJobResult) {
			log.info("FileResults - INSTANCEOF FileJobResult");
			handleFilJobResultMessage(message);
		}
		else {
			log.info("FileResults - received unknown Message: " + message.toString());
		}
	}
	
	private void handleFilJobResultMessage(Object message) {
		FileJobResult result = (FileJobResult) message;
		results.add(result.getFileName());
		
		log.info("FileResults - adding result: " + result.getFileName());
		if (results.size() == expectedFiles) {
			log.info("FileResults - Total number of Results: {}", results.size());
			log.info("FileResults - Sending message to: {}", replyTo);
			replyTo.tell(new ConsistentHashableEnvelope(new FolderJobResult(results.size()), "hash"), replyTo);
			self().tell(PoisonPill.getInstance(), ActorRef.noSender());
		}
	}
}

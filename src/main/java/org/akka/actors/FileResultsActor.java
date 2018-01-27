package org.akka.actors;

import java.util.ArrayList;
import java.util.List;

import org.akka.messages.FileMessage.FileJobResult;

import akka.actor.ActorRef;
import akka.actor.UntypedAbstractActor;

public class FileResultsActor extends UntypedAbstractActor {

	private final int expectedFiles;
	private final List<String> results = new ArrayList<>();
	private final ActorRef replyTo;
	
	public FileResultsActor(int expectedFiles, ActorRef replyTo) {
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
		
		if (message instanceof FileJobResult) {
			FileJobResult result = (FileJobResult) message;
			results.add(result.getFileName());
			
			if (results.size() == expectedFiles) {
				replyTo.tell(results.size(), replyTo);
				getContext().stop(getSelf());
			}
		}
	}
}

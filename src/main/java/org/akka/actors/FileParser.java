package org.akka.actors;

import org.akka.messages.FileMessage.FileJob;
import org.akka.messages.FileMessage.FileJobFailed;
import org.akka.messages.FileMessage.FileJobResult;

import akka.actor.UntypedAbstractActor;
import akka.cluster.Cluster;

public class FileParser extends UntypedAbstractActor {
	
	Cluster cluster = Cluster.get(getContext().system());

	@Override
	public void preStart() {
		
	}
	
	@Override
	public void postStop() {
		
	}
	
	@Override
	public void onReceive(Object message) throws Throwable {
		
		if (message instanceof FileJob) {
			FileJob job = (FileJob) message;
			String fileName = job.getFile().getName();
			
			if(fileName == null) {
				FileJobFailed failed = new FileJobFailed("fileName is NULL");
				getSender().tell(failed, getSelf());
			}
			FileJobResult result = new FileJobResult(fileName);
			getSender().tell(result, getSelf());
		}
		else {
			unhandled(message);
		}
	}

}

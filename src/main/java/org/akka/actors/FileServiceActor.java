package org.akka.actors;

import java.io.File;

import org.akka.messages.FileMessage.FolderJob;
import org.akka.messages.FileMessage.FileJob;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.routing.FromConfig;

public class FileServiceActor extends UntypedAbstractActor {

	ActorRef fileRouter = getContext().actorOf(
		FromConfig.getInstance().props(Props.create(FileParserActor.class)),
		"fileRouter");
			
	@Override
	public void preStart() {
		
	}
	
	@Override
	public void postStop() {
		
	}
	
	@Override
	public void onReceive(Object message) throws Throwable {
		
		if (message instanceof FolderJob) {
			ActorRef replyTo = getSender();
			FolderJob folderJob = (FolderJob) message;
			File[] files = folderJob.getPath().toFile().listFiles();
			
			ActorRef fileResults = getContext().actorOf(
				Props.create(FileResultsActor.class, files.length, replyTo));
			
			for (File file : files) {
				fileRouter.tell(new FileJob(file), fileResults);
			}
		}
	}

}

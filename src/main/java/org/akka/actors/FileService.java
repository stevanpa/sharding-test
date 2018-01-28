package org.akka.actors;

import java.io.File;
import java.nio.file.Paths;

import org.akka.messages.FileMessage.FolderJob;
import org.akka.messages.FileMessage.FileJob;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope;

public class FileService extends UntypedAbstractActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	ActorRef fileRouter = getContext().actorOf(
		FromConfig.getInstance().props(Props.create(FileParser.class)),
		"fileRouter");
			
	@Override
	public void preStart() {
		log.info("Starting FileService Actor: {}", getSelf());
	}
	
	@Override
	public void postStop() {
		
	}
	
	@Override
	public void onReceive(Object message) throws Throwable {
		
		if (message instanceof ConsistentHashableEnvelope) {
			ConsistentHashableEnvelope envelope = (ConsistentHashableEnvelope) message;
			if (envelope.message() instanceof FolderJob) {
				ActorRef replyTo = getSender();
				FolderJob folderJob = (FolderJob) envelope.message();
				String folderPath = folderJob.getPath();
				log.info("Received FolderJob: {}", folderPath);
				File[] files = Paths.get(folderPath).toFile().listFiles();
				log.info("Number of Files: {}", files.length);
				
				ActorRef fileResults = getContext().actorOf(
					Props.create(FileResults.class, files.length, replyTo));
				
				log.info("FileRouter address: {}", fileRouter.toString());
				for (File file : files) {
					log.info("Sending FileJobs to: {}", fileRouter.path().toString());
					fileRouter.tell(
							new ConsistentHashableEnvelope(new FileJob(file.getPath()), file.getPath())
							, fileResults);
				}
			}
		}
		else {
			log.info("FileService Received unknown message {}", message);
		}
	}

}

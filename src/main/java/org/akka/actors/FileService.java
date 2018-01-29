package org.akka.actors;

import java.io.File;
import java.nio.file.Paths;
import org.akka.messages.FileMessage.FolderJob;
import org.akka.messages.FileMessage.FileJob;

import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope;

public class FileService extends UntypedAbstractActor {

	LoggingAdapter log = Logging.getLogger(context().system(), this);
	ActorRef fileRouter = context().actorOf(
		FromConfig.getInstance().props(Props.create(FileParser.class)),
		"fileRouter");
	
	@Override
	public void preStart() {
		log.info("FileService - Starting FileService Actor: {}", getSelf());
		log.info("FileService - Context: {}", context());
		log.info("FileService - Context dispatcher: {}", context().dispatcher());
		log.info("FileService - Context system: {}", context().system());
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
				log.info("FileService - Received FolderJob: {}", folderPath);
				File[] files = Paths.get(folderPath).toFile().listFiles();
				log.info("FileService - Number of Files: {}", files.length);
				
				ActorRef fileResults = context().actorOf(
					Props.create(FileResults.class, files.length, replyTo));
				
				log.info("FileService - FileRouter address: {}", fileRouter);
				for (File file : files) {
					log.info("FileService - Sending FileJobs to: {}", fileRouter);
					fileRouter.tell(
							new ConsistentHashableEnvelope(new FileJob(file.getPath()), file.getPath())
							, fileResults);
				}
			}
			else {
				log.info("FileService - Unknown message in Envelpope: {}", envelope.message());
			}
		}
		else if (message instanceof ActorIdentity) {
			ActorIdentity identity = (ActorIdentity) message;
			log.info("FileService - Identity of actor: {}", identity);
		}
		else {
			log.info("FileService - Received unknown message {}", message);
		}
	}

}

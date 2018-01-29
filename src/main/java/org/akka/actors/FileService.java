package org.akka.actors;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.akka.messages.FileMessage.FolderJob;
import org.akka.messages.FileMessage.FileJob;

import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Identify;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.AskableActorSelection;
import akka.routing.FromConfig;
import akka.util.Timeout;
import scala.concurrent.Future;
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope;

public class FileService extends UntypedAbstractActor {

	LoggingAdapter log = Logging.getLogger(context().system(), this);
//	ActorRef fileRouter = context().actorOf(
//		FromConfig.getInstance().props(Props.create(FileParser.class)),
//		"fileRouter");
			
	ActorRef fileParser = context().actorOf(Props.create(FileParser.class), "fileParser");
	
	@Override
	public void preStart() {
		log.info("Starting FileService Actor: {}", getSelf());
		log.info("Context: {}", context());
		log.info("Context dispatcher: {}", context().dispatcher());
		log.info("Context system: {}", context().system());
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
				
				ActorRef fileResults = context().actorOf(
					Props.create(FileResults.class, files.length, replyTo));
				
//				log.info("FileRouter address: {}", fileRouter);
				for (File file : files) {
//					log.info("Sending FileJobs to: {}", fileRouter);
//					fileRouter.tell(
//							new ConsistentHashableEnvelope(new FileJob(file.getPath()), file.getPath())
//							, fileResults);
					
					log.info("Sending FileJobs to: {}", fileParser);
					fileParser.tell(
							new ConsistentHashableEnvelope(new FileJob(file.getPath()), file.getPath())
							, fileResults);
				}
			}
			else {
				log.info("Unknown message in Envelpope: {}", envelope.message());
			}
		}
		else if (message instanceof ActorIdentity) {
			ActorIdentity identity = (ActorIdentity) message;
			log.info("Identity of actor: {}", identity);
		}
		else {
			log.info("FileService Received unknown message {}", message);
		}
	}

}

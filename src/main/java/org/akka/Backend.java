package org.akka;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import static akka.pattern.PatternsCS.pipe;

public class Backend extends AbstractActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	@Override
	public Receive createReceive() {
		
		return receiveBuilder()
				.match(FileMessage.class, fileMessage -> {
					CompletableFuture<TrackResult> result =
							CompletableFuture.supplyAsync(() -> parsePoints(fileMessage.getFile()))
							.thenApply((pointList) -> new TrackResult(fileMessage.getFile().getName(), pointList));
					
					pipe(result, context().system().dispatcher())
					.to(context().sender());
				})
				.build();
	}
	
	private List<Integer> parsePoints(File file) {
		
		log.debug("File received: {}", file.getName());
		List<Integer> fakeList = new ArrayList<Integer>();
		fakeList.add(0);
		fakeList.add(1);
		fakeList.add(2);
		return fakeList;
	}

}

package org.akka.actors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.akka.messages.FileMessage.FileJobFailed;
import org.akka.messages.FileMessage.FileJobResult;
import org.akka.messages.FileMessage.FolderJob;

import akka.actor.ActorSelection;
import akka.actor.Address;
import akka.actor.UntypedAbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.ReachabilityEvent;
import akka.cluster.ClusterEvent.ReachableMember;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope;
import akka.cluster.Member;
import akka.cluster.MemberStatus;

public class ClusterClient extends UntypedAbstractActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	Cluster cluster = Cluster.get(getContext().system());
	
	private final String servicePath;
	private final String folderPath;
	private final Set<Address> nodes = new HashSet<Address>();
	
	public ClusterClient(String servicePath) {
		this.servicePath = servicePath;
		this.folderPath = "/home/bender/test/Trajectory";
	}
	
	//subscribe to cluster changes
	@Override
	public void preStart() {
		log.info("Client Subscribing to cluster");
	    cluster.subscribe(self(), MemberEvent.class, ReachabilityEvent.class);
	    //sendJob();
	}

	//re-subscribe when restart
	@Override
	public void postStop() {
	    cluster.unsubscribe(self());
	}
	
	private void sendJob() {
		log.info("Folder to pass: {}", folderPath);
		
		List<Address> nodesList = new ArrayList<>(nodes);
		Address address = nodesList.get(
				ThreadLocalRandom.current().nextInt(nodesList.size()));
		log.info("Sending Folderjob to: {}", address.toString() + servicePath);
		ActorSelection service = 
				getContext().actorSelection(address.toString() + servicePath);
		service.tell(new ConsistentHashableEnvelope(new FolderJob(folderPath), 
				"someSoCalledRandomHash"), getSelf());
	}
		
	@Override
	public void onReceive(Object message) throws Throwable {
		
		if(message instanceof FileJobResult) {
			
			FileJobResult result = (FileJobResult) message;
			log.info(result.toString());
		}
		else if(message instanceof FileJobFailed) {
			
			FileJobFailed failed = (FileJobFailed) message;
			log.info(failed.toString());
		}
		else if(message instanceof CurrentClusterState) {
			
			CurrentClusterState cState = (CurrentClusterState) message;
			log.info("Members before clear: {}", cState.members().mkString());
			nodes.clear();
			log.info("Members: {}", cState.members().mkString());
			for (Member member : cState.getMembers()) {
				
				if(member.hasRole("compute") 
				&& member.status().equals(MemberStatus.up())) {
					log.info("Adding member: {}", member);
					nodes.add(member.address());
				}
			}
		}
		else if(message instanceof MemberUp) {
			
			MemberUp mUp = (MemberUp) message;
			if(mUp.member().hasRole("compute")) {
				log.info("Member is Up: {}", mUp.member());
				nodes.add(mUp.member().address());
				log.info("Number of members: {}", nodes.size());
				if (nodes.size() == 3) {
					log.info("Send job");
					sendJob();
				}
			}
		}
		else if(message instanceof MemberEvent) {
			
			MemberEvent mEvent = (MemberEvent) message;
			nodes.remove(mEvent.member().address());
		}
		else if(message instanceof UnreachableMember) {
			
			UnreachableMember uMember = (UnreachableMember) message;
			nodes.remove(uMember.member().address());
		}
		else if(message instanceof ReachableMember) {
			
			ReachableMember rMember = (ReachableMember) message;
			if(rMember.member().hasRole("compute")) {
				log.info("Adding ReachableMember: {}", rMember.member());
				nodes.add(rMember.member().address());
			}
		}
	}

}

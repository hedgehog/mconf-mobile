package org.mconf.bbb.api;

import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoinService {
	private static final Logger log = LoggerFactory.getLogger(JoinService.class);

	private String serverUrl;
	private Meetings meetings = new Meetings();
	private JoinedMeeting joinedMeeting = null;
	
	public JoinedMeeting getJoinedMeeting() {
		return joinedMeeting;
	}

	public List<Meeting> getMeetings() {
		return meetings.getMeetings();
	}
	
	public String getServerUrl() {
		return serverUrl;
	}

	public boolean load(String serverUrl) {
		this.serverUrl = serverUrl;
		
		String getMeetingsUrl = serverUrl + "/bigbluebutton/demo/mobile.jsp?action=getMeetings";
		String strMeetings = null;
		try {
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(getMeetingsUrl);
			client.executeMethod(method);
			strMeetings = method.getResponseBodyAsString().trim();
			method.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (strMeetings == null) {
			log.info("Can't connect to {}", serverUrl);
			return false;
		}
		
		meetings.parse(strMeetings);
		log.debug(meetings.toString());
		
		return true;
	}

	public JoinedMeeting join(String meetingID, String name, boolean moderator) {
		for (Meeting m : meetings.getMeetings()) {
			log.info(m.getMeetingID());
			if (m.getMeetingID().equals(meetingID)) {
				return join(m, name, moderator);
			}
		}
		return null;
	}
	
//	public boolean createMeeting(Meeting meeting) {
//		String create = api.createMeeting(meeting.getMeetingID(), 
//				"Welcome message", 
//				meeting.getModeratorPW(), 
//				meeting.getAttendeePW(), 
//				0, 
//				serverUrl);
//		log.debug("createMeeting: {}", create);
//		return create.equals(meeting.getMeetingID()); 
//	}
	
	public JoinedMeeting join(Meeting meeting, String name, boolean moderator) {
//		if (api.isMeetingRunning(meeting.getMeetingID()).equals("false")) {
//			if (!createMeeting(meeting)) {
//				log.error("The meeting {} is not running", meeting.getMeetingID());
//				return null;
//			}
//		}
		
		String joinUrl = serverUrl + "/bigbluebutton/demo/mobile.jsp?action=join"
			+ "&meetingID=" + urlEncode(meeting.getMeetingID())
			+ "&fullName=" + urlEncode(name)
			+ "&password=" + urlEncode(moderator? meeting.getModeratorPW(): meeting.getAttendeePW());
		log.debug(joinUrl);
		
		JoinedMeeting joinedMeeting = new JoinedMeeting();
		try {
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(joinUrl);
			client.executeMethod(method);
			joinedMeeting.parse(method.getResponseBodyAsString().trim());
			method.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Can't join the meeting {}", meeting.getMeetingID());
			
			return null;
		}
		
		if (joinedMeeting.getReturncode().equals("SUCCESS")) {
			this.joinedMeeting = joinedMeeting;
		} else {
			log.error(joinedMeeting.getMessage());
		}
		
		return joinedMeeting;
	}

	public static String urlEncode(String s) {	
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
}

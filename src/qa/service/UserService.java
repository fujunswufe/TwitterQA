package qa.service;

import qa.analysis.TextTokenizer;
import qa.connection.Parameter;
import qa.datahelper.UserHelper;
import twitter4j.IDs;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class UserService {
	private UserHelper userHelper = new UserHelper();
	private static Twitter twitter = null;
	
	public boolean isExist(User user) {
		// TODO Auto-generated method stub
		return userHelper.isExistByUserId(user.getId());
	}
	public UserService(){
		if(twitter == null){
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true);
			cb.setOAuthConsumerKey(Parameter.CONSUMER_KEY);
		    cb.setOAuthConsumerSecret(Parameter.CONSUMER_KEY_SECRET);
		    cb.setOAuthAccessToken(Parameter.ACCESS_TOKEN);
		    cb.setOAuthAccessTokenSecret(Parameter.ACESS_TOKEN_SECRET);
		    twitter= new TwitterFactory(cb.build()).getInstance();
		} 
	}
	public void createIndex(User user) {
		// TODO Auto-generated method stub
		
		try {
			
			// construct the follower index
			IDs followerIter = null;
			followerIter = twitter.getFollowersIDs(user.getId(), -1);
			long[] followers = followerIter.getIDs();
			for(long id : followers){
				System.out.println(id);
				userHelper.addFollower(user.getId(), id);
				if(!userHelper.isExistByUserId(id)){
					parseUser(id);
				}
			}
			
			// construct the following index
			IDs following = null;
			following = twitter.getFriendsIDs(user.getId(), -1);
			long[] followings = following.getIDs();
			for(long id : followings){
				System.out.println(id);
				userHelper.addFollowing(user.getId(), id);
				if(!userHelper.isExistByUserId(id)){
					parseUser(id);
				}
			}
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void parseUser(long id){
		
		try {
			
			ResponseList<Status> statusList= twitter.getUserTimeline(id);
			for(Status status : statusList){
				
				String text = status.getText();
				//System.out.println(text);
				TextTokenizer tokenizer = new TextTokenizer(text);
				String token = null;
				while((token = tokenizer.nextWord()) != null ){
					userHelper.addIndex(token, id);
				}
			}
			//System.out.println("------------End--------------");
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}

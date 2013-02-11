package code.comet.sub

import net.liftweb._
import http._
import js._
import JsCmds._
import common._
import util.Helpers._

import code.security.realms._

import org.apache.shiro._
import org.apache.shiro.authc._
import org.apache.shiro.subject._
import code.comet.{RemActor, AddActor, LeavedUser, TweetServer}

case class TweetMsg(username: String, tweet: String)

class UserActor extends CometActor {

  val subject = SecurityUtils.getSubject() 
  
  /**
   * knows all tweet messages, that has to be shown to this user inside this
   * session
   */
  private var tweets : List[TweetMsg] = Nil

  def registerWith = TweetServer

  def username = if (subject.isAuthenticated) subject.getPrincipal.asInstanceOf[String] else "guest"

  override def lowPriority = {
    case msg: TweetMsg => {
      val name = username
      println(":: :: USERACTOR :: :: Reveived a tweet of " +
          msg.username +
          " with tweet: " +
          msg.tweet +
          " for this session user with name: " +
          name)
      tweets ::= msg
      reRender
    }

    case LeavedUser(leavedUser) => {
      println("USERACTOR FOR USER " + username + " RECEIVES MSG ABOUT THE LEAVED USER = " + leavedUser)
      reRender
    }
  }

  /**
   * test manipulation of lifespan
   * @return
   */
  override def lifespan: Box[TimeSpan] = Full(1 minute)

  override def localSetup() {
    println("LOCAL SETUP OF USERACTOR FOR USER " + username)
    TweetServer ! AddActor(this)
    super.localSetup()
  }

  override def localShutdown() {
    println("LOCAL >>> SHUTDOWN <<< OF USERACTOR FOR USER " + username)
    TweetServer ! RemActor(this)
    super.localShutdown()
  }

  def render = {
    println("RENDER TWEET LIST ACTOR PART")
    if (!tweets.isEmpty) {
      "ul #allTweets li *" #> tweets.map(tweet =>
        "@user" #> tweet.username &
        "@tweetMsg" #> tweet.tweet) &
        "#emptyList" #> "" & whoIsOnline
    } else {
      "ul #allTweets" #> "" & whoIsOnline
    }
  }

  def whoIsOnline = {
    println("RENDER WHO IS ONLINE ACTOR PART")
    val onlineUsers = TweetServer.loggedInUsers
    if (!onlineUsers.isEmpty) {
      "ul #onlineUsers li *" #> onlineUsers.map(onlineUser =>
        "@who" #> onlineUser) &
        "#allOffline" #> ""
    } else {
      "ul #onlineUsers" #> ""
    }
  }
  
}
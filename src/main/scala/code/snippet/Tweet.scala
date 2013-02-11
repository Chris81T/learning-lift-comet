package code.snippet

import net.liftweb._
import http._
import js._
import JsCmds._
import common._
import util.Helpers._
import scala.xml.NodeSeq

import scala.collection.mutable._

import code.security.realms._

import org.apache.shiro._
import org.apache.shiro.authc._
import org.apache.shiro.subject._;

import shiro._
import code.comet.{TweetServer}
import code.comet.sub.TweetMsg
;

case class Entry(label: String)

class Tweet {

  private val fMap = UserManagement.followers
  
  def followers = {
    
    val entries = new ListBuffer[Entry]
    
    for(follower <- fMap) {
      val user = follower._1
      val users = follower._2
      
      println("%%%%%% TWEET: user " + user)
      println("%%%%%% TWEET: users " + users)

      def parseUsers() : String = {
        var parsed = ""
        users.foreach(parsed += _.name + ", ")
        parsed
      }

      entries.append(Entry(user.name + " is followed by: " + parseUsers))
    }
    
    "li *" #> entries.map(_.label) 
  }
  
  def tweet = {
    var msg = ""
    
    def execTweet() : JsCmd = {
      println("%%%%%% TWEET: execTweet with " + msg)
      val subject = SecurityUtils.getSubject()
      val username = if (subject.isAuthenticated) subject.getPrincipal.asInstanceOf[String] else "guest"
      TweetServer ! TweetMsg(username, msg)
      SetValById("tweetIn", "")
    }  
      
    "#tweetIn" #> SHtml.text(msg, msg = _) &
      "#tweetSubmit" #> SHtml.ajaxSubmit("tweet", execTweet)
  }
  
}
package code.comet

import net.liftweb.actor._
import net.liftweb._
import http._
import js._
import JsCmds._
import common._
import sub.{TweetMsg, UserActor}
import util.Helpers._

import code.security.realms._

import org.apache.shiro._
import org.apache.shiro.authc._
import org.apache.shiro.subject._;

/**
 * following case classes will be used as a message between the actors
 */
final case class AddActor(actor: UserActor)
final case class RemActor(actor: UserActor)
final case class LeavedUser(username: String)

object TweetServer extends LiftActor {

	private var users: List[UserActor] = Nil

  private def findFollowersOf(username: String) : List[UserActor] = {

    var actors : List[UserActor] = Nil

    for(user <- UserManagement.followers if user._1.name.equals(username)) {
      user._2.foreach(follower =>
        findUserActor(follower.name) match {
          case Some(actor) => actors ::= actor
          case None => println("%% %% %% SORRY, NO USER_ACTOR FOUND")
        }
      )
    }

    actors
  }

  private def findUserActor(username: String) : Option[UserActor] = {
    users.find(_.username.equals(username))
  }

	def messageHandler = {
	  case AddActor(actor) => {
	    println("%% %% %% RECEIVED USER ACTOR TO ADD INTO INTERNAL LIST :: " + actor)
	    users ::= actor
	  }
    case RemActor(actor) => {
      println("%% %% %% RECEIVED USER ACTOR TO REMOVE FROM INTERNAL LIST :: " + actor)
      users = users.filter(_ ne actor)
      // send all actor's the information about the logged out user
      users.foreach(_ ! LeavedUser(actor.username))
    }
	  case tweet: TweetMsg => {
	    println("%% %% %% RECEIVED TWEET MSG :: " + tweet)
      for (targetActor <- findFollowersOf(tweet.username)) {
        println("%% %% %% SEND MESSAGE TO ACTOR OF USER WITH NAME = " + targetActor.username)
        targetActor ! tweet
        println("%% %% %% SENDING MESSAGE FINISHED")
      }
	  }
	}

  def loggedInUsers : List[String] = {
    var loggedInUsers : List[String] = Nil
    users.foreach(loggedInUsers ::= _.username)
    loggedInUsers
  }

}
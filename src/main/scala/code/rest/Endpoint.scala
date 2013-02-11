package code.rest

import net.liftweb.http.rest.{RestContinuation, RestHelper}
import net.liftweb.http.OkResponse
import net.liftweb.json.JsonAST.JNull
import net.liftweb.util._
import net.liftweb.util.TimeHelpers.TimeSpan

/**
 * Created with IntelliJ IDEA.
 * User: christian
 * Date: 2/9/13
 * Time: 10:15 AM
 * To change this template use File | Settings | File Templates.
 */
object Endpoint extends RestHelper {

  serve {
    case "hello" :: "world" :: Nil Get req => println("$$$$$$$$$$$$$ REST ENDPOINT --> /hello/world --> [session id] " + req.sessionId); OkResponse()
    case "actors" :: Nil JsonGet req => RestContinuation.async {
      reply => {
        println("$$$$$$$$$$$$$ REST ENDPOINT --> /actors {JSON GET} --> [session id] " + req.sessionId)
        JNull
      }
    }
  }

}

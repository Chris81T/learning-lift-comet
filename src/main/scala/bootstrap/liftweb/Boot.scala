package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._

import shiro.Shiro
import shiro.sitemap.Locs._
import code.rest.Endpoint

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
		// initialize shiro
		Shiro.init()

		// where to search snippet
    LiftRules.addToPackages("code")
    
    // Build SiteMap
    LiftRules.setSiteMap(Pages.siteMap)

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.jsArtifacts = net.liftweb.http.js.jquery.JQueryArtifacts

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    // add rest endpoint -- using session
    LiftRules.dispatch.append(Endpoint)
  }
}

object Pages {

	val welcome = Menu.i("Welcome") / "index"
	val login = Menu.i("Login") / "login" >> RequireNoAuthentication >> DefaultLogin 
	val publicPage = Menu.i("Public Site") / "public"
	val privatePage = Menu.i("Private Site") / "private" >> RequireAuthentication >> HasRole("admin")
	val tweetPage = Menu.i("Tweet Site") / "tweet" >> RequireAuthentication >> HasRole("admin")

	def siteMap = SiteMap(List(welcome, publicPage, privatePage, tweetPage, login) ::: Shiro.menus : _*)

}
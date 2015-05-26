package controllers

import models.{Movie, MovieForm, MovieFile}
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

/**
 * Created by yabumoto on 2015/05/21.
 */
object Uploads extends Controller {

  val movieForm = Form(mapping(
    "title" -> text,
    "explain" -> text
  )(MovieForm.apply)(MovieForm.unapply))

  def view = Action { implicit request =>
    Ok(views.html.upload(movieForm, None))
  }

  def upload = Action(parse.multipartFormData) { implicit request =>
    movieForm.bindFromRequest.fold(
      errors => BadRequest,
      success => {
        val uploadFile = Movie.upload(success, request.body.file("movie_file").map { f =>
          MovieFile(f.ref.file, f.filename, f.contentType)
        })
        Ok(views.html.upload(movieForm, uploadFile))
      }
    )
  }
}

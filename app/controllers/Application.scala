package controllers

import play.api._
import play.api.mvc._
import models.dao.{Movie => MovieDao}

object Application extends Controller {

  def index = Secured { implicit auth =>
    implicit request =>
      Ok(views.html.index(MovieDao.findAllModels()))
  }

}